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
import static request.Request.finalUrl
import static utils.MethodUtils.placeHoldersReplacer

class TestMethodCall extends GroovyTestCase{
	
	Method method = new Method([
		name:methodName,
		base_url:"http://localhost:8080/base",
		path:"/test/:id",
		method:'GET',
		formats:"application/json",
		optional_params:['id']
	])
	@Test
	void testRequestSender(){
		println method.request([id:"unid"])
	}
	@Test
	void testfinalUrlWithOnePlaceHolder(){
		assertEquals "http://localhost:8080/base/test/unid",finalUrl(method.baseEnviron())+placeHoldersReplacer([id:"unid"],method.path,method)[1]
	}
}
