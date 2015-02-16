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

import org.apache.http.client.entity.UrlEncodedFormEntity;

class Request {
	static contentTypes = ['JSON':JSON,'TEXT':TEXT,'XML':XML,"HTML":HTML,"URLENC":URLENC,"BINARY":BINARY,"ANY":ANY]
	static methods = ["GET":GET,"POST":POST,"PUT":PUT,"PATCH":PATCH,"HEAD":HEAD,"DELETE":DELETE]
	static HTTPBuilder builder = new HTTPBuilder();
	
	public static def requestSend(args){
		def requiresScan={j->j.class in org.apache.http.conn.EofSensorInputStream }
		def data = {s->s.hasNext() ? s.next() : ""}
		def ret
		/*The response behavior
		 *when the request is successful
		 */
		def success={resp,json->
			String statusCode=String?.valueOf(resp.statusLine.statusCode)
			if (args['success'] ){
				ret = args['success'](resp,json)
			}else{
				if (requiresScan(json)){
					def s = new java.util.Scanner(json).useDelimiter("\\A");
					ret=['response':resp,"data":data(s)];
				}else{
				ret=['response':resp,"data":json]
				}
			}
		}
		/*The response behavior
		 *when the request fails
		 */
		def failure={resp,json->
				if (requiresScan(json)){
					def s = new java.util.Scanner(json).useDelimiter("\\A");
					ret=['response':resp,"data":data(s)];
				}else{
				ret=['response':resp,"data":json]
				}
		}
		builder.handler.success=success
		builder.handler.failure=failure
		
		/*The builder's request method is 
		 *the spot where the request is actually sent.
		 *Its handlers are the response formatters.
		 */
		def ct = args['spore.headers']?.keySet().contains('Content-Type') ? args['spore.headers']['Content-Type'] : args["spore.format"]
//		args.containsKey("spore.format") && args["spore.format"]!=""?ct=args["spore.format"]:""
		builder.request(finalUrl(args),methods[args['method']], ct) {
			uri.path += finalPath(args)
			uri.query = args['QUERY_STRING']
			args["spore.headers"].each{k,v->
				headers."$k"="$v"
			}
			headers.'User-Agent' = "GSPORE"
			headers.'Accept' = contentTypes.ANY
			if (["POST", "PUT", "PATCH"].contains(request.method)){
//				send contentTypesNormalizer(args),args['spore.payload']
				send ct, args['spore.payload']
			}
		}
		return ret
	}
}
