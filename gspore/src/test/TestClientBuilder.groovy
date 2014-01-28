package test

import org.junit.Test
import groovy.util.GroovyTestCase
import spore.Spore
import errors.SporeError

class TestClientBuilder extends GroovyTestCase {


	@Test
	void testMissingRequiredPropertyInDesc(){
		
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
		
		def errorMessage=""
		
		try{
			Spore spore= new Spore(args)
		}catch(SporeError se){
			errorMessage=se.getMessage()
		}
		
		assertTrue errorMessage==""
	}
	
	@Test
	void testDefaultAttrs(){
		
	}
	
	@Test
	void testDynamicMethodInjection(){
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
		
		Spore spore= new Spore(args)
	
		assertTrue spore.description()==["method1","method2"]
	}
	
}
