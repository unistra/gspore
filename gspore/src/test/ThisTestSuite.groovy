package test

import junit.framework.TestSuite

class ThisTestSuite extends TestSuite{
	
	private static final String TEST_ROOT = "src/test/";
	public static TestSuite suite() throws Exception {
		TestSuite suite = new TestSuite();
		GroovyTestSuite gsuite = new GroovyTestSuite();
		//bon ici tu peux pas faire le compile tout le temps, notamment 
		//quand c'est appelé depuis un jar c'est uncool
		// tu peux tester pour voir avec .class
		//suite.addTestSuite(TestClientBuilder)
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestClientBuilder.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestClientGenerator.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodAuthentication.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodRequiredParameters.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodBuilder.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodPayload.groovy"));
		suite.addTestSuite(gsuite.compile(TEST_ROOT + "TestMethodBaseEnviron.groovy"));
		return suite;
	}

}
