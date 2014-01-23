package middleware
import request.Response

class Mock extends middleware.Middleware{
	
	/*
	 *  def expected_response(request):
return utils.fake_reponse(request, 'OK', status_code=200,
headers={'Content-Type': 'text-plain'})

client = britney.spyre('/path/to/spec.json')
client.enable(utils.Mock, fakes={'/test': expected_response})

result = client.test()

assert(result.text == 'OK')
assert(result.status_code == 200)
assert('Content-Type' in result.headers)
	 */
	
	public Mock() {
	
	}
	public Mock(args) {
		args.each{k,v->
			this.metaClass."$k"=v
		}
	this.metaClass."fakes" = args['fakes']
	this.metaClass."middlewares" = args['middlewares']?:[]
		this.properties.each(){k,v->
			println k
			println v
		
		}
	}
	
	def processRequest(environ){
		def args = [:]
		this."fakes".each{path,closure->
			if (path == ('/'+environ['name'])){
				args[path]=closure
				args["response"]=closure().response
			}
		}
		Response r = new Response(args)
		return r
	}
	def mockProcessResponse(){
		
	}
	public static fakeResponse(){
		return 'FAKE'
	}

}
