package middleware

class ContentTypeSetter extends Middleware{
	def contentType
	def ContentTypeSetter(){
		
	}
	def processRequest(environ){
		environ ['spore.format']=this?.contentType
		return null
	}
}
