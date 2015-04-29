package middleware

class AuthToken extends middleware.Middleware {
	def authorization

	public AuthToken(){}

	def processRequest(args){
		args['spore.headers'] = ["Authorization":"Token ${this?.authorization}"]
		return null
	}
}
