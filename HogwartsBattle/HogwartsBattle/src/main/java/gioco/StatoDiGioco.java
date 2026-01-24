package gioco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import carte.*;
import data.CardFactory;
import data.DiceFactory;
import data.GameConfig;
import data.LocationFactory;
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
    private List<Carta> alleatiGiocatiInQuestoTurno; // Lista degli alleati giocati nel turno corrente
    private Luogo currentLocation;
    private LinkedList<Luogo> listaLuoghi; // I luoghi da proteggere
    private Luogo luogoAttuale;
    private int luoghiConquistati = 0;

    public Luogo getLuogoAttuale() {
		return luogoAttuale;
	}

	public void setLuogoAttuale(Luogo luogoAttuale) {
		this.luogoAttuale = luogoAttuale;
	}

	public Map<String, Dado> getDadi() {
		return dadi;
	}
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
    
    private final Map<String, Dado> dadi;
    private Map<Malvagio, Integer> attacchiAssegnati;

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
        this.alleatiGiocatiInQuestoTurno = new ArrayList<>();
        
        this.dadi = new HashMap<>();
        if(config.getContieneDadi() == true) {
        	caricaDadi();
        }
        this.attacchiAssegnati = new HashMap<>();

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
            
            // Imposta il primo luogo come attuale
            if (!listaLuoghi.isEmpty()) {
                luogoAttuale = listaLuoghi.getFirst();
                System.out.println("🏰 Luogo iniziale: " + luogoAttuale.getNome());
            }
        }

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

        // 6. Pulisci la lista degli alleati giocati in questo turno
        alleatiGiocatiInQuestoTurno.clear();
        
        // 7. Passa al prossimo giocatore
        giocatoreCorrente += 1;
        
        if(giocatoreCorrente == giocatori.size()) {
        	giocatoreCorrente = 0;
        }
        
        System.out.println("Il turno passa a: " + giocatori.get(giocatoreCorrente).getEroe().getNome());
    }
    
    /**
     * Verifica le condizioni di vittoria
     * VITTORIA: Tutti i malvagi sconfitti (+ horcrux se anno >= 4)
     * 
     * Quando la vittoria viene rilevata, notifica automaticamente il GameController
     * per mostrare la schermata di vittoria
     */
    private void verificaCondizioneVittoria() {
        // Debug: Stampa stato corrente
        System.out.println("\n🔍 Verifica Condizione Vittoria:");
        System.out.println("  Mazzo malvagi vuoto: " + mazzoMalvagi.isEmpty());
        System.out.println("  Malvagi attivi: " + malvagiAttivi.size());
        System.out.println("  Ha Horcruxes: " + hasHorcruxes);
        
        // Vittoria: Tutti i malvagi sconfitti
        boolean malvagiSconfitti = mazzoMalvagi.isEmpty() && malvagiAttivi.isEmpty();
        
        if (hasHorcruxes) {
            // Anno >= 4: Servono anche horcrux distrutti
            boolean horcruxDistrutto = mazzoHorcrux.isEmpty() && 
                                       (horcruxAttivi == null || horcruxAttivi.isEmpty());
            
            System.out.println("  Mazzo horcrux vuoto: " + mazzoHorcrux.isEmpty());
            System.out.println("  Horcrux attivi: " + (horcruxAttivi != null ? horcruxAttivi.size() : 0));
            System.out.println("  Malvagi sconfitti: " + malvagiSconfitti);
            System.out.println("  Horcrux distrutto: " + horcruxDistrutto);
            
            if (malvagiSconfitti && horcruxDistrutto) {
                System.out.println("\n🎉 ========================================");
                System.out.println("🎉 ===== VITTORIA! =====");
                System.out.println("🎉 ========================================");
                System.out.println("✅ Tutti i malvagi sono stati sconfitti!");
                System.out.println("✅ Tutti gli Horcrux sono stati distrutti!");
                System.out.println("🏆 Gli eroi hanno salvato Hogwarts!");
                System.out.println("🎉 ========================================\n");
                
                setVictory(true);
                setGameOver(true);
                
                // ⭐ FONDAMENTALE: Notifica GameController
                javafx.application.Platform.runLater(() -> {
                    System.out.println("📢 Notifica GameController della vittoria...");
                    if (grafica.GameController.getInstance() != null) {
                        grafica.GameController.getInstance().onVittoria();
                    } else {
                        System.err.println("❌ GameController.getInstance() è null!");
                    }
                });
            }
        } else {
            // Anno < 4: Solo malvagi
            System.out.println("  Malvagi sconfitti: " + malvagiSconfitti);
            
            if (malvagiSconfitti) {
                System.out.println("\n🎉 ========================================");
                System.out.println("🎉 ===== VITTORIA! =====");
                System.out.println("🎉 ========================================");
                System.out.println("✅ Tutti i malvagi sono stati sconfitti!");
                System.out.println("🏆 Gli eroi hanno salvato Hogwarts!");
                System.out.println("🎉 ========================================\n");
                
                setVictory(true);
                setGameOver(true);
                
                // ⭐ FONDAMENTALE: Notifica GameController
                javafx.application.Platform.runLater(() -> {
                    System.out.println("📢 Notifica GameController della vittoria...");
                    if (grafica.GameController.getInstance() != null) {
                        grafica.GameController.getInstance().onVittoria();
                    } else {
                        System.err.println("❌ GameController.getInstance() è null!");
                    }
                });
            }
        }
    }

    /**
     * Verifica le condizioni di sconfitta
     * SCONFITTA: Tutti i luoghi sono stati persi
     * (Un luogo è perso quando marchi neri >= max)
     * 
     * Quando la sconfitta viene rilevata, notifica automaticamente il GameController
     * per mostrare la schermata di Game Over
     */
    private void verificaCondizioneSconfitta() {
        if (listaLuoghi == null || listaLuoghi.isEmpty()) {
            System.out.println("⚠️ Lista luoghi vuota o null!");
            return;
        }
        
        // Conta quanti luoghi sono persi
        int luoghiPersi = 0;
        int luoghiTotali = listaLuoghi.size();
        
        // Debug: Stampa stato luoghi
        System.out.println("\n🔍 Verifica Condizione Sconfitta:");
        for (int i = 0; i < listaLuoghi.size(); i++) {
            Luogo luogo = listaLuoghi.get(i);
            int marchi = luogo.getNumeroMarchiNeri();
            int max = luogo.getMarchiNeriMax();
            boolean perso = marchi >= max;
            
            System.out.println("  Luogo " + (i+1) + ": " + luogo.getNome());
            System.out.println("    Marchi: " + marchi + "/" + max + " - " + (perso ? "❌ PERSO" : "✅ OK"));
            
            if (perso) {
                luoghiPersi++;
            }
        }
        
        System.out.println("  Totale luoghi persi: " + luoghiPersi + "/" + luoghiTotali);
        
        // Sconfitta: Tutti i luoghi persi
        if (luoghiPersi >= luoghiTotali) {
            System.out.println("\n💀 ========================================");
            System.out.println("💀 ===== SCONFITTA! =====");
            System.out.println("💀 ========================================");
            System.out.println("❌ Tutti i luoghi sono stati persi!");
            System.out.println("🌑 I Marchi Neri hanno sopraffatto Hogwarts!");
            System.out.println("💀 ========================================\n");
            
            setVictory(false);
            setGameOver(true);
            
            // ⭐ FONDAMENTALE: Notifica GameController
            javafx.application.Platform.runLater(() -> {
                System.out.println("📢 Notifica GameController della sconfitta...");
                if (grafica.GameController.getInstance() != null) {
                    grafica.GameController.getInstance().onSconfitta();
                } else {
                    System.err.println("❌ GameController.getInstance() è null!");
                }
            });
        }
    }

    /**
     * Passa al prossimo luogo quando l'attuale è perso
     * Chiamato quando marchi neri >= max
     */
    public void passaAlProssimoLuogo() {
        if (luogoAttuale == null) {
            System.out.println("⚠️ Nessun luogo attuale!");
            return;
        }
        
        System.out.println("\n💀 LUOGO PERSO: " + luogoAttuale.getNome());
        System.out.println("   Marchi Neri: " + luogoAttuale.getNumeroMarchiNeri() + "/" + 
                           luogoAttuale.getMarchiNeriMax());
        
        // Trova l'indice del luogo attuale
        int indiceLuogoAttuale = listaLuoghi.indexOf(luogoAttuale);
        
        if (indiceLuogoAttuale == -1) {
            System.out.println("⚠️ Luogo attuale non trovato nella lista!");
            return;
        }
        
        // Controlla se ci sono altri luoghi
        if (indiceLuogoAttuale < listaLuoghi.size() - 1) {
            // Passa al prossimo luogo
            luogoAttuale = listaLuoghi.get(indiceLuogoAttuale + 1);
            System.out.println("🏰 Nuovo luogo: " + luogoAttuale.getNome());
            System.out.println("   Marchi Neri: " + luogoAttuale.getNumeroMarchiNeri() + "/" + 
                             luogoAttuale.getMarchiNeriMax());
        } else {
            System.out.println("⚠️ Era l'ultimo luogo!");
        }
        
        // Verifica se tutti i luoghi sono persi
        verificaCondizioneSconfitta();
    }

    /**
     * Metodo chiamato quando un malvagio viene sconfitto
     * ⭐ MODIFICATO: Aggiunge verifica vittoria
     */
    public void sconfiggiMalvagio(Malvagio m) {
        System.out.println("💀 " + m.getNome() + " è stato sconfitto!");
        m.defeat(this, giocatori.get(giocatoreCorrente));
        
        malvagiAttivi.remove(m);
        
        gestoreTrigger.attivaTrigger(TipoTrigger.NEMICO_SCONFITTO, this, giocatori.get(giocatoreCorrente));
        
        gestoreTrigger.rimuoviTrigger(m);
        
        // ⭐ Verifica condizione vittoria
        verificaCondizioneVittoria();
    }

    /**
     * Metodo chiamato quando un Horcrux viene distrutto
     * ⭐ MODIFICATO: Aggiunge verifica vittoria
     */
    public void distruggiHorcrux(Horcrux h) {
        System.out.println("💀 Horcrux distrutto: " + h.getNome());
        
        horcruxAttivi.remove(h);
        if (!mazzoHorcrux.isEmpty()) {
            horcruxAttivi.add(mazzoHorcrux.pop());
        }
        
        gestoreTrigger.rimuoviTrigger(h);
        
        // ⭐ Verifica condizione vittoria
        verificaCondizioneVittoria();
    }
    public void incrementaLuoghiConquistati() {
        luoghiConquistati++;
        
        if (luoghiConquistati >= getTotaleLuoghi()) {
            setGameOver(true);
        }
    }
    
 // ============================================
 // TRACKING ATTACCHI AI MALVAGI
 // ============================================

 /**
  * Assegna attacchi a un malvagio
  */
 public void assegnaAttacco(Malvagio malvagio, int quantita) {
     if (malvagio == null || quantita <= 0) return;
     
     int attuali = attacchiAssegnati.getOrDefault(malvagio, 0);
     attacchiAssegnati.put(malvagio, attuali + quantita);
     
     System.out.println("⚔️ Assegnati " + quantita + " attacchi a " + malvagio.getNome() + 
                      " (Totale: " + (attuali + quantita) + ")");
 }

 /**
  * Ottiene la mappa degli attacchi assegnati
  */
 public Map<Malvagio, Integer> getAttacchiAssegnati() {
     return attacchiAssegnati;
 }

 /**
  * Resetta gli attacchi assegnati (chiamato a inizio turno)
  */
 public void resetAttacchi() {
     attacchiAssegnati.clear();
     System.out.println("🔄 Attacchi resettati");
 }

 /**
  * Applica gli attacchi assegnati ai malvagi e li rimuove se sconfitti
  */
 public void applicaAttacchi() {
     List<Malvagio> malvagiDaRimuovere = new ArrayList<>();
     
     for (Map.Entry<Malvagio, Integer> entry : attacchiAssegnati.entrySet()) {
         Malvagio malvagio = entry.getKey();
         int attacchi = entry.getValue();
         
         // Applica danno
         malvagio.setDanno(malvagio.getDanno() + attacchi);
         
         System.out.println("⚔️ " + malvagio.getNome() + " riceve " + attacchi + " danni " +
                          "(Vita: " + malvagio.getVita() + ")");
         
         // Se sconfitto, aggiungilo alla lista di rimozione
         if (malvagio.getDanno() >= malvagio.getVita()) {
             System.out.println("💀 " + malvagio.getNome() + " è stato sconfitto!");
             malvagiDaRimuovere.add(malvagio);
             
             // Trigger NEMICO_SCONFITTO
             gestoreTrigger.attivaTrigger(TipoTrigger.NEMICO_SCONFITTO, this, this.getGiocatori().get(this.getGiocatoreCorrente()));
         }
     }
     
     // Rimuovi i malvagi sconfitti
     for (Malvagio malvagio : malvagiDaRimuovere) {
         malvagiAttivi.remove(malvagio);
     }
     
     // Resetta gli attacchi dopo averli applicati
     resetAttacchi();
 }
    
    public int getTotaleLuoghi() {
        return listaLuoghi.size();
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
    public List<Carta> getAlleatiGiocatiInQuestoTurno() {
        return alleatiGiocatiInQuestoTurno;
    }

	public void setVictory(boolean b) {
		this.victory = b;
	}
	
	public int getLuoghiConquistati() {
		return luoghiConquistati;
	}
    
	// Aggiungi metodo per ottenere un dado specifico
	public Dado getDado(String nome) {
	    return dadi.get(nome);
	}
}
