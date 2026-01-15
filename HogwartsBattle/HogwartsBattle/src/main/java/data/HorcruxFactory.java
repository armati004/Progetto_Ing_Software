package data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import carte.Horcrux;

public class HorcruxFactory {
	private static Map<String, Horcrux> registroHorcrux = new HashMap<>();
	private static Boolean inizializzata = false;

	public static void inizializza() {
		if (inizializzata == true) {
			return;
		}

		inizializzata = true;
	}

	private static void caricaHorcrux(String nomeFile) {
		try (Reader reader = new InputStreamReader(
				HorcruxFactory.class.getClassLoader().getResourceAsStream("json/" + nomeFile))) {
			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, List<Horcrux>>>() {
			}.getType();
			Map<String, List<Horcrux>> data = gson.fromJson(reader, type);

			if (data != null) {
				for (List<Horcrux> list : data.values()) {
					for (Horcrux datiHorcrux : list) {
						registroHorcrux.put(datiHorcrux.getId(), datiHorcrux);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Horcrux creaHorcrux(String idHorcrux) {
		if (inizializzata == false) {
			inizializza();
		}

		Horcrux data = registroHorcrux.get(idHorcrux);

		if (data == null) {
			throw new IllegalArgumentException("Horcrux non trovato: " + idHorcrux);
		}

		return new Horcrux(data.getNome(), data.getId(), data.getClasse(), data.getDescrizione(), data.getCosto(),
				data.getPathImmagine(), data.getEffetti(), data.getTriggers(), data.getSegnaliniRichiesti(),
				data.getRicompensa());
	}
}
