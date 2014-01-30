package test

import junit.framework.TestSuite

class ThisTestSuite extends TestSuite{
	
	private static final String TEST_ROOT = "src/test/";
	public static TestSuite suite() throws Exception {
		TestSuite suite = new TestSuite();
		GroovyTestSuite gsuite = new GroovyTestSuite();
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestClientBuilder.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestClientGenerator.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodAuthentication.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodRequiredParameters.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodBuilder.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodBuilder.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodPayload.groovy"));
		//suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodAuthentication.groovy"));
		return suite;
	}

}
