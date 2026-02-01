package gioco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carte.Carta;
import carte.Competenza;
import carte.Eroe;
import carte.Mazzo;
import carte.DarkArtsPotion;
import gestoreEffetti.Trigger;

/**
 * Rappresenta un giocatore nel gioco.
 * Gestisce il mazzo personale, la mano, le risorse e gli effetti attivi.
 * 
 * ESPANSIONE CHARMS & POTIONS:
 * - Sistema ingredienti per preparare pozioni (Pack 2+)
 * - Dark Arts Potions attive sul giocatore (Pack 3+)
 * - Contatori per Encounter
 */
public class Giocatore {
    private Eroe eroe;
    private int salute;
    private final int saluteMax = 10;
    private Mazzo mazzo;
    private Mazzo scarti;
    private List<Carta> mano;
    private int gettone;        // Influenza (usata per acquistare carte)
    private int attacco;        // Attacco (usato per sconfiggere malvagi)
    private Competenza competenza;
    
    private List<Carta> carteAcquistateQuestoTurno = new ArrayList<>();
    
    // Sistema Ingredienti (Pack 2+)
    private Map<String, Integer> ingredienti;
    
    // Dark Arts Potions attive (Pack 3+)
    private List<DarkArtsPotion> darkArtsPotionsAttive;
    
    // Pozioni brewate questo turno (per Encounter)
    private int pozioniBrewateQuestoTurno;
    
    /**
     * Costruttore per creare un nuovo giocatore.
     * 
     * @param eroe Eroe scelto dal giocatore
     */
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
    
    /**
     * Scarta una carta dalla mano.
     * La carta viene spostata nel mazzo degli scarti.
     * 
     * @param carta Carta da scartare
     */
    public void scartaCarta(Carta carta) {
        this.getScarti().getCarte().add(carta);
        this.getMano().remove(carta);
    }
    
    /**
     * Inizializza la mano pescando 5 carte dal mazzo.
     */
    public void inizializzaMano() {
        for (int i = 0; i < 5; i++) {
            Carta cartaPescata = mazzo.pescaCarta();
            if (cartaPescata != null) {
                mano.add(cartaPescata);
            }
        }
    }
    
    /**
     * Gioca una carta dalla mano.
     * Applica gli effetti, rimuove trigger e sposta la carta negli scarti.
     * 
     * @param stato Stato di gioco corrente
     * @param carta Carta da giocare
     */
    public void giocaCarta(StatoDiGioco stato, Carta carta) {
        if (!mano.contains(carta)) {
            System.out.println("ERRORE: La carta " + carta.getNome() + " non è nella mano!");
            return;
        }

        // Rimuovi trigger "in mano" PRIMA di giocare
        rimuoviTriggersCartaDaMano(stato, carta);

        // Applica effetto della carta
        carta.applicaEffetto(stato, this);

        // Traccia alleati giocati
        if (carta.getClasse().equals("Alleato")) {
            stato.getAlleatiGiocatiInQuestoTurno().add((carte.Alleato) carta);
        }
        
        // Sposta carta dalla mano agli scarti
        mano.remove(carta);
        scarti.aggiungiCarta(carta);

        System.out.println("Giocata: " + carta.getNome());
    }
    
    /**
     * Cerca carte di un tipo specifico nel mazzo.
     * 
     * @param tipo Tipo di carta da cercare (es. "Alleato", "Incantesimo")
     * @return Lista di carte trovate
     */
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

    /**
     * Cerca carte di un tipo specifico negli scarti.
     * 
     * @param tipo Tipo di carta da cercare
     * @return Lista di carte trovate negli scarti
     */
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
    
    /**
     * Pesca una carta dal mazzo e la aggiunge alla mano.
     * 
     * @return Carta pescata o null se il mazzo è vuoto
     */
    public Carta pescaCarta() {
        Carta carta = mazzo.pescaCarta();
        if (carta != null) {
            mano.add(carta);
        }
        return carta;
    }
    
    /**
     * Rimuove i trigger associati a una carta specifica quando viene giocata.
     * 
     * @param stato Stato di gioco
     * @param carta Carta i cui trigger devono essere rimossi
     */
    public void rimuoviTriggersCartaDaMano(StatoDiGioco stato, Carta carta) {
        List<Trigger> triggersRimossi = new ArrayList<>();
        for (Carta t : stato.getGestoreTrigger().getTriggers()) {
            if (t.getCartaOrigine() != null && t.getCartaOrigine() == carta) {
                triggersRimossi.add(t);
            }
        }
        stato.getGestoreTrigger().getTriggers().removeAll(triggersRimossi);
    }
    
