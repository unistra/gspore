package feed

import groovy.json.JsonSlurper
import spore.Spore
import java.io.Reader

class SporeFeeder {
	
	static slurper = new JsonSlurper()
	
	/**Depending on whether the spore is being created from an url
	 * or from a file feed calls feedFromJson or feedFromUrl to
	 * slurp the Json into a Spore constructor
	 */
	public static Spore feed(spec_uri,base_url=null){
		
		Map api_description = spec_uri.startsWith('http')?feedFromUrl(spec_uri):feedFromJson(spec_uri)
		
		if (!api_description["base_url"]){
			
			api_description["base_url"]=base_url
			
		}
		return new Spore(api_description)
	}
	private static def feedFromJson(specs) throws java.io.FileNotFoundException,groovy.json.JsonException{
		 slurper.parse(new FileReader(specs))
	}
	private static def feedFromUrl (specs) throws java.net.UnknownHostException,groovy.json.JsonException{
		URL url = new URL(specs)
		InputStream urlStream = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream));
		slurper.parse(reader)
	}
}
