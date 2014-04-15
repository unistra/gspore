package test

import junit.framework.TestSuite

class ThisTestSuite extends TestSuite{
	
	private static final String TEST_ROOT = "src/test/java/test/";
	public static TestSuite suite() throws Exception {
		TestSuite suite = new TestSuite();
		GroovyTestSuite gsuite = new GroovyTestSuite();
		//TODO modifier pour que ça puisse être appelé depuis un jar
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestClientBuilder.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestClientGenerator.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodAuthentication.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodBaseEnviron.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodBaseUrl.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodCall.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodPlaceholderReplacer.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMiddlewareConditionalEnablement.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMiddlewareMock.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestSporeMiddlewares.groovy"));
	
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodRequiredParameters.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodBuilder.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodPayload.groovy"));
	
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMiddlewaresCallbacks.groovy"));
		return suite;
	}

}
