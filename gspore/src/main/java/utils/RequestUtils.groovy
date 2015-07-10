package utils

class RequestUtils {

	public RequestUtils() {
		// TODO Auto-generated constructor stub
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
	 * And is useless now. Just rewrite it, or
	 * remove it, please.
	 */
	//	public static contentTypesNormalizer(args){
	//		def normalized
	//		def format=args['spore.format']?:args['formats']?:args['global_formats']?:"application/json"
	//		return format=="json"||format.contains("json")?"application/json":format
	//	}
	public static finalPath(args){
		args['finalPath']
	}

	static def statusIsNotInExpectedStatuses ={args,ret->args['spore.expected_status'] && !args['spore.expected_status'].collect{it.toString()}.contains(ret.response.status.toString())}
	static def queryString = {a->
		a['QUERY_STRING'].findAll { it?.key != "payload" }
		}
	static def requiresScan={j->j?.class in org.apache.http.conn.EofSensorInputStream || j?.class in java.io.InputStreamReader }
	static def data = {s->s.hasNext() ? s.next() : ""}
	static def requiredContentType = {args->args['spore.headers']?.keySet()?.contains('Content-Type') ? args['spore.headers']['Content-Type'] : args["spore.format"]}
	public static finalUrl(args){
		args['wsgi.url_scheme']+"://"+domainNameAndServerPort(args['SERVER_NAME'],args['SERVER_PORT'])+(args['SCRIPT_NAME'][args['SCRIPT_NAME'].length() - 1]!="/"?args['SCRIPT_NAME']:args['SCRIPT_NAME'].subSequence(0, args['SCRIPT_NAME'].length() - 1))
	}

	public static String domainNameAndServerPort(domainName,serverPort){
		def ret
		if(domainName.indexOf(':')==-1){
			ret=domainName+":"+serverPort
		}
		else {
			ret=domainName
		}
	}
}
