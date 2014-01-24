package spore

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.HEAD
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT
import static groovyx.net.http.Method.PATCH
import static groovyx.net.http.Method.DELETE
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.XML
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.HTML
import errors.MethodCallError
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher
import java.util.regex.Pattern

import request.Response

class Method {
	static contentTypes = ['JSON':JSON,'TEXT':TEXT,'XML':XML,"HTML":HTML,"URLENC":URLENC,"BINARY":BINARY]
	static methods = ["GET":GET,"POST":POST,"PUT":PUT,"PATCH":PATCH]

	HTTPBuilder builder = new HTTPBuilder();

	@Mandatory
	def name
	@Mandatory
	def api_base_url
	@Mandatory
	String method
	@Mandatory
	def path
	def required_params=[]
	def optional_params=[]
	def expected_status
	def required_payload
	def description
	def authentication
	def formats
	def base_url
	def documentation
	def middlewares
	def global_authentication
	def global_formats
	def defaults

	//Explicit  constructor
	Method(args){
		args?.each(){k,v->
			if (this.properties.find({ it.key==k})){
				this."$k"=v
			}
		}
	}
	
	/**@return Request environment 
	 * before middleware or effective 
	 * request modifications
	 */
	def baseEnviron(){

		def normalizedPath=path.split ('/').collect{it.trim()}-null-""
		def formatedPathRemainder=path.replace('/'+(normalizedPath[0]),'')
		return [

			'REQUEST_METHOD': method,
			'SERVER_NAME': urlParse().hostName,
			'SERVER_PORT': urlParse().serverPort,
			'SCRIPT_NAME': normalizedPath.size()>0?('/'+(normalizedPath[0])):"",
			'PATH_INFO': formatedPathRemainder,
			'QUERY_STRING': "",
			'HTTP_USER_AGENT': 'whatever',
			'spore.expected_status': expected_status?:"",
			'spore.authentication': authentication,
			'spore.params': '',
			'spore.payload': '',
			'spore.errors': '',
			'spore.format': contentTypesNormalizer(),
			'spore.userinfo': urlParse().userInfo,
			'wsgi.url_scheme': urlParse().scheme,
			'name':name

		]
	}

	/**A closure that builds the actual request from 
	 * environ, parameters and enabled middlewares
	 */
	def request={reqParams->

		boolean noRequest=false
		Map environ = baseEnviron()
		Map modifiedEnvirons = [:]
		Map middlewareModifiedEnviron=[:]
		def ret = ""
		def (requiredParamsMissing,whateverElseMissing,errors,storedCallbacks)=[[], [], [], []]


		int i=0
	
		def finalPath = placeHoldersReplacer(reqParams).finalPath
		def queryString = placeHoldersReplacer(reqParams).queryString

		environ['QUERY_STRING']=queryString
		environ['spore.params']=buildParams(reqParams)
		environ['spore.payload']=buildPayload(reqParams)
		
		/**rather not idiomatic breakable loop
		 * that calls middlewares. Breaks if a Response
		 * is found. Can modify any of the keys and values
		 * of the request's base environment or create new
		 * ones, via middleware logic and store callbacks
		 * intended on modifying the response
		 * */
		delegate.middlewares.find{condition,middleware->
			def callback

			/**If the condition was written in Java*/
			if (condition.class == java.lang.reflect.Method){
				def declaringClass = condition.getDeclaringClass()
				Object obj = declaringClass.newInstance([:])
				if (condition.invoke(obj,environ)){
				
					callback =	middleware.call(environ)
				}
			}
			/**else (i.e if it is a groovy.lang.Closure)*/
			else if (condition(environ)){
				//TEMPLE OF BOOM :middlewares are callable
				callback=middleware(environ)
				//callback =	middleware?.call(environ)?:null
			}

			/**break loop
			 */
			if (callback in Response){
				noRequest=true
				//TODO fais quelque chose pour ça s'il te-plait
				ret = callback(environ)
				return true
			}

			/**store to process after request*/
			if (callback!=null){
				storedCallbacks+=callback
			}
			i++
			/**pass control to next middleware*/
			return false
		}
		
		/**Resolution of the stored callbacks,
		 * which should be either reflect.Methods
		 * either Closures,
		 * in reverse order.
		 */
		 storedCallbacks.reverseEach{
						 if (it.class==java.lang.reflect.Method){
							 def declaringClass = it.getDeclaringClass()
							 Object obj = declaringClass.newInstance([:])
							 it.invoke(obj, environ)
						 }else{
						 	it(environ)
						 }
					 }
		
		/**From here environ is not 
		*modified anymore
		*that's where missing
		*or exceeding parameters
		*errors can be raised.
		**/
		required_params.each{
			if (!reqParams.containsKey(it) &&  ! environ['spore.params']){
				requiredParamsMissing+=it
			}
		}
		[
			requiredParamsMissing,
			whateverElseMissing
		].each() {
			!it.empty?errors+=it:''
		}
		/**Effective processing of the request
		 * */
		if (errors.size()==0 && noRequest==false){
			
			builder.request(base_url,methods[method],contentTypesNormalizer(environ)) {
				uri.path = finalPath
				uri.query = queryString
				environ["spore.headers"].each{k,v->
					headers."$k"="$v"
				}
				headers.'User-Agent' = 'Satanux/5.0'
				headers.Accept=contentTypesNormalizer(environ)

				if (["POST", "PUT", "PATCH"].contains(request.method)){
					send contentTypesNormalizer(),environ['spore.payload']
				}

				response.success =  {resp,json->
					String statusCode=String?.valueOf(resp.statusLine.statusCode)
					ret += json
					ret+=" : "
					ret+=statusCode
					resp.properties.each{k,v->
						//println k
						//println v
						
					}
				}

				response.failure ={resp->
					String statusCode=String?.valueOf(resp.statusLine.statusCode)
					ret+="request failure"+" : "+statusCode
				}
			}
		}
		if (!requiredParamsMissing.empty){
			requiredParamsMissing.each{
				ret += "$it is missing for $name"
				environ["spore.errors"]?environ["spore.errors"]+="$it is missing for $name":(environ["spore.errors"]="$it is missing for $name")
			}
		}
		println environ
		return ret
	}
	
