package test

import org.apache.wink.client.MockHttpServer;
class Utils{
	public static mockHttpServer(){
		MockHttpServer mockServer = new MockHttpServer(3333);
		mockServer.startServer();
		String url = "http://localhost:" + (mockServer.getServerPort()+1) + "/target/method1";
		MockHttpServer.MockHttpServerResponse response = new MockHttpServer.MockHttpServerResponse();
		response.setMockResponseContent('{"some":"response"}');
		response.setMockResponseCode(200);
		mockServer.setMockHttpServerResponses(response);
		return mockServer
	}
}