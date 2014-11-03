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

	public static def requestSend(args){

		def ret
		/*The response behavior
		 *when the request is successful
		 */
		def defaultBehavior={resp,json->
			String statusCode=String?.valueOf(resp.statusLine.statusCode)
			if (args['success'] ){
				ret = args['success'](resp,json)
			}else{
				ret = json
			}
		}
		/*The response behavior
		 *when the request fails
		 */
		def defaultFailureBehavior={resp,json->
			String statusCode=String?.valueOf(resp.statusLine.statusCode)
			ret=json
		}

		builder.handler.success=defaultBehavior
		builder.handler.failure=defaultFailureBehavior
		
		/*The builder's request method is 
		 *the spot where the request is actually sent.
		 *Its handlers are the response formatters.
		 */
		builder.request(finalUrl(args),methods[args['method']],contentTypesNormalizer(args)) {
			uri.path = finalPath(args)
			uri.query = args['QUERY_STRING']
			args["spore.headers"].each{k,v->
				headers."$k"="$v"
			}
			headers.'User-Agent' = 'GSPORE'
			headers.'Accept' = contentTypesNormalizer(args)
			if (["POST", "PUT", "PATCH"].contains(request.method)){
				send contentTypesNormalizer(args),args['spore.payload']
			}
		}
		return ret
	}



	
}
