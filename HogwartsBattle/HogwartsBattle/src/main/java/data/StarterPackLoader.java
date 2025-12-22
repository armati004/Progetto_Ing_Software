package data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import carte.Carta;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StarterPackLoader {
	private static Map<String, List<String>> starterDeckEroi = new HashMap<>();

	public static void inizializza() {
		try (Reader reader = new InputStreamReader(
				StarterPackLoader.class.getClassLoader().getResourceAsStream("json/starter_pack.json"))) {
			Gson gson = new Gson();
			
			Type type = new TypeToken<Map<String, List<Carta>>>(){}.getType();
			Map<String, List<Carta>> data = gson.fromJson(reader, type);
			
			if(data != null) {
				for(Map.Entry<String, List<Carta>> entry : data.entrySet()) {
					String nomeEroe = entry.getKey();
					List<String> ids = new ArrayList<>();
					
					for(Carta c : entry.getValue()) {
						ids.add(c.getId());
					}
					
					starterDeckEroi.put(nomeEroe.toLowerCase(), ids);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getDeckPerEroe(String nomeEroe){
		return starterDeckEroi.getOrDefault(nomeEroe.toLowerCase(), new ArrayList<>());
	}
}
