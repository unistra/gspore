package middleware

class Paginator extends middleware.Middleware{
	def methodName
	def results=[]
	def spore
	def isLooping= false
	public Paginator() {
		// TODO Auto-generated constructor stub
	}
	def processResponse(args){
		if (args['spore.method_name'] && args['spore.method_name']!=""){
			this.methodName=args['spore.method_name']
		}
		return !isLooping?{ return loop(it) }:null
	}
	static returnedNextParams(stuff){
		isAHashMap(stuff)  && stuff['next_params']!=null
	}
	static isAHashMap(obj){
		obj.getClass()== java.util.HashMap
	}
	def loop(stuff){
		isLooping = true
		results+=isAHashMap(stuff)?stuff["results"]:stuff
		if (returnedNextParams(stuff)){
			def params = stuff["next_params"]
			params['format']="json"
			loop(spore."$methodName"(params).data)
		}else{
			isLooping=false
			return results
		}
	}
}
