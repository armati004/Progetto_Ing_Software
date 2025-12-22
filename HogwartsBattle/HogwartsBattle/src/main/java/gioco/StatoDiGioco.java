package gioco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import carte.*;
import data.CardFactory;
import data.GameConfig;
import data.VillainFactory;
import gestoreEffetti.GestoreEffetti;
import gestoreEffetti.GestoreTrigger;
import gestoreEffetti.TipoTrigger;

public class StatoDiGioco {
	// --- Core State ---
    private int annoCorrente;
    private FaseTurno faseCorrente;
    private boolean gameOver = false;
    private boolean victory = false;

    // --- Entità di Gioco ---
    private List<Giocatore> giocatori;
    private int giocatoreCorrente;
    private Luogo currentLocation;
    private LinkedList<Luogo> listaLuoghi; // I luoghi da proteggere

    // --- Mazzi (Riserve) ---
    // Usiamo LinkedList come Stack/Coda
    private LinkedList<Carta> mazzoNegozio;       // Il mazzo da cui si pesca per il mercato
    private LinkedList<Malvagio> mazzoMalvagi;     // Il mazzo dei cattivi
    private LinkedList<ArteOscura> mazzoArtiOscure;       // Il mazzo degli eventi oscuri
    private LinkedList<Horcrux> mazzoHorcrux;     // Solo per Anno 7

    // --- Tabellone (Elementi Attivi) ---
    private List<Carta> mercato;                   // Le 6 carte acquistabili
    private List<Malvagio> malvagiAttivi;        // I nemici attualmente a faccia in su
    private List<Horcrux> horcruxAttivi;       // Gli Horcrux attivi (Anno 7)
    private List<ArteOscura> scartiArtiOscure;      // Scarti eventi oscuri

    // --- Managers ---
    private GestoreEffetti gestoreEffetti;
    private GestoreTrigger gestoreTrigger;

    // --- Configurazione ---
    private boolean hasHorcruxes;

    /**
     * Costruttore: Inizializza tutto lo stato basandosi sulla Configurazione e i Giocatori.
     */
    public StatoDiGioco(GameConfig config, List<Giocatore> giocatori) {
        this.annoCorrente = config.getAnno();
        this.giocatori = giocatori;
        this.giocatoreCorrente = 0;
        this.faseCorrente = FaseTurno.ARTI_OSCURE; // Si inizia sempre dai cattivi
        
        this.hasHorcruxes = config.getContieneHorcrux();

        // 1. Inizializza i Manager
        this.gestoreEffetti = new GestoreEffetti();
        this.gestoreTrigger = new GestoreTrigger();

        // 2. Inizializza le Liste
        this.mazzoNegozio = new LinkedList<>();
        this.mazzoMalvagi = new LinkedList<>();
        this.mazzoArtiOscure = new LinkedList<>();
        this.mazzoHorcrux = new LinkedList<>();
        this.listaLuoghi = new LinkedList<>();
        
        this.mercato = new ArrayList<>();
        this.malvagiAttivi = new ArrayList<>();
        this.horcruxAttivi = new ArrayList<>();
        this.scartiArtiOscure = new ArrayList<>();

        // 3. Carica i Mazzi usando gli ID della Configurazione
        populateDecks(config);
        
        // 4. Setup Iniziale del Tavolo
        setupTabellone();
    }

    // ----------------------------------------------------------------
    // SEZIONE DI INIZIALIZZAZIONE
    // ----------------------------------------------------------------

