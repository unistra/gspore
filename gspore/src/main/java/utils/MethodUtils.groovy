package utils

import java.util.Map;
import errors.MethodCallError
import java.util.regex.Matcher
import java.util.regex.Pattern

class MethodUtils {

	public static Map urlParse(base_url){
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
	//here, is it necessary to remove ["payload"]from parameters??????????????
	/**pop ["payload"]from parameters and add payload to environ
	 * @param p : the request effective parameters
	 * @return the payload
	 */
	public static buildPayload(p,method){
		def entry = p["payload"]?:null
		if (method.required_payload && !entry) throw new MethodCallError('Payload is required for this function')
		p.remove("payload")
		return entry
	}
	/**Transforms the raw path still
	 * containing placeHolders
	 * with matching values found
	 * in the effective method call
	 * parameters
	 * @param req the effective request
	 * @param path path
	 * @return the corrected path
	 */
	public static placeHoldersReplacer(req,path,method){
		Map queryString = req
		String corrected=""
		Map finalQuery=[:]

		/**If the path contains placeHolders marks*/
		def correctedList=[]
		def keys=[]
		def gotReplaced=[]
		if (path.contains(':')){
			
			/**For each component delimited by a slash*/
			path.split ('/').each{
				def correctedElement=""
				/**if it contains multipleplaceholders*/
				if (it.contains('.') && it.contains(':')){
					
					correctedElement= it.split (/\./).collect{it.indexOf(':')!=-1?req.find({k,v->k==it-(":")})?.value:it}.join('.')
					it.split (/\./).each{
						gotReplaced+=it.replace(':','')
					}
					}else if (it.contains(':')){
				
					correctedElement=req.find({k,v->k==it-(":")})?.value
					gotReplaced+=it.replace(':','')
				}else{
				correctedElement=it
				}
				correctedList+=correctedElement
			}
			corrected=correctedList.join('/')
		}
		queryString.each{k,v->
			if (param(k,method) && ! gotReplaced.contains(k)){
				finalQuery[k]=v
			}
		}
		return [finalQuery,(corrected!=""?corrected:path)]
	}
	/**For each effective request parameter, checks if it is registered under
	 * optional or required params
	 * @param method : the method for which the parameterBuilder is called
	 * @param param : the parameter to test
	 */
	public static boolean param(param,method){
		List params=[]
		[
			method?.optional_params,
			method?.required_params
		].each(){
			if (it!=null && !it.empty && it!=""){
				params+=it
			}
		}
		if (param!="payload" && !params.contains(param)){
			throw new MethodCallError("Unregistered parameter")
		}else{
			return true
		}
		//return param && param!="" && params.contains(param)
	}
	/**
	 * @param method : the method for which the parameterBuilder is called
	 * @param p : the request effective parameters
	 * @return only parameters that are listed under optional or required params
	 */
	public static buildParams(p,method){
		return p.findAll{k,v->
			param(k,method)
		}
	}
}
