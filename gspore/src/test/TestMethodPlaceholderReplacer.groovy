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
import static spore.Method.middlewareBrowser
import static utils.MethodUtils.placeHoldersReplacer

class TestMethodPlaceholderReplacer extends GroovyTestCase{

	@Test
	void test(){
		Method methoda = new Method([
			name:"method2",
			base_url:"http://my_test.org",

			path:"/test/:unelementdurl/:username.:format",
			method:'GET',
			required_params : [
				"format",
				"username",
				"unelementdurl"
			],
			optional_params :["anoptionalparam"],
			formats:" application/json",

		])
		def results =placeHoldersReplacer(['format':'json','username':'keven',"unelementdurl":"bla","anoptionalparam":"optionalstuff"],methoda.path,methoda)
		assertTrue results[0] == ['anoptionalparam':'optionalstuff']
		assertEquals "/test/bla/keven.json",results[1]
	}

}
