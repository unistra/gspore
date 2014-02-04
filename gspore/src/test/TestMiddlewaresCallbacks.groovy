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

class TestMiddlewaresCallbacks extends GroovyTestCase{

	@Test
	void testSimpleClosureReturningMiddleware(){
		def storedCallbacks=[]
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
		Method methoda = new Method([
			name:"method2",
			base_url:"http://my_test.org",
			path:"/test",
			method:'GET',
			formats:" application/json",

		])
		def environ= methoda.baseEnviron()
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
		
			def results = middlewareBrowser(spore.middlewares,environ,storedCallbacks,"")
			storedCallbacks = results.storedCallbacks
		println storedCallbacks
		assertTrue storedCallbacks.size()==2
		assertTrue storedCallbacks[0] in Closure
		assertTrue storedCallbacks[1] in Closure
		
	}
}
