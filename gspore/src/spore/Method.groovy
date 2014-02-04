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
import static middleware.MiddlewareBrowser.*;
import static utils.MethodUtils.urlParse
import static utils.MethodUtils.placeHoldersReplacer
import static utils.MethodUtils.buildPayload;
import static utils.MethodUtils.buildParams;
import static request.Request.requestSend;
import errors.MethodCallError

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
			'SERVER_NAME': urlParse(base_url).hostName,
			'SERVER_PORT': urlParse(base_url).serverPort!=-1?urlParse(base_url).serverPort:base_url.startsWith('https')?443:base_url.startsWith('http')?80:'',
			'SCRIPT_NAME': urlParse(base_url).path,
			'PATH_INFO': path,
			'QUERY_STRING': "",
			'HTTP_USER_AGENT': 'whatever',
			'spore.expected_status': expected_status?:"",
			'spore.authentication': authentication,
			'spore.params': '',
			'spore.payload': '',
			'spore.errors': '',
			'spore.format': contentTypesNormalizer(),
			'spore.userinfo': urlParse(base_url).userInfo,
			'wsgi.url_scheme': urlParse(base_url).scheme,
			'name':name
		]
	}
	/**A closure that builds the actual request from 
	 * environ, parameters and enabled middlewares
	 */
	def request={reqParams->

		
		Map environ = baseEnviron()
		Map modifiedEnvirons = [:]
		Map middlewareModifiedEnviron=[:]
		def ret = ""
		def (requiredParamsMissing,whateverElseMissing,errors,storedCallbacks)=[[], [], [], []]
		def finalPath = placeHoldersReplacer(reqParams,path,this).finalPath
		def queryString = placeHoldersReplacer(reqParams,path,this).queryString
		environ['QUERY_STRING']=queryString
		environ['base_url']=base_url
		environ['method']=method
		environ['finalPath']=finalPath
		environ['spore.params']=buildParams(reqParams,this)
		environ['spore.payload']=buildPayload(reqParams,this)
		
		/**Loop that breaks if a Response
		 * is found. Can modify any of the keys and
		 * values of the request's base environment
		 * or create new ones, via middleware logic
		 * and store callbacks intended on modifying
		 * the response
		 */
		def afterLoopMap=  middlewareBrowser(delegate.middlewares,environ,storedCallbacks,ret)
		
		ret = afterLoopMap.ret
		environ = afterLoopMap.environ
		
		/**Resolution of the stored callbacks,
		 * which should be either reflect.Methods
		 * either Closures,
		 * in reverse order.
		 */
		afterLoopMap.storedCallbacks.reverseEach{
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
		if (afterLoopMap.noRequest==false){
			required_params.each{
				if (!reqParams.containsKey(it) &&  ! environ['spore.params'].containsKey(it)){
					requiredParamsMissing+=it
				}
			}
			[
				requiredParamsMissing,
				whateverElseMissing
			].each() {
				!it.empty?errors+=it:''
			}
			if(errors.size()>0){
				throw new MethodCallError("required param missing : $errors")
			}
		}
		/**Effective processing of the request
		 * */
		if (errors.size()==0 && afterLoopMap.noRequest==false){
			
			ret = requestSend(environ)
		}

		if (!requiredParamsMissing.empty){
			ret=""
			requiredParamsMissing.each{
				ret += "$it is missing for $name"
				environ["spore.errors"]?environ["spore.errors"]+="$it is missing for $name":(environ["spore.errors"]="$it is missing for $name")
			}
		}
		return ret
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
		normalized=contentTypes[format?.class==java.lang.String?format.toUpperCase():format[0].toUpperCase()]?:format
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
		//voilà ici, c'est naze tu dois t'en débarasser
		normalized=format.class==groovyx.net.http.ContentType?format:contentTypes[format.class==java.lang.String?format.toUpperCase():format[0].toUpperCase()]
	}
	
	def middlewareBrowser(middlewares,environ,storedCallbacks,ret){
		boolean noRequest=false
		/**rather not idiomatic breakable loop that
		 * calls middlewares. Breaks if a Response
		 * is found. Can modify any of the keys and
		 * values of the request's base environment
		 * or create new ones, via middleware logic
		 * and store callbacks intended on modifying
		 * the response
		 * */
		middlewares.find{condition,middleware->
			def callback
			/**If the condition was written in Java*/
			if (condition.class == java.lang.reflect.Method){
				def declaringClass = condition.getDeclaringClass()
				Object obj = declaringClass.newInstance([:])
				if (condition.invoke(obj,environ)){
					callback = middleware.call(environ)
				}
			}
			/**else (i.e if it is a groovy.lang.Closure)*/
			else if (condition(environ)){
				callback = middleware(environ)
			}
			/**break loop
			 */
			if (callback in Response){
				noRequest=true
				ret = callback(environ)
				return true
			}
			/**store to process after request*/
			if (callback!=null){
				storedCallbacks+=callback
			}
			/**pass control to next middleware*/
			return false
		}
		//return noRequest
		return ["noRequest":noRequest,"environ":environ,"storedCallbacks":storedCallbacks,"ret":ret]
	}
}
