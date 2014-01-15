package request

import java.util.HashMap;
import java.util.Map;

class JResponse {

	public JResponse() {
	
	}
	public static boolean condition(Object arg0){
		return true;
	}
	public static void processResponse(Map arg0){
		Map<Object,Object> map2 = new HashMap<Object ,Object>();
		map2.put("tralala", 1);
		map2.put("trololo", 2);
		arg0.put("spore.headers",map2);
	}
}
