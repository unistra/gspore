package test

import org.junit.Test
import groovy.util.GroovyTestCase
import static feed.SporeFeeder.feed
import java.net.UnknownHostException
import java.io.FileNotFoundException
import groovy.json.JsonSlurper
import groovy.json.JsonException
import spore.Spore

class TestClientGenerator extends GroovyTestCase{
	Spore spore
	JsonSlurper slurper = new JsonSlurper()
	@Test
	void testNoDescriptionFile(){
		def errorMessage=""
		try{
			feed("/path/to/nowhere/nice.json")
		}catch(FileNotFoundException f){
			errorMessage=f.getMessage()
		}
		assertTrue errorMessage!=""
	}
	@Test
	void testNoDescriptionUrl(){
		def errorMessage=""
		try{
			feed("http://description.not.fou.nd/api.json")
		}catch(UnknownHostException u){
			errorMessage=u.getMessage()
		}
		assertTrue errorMessage!=""
	}
	@Test
	void testNoValidJsonDocument(){
		def errorMessage=""
		try{
			def wrongJson = this.getClass().getResource("wrong.json")
			feed(wrongJson.path)
		}catch (JsonException j){
			errorMessage=j.getMessage()
		}
		assertTrue errorMessage!=""
	}
	@Test
	void testWithoutBaseUrl(){
		def jsonMissingBaseUrl = this.getClass().getResource("right.json")
		InputStream urlStream = jsonMissingBaseUrl.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream));
		def content=slurper.parse(reader)
		spore = feed(jsonMissingBaseUrl.path)
		assertEquals(content['base_url'],spore.base_url)
	}
	@Test
	void testWithBaseUrl(){
		def jsonMissingBaseUrl = this.getClass().getResource("nobaseurl")
		spore= feed(jsonMissingBaseUrl.path,'http://my_base.url/')
		assertEquals(spore.base_url, 'http://my_base.url/')
	}
	@Test
	void testGeneratedMethodsMatchDescritpionMethods(){
		def jsonMissingBaseUrl = this.getClass().getResource("right.json")
		InputStream urlStream = jsonMissingBaseUrl.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream));
		def content=slurper.parse(reader)
		spore= feed(jsonMissingBaseUrl.path,'http://my_base.url/')
		content['methods'].each{k,v->
		assertTrue spore.metaClass.methods*.name.contains(k)
		}
	}
}
