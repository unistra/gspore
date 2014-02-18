package middleware

import java.util.HashMap;
import java.util.Map;

class JAuth extends Jizzleware{

	public JAuth(arg0) {
		super(arg0);
	}
	public JAuth(Map arg0) {
		super(arg0);
	}
	public JAuth(){
		
	}
	def processRequest(Map arg0){
		Map<Object,Object> headers = new HashMap<Object ,Object>();
		arg0["spore.headers"]=["Authorization":"Token "+this?.Authorization]
		return null
	}

}
