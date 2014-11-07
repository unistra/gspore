package spore

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
import static utils.MethodUtils.urlParse
import static utils.MethodUtils.placeHoldersReplacer
import static utils.MethodUtils.buildPayload;
import static utils.MethodUtils.buildParams;
import static request.Request.requestSend;
import errors.MethodCallError

class Method {
	static contentTypes = ['JSON':JSON,'TEXT':TEXT,'XML':XML,"HTML":HTML,"URLENC":URLENC,"BINARY":BINARY]
	static methods = ["GET":GET,"POST":POST,"PUT":PUT,"PATCH":PATCH]
	
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
	
	/**Explicit constructor 
	 * For each argument whose key 
	 * matches one of the Class 
	 * properties name, 
	 * set the value of
	 * the matching property 
	 * to its value
	 */
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
		Map responseClosures=[:]
		def (requiredParamsMissing,whateverElseMissing,errors)=[[], [], []]
		
		/**Modification of environment
		 * where request parameters
		 * require it
		 */
		environ+=beforeMiddlewareRewritingMap(reqParams)
		
		/**Loop through middlewares
		 * modify environment, store callbacks,
		 * set the response if a middleware breaks the workflow.
		 */
		def(noRequest,ret,middlewareModifiedenviron,storedCallbacks) =  middlewareBrowser(delegate.middlewares,environ)
		/**Resolution of the stored callbacks,
		 * which should be either reflect.Methods
		 * either Closures,
		 * in reverse order.
		 */
		storedCallbacks.reverseEach{
			/**The stored callback is 
			 * a java.lang.reflect.Method
			 */
			if (it.class==java.lang.reflect.Method){
				def declaringClass = it.getDeclaringClass()
				Object obj = declaringClass.newInstance([:])
				it.invoke(obj, middlewareModifiedenviron)
				responseClosures['success']=it
			}
			/**The stored callback
			 * is a Closure
			 */
			else{
			
				responseClosures['success']=it
			}
		}
		
		/**From here environ is not 
		 *modified anymore
		 *that's where missing
		 *or exceeding parameters
		 *errors can be raised.
		 **/
		if (noRequest==false){
			requiredParamsMissing = required_params.findAll{!reqParams.containsKey(it) &&  ! environ['spore.params'].containsKey(it)}
			[requiredParamsMissing,whateverElseMissing].each() {
				!it.empty?errors+=it:''
			}
			if(errors.size()>0){
				throw new MethodCallError("required param missing : $errors")
			}
			/**Effective processing of the request
			 * */
			if (errors.size()==0){
				responseClosures.each{clef,valeur->
					environ[clef]=valeur
				}
				def t = 3345
				try{
				ret = requestSend(environ)
				}catch(Exception e){
				ret = e
				}
			}
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
		def format=formats?:global_formats?:"application/json"
		//normalized=contentTypes[format?.class==java.lang.String?format.toUpperCase():format[0].toUpperCase()]?:format
	}
	
	
	public beforeMiddlewareRewritingMap(reqParams){
		def (queryString,finalPath) = placeHoldersReplacer(reqParams,path,this)
		return ['QUERY_STRING':queryString,
			    'base_url':base_url,
				'method':method,
				'finalPath':finalPath,
				'spore.params':buildParams(reqParams,this),
				'spore.payload':buildPayload(reqParams,this)]
	}
	
	
	
	public static def middlewareBrowser(middlewares,environ){
		boolean noRequest=false
		def ret=""
		def storedCallbacks=[]
		
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
		return [noRequest,ret,environ,storedCallbacks]
	}
}
