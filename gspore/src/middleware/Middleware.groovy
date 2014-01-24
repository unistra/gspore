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
		println "je viens jusqu'ici"+this
		List l =this?.metaClass?.methods*.name
		println l
		def methodName = this?.metaClass?.methods?.find{['processRequest','processResponse'].contains(it.name)}.name
		println "MAIS PEUT-ÊTRE PAS ICI"
		def ret=this?."$methodName"(params)
		
		println "et encore moins là"+ret
		return ret
	}
}
