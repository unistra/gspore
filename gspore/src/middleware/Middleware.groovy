package middleware

class Middleware {

	//Explicit constructor
	def Middleware(args){
		println "YO"+args
		args.each{k,v->
			this.metaClass."$k"=v
		}
		println this.metaClass
		println this.getProperties()
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
