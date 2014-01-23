package middleware

class Middleware {

	//Explicit constructor
	def Middleware(args){
		args.each{k,v->
			this.metaClass."$k"=v
		}
	}

	def Middleware(){
	}
	def call={params->
		def methodName = this?.metaClass?.methods.find{['processRequest','processResponse'].contains(it.name)}.name
		def ret=this?."$methodName"(params)
		return ret
	}
}
