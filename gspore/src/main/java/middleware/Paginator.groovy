package middleware

class Paginator extends middleware.Middleware{
	def methodName
	def results=[]
	def spore
	def isLooping= false
	def next_params=""
	public Paginator() {
		// TODO Auto-generated constructor stub
	}
	def processResponse(args){
		if (args['spore.method_name'] && args['spore.method_name']!=""){
			this.methodName=args['spore.method_name']
			
		}
			return !isLooping?{
				return loop(it)
				}:null
			}
	
	def loop(stuff){
		isLooping = true
		results+=stuff["results"]
		if (stuff['next_params']){
			def params = stuff["next_params"]
			params['format']="json"
			next_params=params
			loop(spore."$methodName"(params).data)
		}else{
		isLooping=false
		return results
		}
	}
}
