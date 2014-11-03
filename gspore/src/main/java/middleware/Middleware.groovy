package middleware

class Middleware {

	//Explicit constructor
	def Middleware(args){
		args.each{k,v->
			this.metaClass."$k"=v
		}
	}

	def Middleware(){}

	def call={params->
		this?.metaClass?.methods?.find{
			['processRequest','processResponse'].contains(it.name)
			}.invoke(this,params)
	}
}
//def methodName = this?.metaClass?.methods?.find{['processRequest','processResponse'].contains(it.name)}.name
//this?."$methodName"(params)