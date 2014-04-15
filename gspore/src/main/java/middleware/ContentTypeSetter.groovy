package middleware

class ContentTypeSetter extends Middleware{
	def contentType
	def ContentTypeSetter(){
		
	}
	def processRequest(environ){
		environ ['spore.format']="application/json"
		return null
	}
}
