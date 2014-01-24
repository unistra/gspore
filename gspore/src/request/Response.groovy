package request

class Response {
	/**Responses 
	 * should be callable so that 
	 * when the processing of 
	 * an enabled middleware
	 * returns a response,
	 * the request can be shortcut
	 * and a content still be returned 
	 * just by doing something like
	 * result = response(environ)
	 * and not verbose stuff like
	 * result = response(environ).content
	 * etc..
	 * @param args
	 */
	Response (args){
		args.each(){k,v->
			this.metaClass."$k"=v
		}
	}
	def call(){
		
	}
	def call(args){
		println args
		this.properties.each{k,v->
			println k
			println v
		}
	}
}

