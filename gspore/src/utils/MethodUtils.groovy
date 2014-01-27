package utils

import java.util.Map;

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

	/**pop ["payload"]from parameters and add payload to environ
	 * @param p : the request effective parameters
	 * @return the payload
	 */
	public static buildPayload(p){
		def entry = p["payload"]
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
		if (path.indexOf(':')!=-1){
			corrected = path.split ('/').collect{it.startsWith(":")?req.find({k,v->k==it-(":")})?.value:it}.join('/')
		}
		def usedToBuildFinalPath=path.split ('/').findAll{it.startsWith(":")}.collect{
			it.replace(':','')
		}
		queryString.each{k,v->
			if (param(k,method) && ! usedToBuildFinalPath.contains(k)){
				finalQuery[k]=v
			}
		}
		return [queryString:finalQuery,finalPath:corrected!=""?corrected:path]
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
		param && param!="" && params.contains(param)
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
