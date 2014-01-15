package request

class Response {
	
	Response (args){
		args.each(){k,v->
			this.metaClass."$k"=v
		}
	}
}
