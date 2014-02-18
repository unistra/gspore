package middleware

class JContentTypeSetter extends Jizzleware{

	public JContentTypeSetter() {
		// TODO Auto-generated constructor stub
	}
	def processRequest(environ){
		environ ['spore.format']="application/json"
		return null
	}

}
