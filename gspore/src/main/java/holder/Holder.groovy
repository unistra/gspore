package holder

import spore.Spore;
import static feed.SporeFeeder.feed;

class Holder {
	
	private static Map Spores=[:]
	public Holder() {
		
	}
	
	public static Spore getClient(name,url){
		Spore spore
		
		if (Spores.containsKey(name)){
			spore = Spores[name]
		}else{
			spore = feed(url)
			Spores[name]=spore
		}
		return spore
	} 

}
