package gioco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carte.Carta;
import carte.Competenza;
import carte.Eroe;
import carte.Mazzo;
import carte.Pozione;
import carte.DarkArtsPotion;
import gestoreEffetti.Trigger;

/**
 * Giocatore - Rappresenta un giocatore nel gioco
 * 
 * AGGIORNATO per espansione Charms & Potions:
 * - Sistema ingredienti per brewre pozioni
 * - Dark Arts Potions attive sul giocatore
 */
public class Giocatore {
    private Eroe eroe;
    private int salute;
    private final int saluteMax = 10;
    private Mazzo mazzo;
    private Mazzo scarti;
    private List<Carta> mano;
    private int gettone;
    private int attacco;
    private Competenza competenza;
    
    private SelettoreCarta selettoreCarta;
    private List<Carta> carteAcquistateQuestoTurno = new ArrayList<>();
    
    // NUOVO: Sistema Ingredienti (Pack 2+)
    private Map<String, Integer> ingredienti;
    
    // NUOVO: Dark Arts Potions attive (Pack 3+)
    private List<DarkArtsPotion> darkArtsPotionsAttive;
    
    // NUOVO: Pozioni brewate questo turno (per Encounter)
    private int pozioniBrewateQuestoTurno;
    
    public Giocatore(Eroe eroe) {
    	this.eroe = eroe;
    	this.salute = saluteMax;
    	this.mazzo = new Mazzo();
    	this.mazzo.inizializzaMazzo(eroe.getNome());
    	this.scarti = new Mazzo();
    	this.mano = new ArrayList<>();
    	inizializzaMano();
    	this.gettone = 0;
    	this.attacco = 0;
    	this.competenza = null;
    	
    	// Inizializza sistema Pozioni
    	this.ingredienti = new HashMap<>();
    	this.darkArtsPotionsAttive = new ArrayList<>();
    	this.pozioniBrewateQuestoTurno = 0;
    }
    
    public void scartaCarta(Carta carta) {
		this.getScarti().getCarte().add(carta);
		this.getMano().remove(carta);
	}
    
    public void inizializzaMano() {
    	for(int i = 0; i < 5; i++) {
    		Carta cartaPescata = mazzo.pescaCarta();
    		if(cartaPescata != null) {
    			mano.add(cartaPescata);
    		}
    	}
    }
	
    public void giocaCarta(StatoDiGioco stato, Carta carta) {
        if (!mano.contains(carta)) {
            System.out.println("ERRORE: La carta " + carta.getNome() + " non è nella mano!");
            return;
        }

        // Rimuovi trigger "in mano" PRIMA di giocare
        rimuoviTriggersCartaDaMano(stato, carta);

        carta.applicaEffetto(stato, this);

        if (carta.getClasse().equals("Alleato")) {
            stato.getAlleatiGiocatiInQuestoTurno().add((carte.Alleato) carta);
        }
        
        mano.remove(carta);
        scarti.aggiungiCarta(carta);

        System.out.println("Giocata: " + carta.getNome());
    }
	
	public List<Carta> cercaNelMazzo(String tipo) {	
	    List<Carta> carteCorrispondenti = new ArrayList<>();
	    
	    for (int i = 0; i < mazzo.getCarte().size(); i++) { 
	        Carta carta = mazzo.getCarte().get(i);
	        if (carta.getClasse().equalsIgnoreCase(tipo)) {
	            carteCorrispondenti.add(carta);
		        System.out.println("carta: " + carta.getNome() + ", descrizione:" + carta.getDescrizione());
	        }
	    }
		return carteCorrispondenti;
	}

	public List<Carta> cercaNelleDiscard(String tipo) {	
	    List<Carta> carteCorrispondenti = new ArrayList<>();
	    
	    for (int i = 0; i < scarti.getCarte().size(); i++) { 
	        Carta carta = scarti.getCarte().get(i);
	        if (carta.getClasse().equalsIgnoreCase(tipo)) {
	            carteCorrispondenti.add(carta);
		        System.out.println("carta discard: " + carta.getNome() + ", descrizione:" + carta.getDescrizione());
	        }
	    }
		return carteCorrispondenti;
	}
	
	public Carta pescaCarta() {
		Carta carta = mazzo.pescaCarta();
		if (carta != null) {
		    mano.add(carta);
		}
		return carta;
	}
	
	public void rimuoviTriggersCartaDaMano(StatoDiGioco stato, Carta carta) {
	    List<Trigger> triggersRimossi = new ArrayList<>();
	    for (Trigger t : stato.getGestoreTrigger().getTriggers()) {
	        if (t.getCartaOrigine() != null && t.getCartaOrigine() == carta) {
	            triggersRimossi.add(t);
	        }
	    }
	    stato.getGestoreTrigger().getTriggers().removeAll(triggersRimossi);
	}
	
	public void aggiornaTriggersInMano(StatoDiGioco stato) {
	    for (Carta carta : mano) {
	        if (carta.getTriggersInMano() != null) {
	            for (Trigger t : carta.getTriggersInMano()) {
	                if (!stato.getGestoreTrigger().getTriggers().contains(t)) {
	                    t.setCartaOrigine(carta);
	                    stato.getGestoreTrigger().getTriggers().add(t);
	                }
	            }
	        }
	    }
	}