    private void populateDecks(GameConfig config) {
        // Popola Mazzo Hogwarts
        for (String id : config.getCarteNegozioId()) {
            mazzoNegozio.add(CardFactory.creaCarta(id));
        }
        Collections.shuffle(mazzoNegozio);

        // Popola Mazzo Malvagi
        for (String id : config.getMalvagiId()) {
            mazzoMalvagi.add(VillainFactory.creaMalvagio(id));
        }
        Collections.shuffle(mazzoMalvagi);

        // Popola Mazzo Arti Oscure
        for (String id : config.getArtiOscureId()) {
            mazzoArtiOscure.add((ArteOscura) CardFactory.creaCarta(id));
        }
        Collections.shuffle(mazzoArtiOscure);

        // Popola Luoghi (TODO: LocationFactory non l'abbiamo scritta, assumiamo ci sia o si faccia a mano qui)
        // Per ora creo location fittizie per il test
        // locationDeck.add(new Location("Diagon Alley", 4)); 
        // currentLocation = locationDeck.poll();

        // Popola Horcrux (Solo Anno 7)
        if (hasHorcruxes) {
            // Qui dovresti avere una lista di ID horcrux nel config, se non c'è la simuliamo
            // for (String id : config.getHorcruxIds()) horcruxDeck.add((Horcrux)VillainFactory.createVillain(id));
            //Collections.shuffle(horcruxDeck);
        }
    }

    private void setupTabellone() {
        // 1. Riempi il Mercato (6 slot)
        rifornisciMercato();

        // 2. Rivela il primo Malvagio
        // (La regola cambia in base all'anno, es. Anno 1 = 1 Malvagio, Anno 7 = 3 Malvagi)
        int initialVillains = (annoCorrente >= 3) ? 2 : 1; 
        for (int i = 0; i < initialVillains; i++) {
            addMalvagioAttivo();
        }
        
        // 3. Rivela Horcrux (se Anno 7)
        if (hasHorcruxes && !mazzoHorcrux.isEmpty()) {
            horcruxAttivi.add(mazzoHorcrux.pop());
        }
    }

    // ----------------------------------------------------------------
    // LOGICA DI GIOCO (AZIONI)
    // ----------------------------------------------------------------

    /**
     * Pesca una carta Arti Oscure, gestisce il mazzo vuoto.
     */
    public ArteOscura pescaArteOscura() {
        if (mazzoArtiOscure.isEmpty()) {
            if (scartiArtiOscure.isEmpty()) {
                System.out.println("Nessuna carta Arti Oscure rimasta!");
                return null;
            }
            // Rimescola gli scarti
            mazzoArtiOscure.addAll(scartiArtiOscure);
            scartiArtiOscure.clear();
            Collections.shuffle(mazzoArtiOscure);
        }
        ArteOscura pescata = mazzoArtiOscure.pop();
        scartiArtiOscure.add(pescata); // Va subito negli scarti dopo l'uso
        return pescata;
    }

    /**
     * Aggiunge un nuovo malvagio al tabellone se c'è spazio e carte.
     */
    public void addMalvagioAttivo() {
        if (!mazzoMalvagi.isEmpty()) {
            Malvagio m = mazzoMalvagi.pop();
            malvagiAttivi.add(m);
            System.out.println("Nuovo Malvagio rivelato: " + m.getNome());
        }
    }

    /**
     * Sconfigge un Malvagio.
     */
    public void sconfiggiMalvagio(Malvagio m) {
        System.out.println(m.getNome() + " è stato sconfitto!");
        m.defeat(this, giocatori.get(giocatoreCorrente));
        
        malvagiAttivi.remove(m);
        // Aggiungi subito il rimpiazzo (regola standard)
        
        gestoreTrigger.attivaTrigger(TipoTrigger.NEMICO_SCONFITTO, this, giocatori.get(giocatoreCorrente));
    }

    public void distruggiHorcrux(Horcrux h) {
        System.out.println("Horcrux distrutto: " + h.getNome());
        //h.onDefeat(this, getGiocatori().get(giocatoreCorrente));
        
        horcruxAttivi.remove(h);
        if (!mazzoHorcrux.isEmpty()) {
            horcruxAttivi.add(mazzoHorcrux.pop());
        }
    }

    /**
     * Riempie gli slot vuoti del mercato.
     */
    public void rifornisciMercato() {
        while (mercato.size() < 6 && !mazzoNegozio.isEmpty()) {
            mercato.add(mazzoNegozio.pop());
        }
    }
    
    /**
     * Rimpiazza una carta specifica (usato dopo un acquisto).
     */
    public void rimpiazzaCartaMercato(int indice) {
        if (indice >= 0 && indice < mercato.size()) {
            mercato.remove(indice);
            if (!mazzoNegozio.isEmpty()) {
                mercato.add(indice, mazzoNegozio.pop());
            }
        }
    }

