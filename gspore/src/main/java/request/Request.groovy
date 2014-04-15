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
import static utils.RequestUtils.contentTypesNormalizer
import static utils.RequestUtils.finalPath
import static utils.RequestUtils.finalUrl
import static utils.RequestUtils.domainNameAndServerPort
class Request {
	static contentTypes = ['JSON':JSON,'TEXT':TEXT,'XML':XML,"HTML":HTML,"URLENC":URLENC,"BINARY":BINARY]
	static methods = ["GET":GET,"POST":POST,"PUT":PUT,"PATCH":PATCH,"HEAD":HEAD,"DELETE":DELETE]
	static HTTPBuilder builder = new HTTPBuilder();

	public static String requestSend(args){

		def ret=""
		def defaultBehavior={resp,json->
			String statusCode=String?.valueOf(resp.statusLine.statusCode)
			if (args['success'] ){
				ret = args['success'](resp,json)
			}else{
				ret += json
				ret+=" : "
				ret+=statusCode
			}
		}

		def defaultFailureBehavior={resp,json->
			String statusCode=String?.valueOf(resp.statusLine.statusCode)
			ret+="request failure"+" : "+statusCode
		}

		builder.handler.success=defaultBehavior
		builder.handler.failure=defaultFailureBehavior
		builder.request(finalUrl(args),methods[args['method']],contentTypesNormalizer(args)) {
			uri.path = finalPath(args)
			uri.query = args['queryString']
			args["spore.headers"].each{k,v->
				headers."$k"="$v"
			}
			headers.'User-Agent' = ' Mozilla/5.0 '
			headers.'Accept'=contentTypesNormalizer(args)
			if (["POST", "PUT", "PATCH"].contains(request.method)){
				send contentTypesNormalizer(args),args['spore.payload']
			}
		}
		return ret
	}



	
}
