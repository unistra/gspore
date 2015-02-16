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

class TestRequest extends GroovyTestCase{

	public TestRequest() {
		// TODO Auto-generated constructor stub
	}
	@Test
	void testRequestFailure(){
		MockHttpServer mockServer = new MockHttpServer(3333);
		mockServer.startServer();
		
		String url = "http://localhost:" + (mockServer.getServerPort()+1) + "/target/method1";
		MockHttpServer.MockHttpServerResponse response = new MockHttpServer.MockHttpServerResponse();
		response.setMockResponseContent('{"nota":"response"}');
		response.setMockResponseCode(400);
		mockServer.setMockHttpServerResponses(response);
		Spore spore = new Spore([
			'name':'name',
			'base_url':'http://localhost:3334/',
			'methods':[
				"method1" : [
					"path" : "/target/method1",
					"method" : "GET",
					"formats":"application/json",
					"required_params":["arg"]
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
	void testRequestPost(){
		MockHttpServer mockServer = new MockHttpServer(3333);
		mockServer.startServer();
		
		String url = "http://localhost:" + (mockServer.getServerPort()+1) + "/target/method1";
		MockHttpServer.MockHttpServerResponse response = new MockHttpServer.MockHttpServerResponse();
		response.setMockResponseContent('{"nota":"response"}');
		response.setMockResponseCode(400);
		mockServer.setMockHttpServerResponses(response);
		Spore spore = new Spore([
			'name':'name',
			'base_url':'http://localhost:3334/',
			'methods':[
				"method1" : [
					"path" : "/target/method1",
					"method" : "POST",
					"formats":"application/json",
					"required_params":["arg"]
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
		spore.method1(["arg":"arg", "payload":["oui":"non","non":"oui"]]);
		mockServer.stopServer()

	}

}
