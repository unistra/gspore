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

class TestMethodBaseEnviron extends GroovyTestCase{
	
	@Test
	void testUserInfo(){
		Method method = new Method([
			name:"aMethodName",
			base_url:"http://my_test.org",
			path:"/test",
			method:'GET',
			formats:" application/json",
			
		])
		println method.baseEnviron()
	}
	@Test
	void testScriptName(){
		Method method = new Method([
			name:"aMethodName",
			base_url:"http://my_test.org",
			path:"/test",
			method:'GET',
			formats:" application/json",
			
		])
		println method.baseEnviron()
	}
	@Test
	void testPathAndQuery(){
		Method method = new Method([
			name:"aMethodName",
			base_url:"http://my_test.org",
			path:"/test",
			method:'GET',
			formats:" application/json",
			
		])
		println method.baseEnviron()
	}
	
	@Test
	void testHttps(){
		Method method = new Method([
			name:"aMethodName",
			base_url:"http://my_test.org",
			path:"/test",
			method:'GET',
			formats:" application/json",
			
		])
		println method.baseEnviron()
	}
	@Test
	void testServerPortNotSpecifiedAndProtocolIsHttp(){
		Method method = new Method([
			name:"aMethodName",
			base_url:"http://my_test.org",
			path:"/test",
			method:'GET',
			formats:" application/json",
			
		])
		assertTrue method.baseEnviron()."SERVER_NAME"=="my_test.org"
		assertTrue method.baseEnviron()."SERVER_PORT"==80
		assertTrue method.baseEnviron().'wsgi.url_scheme'=="http"
	}
	@Test
	void testServerPortNotSpecifiedAndProtocolIsHttps(){
		Method method = new Method([
			name:"aMethodName",
			base_url:"https://my_test.org/v2",
			path:"/test",
			method:'POST',
			formats:" application/json",
			
		])
		assertTrue method.baseEnviron()."SCRIPT_NAME"=="/v2"
		assertTrue method.baseEnviron()."PATH_INFO"=="/test"
		assertTrue method.baseEnviron()."SERVER_NAME"=="my_test.org"
		assertTrue method.baseEnviron()."SERVER_PORT"==443
		assertTrue method.baseEnviron().'wsgi.url_scheme'=="https"
	}

}
