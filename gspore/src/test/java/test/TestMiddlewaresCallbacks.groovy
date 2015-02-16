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
import org.apache.wink.client.MockHttpServer;


class TestMiddlewaresCallbacks extends GroovyTestCase{
	@Test
	void testResponseRewrittingMiddleware(){
	}

	@Test
	void testMiddleware(){
		MockHttpServer mockServer = new MockHttpServer(3333);
		mockServer.startServer();
		String url = "http://localhost:" + (mockServer.getServerPort()+1) + "/target/method1";
		MockHttpServer.MockHttpServerResponse response = new MockHttpServer.MockHttpServerResponse();
		response.setMockResponseContent('{"some":"response"}');
		response.setMockResponseCode(200);
		mockServer.setMockHttpServerResponses(response);
		Spore spore = new Spore([
			'name':'name',
			'base_url':'http://localhost:3334/',
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
		mockServer.stopServer()
	}
	@Test
	void testEncodingMiddleware(){
		MockHttpServer mockServer = new MockHttpServer(3333);
		mockServer.startServer();
		String url = "http://localhost:" + (mockServer.getServerPort()+1) + "/target/method1";
		MockHttpServer.MockHttpServerResponse response = new MockHttpServer.MockHttpServerResponse();
		response.setMockResponseContent('{"some":"response"}');
		response.setMockResponseCode(200);
		mockServer.setMockHttpServerResponses(response);
		Spore spore = new Spore([
			'name':'name',
			'base_url':'http://localhost:3334/',
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
		mockServer.stopServer()
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
