package test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.junit.WireMockClassRule
import com.github.tomakehurst.wiremock.junit.WireMockRule
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import groovy.util.GroovyTestCase;
import spore.Spore
import static Utils.mockHttpServer
import static Utils.mockHttpServerDescriptionFile
import static feed.SporeFeeder.feed
import static holder.Holder.getClient

class TestEffectiveCalls extends GroovyTestCase{

	@ClassRule
	public static WireMockClassRule wireMockRule = new WireMockClassRule(8089);

	@Rule
	public WireMockClassRule instanceRule = wireMockRule;
	//   @Rule
	//   public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8888));

	//	@Before
	//	void setUp(){
	//		mockServer = mockHttpServer()
	//	}
	//	@After
	//	void tearDown() {
	//		wireMockRule.stopServer()
	//	}
	@AfterClass
	void stop() {
		instanceRule.stopServer()
		wireMockRule.stopServer()
	}

	@Test
	void testBasicCall(){

		wireMockRule.start()
		wireMockRule.stubFor(get(urlEqualTo("/thing"))
				.willReturn(aResponse()
				.withStatus(200)
				.withBodyFile("thing.json")
				));
		Spore spore = new Spore([
			'name':'name',
			'base_url':"http://localhost:8089",
			'methods':[
				"method1" : [
					"path" : "/thing",
					"method" : "GET",
				]
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
		assertTrue ((spore.method1([:])).data == ['animals':[
				'cats',
				'wolverine',
				'rat',
				'boar',
				'bear',
				'giraffe',
				'pegasus',
				'unicorn',
				'pony'
			]])
	}
	
	@Test(expected = errors.UnexpectedStatusError)
	void testRightResponseUnexpectedStatus(){
		wireMockRule.start()
		wireMockRule.stubFor(get(urlEqualTo("/thing"))
				.willReturn(aResponse()
				.withStatus(202)
				.withBodyFile("thing.json")
				));
		Spore spore = new Spore([
			'name':'name',
			'base_url':"http://localhost:8089",
			'methods':[
				"method1" : [
					"path" : "/thing",
					"method" : "GET",
					"expected_status" : ["200"]]
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
		spore.method1([:]) 
	}
	@Test
	void testCallWithArgs(){

		instanceRule.start()

		instanceRule.stubFor(get(urlPathEqualTo("/target"))
				.withQueryParam("arg", containing("value"))
				.withQueryParam("barg", containing("value2"))
				.willReturn(aResponse()
				.withStatus(200)
				.withBodyFile("thing2.json")
				));
		Spore spore = new Spore(['name':'name',
			'base_url':'http://localhost:8089',
			'methods':[
				"method1" : [
					"path" : "/target",
					"method" : "GET",
					"formats":"application/json",
					"required_params":["arg", "barg"]]
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
		spore.enable(middleware.AuthToken,
				["authorization":'token']
				)

		assertTrue ((spore.method1(["arg":"value","barg":"value2"])).data == ["some":"stuff1"])
	}
	@Test
	void testPost(){

		instanceRule.start()

		instanceRule.stubFor(post(urlPathEqualTo("/target"))
				.willReturn(aResponse()
				.withStatus(200)
				.withBodyFile("thing2.json")
				));
		Spore spore = new Spore(['name':'name',
			'base_url':'http://localhost:8089',
			'methods':[
				"method1" : [
					"path" : "/target",
					"method" : "POST",
					"formats":"text/html"]
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
		spore.enable(middleware.AuthToken,
				["authorization":'token']
				)
		assertTrue (spore.method1("payload":["bla":"bla"]).data == '{"some":"stuff1"}')
	}
	@Test
	void testFeedFromUrl(){

		instanceRule.start()
		instanceRule.stubFor(get(urlEqualTo("/target/description.json"))
				.willReturn(aResponse()
				.withStatus(200)
				.withBodyFile("right.json")
				));
		Spore spore = getClient("fed","http://localhost:8089/target/description.json")
		assertTrue(spore.methods == [
			'user_profile',
			'new_posts',
			'askhn_posts',
			'vote',
			'retrieve_page',
			'posts_from_user',
			'auth_token',
			'comment',
			'comments_for_post'
		])
	}
	@Test
	void testJizzleware(){
		wireMockRule.start()
		wireMockRule.stubFor(get(urlEqualTo("/thing"))
				.willReturn(aResponse()
				.withStatus(200)
				.withBodyFile("thing.json")
				));
		Spore spore = new Spore([
			'name':'name',
			'base_url':"http://localhost:8089",
			'methods':[
				"method1" : [
					"path" : "/thing",
					"method" : "GET",
				]
			]
		])
		spore.enable(
				middleware.Jizzleware,
				[
					processRequest:{args->
						args['spore.headers'] = ["Authorization":64536546]
						return null
					}
				]
				)
		assertTrue ((spore.method1([:])).data == ['animals':[
				'cats',
				'wolverine',
				'rat',
				'boar',
				'bear',
				'giraffe',
				'pegasus',
				'unicorn',
				'pony'
			]])
	}

}

//	//	@Test
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
