package middleware

class Middleware {

	//Explicit constructor
	def Middleware(args){
		args.each{k,v->
			println k
			this.metaClass."$k"=v
		}
	}

	def Middleware(){
	}
	def call={params->
		List l =this?.metaClass?.methods*.name

		def methodName = this?.metaClass?.methods?.find{['processRequest','processResponse'].contains(it.name)}.name
		
		def ret=this?."$methodName"(params)
		
		return ret
	}
}
