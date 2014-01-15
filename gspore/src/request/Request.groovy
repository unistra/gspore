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
	
 	Request(args,environ){
		 
		 		HTTPBuilder builder = new HTTPBuilder();
		 	 	def ret=""
				  
			 	builder.request(environ['base_url'],methods[environ['method']],contentTypesNormalizer(args)) {
				uri.path = args['finalPath']
				uri.query = args['queryString']
				headers.'User-Agent' = 'Satanux/5.0'
				headers.Accept=contentTypesNormalizer(args)

				if (["POST", "PUT", "PATCH"].contains(request.method)){
					send contentTypesNormalizer(environ),environ['spore.payload']
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

	 }
	 
	 def contentTypesNormalizer(args){
		 def normalized
		 def format=args['formats']?:args['global_formats']
		 normalized=contentTypes[format.class==java.lang.String?format.toUpperCase():format[0].toUpperCase()]
	 }
}
