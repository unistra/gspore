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
import static spore.Method.middlewareBrowser

class TestMiddlewareConditionalEnablement extends GroovyTestCase{
	Spore spore = new Spore([
		'name':'name',
		'base_url':'http://localhost:8080/',
		'methods':[
			"method1" : [
				"path" : "/target/method1",
				"method" : "POST",
				"formats":"application/json"
			]
		]
	])
	@Test
	void testEnableIfConditionAssertToTrue(){
		def storedCallbacks=[]
		
		Method methoda = new Method([
			name:"method2",
			base_url:"http://my_test.org",
			path:"/test",
			method:'GET',
			formats:" application/json",

		])
		def environ= methoda.baseEnviron()
		spore.enableIf(
			middleware.Middleware,
			[
				processRequest:{args->
					args['spore.headers'] = ["Authorization":64536546]
					return { "blabla" }
				}
			]
			){args->
			args['REQUEST_METHOD']=="GET"
		}
			def results = middlewareBrowser(spore.middlewares,environ)
			assertTrue results[2]['spore.headers']==["Authorization":64536546]
	}
	@Test
	void testEnableIfConditionAssertToFalse(){
		def storedCallbacks=[]
		
		Method methoda = new Method([
			name:"method2",
			base_url:"http://my_test.org",
			path:"/test",
			method:'POST',
			formats:" application/json",

		])
		def environ= methoda.baseEnviron()
		spore.enableIf(
			middleware.Middleware,
			[
				processRequest:{args->
					args['spore.headers'] = ["Authorization":64536546]
					return { "blabla" }
				}
			]
			){args->
			args['REQUEST_METHOD']=="GET"
		}
			
			def results = middlewareBrowser(spore.middlewares,environ)
			assertTrue !results[2]['spore.headers']
	}

}
