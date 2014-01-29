package test

import org.junit.Test;
import org.junit.*;
import groovy.util.GroovyTestCase
import spore.Method
import errors.MethodCallError

class TestMethodRequiredParameters extends GroovyTestCase{
	/** Tests the value of the built parameters in 
	 *case of required and
	 *optional parameters defined in description, and 
	 *the exceptions raised when
	 *params are missing or too much args are passed
	 */

	@Test
	void testNoRequiredParams(){
	}
	@Test
	void testMissingRequiredArgs(){
	Method method = new Method([
			name:methodName,
			base_url:"http://my_test.org",
			path:"/test",
			method:'GET',
			formats:" application/json",
			required_params:['user_id', 'format']
		])
		try {
			method.request(['user_id':879])
		}catch(MethodCallError mce){
			assertNotNull (mce)
		}
	}
	@Test
	void testTooMuchArgs(){
		/**déjà, ça c'est pas fait
		 * pour l'instant tu es tolérant
		 * avec les exceeding args 
		 * qui sont just rolled back
		 */
	}
	@Test
	void testRequiredAndOptionalArgs(){
	}
}
