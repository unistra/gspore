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
import static utils.MethodUtils.placeHoldersReplacer


class TestMiddlewaresCallbacks extends GroovyTestCase{
	@Test
	void testResponseRewrittingMiddleware(){
		
	}
	@Test
	void testTests(){
		Method methoda = new Method([
			name:"method2",
			base_url:"http://my_test.org",
			
			path:"/test/:unelementdurl/:username.:format",
			method:'GET',
			required_params : [
				"format",
				"username",
				"unelementdurl"
			],
			formats:" application/json",

		])
		println placeHoldersReplacer(['format':'json','username':'keven',"unelementdurl":"bla"],methoda.path,methoda).queryString
		println placeHoldersReplacer(['format':'json','username':'keven',"unelementdurl":"bla"],methoda.path,methoda).finalPath
		
		
	}
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
		//println placeHoldersReplacer([:],methoda.base_url,methoda).queryString
		//println placeHoldersReplacer([:],methoda.base_url,methoda).finalPath
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
		assertTrue storedCallbacks.size()==2
		assertTrue storedCallbacks[0] in Closure
		assertTrue storedCallbacks[1] in Closure
		
	}
}
