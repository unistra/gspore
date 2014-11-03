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
	public static contentTypesNormalizer(args){
		//bon ici faut que tu
		//interceptes les occurrences de "json"
		//et que tu foutes application/json
		//c'est dans la spec donc do it.
		def normalized
		def format=args['spore.format']?:args['formats']?:args['global_formats']?:"application/json"
		return format=="json"?"application/json":format
	}
	public static finalPath(args){
		// ici c'est vraiment bizarre
		//args['finalPath'].startsWith('/')?args['finalPath'][1..-1]:args['finalPath'].startsWith('/')
		args['finalPath']
	}

	public static finalUrl(args){
		args['wsgi.url_scheme']+"://"+domainNameAndServerPort(args['SERVER_NAME'],args['SERVER_PORT'])+args['SCRIPT_NAME']
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
