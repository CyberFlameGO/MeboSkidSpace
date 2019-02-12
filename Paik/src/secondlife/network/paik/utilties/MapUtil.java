package secondlife.network.paik.utilties;

import java.util.HashMap;
import java.util.Map;

public class MapUtil<K,V> {
	
	private Map<K,V> map = new HashMap<K,V>();

	public static <K, V> MapUtil<K, V> of() {
		return new MapUtil<K, V>();
	}
	
	
	public  MapUtil<K,V> put(K k,V v){
		map.put(k, v);
		return this;
	}
	
	public Map<K,V> map(){
		return map;
	}
	
	public HashMap<K,V> hashMap(){
		return (HashMap<K, V>) map;
	}
	
	
}