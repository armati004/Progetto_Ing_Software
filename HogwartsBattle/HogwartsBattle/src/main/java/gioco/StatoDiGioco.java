package gioco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import gestoreEffetti.*;


import carte.*;
import data.CardFactory;
import data.DiceFactory;
import data.GameConfig;
import data.LocationFactory;
import data.VillainFactory;
import data.PotionFactory;
import data.DarkArtsPotionFactory;
import gestoreEffetti.GestoreEffetti;
import gestoreEffetti.GestoreTrigger;

/**
 * StatoDiGioco - Stato completo del gioco
 * 
 * AGGIORNATO per espansione Charms & Potions:
 * - Sistema Pozioni (Pack 2+)
 * - Sistema Dark Arts Potions (Pack 3+)
 * - Sistema Encounter (Pack 1-4)
 * - Gestori specializzati per ogni meccanica
 */
public class StatoDiGioco {
	// --- Core State ---
    private int annoCorrente;
    private FaseTurno faseCorrente;
    private boolean gameOver = false;
    private boolean victory = false;

    // --- Entità di Gioco ---
    private List<Giocatore> giocatori;
    private int giocatoreCorrente;
    private List<Carta> alleatiGiocatiInQuestoTurno;
    private Luogo currentLocation;
    private LinkedList<Luogo> listaLuoghi;
    private Luogo luogoAttuale;
    private int luoghiConquistati = 0;

    // --- Mazzi (Riserve) ---
    private LinkedList<Carta> mazzoNegozio;
    private LinkedList<Malvagio> mazzoMalvagi;
    private LinkedList<ArteOscura> mazzoArtiOscure;
    private LinkedList<Horcrux> mazzoHorcrux;

    // --- Tabellone (Elementi Attivi) ---
    private List<Carta> mercato;
    private List<Malvagio> malvagiAttivi;
    private List<Horcrux> horcruxAttivi;
    private List<ArteOscura> scartiArtiOscure;

    // --- Managers ---
    private GestoreEffetti gestoreEffetti;
    private GestoreTrigger gestoreTrigger;
    
    // NUOVO: Pack 1 - Encounter System
    private EncounterManager encounterManager;
    
    // NUOVO: Pack 2 - Potion System
    private PotionManager potionManager;
    
    // NUOVO: Pack 3 - Dark Arts Potion System
    private DarkArtsPotionManager darkArtsPotionManager;
    
    private final Map<String, Dado> dadi;
    private Map<Malvagio, Integer> attacchiAssegnati;

    // --- Configurazione ---
    private boolean hasHorcruxes;
    private boolean hasEncounters;
    private boolean hasPotions;
    private boolean hasDarkArtsPotions;

    /**
     * Costruttore: Inizializza tutto lo stato basandosi sulla Configurazione e i Giocatori.
     */
    public StatoDiGioco(GameConfig config, List<Giocatore> giocatori) {
        this.annoCorrente = config.getAnno();
        this.giocatori = giocatori;
        this.giocatoreCorrente = 0;
        this.faseCorrente = FaseTurno.ARTI_OSCURE;
        
        this.hasHorcruxes = config.getContieneHorcrux();
        this.hasEncounters = config.getContieneEncounter();
        this.hasPotions = config.getContienePozioni();
        this.hasDarkArtsPotions = config.getContieneDarkArtsPozioni();

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
        this.alleatiGiocatiInQuestoTurno = new ArrayList<>();
        
        this.dadi = new HashMap<>();
        if(config.getContieneDadi() == true) {
        	caricaDadi();
        }
        this.attacchiAssegnati = new HashMap<>();
        
        // 3. Inizializza Manager Espansione
        inizializzaManagerEspansione(config);

        // 4. Carica i Mazzi usando gli ID della Configurazione
        populateDecks(config);
        
        // 5. Setup Iniziale del Tavolo
        setupTabellone();
    }
    
