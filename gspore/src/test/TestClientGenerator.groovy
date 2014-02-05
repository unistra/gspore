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
			InputStream urlStream = wrongJson.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream));
			File j = new File("newwrong.json")
			j.append(reader.getText())
			String jsonString = j.text
			def content=slurper.parseText(j.text)
			feed(j.path)
			j.delete()
		}catch (JsonException j){
			errorMessage=j.getMessage()
		}
		
		assertTrue errorMessage!=""
	}
	@Test
	void testWithoutBaseUrl(){
		def correctJson = this.getClass().getResource("right.json")
		InputStream urlStream = correctJson.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream));
		File j = new File("newright.json")
		j.append(reader.getText())
		String jsonString = j.text
		def content=slurper.parseText(j.text)
		spore = feed(j.path)
		j.delete()
		assertEquals(content['base_url'],spore.base_url)
		
		
		
	}
	@Test
	void testWithBaseUrl(){
		def jsonMissingBaseUrl = this.getClass().getResource("nobaseurl")
		InputStream urlStream = jsonMissingBaseUrl.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream));
		File j = new File("nobaseurl.json")
		j.append(reader.getText())
		String jsonString = j.text
		def content=slurper.parseText(j.text)
		spore= feed(j.path,'http://my_base.url/')
		j.delete()
		assertEquals(spore.base_url, 'http://my_base.url/')
	}
	@Test
	void testGeneratedMethodsMatchDescritpionMethods(){
	def correctJson = this.getClass().getResource("right.json")
		InputStream urlStream = correctJson.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream));
		File j = new File("newright.json")
		j.append(reader.getText())
		String jsonString = j.text
		def content=slurper.parseText(j.text)
		spore= feed(j.path,'http://my_base.url/')
		j.delete()
		content['methods'].each{k,v->
			assertTrue spore.metaClass.methods*.name.contains(k)
		}
	}
}
