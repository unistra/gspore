package spore
import java.lang.reflect.Method

class Jizzleware extends Middleware{
	
	Jizzleware(){
	}
	public Jizzleware(Object arg0) {
		super(arg0);
	}
	public Jizzleware(Map arg0) {
		super(arg0);
	}
	/**
	 * @param the name of the method you
	 * want to get.
	 * @return whatever java.lang.reflect.Method is
	 * registered under the specified method name,
	 * else null.
	 */
	public java.lang.reflect.Method methodJizz(String methodName){
		def methodToJizz 
		Method[] methods = this.getClass().getMethods();
		methods.each{
			if (it.getName()==methodName){
				methodToJizz = it
			}
		}
		return methodToJizz
	}
	public static boolean condition(args){
		
	}
}
