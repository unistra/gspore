package spore

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
		params.each {propName,propVal->
			//this.metaClass."$propName"=propVal
		}
		//ici, si tu veux fonctionner comme ça, il te faudra un bazar générique, parce que
		//de prime abord y'a pas de metaClass à tes java.lang.Object
		//par ailleurs c'est pas mutuellement exclusif les deux traitements là
		//donc ça serait sympa de pas faire comme ça.
		if (this?.metaClass?.methods*.name?.contains('processRequest')){
			println "non serieux c'est depuis ici que ça se passe???????"
			def ret =this?.processRequest(params)
			return ret
		}else if (this?.metaClass?.methods*.name?.contains('processResponse')){
			 this?.processResponse(params)
		}else return null
	}
}
