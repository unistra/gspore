package test

import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.rules.*
import groovy.util.GroovyTestCase
import spore.Spore
import errors.MethodError
import errors.SporeError
import groovy.util.GroovyTestCase
import static feed.SporeFeeder.feed
import java.net.UnknownHostException
import java.io.FileNotFoundException
import groovy.json.JsonSlurper
import groovy.json.JsonException
import spore.Spore
import static holder.Holder.getClient


class TestErrors extends GroovyTestCase {
	JsonSlurper slurper = new JsonSlurper()
	def throwingErrorsArgs = [
		'name':'name',
		'base_url':'base_url',
		'methods':[
			"method1" : [
				"path" : "/target/method1",
				"method" : "POST",
				"required_params":['name'],
				"optional_params":['name']],
			"method2" : [
				"path" : "/target/method2",
				"method" : "GET"
			]
		]
	]

	@Test
	void testThrowsErrors(){
		Throwable e = null;

		try {
			Spore spore= new Spore(throwingErrorsArgs)
		} catch (Throwable ex) {
			e = ex;
		}

		assertTrue(e instanceof MethodError);
	}
	@Test
	void testSporeError(){
		Throwable e = null;
		
		try {
			def correctJson = this.getClass().getResource("no_name.json")
			InputStream urlStream = correctJson.openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream));
			File j = new File("no_name.json")
			j.append(reader.getText())
			String jsonString = j.text
			def content=slurper.parseText(j.text)
			Spore spore = feed(j.path)
			j.delete()
		}catch (Throwable ex) {
			e = ex;
		}
		assertTrue(e instanceof SporeError);
	}

}