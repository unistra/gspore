package test

import org.junit.Test
import groovy.util.GroovyTestCase
import static feed.SporeFeeder.feed
import java.net.UnknownHostException
import java.io.FileNotFoundException
import groovy.json.JsonSlurper
import groovy.json.JsonException
import spore.Spore
import static utils.MethodUtils.buildPayload;
import spore.Method
import errors.MethodCallError

class TestMethodPayload extends GroovyTestCase {
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
	Method m =z.createMethod(["name":"requiringPayloadMethod","path":"/path","method":"method","required_payload":true])[0]
	@Test 
	void testPayloadError(){
		def message = ""
		try{
			buildPayload([],m)
		}catch(MethodCallError mc){
		message=mc.getMessage()
		}
		assertTrue (message!="")
	}
	@Test
	void testPayload(){
		def payload = buildPayload(["payload":['test':"data"]],m)
		assertTrue (payload==['test':"data"])
	}

}
