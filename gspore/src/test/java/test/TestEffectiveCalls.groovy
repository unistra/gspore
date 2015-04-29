package test

import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import groovy.util.GroovyTestCase;
import spore.Spore
import static Utils.mockHttpServer
import static Utils.mockHttpServerDescriptionFile
import static feed.SporeFeeder.feed

class TestEffectiveCalls extends GroovyTestCase{
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
	void testBasicCall(){
		Spore spore = new Spore([
			'name':'name',
			'base_url':"http://localhost:${mockServer.fixed_port-1}/",
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
		assertTrue (spore.method1(["arg":"arg","barg":"barg"]).data == ['some':'response'])

		
	}
	
//	@Test
//	void testSporeErrorPost(){
//	def mockServer = mockHttpServer()
//		Spore spore = new Spore([
//			'name':'name',
//			'base_url':'http://localhost:3334/',
//			'methods':[
//				"method1" : [
//					"path" : "/target/method1",
//					"method" : "POST",
//					"formats":"application/json",
//					"required_params":["arg","barg"]]
//			]
//		])
//		spore.enable(
//					middleware.Middleware,
//					[
//						processRequest:{args->
//							args['spore.headers'] = ["Authorization":64536546]
//							return null
//						}
//					]
//				)
//		mockServer.stopServer()
//		assertTrue spore.method1(["arg":"arg","barg":"barg"]).data == ['some':'response']
//		
//	}
//	@Test
//	void testFeedSporeErrorPost(){
//		def mockServer =null
//		try{
//
//	   mockServer = mockHttpServerDescriptionFile()
//		Spore spore = feed("http://localhost:4445/api/description.json")
//		println spore
//		spore.enable(
//					middleware.Middleware,
//					[
//						processRequest:{args->
//							args['spore.headers'] = ["Authorization":64536546]
//							return null
//						}
//					]
//				)
//		assertTrue spore.method1(["arg":"arg","barg":"barg"]).data == ['some':'response']
//		}catch(Exception e){
//		
//		}
//		mockServer?.stopServer()
//	}
}