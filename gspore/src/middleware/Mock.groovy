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
	}
	
	def processRequest(environ){
		def args = [:]
		this?."fakes"?.each{path,closure->
			//il faudrait virer la référence à 'name'
			//et ça serait bien de ne pas le mettre dans les base environs.
			//Si la méthode doit être désactivée
			//par le middleware
			if (path == ('/'+environ['name'])){
				args[path]=closure
				args["response"]=closure().response
			}
		}
		if (args["response"]){
		Response r = new Response(args)
		return r
		}else {
		println 'else'
		return null
		}
	}
	def call(){
		println "I AM CALLABLE"
	}
	def call(oneArg){
		println oneArg
	}
	def mockProcessResponse(){
		
	}
	public static fakeResponse(){
		return 'FAKE'
	}

}