    // ----------------------------------------------------------------
    // GESTIONE FINE TURNO
    // ----------------------------------------------------------------

    public void fineTurno() {
        Giocatore g = getGiocatori().get(giocatoreCorrente);
        
        // 1. Trigger Fine Turno
        //triggerManager.fire(TriggerType.ON_TURN_END, this, p);
        
        // 2. Pulizia EffectManager
        gestoreEffetti.fineTurno();
        
        // 3. Reset del Giocatore (Scarta mano, risorse a 0, pesca 5)
        if(!g.getMano().isEmpty()) {
        	while(!g.getMano().isEmpty()) {
        		g.scartaCarta(g.getMano().get(0));
        	}
        }
        g.setAttacco(0);
        g.setGettone(0);
        
        if(g.getMazzo().getCarte().isEmpty()) {
        	g.getMazzo().getCarte().addAll(g.getScarti().getCarte());
        	g.getScarti().getCarte().removeAll(g.getScarti().getCarte());
        }
        
        for(int i = 0; i < 5; i++) {
        	if(!g.getMazzo().getCarte().isEmpty()) {
        		g.getMano().add(g.getMazzo().pescaCarta());
        	}
        	else {
        		break;
        	}
        }
        
        // 4. Reset Competenze
        /*if (p.getProficiency() != null) {
            p.getProficiency().refreshTurn();
        }*/
        
        // 5. Riempi Mercato (nel caso ci fossero buchi non riempiti)
        rifornisciMercato();
        
        // 6. Passa al prossimo giocatore
        giocatoreCorrente = (giocatoreCorrente + 1) % giocatori.size();
        
        System.out.println("Il turno passa a: " + giocatori.get(giocatoreCorrente).getEroe().getNome());
    }
    
	public int getAnnoCorrente() {
		return annoCorrente;
	}

	public void setAnnoCorrente(int annoCorrente) {
		this.annoCorrente = annoCorrente;
	}

	public Luogo getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Luogo currentLocation) {
		this.currentLocation = currentLocation;
	}

	public FaseTurno getFaseCorrente() {
		return faseCorrente;
	}

	public void setFaseCorrente(FaseTurno faseCorrente) {
		this.faseCorrente = faseCorrente;
	}

	public List<Giocatore> getGiocatori() {
		return giocatori;
	}
	public void setGiocatori(List<Giocatore> giocatori) {
		this.giocatori = giocatori;
	}
	
	public int getGiocatoreCorrente() {
		return giocatoreCorrente;
	}

	public void setGiocatoreCorrente(int giocatoreCorrente) {
		this.giocatoreCorrente = giocatoreCorrente;
	}

	public List<Malvagio> getMalvagiAttivi() {
		return malvagiAttivi;
	}

	public List<Carta> getMercato() {
		return mercato;
	}

	public GestoreEffetti getGestoreEffetti() {
		return gestoreEffetti;
	}
	public void setGestoreEffetti(GestoreEffetti gestoreEffetti) {
		this.gestoreEffetti = gestoreEffetti;
	}
	public GestoreTrigger getGestoreTrigger() {
		return gestoreTrigger;
	}
	public void setGestoreTrigger(GestoreTrigger gestoreTrigger) {
		this.gestoreTrigger = gestoreTrigger;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public boolean isVictory() {
		return victory;
	}

	public LinkedList<Luogo> getListaLuoghi() {
		return listaLuoghi;
	}

	public LinkedList<Carta> getMazzoNegozio() {
		return mazzoNegozio;
	}

	public LinkedList<Malvagio> getMazzoMalvagi() {
		return mazzoMalvagi;
	}

	public LinkedList<ArteOscura> getMazzoArtiOscure() {
		return mazzoArtiOscure;
	}

	public LinkedList<Horcrux> getMazzoHorcrux() {
		return mazzoHorcrux;
	}

	public List<Horcrux> getHorcruxAttivi() {
		return horcruxAttivi;
	}

	public List<ArteOscura> getScartiArtiOscure() {
		return scartiArtiOscure;
	}

	public boolean isHasHorcruxes() {
		return hasHorcruxes;
	}
}
