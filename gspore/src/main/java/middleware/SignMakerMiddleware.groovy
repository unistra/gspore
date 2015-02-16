package middleware
import org.apache.commons.codec.binary.Base64;
import java.security.MessageDigest

class SignMakerMiddleware extends Middleware{
	def token = ""
	public SignMakerMiddleware(args){
		
		args.each{k,v->
			this.metaClass."$k"=v
		}
	}
	
	def processRequest(environ){
		def sign = ""
		environ["spore.params"].sort().each{k,v ->
			sign+="$k$v"
		}
		def list = digest(this?.token?:""+sign)
		environ['spore.params']['sign']=list
		return null
	}
//	public static String base64SafeUrlEncode(String input) {
//		String result = null;
//		Base64 decoder = new Base64(true);
//		byte[] decodedBytes = decoder.encode(input.bytes);
//		result = new String(decodedBytes);
//		return result;
//	}
	def digest(string){
		MessageDigest cript = MessageDigest.getInstance("SHA-1")
		cript.reset()
		cript.update(string.getBytes("utf8"))
		def list = cript.digest().collect{
			String.format('%1$02x',it)
		}.join('')
		return list
	}
}
