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

public class ProficiencyFactory {
	private static Map<String, Competenza> registroMalvagio = new HashMap<>();
    private static Boolean inizializzata = false;
    
    public static void inizializza() {
    	if(inizializzata == true) {
    		return;
    	}
    	
    	caricaCompetenza("competenza.json");
    	inizializzata = true;
    }
    
    private static void caricaCompetenza(String nomeFile) {
    	try(Reader reader = new InputStreamReader(ProficiencyFactory.class.getResourceAsStream("json/" + nomeFile))){
    		Gson gson = new Gson();
    		Type type = new TypeToken<Map<String, List<Competenza>>>(){}.getType();
            Map<String, List<Competenza>> data = gson.fromJson(reader, type);
            
            if (data != null) {
                for (List<Competenza> list : data.values()) {
                    for (Competenza datiCompetenza : list) {
                        registroMalvagio.put(datiCompetenza.getId(), datiCompetenza);
                    }
                }
            }
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static Competenza creaCompetenza(String idCompetenza) {
    	if(inizializzata == false) {
    		inizializza();
    	}
    	
    	Competenza data = registroMalvagio.get(idCompetenza);
    	
    	if(data == null) {
    		throw new IllegalArgumentException("Competenza non trovata: " + idCompetenza);
    	}
    	
    	return new Competenza(data.getNome(), data.getId(), data.getClasse(), data.getDescrizione(), data.getPathImmagine(),
    			data.isAttivabile(), data.getEffetti(), data.getTriggers(), data.getCommento());
    }
}
