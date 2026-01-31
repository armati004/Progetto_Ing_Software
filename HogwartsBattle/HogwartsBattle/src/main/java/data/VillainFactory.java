package data;

import carte.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillainFactory {
	private static Map<String, Malvagio> registroMalvagio = new HashMap<>();
    private static Boolean inizializzata = false;
    
    public static void inizializza() {
    	if(inizializzata == true) {
    		return;
    	}
    	
    	caricaMalvagi("malvagio.json");
    	inizializzata = true;
    }
    
    private static void caricaMalvagi(String nomeFile) {
    	try (Reader reader = new InputStreamReader(
                VillainFactory.class.getClassLoader().getResourceAsStream("json/" + nomeFile))){
    		Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<Malvagio>>>(){}.getType();
            Map<String, List<Malvagio>> data = gson.fromJson(reader, type);
            
            if (data != null) {
                for (List<Malvagio> list : data.values()) {
                    for (Malvagio datiMalvagio : list) {
                        registroMalvagio.put(datiMalvagio.getId(), datiMalvagio);
                    }
                }
            }
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static Malvagio creaMalvagio(String idMalvagio) {
    	if(inizializzata == false) {
    		inizializza();
    	}
    	
    	Malvagio data = registroMalvagio.get(idMalvagio);
    	
    	if(data == null) {
    		throw new IllegalArgumentException("Malvagio non trovato: " + idMalvagio);
    	}
    	
    	return new Malvagio(data.getNome(), data.getId(), data.getClasse(), data.getDescrizione(), data.getCosto(),
    			data.getPathImmagine(), data.getEffetti(), data.getTriggers(), data.getReward(), data.getVita());
    }
}