    /**
     * Inizializza i manager delle espansioni in base alla configurazione.
     */
    private void inizializzaManagerEspansione(GameConfig config) {
        // Pack 1: Encounter System
        if (hasEncounters) {
            System.out.println("🎯 Inizializzazione Encounter System...");
            this.encounterManager = new EncounterManager(this);
        }
        
        // Pack 2: Potion System
        if (hasPotions) {
            System.out.println("🧪 Inizializzazione Potion System...");
            this.potionManager = new PotionManager(this);
        }
        
        // Pack 3: Dark Arts Potion System
        if (hasDarkArtsPotions) {
            System.out.println("☠️ Inizializzazione Dark Arts Potion System...");
            this.darkArtsPotionManager = new DarkArtsPotionManager(this);
        }
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
        
        // NUOVO: Aggiungi Dark Arts Potions al mazzo Arti Oscure (Pack 3+)
        if (hasDarkArtsPotions && config.getDarkArtsPotionId() != null) {
            System.out.println("☠️ Aggiunta Dark Arts Potions al mazzo Arti Oscure...");
            for (String id : config.getDarkArtsPotionId()) {
                DarkArtsPotion dap = DarkArtsPotionFactory.getDarkArtsPotionById(id);
                if (dap != null) {
                    mazzoArtiOscure.add(dap);
                }
            }
        }
        Collections.shuffle(mazzoArtiOscure);

        // Luoghi
        if (config.getLuoghiId() != null && !config.getLuoghiId().isEmpty()) {
            for (String id : config.getLuoghiId()) {
                try {
                    Luogo luogo = LocationFactory.creaLuogo(id);
                    listaLuoghi.add(luogo);
                } catch (Exception e) {
                    System.err.println("⚠️ Errore caricamento luogo: " + id);
                }
            }
            
            if (!listaLuoghi.isEmpty()) {
                luogoAttuale = listaLuoghi.getFirst();
                System.out.println("🏰 Luogo iniziale: " + luogoAttuale.getNome());
            }
        }

        // Popola Horcrux (Anno 7)
        if (hasHorcruxes && config.getHorcruxId() != null) {
            for (String id : config.getHorcruxId()) {
                // TODO: Horcrux factory
                // mazzoHorcrux.add(HorcruxFactory.creaHorcrux(id));
            }
            Collections.shuffle(mazzoHorcrux);
        }
        
        // NUOVO: Carica Encounter (Pack 1-4)
        if (hasEncounters && config.getEncounterId() != null && encounterManager != null) {
            System.out.println("🎯 Caricamento Encounter...");
            for (String id : config.getEncounterId()) {
                encounterManager.aggiungiEncounterAlMazzo(id);
            }
            encounterManager.inizializza();
        }
        
        // NUOVO: Carica Pozioni (Pack 2-4)
        if (hasPotions && config.getPozioniId() != null && potionManager != null) {
            System.out.println("🧪 Caricamento Pozioni...");
            for (String id : config.getPozioniId()) {
                Pozione pozione = PotionFactory.getPotionById(id);
                if (pozione != null) {
                    potionManager.aggiungiPozioneAlMazzo(pozione);
                }
            }
            // Imposta il lato degli scaffali
            if (config.getPotionShelfSide() != null) {
                potionManager.setLatoCorrente(config.getPotionShelfSide());
            }
            potionManager.inizializza();
        }
    }

    private void setupTabellone() {
        // 1. Riempi il Mercato (6 slot)
        rifornisciMercato();

        // 2. Rivela il primo Malvagio
        int initialVillains = (annoCorrente >= 3) ? 2 : 1; 
        for (int i = 0; i < initialVillains; i++) {
            addMalvagioAttivo();
        }
        
        // 3. Rivela Horcrux (se Anno 7)
        if (hasHorcruxes && !mazzoHorcrux.isEmpty()) {
            horcruxAttivi.add(mazzoHorcrux.pop());
        }
    }
    
