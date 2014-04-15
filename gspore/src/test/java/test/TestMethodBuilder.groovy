package test

import org.junit.Test
import groovy.util.GroovyTestCase
import static feed.SporeFeeder.feed
import java.net.UnknownHostException
import java.io.FileNotFoundException
import groovy.json.JsonSlurper
import groovy.json.JsonException
import spore.Spore
import errors.MethodError

class TestMethodBuilder extends GroovyTestCase{

	@Test
	void testMissingRequiredPropertyInDesc(){
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
		String output
		[
			'name':["path":"/path","method":"method"],
			'path':["name":"methodName","method":"method"],
			'method':["name":"methodName","path":"/path"]
		].each{requiredProperty,args->

			try{
				z.createMethod(args)
			}catch(MethodError m){
				output= m.getMessage()
			}
			assertTrue output.contains(requiredProperty)
		}
	}
}
