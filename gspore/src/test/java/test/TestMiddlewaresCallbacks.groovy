package test

import org.junit.After;
import org.junit.Before;
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

import org.apache.wink.client.MockHttpServer;

import static Utils.mockHttpServer


class TestMiddlewaresCallbacks extends GroovyTestCase{
	@Test
	void testResponseRewrittingMiddleware(){
	}
	def mockServer
	@Before
	void setUp(){
		mockServer = mockHttpServer()
	}
	@After
	void tearDown() {
		mockServer.stopServer()
	}
	@Test
	void testMiddleware(){
		Spore spore = new Spore([
			'name':'name',
			'base_url':"http://localhost:${mockServer.fixed_port-1}/",
			'methods':[
				"method1" : [
					"path" : "/target/method1",
					"method" : "GET",
					"formats":"application/json",
					"required_params":["arg"]]
			]
		])
		spore.enable(
				middleware.Middleware,
				[
					processRequest:{args->
						args['spore.headers'] = ["Authorization":64536546]
						return null
					}
				]
				)
		spore.enable(
				middleware.JContentTypeSetter,
				[:]
				)
		spore.enable(
				middleware.JAuth,
				["Authorization":"YES"]
				)
		spore.enable(
				middleware.JAuth,
				["Authorization":"YES"]
				)
		spore.description();
		spore.method1(["arg":"arg"]);
	}
	@Test
	void testEncodingMiddleware(){
		Spore spore = new Spore([
			'name':'name',
			'base_url':"http://localhost:${mockServer.getServerPort()}/",
			'methods':[
				"method1" : [
					"path" : "/target/method1",
					"method" : "GET",
					"formats":"application/json",
					"required_params":["arg","barg"]]
			]
		])
		spore.enable(
				middleware.Middleware,
				[
					processRequest:{args->
						args['spore.headers'] = ["Authorization":64536546]
						return null
					}
				]
				)
		spore.enable(
				middleware.SignMakerMiddleware,
				[:
				]
				)
		assertTrue middlewareBrowser(spore.middlewares,["spore.params":["arg":"arg","barg":"barg"]])[2]['spore.params']['sign']=="8b89c166113948bdcec3dcfbf45fa944da31cab6"
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
			formats:"application/json",

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
		spore.enable(middleware.ContentTypeSetter,[
			contentType:"application/json"
		]
		)

		def results = middlewareBrowser(spore.middlewares,environ)
		storedCallbacks = results[3]
		assertTrue storedCallbacks.size()==2
		assertTrue storedCallbacks[0] in Closure
		assertTrue storedCallbacks[1] in Closure
	}
}