    private void caricaDadi() {
        System.out.println("🎲 Caricamento dadi delle casate...");
        
        try {
            Map<String, Dado> dadiCasate = DiceFactory.creaDadiCasate();
            this.dadi.putAll(dadiCasate);
            
            System.out.println("✅ Caricati " + dadiCasate.size() + " dadi");
            
        } catch (Exception e) {
            System.err.println("⚠️ Errore nel caricamento dei dadi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------
    // LOGICA DI GIOCO (AZIONI)
    // ----------------------------------------------------------------

    /**
     * Pesca una carta Arti Oscure, gestisce il mazzo vuoto.
     * AGGIORNATO: Può pescare anche Dark Arts Potions (Pack 3+)
     */
    public ArteOscura pescaArteOscura() {
        if (mazzoArtiOscure.isEmpty()) {
            if (scartiArtiOscure.isEmpty()) {
                System.out.println("Nessuna carta Arti Oscure rimasta!");
                return null;
            }
            mazzoArtiOscure.addAll(scartiArtiOscure);
            scartiArtiOscure.clear();
            Collections.shuffle(mazzoArtiOscure);
        }
        
        ArteOscura pescata = mazzoArtiOscure.pop();
        
        // Se è una Dark Arts Potion, gestiscila diversamente
        if (pescata instanceof DarkArtsPotion && darkArtsPotionManager != null) {
            darkArtsPotionManager.assegnaPozioneAlGiocatore((DarkArtsPotion) pescata, 
                                                           giocatori.get(giocatoreCorrente));
        } else {
            scartiArtiOscure.add(pescata);
        }
        
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
        
        // 1. Pulizia EffectManager
        gestoreEffetti.fineTurno();
        
        // 2. Reset del Giocatore (Scarta mano, risorse a 0, pesca 5)
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
        
        // 3. Riempi Mercato
        rifornisciMercato();

        // 4. Pulisci la lista degli alleati giocati
        alleatiGiocatiInQuestoTurno.clear();
        
        // 5. NUOVO: Verifica completamento Encounter (Pack 1)
        if (hasEncounters && encounterManager != null) {
            encounterManager.verificaCompletamentoEncounter();
        }
        
        // 6. Passa al prossimo giocatore
        giocatoreCorrente += 1;
        if(giocatoreCorrente == giocatori.size()) {
        	giocatoreCorrente = 0;
        }
        
        // 7. Reset fase turno
        faseCorrente = FaseTurno.ARTI_OSCURE;
    }

    // ----------------------------------------------------------------
    // GESTIONE LUOGO
    // ----------------------------------------------------------------
    
    public void aggiungiControlloLuogo(int quantita) {
        if (luogoAttuale != null) {
            luogoAttuale.aggiungiControllo(quantita);
            System.out.println("⚡ Aggiunto " + quantita + " controllo a " + luogoAttuale.getNome() + 
                             " (" + luogoAttuale.getControllo() + "/" + luogoAttuale.getControlloMax() + ")");
            
            if (luogoAttuale.isConquistato()) {
                conquistaLuogo();
            }
        }
    }
    
    public void rimuoviControlloLuogo(int quantita) {
        if (luogoAttuale != null) {
            luogoAttuale.rimuoviControllo(quantita);
            System.out.println("✨ Rimosso " + quantita + " controllo da " + luogoAttuale.getNome() + 
                             " (" + luogoAttuale.getControllo() + "/" + luogoAttuale.getControlloMax() + ")");
        }
    }
    
    private void conquistaLuogo() {
        System.out.println("💀 LUOGO CONQUISTATO: " + luogoAttuale.getNome());
        luoghiConquistati++;
        
        // Verifica game over
        if (luoghiConquistati >= listaLuoghi.size()) {
            gameOver = true;
            victory = false;
            System.out.println("💀 GAME OVER - Tutti i luoghi sono stati conquistati!");
        } else {
            // Passa al prossimo luogo
            if (!listaLuoghi.isEmpty()) {
                listaLuoghi.removeFirst();
                if (!listaLuoghi.isEmpty()) {
                    luogoAttuale = listaLuoghi.getFirst();
                    System.out.println("🏰 Nuovo luogo: " + luogoAttuale.getNome());
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // GETTERS E SETTERS BASE
    // ----------------------------------------------------------------

    public Luogo getLuogoAttuale() {
		return luogoAttuale;
	}

	public void setLuogoAttuale(Luogo luogoAttuale) {
		this.luogoAttuale = luogoAttuale;
	}

	public Map<String, Dado> getDadi() {
		return dadi;
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
	
    public List<Carta> getAlleatiGiocatiInQuestoTurno() {
        return alleatiGiocatiInQuestoTurno;
    }

	public void setVictory(boolean b) {
		this.victory = b;
	}
	
	public int getLuoghiConquistati() {
		return luoghiConquistati;
	}
    
	public Dado getDado(String nome) {
	    return dadi.get(nome);
	}
	
	public Map<Malvagio, Integer> getAttacchiAssegnati() {
		return attacchiAssegnati;
	}
	
	public int getAnnoCorrente() {
		return annoCorrente;
	}
	
	// ----------------------------------------------------------------
	// GETTERS E SETTERS ESPANSIONE
	// ----------------------------------------------------------------
	
	public boolean isHasEncounters() {
		return hasEncounters;
	}
	
	public boolean isHasPotions() {
		return hasPotions;
	}
	
	public boolean isHasDarkArtsPotions() {
		return hasDarkArtsPotions;
	}
	
	public EncounterManager getEncounterManager() {
		return encounterManager;
	}
	
	public void setEncounterManager(EncounterManager encounterManager) {
		this.encounterManager = encounterManager;
	}
	
	public PotionManager getPotionManager() {
		return potionManager;
	}
	
	public void setPotionManager(PotionManager potionManager) {
		this.potionManager = potionManager;
	}
	
	public DarkArtsPotionManager getDarkArtsPotionManager() {
		return darkArtsPotionManager;
	}
	
	public void setDarkArtsPotionManager(DarkArtsPotionManager darkArtsPotionManager) {
		this.darkArtsPotionManager = darkArtsPotionManager;
	}
}
