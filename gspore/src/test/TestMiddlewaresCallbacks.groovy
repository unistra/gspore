package test

import org.junit.Test

import groovy.util.GroovyTestCase
import static feed.SporeFeeder.feed

import java.net.UnknownHostException
import java.io.FileNotFoundException

import groovy.json.JsonSlurper
import groovy.json.JsonException
import spore.Method;
import spore.Spore
import errors.MethodError

class TestMiddlewaresCallbacks extends GroovyTestCase{

	@Test
	void testSimpleClosureReturningMiddleware(){
		Spore spore = new Spore([
		'name':'name',
		'base_url':'http://localhost:8080/',
		'methods':[
			"method1" : [
				"path" : "/target/method1",
				"method" : "POST",
				"formats":"application/json"
			],
			"method2" : [
				"path" : "/target/method2",
				"method" : "GET",
				"formats":"application/json"
			]
		]
	])
		spore.enable(
			middleware.Middleware,
			[
				processRequest:{args->
					args['spore.headers'] = ["Authorization":64536546]
					return { "blabla" }
				}
			]
			)
		spore.enable(
			middleware.Middleware,
			[
				processRequest:{args->
					args['spore.headers'] = ["Authorization":64536546]
					return { "blabla" }
				}
				
			]
			)
		spore.middlewares.find{condition,middleware->
			def callback
			/**If the condition was written in Java*/
			if (condition.class == java.lang.reflect.Method){
				def declaringClass = condition.getDeclaringClass()
				Object obj = declaringClass.newInstance([:])
				if (condition.invoke(obj,environ)){
					callback =        middleware.call(environ)
				}
			}
			/**else (i.e if it is a groovy.lang.Closure)*/
			else if (condition(environ)){
				callback = middleware(environ)
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
	}

}
