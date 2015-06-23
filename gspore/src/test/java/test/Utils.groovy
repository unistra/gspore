package test

import groovy.json.JsonSlurper;

import org.apache.wink.client.MockHttpServer;

import spore.Spore;
class Utils{
	static JsonSlurper slurper = new JsonSlurper()
	public static mockHttpServer(){
		MockHttpServer mockServer = new MockHttpServer(3333);
		mockServer.startServer();
		String url = "http://localhost:" + (mockServer.getServerPort()+1) + "/target/method1";
		MockHttpServer.MockHttpServerResponse response = new MockHttpServer.MockHttpServerResponse();
		response.setMockResponseContent('{"some":"response"}');
		response.setMockResponseCode(200);
		mockServer.setMockHttpServerResponses(response);
		mockServer.metaClass["base_url"]="http://localhost:" + (mockServer.getServerPort()+1)
		mockServer.metaClass["fixed_port"]=mockServer.getServerPort()+1
		
		return mockServer
	}
	public static mockHttpServerDescriptionFile(){
		MockHttpServer mockServer = new MockHttpServer(3333);
		mockServer.startServer();
		String url = "http://localhost:" + (mockServer.getServerPort()+1) + "/api/description.json";
		MockHttpServer.MockHttpServerResponse response = new MockHttpServer.MockHttpServerResponse();
		def correctJson = this.getClass().getResource("right.json")
		InputStream urlStream = correctJson.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream));
		File j = new File("newright.json")
		j.append(reader.getText())
		String jsonString = j.text
		def content=slurper.parseText(j.text)
		j.delete()
		response.setMockResponseContent(content);
		response.setMockResponseCode(200);
		mockServer.setMockHttpServerResponses(response);
		return mockServer
	}
}