    /**
     * Aggiorna i trigger delle carte in mano.
     * Registra i trigger "in mano" delle carte nel gestore trigger.
     * 
     * @param stato Stato di gioco
     */
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
    // SISTEMA INGREDIENTI (Pack 2+)
    // ============================================================================
    
    /**
     * Ottiene la mappa degli ingredienti posseduti dal giocatore.
     * 
     * @return Map con tipo ingrediente e quantità
     */
    public Map<String, Integer> getIngredienti() {
        return ingredienti;
    }
    
    /**
     * Aggiunge un ingrediente al pool del giocatore.
     * 
     * @param tipo Tipo di ingrediente (es. "BICORN_HORN")
     * @param quantita Quantità da aggiungere
     */
    public void aggiungiIngrediente(String tipo, int quantita) {
        ingredienti.put(tipo, ingredienti.getOrDefault(tipo, 0) + quantita);
        System.out.println("🧪 " + eroe.getNome() + " guadagna " + quantita + "x " + tipo);
    }
    
    /**
     * Rimuove un ingrediente dal pool del giocatore.
     * 
     * @param tipo Tipo di ingrediente
     * @param quantita Quantità da rimuovere
     * @return true se c'erano abbastanza ingredienti
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
     * 
     * @param richiesti Map con ingredienti richiesti e quantità
     * @return true se il giocatore ha tutti gli ingredienti necessari
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
     * Consuma gli ingredienti per preparare una pozione.
     * 
     * @param richiesti Map con ingredienti da consumare
     * @return true se il consumo è avvenuto con successo
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
     * Reset ingredienti a fine turno (se necessario per regole speciali).
     */
    public void resetIngredienti() {
        ingredienti.clear();
    }
    
    /**
     * Ottiene il numero di pozioni brewate questo turno.
     * 
     * @return Numero di pozioni completate
     */
    public int getPozioniBrewateQuestoTurno() {
        return pozioniBrewateQuestoTurno;
    }
    
    /**
     * Incrementa il contatore pozioni brewate questo turno.
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

    // ============================================================================
    // DARK ARTS POTIONS (Pack 3+)
    // ============================================================================
    
    /**
     * Aggiunge una Dark Arts Potion attiva al giocatore.
     * 
     * @param potion Dark Arts Potion da aggiungere
     */
    public void aggiungiDarkArtsPotion(DarkArtsPotion potion) {
        darkArtsPotionsAttive.add(potion);
        System.out.println("☠️ " + eroe.getNome() + " riceve Dark Arts Potion: " + potion.getNome());
    }
    
    /**
     * Rimuove una Dark Arts Potion specifica.
     * 
     * @param potion Dark Arts Potion da rimuovere
     */
    public void rimuoviDarkArtsPotion(DarkArtsPotion potion) {
        darkArtsPotionsAttive.remove(potion);
        System.out.println("✨ " + eroe.getNome() + " rimuove Dark Arts Potion: " + potion.getNome());
    }
    
    /**
     * Rimuove tutte le Dark Arts Potions.
     */
    public void rimuoviTutteDarkArtsPotions() {
        darkArtsPotionsAttive.clear();
        System.out.println("✨ " + eroe.getNome() + " rimuove tutte le Dark Arts Potions");
    }
    
    /**
     * Verifica se il giocatore ha Dark Arts Potions attive.
     * 
     * @return true se ci sono Dark Arts Potions attive
     */
    public boolean hasDarkArtsPotions() {
        return !darkArtsPotionsAttive.isEmpty();
    }
    
    /**
     * Ottiene la lista di Dark Arts Potions attive.
     * 
     * @return Lista di Dark Arts Potions
     */
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
        this.salute = Math.max(0, Math.min(salute, saluteMax));
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
        this.gettone = Math.max(0, gettone);
    }
    
    public int getAttacco() {
        return attacco;
    }
    
    public void setAttacco(int attacco) {
        this.attacco = Math.max(0, attacco);
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

    public String getNome() {
        return eroe != null ? eroe.getNome() : "Giocatore sconosciuto";
    }
    
    @Override
    public String toString() {
        return eroe.getNome() + " (" + salute + "/" + saluteMax + " ❤️)";
    }

	public void acquistaCarta(List<Carta> mercato, Carta carta, StatoDiGioco stato) {
		// TODO Auto-generated method stub
		
	}
}
