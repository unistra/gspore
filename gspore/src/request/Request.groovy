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
	HTTPBuilder builder = new HTTPBuilder();
	
	Request(args){
		
		builder.request(args['base_url'],methods[method],contentTypesNormalizer(environ)) {
			uri.path = finalPath
			uri.query = queryString
			environ["spore.headers"].each{k,v->
				headers."$k"="$v"
			}
			headers.'User-Agent' = 'Satanux/5.0'
			headers.Accept=contentTypesNormalizer(environ)

			if (["POST", "PUT", "PATCH"].contains(request.method)){
				send contentTypesNormalizer(),environ['spore.payload']
			}

			response.success =  {resp,json->
				String statusCode=String?.valueOf(resp.statusLine.statusCode)
				ret += json
				ret+=" : "
				ret+=statusCode
				resp.properties.each{k,v->
					//println k
					//println v

				}
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
