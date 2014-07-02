package holder

import spore.Spore;
import static feed.SporeFeeder.feed;

class Holder {
	
	private static Map Spores=[:]
	public Holder() {
		
	}
	
	public static Spore getClient(name,url,base_url=null){
		Spore spore
		
		if (Spores.containsKey(name)){
			spore = Spores[name]
		}else{
			spore = feed(url,base_url)
			Spores[name]=spore
		}
		return spore
	} 

}
