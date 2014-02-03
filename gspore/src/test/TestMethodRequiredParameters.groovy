package test

import org.junit.Test;
import org.junit.*;
import groovy.util.GroovyTestCase
import spore.Method
import errors.MethodCallError
import static utils.MethodUtils.buildParams;

class TestMethodRequiredParameters extends GroovyTestCase{
	
	/** Tests the value of the built parameters 
	 *in case of required and optional 
	 *parameters defined in description, and 
	 *the exceptions raised when params are
	 *missing or too many args are passed
	 */
	Method method = new Method([
		name:methodName,
		base_url:"http://my_test.org",
		path:"/test",
		method:'GET',
		formats:" application/json",
		
	])
	@Test
	void testNoRequiredParams(){
		assertEquals([], method.required_params)
	}
	@Test
	void testMissingRequiredArgs(){
	method.required_params=['user_id', 'format']
		try {
			method.request(['user_id':879])
		}catch(MethodCallError mce){
			assertNotNull (mce)
		}
	}
	@Test
	void testTooMuchArgs(){
		method.required_params=['user_id', 'format']
		method.optional_params=['page']
		try {
		buildParams(["user_id":2, "format":" application/json", "page":3,
                    "offset":true],method)
		}catch(MethodCallError mce){
			assertNotNull (mce)
		}
		
	}
	@Test
	void testRequiredAndOptionalArgs(){
		method.required_params=['user_id', 'format']
		method.optional_params=['page']
		def built = buildParams(["user_id":2, "format":"application/json", "page":3],method)
		//keySet() returns a Set
		assertTrue (['user_id', 'format','page']==new ArrayList(built.keySet()))
		assertTrue ([2, "application/json",3]==new ArrayList(built.values()))
	}
}
