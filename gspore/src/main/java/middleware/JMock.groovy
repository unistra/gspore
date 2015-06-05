package middleware
import request.Response

class JMock extends Jizzleware{

	public JMock() {
	}
	
	public JMock(args) {
		args.each{k,v->
			this.metaClass."$k"=v
		}
	this.metaClass."fakes" = args['fakes']
	this.metaClass."middlewares" = args['middlewares']?:[]
	}
	
	def processRequest(environ){
		def args = [:]
		this?."fakes"?.each{path,fakeResponse->
			if (path == ('/'+environ['spore.method_name'])){
				args=fakeResponse
			}
		}
		if (args.size()>0){
		Response r = new Response(args.findAll{k,v-> !['class'].contains(k)})
		return r
		}else {
		return null
		}
	}

	def mockProcessResponse(){
		
	}
	public static fakeResponse(){
		return 'FAKE'
	}
}
