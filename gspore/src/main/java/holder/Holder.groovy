package holder

import spore.Spore;
import static feed.SporeFeeder.feed;

class Holder {
	
	private static Map Spores=[:]
	public Holder() {
		
	}
	
	public static def getClient(name,url){
		Spore spore
		if (Spores.keySet().contains(name)){
			spore = Spores[name]
		}else{
			spore = feed(url)
			Spores[name]=spore
		}
		return spore
	} 

}