	/**Transforms the raw path still
	 * containing placeHolders
	 * with matching values found 
	 * in the effective method call 
	 * parameters
	 * @param req the effective request
	 * @return the corrected path
	 */
	def placeHoldersReplacer(req){
		Map queryString = req
		String corrected=""
		Map finalQuery=[:]
		if (path.indexOf(':')!=-1){
			corrected = path.split ('/').collect{it.startsWith(":")?req.find({k,v->k==it-(":")})?.value:it}.join('/')
		}
		def usedToBuildFinalPath=path.split ('/').findAll{it.startsWith(":")}.collect{
			it.replace(':','')
		}
		queryString.each{k,v->
			if (param(k) && ! usedToBuildFinalPath.contains(k)){
				finalQuery[k]=v
			}
		}
		return [queryString:finalQuery,finalPath:corrected!=""?corrected:path]
	}

	Map urlParse(){
		URL aURL = new URL(base_url)
		URI aURI = new URI(base_url)
		return [
			"hostName":aURL.getHost(),
			"serverPort":aURI.getPort(),
			"path":aURL.getPath(),
			"query" :aURL.getQuery(),
			"userInfo":aURL.getUserInfo(),
			"scheme":aURI.getScheme()
		]
	}

	/**pop ["payload"]from parameters and add payload to environ
	 * @param p : the request effective parameters
	 * @return the payload
	 */
	def buildPayload(p){
		def entry = p["payload"]
		p.remove("payload")
		return entry
	}

	/**Bon alors là mec t'as eu une interprétation charitable dans laquelle les exceeding paramaters 
	 * ne déclenchent pas d'erreurs et sont juste éliminés
	 * @param p : the request effective parameters
	 * @return only parameters that are listed under optional or required params
	 */
	def buildParams(p){
		return p.findAll{k,v->
			param(k)
		}
	}
	/**For each effective request parameter, checks if it is registered under 
	 * optional or required params
	 */
	boolean param(param){
		List params=[]
		[
			optional_params,
			required_params
		].each(){
			if (it!=null && !it.empty && it!=""){
				params+=it
			}
		}
		param && param!="" && params.contains(param)
	}
	/**The no-parameter version of 
	 *contentTypesNormalizer is used
     *only when generating the
     *base environment of the request
	 * @return a content-type
	 * 
	 */
	def contentTypesNormalizer(){
		def normalized
		def format=formats?:global_formats
		normalized=contentTypes[format.class==java.lang.String?format.toUpperCase():format[0].toUpperCase()]
	}
	/**
	 * @return in this order
	 * the content-type specified
	 * in the environ (so that if it has
	 * been modified by whatever middleware,
	 * it is taken in account), 
	 * the specific content type
	 * for this method, or would it be missing,
	 *  the global_format which 
	 * is inherited from the spore.
	 */
	def contentTypesNormalizer(args){
		def normalized
		def format=args['spore.format']?:formats?:global_formats
		normalized=format.class==groovyx.net.http.ContentType?format:contentTypes[format.class=java.lang.String?format.toUpperCase():format[0].toUpperCase()]
	}
}
