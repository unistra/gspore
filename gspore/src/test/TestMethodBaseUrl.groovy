package test

import org.junit.Test

import groovy.util.GroovyTestCase
import static feed.SporeFeeder.feed
import groovy.json.JsonSlurper
import groovy.json.JsonException
import spore.Method;
import spore.Spore
import errors.MethodError
import static request.Request.finalUrl
import static utils.MethodUtils.placeHoldersReplacer
import request.Response

class TestMethodBaseUrl extends GroovyTestCase{
	
	@Test
	void testWithGlobalBaseUrlOnly(){
		Spore z = new Spore([
			'name':'name',
			'base_url':'http://my_test.org',
			'methods':[
				"method1" : [
					"path" : "/target/method1",
					"method":"get",
					"formats":"application/json"
				]
			]
		])
		def args= ['name':'name','path':'path','method':'method']
		def expected_response={
			[
				text:'OK',
				status_code:200,
				headers:['Content-Type': 'text-plain']
			]
		}
		z.enable(
			middleware.Mock,
			[
				fakes:[
					'/method1':
					expected_response()
				],
				processRequest:{env->
					println "oui"+env
					def arguments = [:]
					this?."fakes"?.each{path,fakeResponse->
						if (path == ('/'+env['name'])){
							args=fakeResponse
						}
					}
					println args
					if (args.size()>0){
					Response r = new Response(env)
					return r
					}else {
					return null
					}
				}
			]
			)
		println z.middlewares
		println z.description()
		def resp =z.method1([:])
		println resp
		//def results = middlewareBrowser(z.middlewares,environ,storedCallbacks,"")
		
		assertTrue "ouais"=="ouais"
	}
	@Test
	void testWithLocalBaseUrlOnly(){
		
	}
	@Test
	void testWithBothBaseUrl(){
		
	}
	@Test
	void testWithNoBaseUrl(){
		
	}
}
