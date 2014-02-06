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
		def expected_response={
			[
				text:'???',
				status_code:200,
				headers:['Content-Type': 'text-plain']
			]
		}
		z.enable(
			middleware.Middleware,
			[
				fakes:[
					'/method1':
					expected_response()
				],
				processRequest:{env->
					def arguments = [:]
					delegate?."fakes"?.each{path,fakeResponse->
						if (path == ('/'+env['name'])){
							arguments=fakeResponse
						}
					}
					if (arguments.size()>0){
					Response r = new Response(env)
					return r
					}else {
					return null
					}
				}
			]
			)
		def resp =z.method1([:])
		
		assertTrue resp['base_url']==z.base_url
	}
	@Test
	void testWithLocalBaseUrlOnly(){
		
		Method method = new Method([
			name:"aMethodName",
			base_url:"http://my_test.org",
			path:"/test",
			method:'GET',
			formats:" application/json",
			
		])
		
		assertTrue method.base_url=='http://my_test.org'
	}
	@Test
	void testWithBothBaseUrl(){
		Spore z = new Spore([
			'name':'name',
			'base_url':'http://my_test.org',
			'methods':[
				"method1" : [
					"path" : "/target/method1",
					"method":"get",
					"formats":"application/json",
					'base_url':'http://another_url.org',
				]
			]
		])
		def expected_response={
			[
				text:'???',
				status_code:200,
				headers:['Content-Type': 'text-plain']
			]
		}
		z.enable(
			middleware.Middleware,
			[
				fakes:[
					'/method1':
					expected_response()
				],
				processRequest:{env->
					def arguments = [:]
					delegate?."fakes"?.each{path,fakeResponse->
						if (path == ('/'+env['name'])){
							arguments=fakeResponse
						}
					}
					if (arguments.size()>0){
					Response r = new Response(env)
					return r
					}else {
					return null
					}
				}
			]
			)
		def resp =z.method1([:])
		
		assertTrue resp['base_url']=='http://another_url.org'
	}
	@Test
	void testWithNoBaseUrl(){
		def errorMessage =  ""
		Spore z = new Spore([
			'name':'name',
			'base_url':'base_url',
			'methods':[
				"method1" : [
					"path" : "/target/method1",
					"method":"get"
				]
			]
		])
		
		z.base_url=null
		
		try {
		z.createMethod([name:"aMethodName",
			path:"/test",
			method:'GET',
			formats:" application/json"])
		}catch (MethodError me){
		errorMessage = me.getMessage()
		}
		assertEquals errorMessage,"Either a base_url or an api_base_url should be specified"
	}
}
