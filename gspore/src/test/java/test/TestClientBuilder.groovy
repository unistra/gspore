package test

import org.junit.Test
import groovy.util.GroovyTestCase
import spore.Spore
import errors.MethodError
import errors.SporeError

class TestClientBuilder extends GroovyTestCase {
	def args = [
		'name':'name',
		'base_url':'base_url',
		'methods':[
			"method1" : [
				"path" : "/target/method1",
				"method" : "POST"
			],
			"method2" : [
				"path" : "/target/method2",
				"method" : "GET"
			]
		]
	]
	def args2 = [
		'name':'name',
		'base_url':'base_url',
		'methods':[
			"method1" : [
				"path" : "/target/method1",
				"method" : "POST",
				"required_params":['name'],
				"optional_params":['name']
			],
			"method2" : [
				"path" : "/target/method2",
				"method" : "GET"
			]
		]
	]

	@Test
	void testMissingRequiredPropertyInDesc(){
		
		
		
		def errorMessage=""
		
		try{
			Spore spore= new Spore(args)
		}catch(SporeError se){
			errorMessage=se.getMessage()
		}
		
		assertTrue errorMessage==""
	}
	@Test
	void testRequiredAndOptionalParamsIntersect(){
		def errorMessage=""
		try{
			Spore spore= new Spore(args2)
		}catch(SporeError se){
			errorMessage=se.getMessage()
		}catch(MethodError me){
		errorMessage=errorMessage!=""?errorMessage+"/"+me.getMessage():me.getMessage()
		}
		assertEquals "errors.MethodError: params cannot be optional and mandatory at the same time",errorMessage
	}
	@Test
	void testDefaultAttrs(){
		
	}
	
	@Test
	void testDynamicMethodInjection(){
	
		
		Spore spore= new Spore(args)
	
		assertTrue spore.description()==["method1","method2"]
	}
	
}
