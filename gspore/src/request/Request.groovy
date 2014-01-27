package request

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT
import static groovyx.net.http.Method.HEAD
import static groovyx.net.http.Method.DELETE
import static groovyx.net.http.Method.PATCH
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.XML
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.HTML

class Request {
	static contentTypes = ['JSON':JSON,'TEXT':TEXT,'XML':XML,"HTML":HTML,"URLENC":URLENC,"BINARY":BINARY]
	static methods = ["GET":GET,"POST":POST,"PUT":PUT,"PATCH":PATCH,"HEAD":HEAD,"DELETE":DELETE]
	static HTTPBuilder builder = new HTTPBuilder();
	
	public static String requestSend(args){
		def ret=""
		builder.request(args['base_url'],methods[args['method']],contentTypesNormalizer(args)) {
			
				uri.path = args['finalPath']
				uri.query = args['queryString']
				args["spore.headers"].each{k,v->
					headers."$k"="$v"
				}
				headers.'User-Agent' = 'Satanux/5.0'
				headers.Accept=contentTypesNormalizer(args)

				if (["POST", "PUT", "PATCH"].contains(request.method)){
					send contentTypesNormalizer(),args['spore.payload']
				}

				response.success =  {resp,json->
					String statusCode=String?.valueOf(resp.statusLine.statusCode)
					ret += json
					ret+=" : "
					ret+=statusCode
				}

				response.failure ={resp->
					String statusCode=String?.valueOf(resp.statusLine.statusCode)
					ret+="request failure"+" : "+statusCode
				}
			}
		return ret
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
	def static contentTypesNormalizer(args){
		def normalized
		def format=args['spore.format']?:args['formats']?:args['global_formats']
		normalized=format.class==groovyx.net.http.ContentType?format:args['contentTypes'][format.class=java.lang.String?format.toUpperCase():args['formats'][0].toUpperCase()]
	}

}