    // ============================================================================
    // NUOVI METODI: SISTEMA INGREDIENTI (Pack 2+)
    // ============================================================================
    
    /**
     * Aggiungi un ingrediente al pool del giocatore.
     */
    public void aggiungiIngrediente(String tipo, int quantita) {
        ingredienti.put(tipo, ingredienti.getOrDefault(tipo, 0) + quantita);
        System.out.println("🧪 " + eroe.getNome() + " guadagna " + quantita + "x " + tipo);
    }
    
    /**
     * Rimuovi un ingrediente dal pool del giocatore.
     */
    public boolean rimuoviIngrediente(String tipo, int quantita) {
        int disponibile = ingredienti.getOrDefault(tipo, 0);
        if (disponibile >= quantita) {
            ingredienti.put(tipo, disponibile - quantita);
            return true;
        }
        return false;
    }
    
    /**
     * Verifica se il giocatore ha abbastanza ingredienti.
     */
    public boolean haIngredienti(Map<String, Integer> richiesti) {
        for (Map.Entry<String, Integer> entry : richiesti.entrySet()) {
            String tipo = entry.getKey();
            int richiesto = entry.getValue();
            int disponibile = ingredienti.getOrDefault(tipo, 0);
            
            if (disponibile < richiesto) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Consuma gli ingredienti per brewre una pozione.
     */
    public boolean consumaIngredienti(Map<String, Integer> richiesti) {
        if (!haIngredienti(richiesti)) {
            return false;
        }
        
        for (Map.Entry<String, Integer> entry : richiesti.entrySet()) {
            rimuoviIngrediente(entry.getKey(), entry.getValue());
        }
        return true;
    }
    
    /**
     * Reset ingredienti a fine turno (se necessario).
     */
    public void resetIngredienti() {
        ingredienti.clear();
    }
    
    public Map<String, Integer> getIngredienti() {
        return ingredienti;
    }
    
    /**
     * Incrementa contatore pozioni brewate questo turno.
     */
    public void incrementaPozioniBrewate() {
        pozioniBrewateQuestoTurno++;
    }
    
    /**
     * Reset contatore pozioni brewate.
     */
    public void resetPozioniBrewate() {
        pozioniBrewateQuestoTurno = 0;
    }
    
    public int getPozioniBrewateQuestoTurno() {
        return pozioniBrewateQuestoTurno;
    }

    // ============================================================================
    // NUOVI METODI: DARK ARTS POTIONS (Pack 3+)
    // ============================================================================
    
    /**
     * Aggiungi una Dark Arts Potion attiva al giocatore.
     */
    public void aggiungiDarkArtsPotion(DarkArtsPotion potion) {
        darkArtsPotionsAttive.add(potion);
        System.out.println("☠️ " + eroe.getNome() + " riceve Dark Arts Potion: " + potion.getNome());
    }
    
    /**
     * Rimuovi una Dark Arts Potion specifica.
     */
    public void rimuoviDarkArtsPotion(DarkArtsPotion potion) {
        darkArtsPotionsAttive.remove(potion);
        System.out.println("✨ " + eroe.getNome() + " rimuove Dark Arts Potion: " + potion.getNome());
    }
    
    /**
     * Rimuovi tutte le Dark Arts Potions.
     */
    public void rimuoviTutteDarkArtsPotions() {
        darkArtsPotionsAttive.clear();
        System.out.println("✨ " + eroe.getNome() + " rimuove tutte le Dark Arts Potions");
    }
    
    /**
     * Verifica se il giocatore ha Dark Arts Potions attive.
     */
    public boolean hasDarkArtsPotions() {
        return !darkArtsPotionsAttive.isEmpty();
    }
    
    public List<DarkArtsPotion> getDarkArtsPotionsAttive() {
        return darkArtsPotionsAttive;
    }

    // ============================================================================
    // GETTERS E SETTERS BASE
    // ============================================================================
    
	public Eroe getEroe() {
		return eroe;
	}
	public void setEroe(Eroe eroe) {
		this.eroe = eroe;
	}
	public int getSalute() {
		return salute;
	}
	public void setSalute(int salute) {
		this.salute = salute;
	}
	public int getSaluteMax() {
		return saluteMax;
	}
	public Mazzo getMazzo() {
		return mazzo;
	}
	public Mazzo getScarti() {
		return scarti;
	}
	public List<Carta> getMano() {
		return mano;
	}
	public void setMano(List<Carta> mano) {
		this.mano = mano;
	}
	public int getGettone() {
		return gettone;
	}
	public void setGettone(int gettone) {
		this.gettone = gettone;
	}
	public int getAttacco() {
		return attacco;
	}
	public void setAttacco(int attacco) {
		this.attacco = attacco;
	}
	
	public Carta scegliCarta(List<Carta> mazzo) {
		return selettoreCarta.selezionaCarta(mazzo);
	}

	public Competenza getCompetenza() {
		return competenza;
	}

	public void setCompetenza(Competenza competenza) {
		this.competenza = competenza;
	}
	
	public List<Carta> getCarteAcquistateQuestoTurno() {
	    return carteAcquistateQuestoTurno;
	}

	public void resetCarteAcquistate() {
	    carteAcquistateQuestoTurno.clear();
	}
}
