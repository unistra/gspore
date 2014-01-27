package middleware

import request.Response

class MiddlewareBrowser {

	public static Map browseMiddlewares(middlewares,environ,noRequest,ret,storedCallbacks){
		
		/**rather not idiomatic breakable loop that 
		 * calls middlewares. Breaks if a Response
		 * is found. Can modify any of the keys and 
		 * values of the request's base environment
		 * or create new ones, via middleware logic
		 * and store callbacks intended on modifying
		 * the response
		 * */
		middlewares.find{condition,middleware->
			def callback
			/**If the condition was written in Java*/
			if (condition.class == java.lang.reflect.Method){
				def declaringClass = condition.getDeclaringClass()
				Object obj = declaringClass.newInstance([:])
				if (condition.invoke(obj,environ)){
					callback =	middleware.call(environ)
				}
			}
			/**else (i.e if it is a groovy.lang.Closure)*/
			else if (condition(environ)){
				callback=middleware(environ)
			}
			/**break loop
			 */
			if (callback in Response){
				noRequest=true
				ret = callback(environ)
				return true
			}
			/**store to process after request*/
			if (callback!=null){
				storedCallbacks+=callback
			}
			/**pass control to next middleware*/
			return false
		}
		return ["environ":environ,"noRequest":noRequest,"ret":ret,"storedCallbacks":storedCallbacks]
	}
	
}
