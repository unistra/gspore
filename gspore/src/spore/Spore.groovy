package spore

import errors.MethodError
import errors.SporeError
import middleware.*

class Spore {
	static errorMessages=[
		'name':'A name for this client is required',
		'base_url':'A base URL to the REST Web Service is required',
		'methods':'One method is required to create the client'
	]
	@Mandatory
	def name
	@Mandatory
	def base_url=null
	def authority
	def formats
	def version
	def authentication
	@Mandatory
	List methods=[]// test purposes
	def meta
	def middlewares=[:]
	def user_agent
	def originalMethods

	/**Explicit constructor
	 * When an explicit constructor is specified,
	 * the default initialization doesn't work
	 * */
	Spore(args){
		originalMethods=this.metaClass.methods*.name
		sporeErrors(args)
		/** Saturations of properties 
		 *  with matching parsed JSON entries
		 */
		args?.each(){k,v->
			if (this.properties.find({it.key==k && !['methods'].contains(k)})){
				this."$k"=v
			}
		}
		/**For each entry in the set of elements
		 * found under "methods" in the parsed
		 * JSON, a Method instance is generated,
		 * and its request property, which is a 
		 * closure, is added to the Spore's methods
		 * under the matching name, with a one parameter 
		 * signature that must be fulfilled with
		 * a parameter Map.
		 * */
		args?."methods".each(){methodName,value->
			try{
				//println "ouais"+![null, ""].contains(value['base_url'])?value['base_url']:base_url
				methods+=methodName
				def m = createMethod([
					name:methodName,
					/**Inherited from spore if not specified in the parsed Json*/
					base_url:![null, ""].contains(value['base_url'])?value['base_url']:base_url,
					/**Found in the Json [k]*/
					path:value['path'],
					method:value['method'],
					required_params:value['required_params'],
					optional_params:value['optional_params'],
					expected_status:value['expected_status'],
					required_payload:value['required_payload'],
					description:value['description'],
					authentication:value['authentication'],
					formats:value['formats'],
					documentation:value['documentation'],
					defaults : value['defaults'],
					/**Inherited from Spore*/
					middlewares:middlewares,
					global_authentication:authentication,
					global_formats:formats
				])
				/**Next is the spot where the Method
				 *is dynamically added to the Spore 
				 *If no Method could be created, nothing happens.
				 **/
				m?.class==spore.Method?this.metaClass[methodName]=m.request:""
			}catch (MethodError me){
				throw new MethodError(me)
			}
		}
	}

	/**@param parsedJson : the Json from which the Method should
	 * be created.
	 * @return either a Method either a String describing what prevented 
	 * the method from being created.
	 */
	def createMethod(parsedJson)throws MethodError{
		def checkResult = methodIntegrityCheck(parsedJson)
		if (checkResult==true){
			return new Method(parsedJson)
		}else{
		String message=checkResult.values().join(';')
		
			
			throw new MethodError(message,new Throwable(message))
			return checkResult
		}
	}

	/**Checks if the Json data from which the Method is to be created
	 * is sufficient, i.e if it contains the mandatory fields.
	 * @param parsedJson
	 * @return true, if the Json contains sufficient data for required fields, or 
	 * a Map containing error messages registered under the concerned property name
	 */
	def methodIntegrityCheck(parsedJson){

		Map methodBuildError=[:]
		List requiredParams = parsedJson['required_params']?:[]
		List optionalParams = parsedJson['optional_params']?:[]

		def mandatoryFields=spore.Method.declaredFields.findAll {
			Mandatory in it.declaredAnnotations*.annotationType()
		}*.name
		if (!requiredParams.disjoint(optionalParams)){
			methodBuildError["params"]="params cannot be optional and mandatory at the same time"

		}
		(mandatoryFields-"api_base_url").each {requiredField->
			if (!parsedJson.find{k,v->
				k==requiredField
			}){
				methodBuildError[requiredField]="$requiredField is a required field for generated methods, $requiredField couldn't  be generated"
			}
		}
		if (!parsedJson['base_url'] && !parsedJson['api_base_url'] && !base_url){

			methodBuildError['base_url']="Either a base_url or an api_base_url should be specified"

		}
		return methodBuildError?.size()==0?true:methodBuildError
	}


	/**The method used to 
	 * enable a Middleware to modifiy
	 * requests and responses
	 * @param middleware
	 * @param args
	 * @return
	 */
	def enable(middleware,args){
		enableIf(middleware,args,{true})
	}
	/**The method used to 
	 * enable CONDITIONNALY a Middleware to modifiy
	 * requests and responses
	 * @param middleware
	 * @param args 
	 * @param clos the condition on which the Middleware should be enabled.
	 * @return
	 */
	def enableIf(middleware,args,Closure clos){
		def instance = middleware.newInstance(args)
		middlewares[clos]= instance
	}
	/**The method used to 
	 * enable CONDITIONNALY a Middleware to modifiy
	 * requests and responses
	 * @param middleware the class of the Middleware to enable
	 * @param args 
	 * @param clos the condition on which the Middleware
	 * should be enabled, derived from a client-consumer
	 * side written java.lang.reflect.Method 
	 * @return
	 */
	def enableIf(middleware,args,java.lang.reflect.Method clos){
		def instance = middleware.newInstance(args)
		middlewares[clos]= instance
	}

	/**Lighter syntax for Jizzlewares
	 * @param middleware
	 * @param args
	 * @param String conditionName : the name of the condition
	 * on which the middleware is to be enabled. Should be 
	 * methodJizzed by the middleware instance.
	 * 
	 * @return
	 */
	def enableIf(middleware,args,String conditionName){
		def instance = middleware.newInstance(args)
		def clos= instance.methodJizz(conditionName)
		middlewares[clos]= instance
	}
	//TODO
	def addDefault(param,value){

	}
	//TODO
	def removeDefault(){

	}
	def description(){
		(this.metaClass.methods*.name-originalMethods).sort().unique()
	}
	def sporeErrors(args){
		def specErrors=[:]

		/**Get all properties declared as mandatory
		 * and register missing properties passed
		 * to the SporeError
		 * */
		def mandatoryFields=this.properties.findAll { prop ->
			this.getClass().declaredFields.find {
				it.name == prop.key && Mandatory in it.declaredAnnotations*.annotationType()
			}
		}.keySet()
		mandatoryFields.each(){
			if (!args."$it") specErrors[it]="missing required field : $it"
		}

		if (specErrors.size()>0){
			def errormessage=""
			specErrors.each{
				errormessage+=errorMessages[it.key]
			}
			throw new SporeError(errormessage)
		}
	}

}
