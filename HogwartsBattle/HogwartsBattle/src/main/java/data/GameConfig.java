package data;

import java.util.ArrayList;
import java.util.List;

import carte.Eroe;
import carte.Luogo;

/**
 * GameConfig - Configurazione di gioco per un anno specifico
 * 
 * ADATTATO: Supporto versioni eroi basate sull'anno
 * - Anno 1-2: Eroi versione base (gioco1)
 * - Anno 3-6: Eroi versione intermedia (gioco3)
 * - Anno 7: Eroi versione finale (gioco7)
 */
public class GameConfig {
    private int anno;
    private List<String> carteNegozioId = new ArrayList<>();
    private List<String> malvagiId = new ArrayList<>();
    private List<String> artiOscureId = new ArrayList<>();
    private List<String> luoghiId = new ArrayList<>();
    private List<String> horcruxId = new ArrayList<>();
    
    // Lista ID eroi disponibili per questo anno
    private List<String> eroiDisponibiliId = new ArrayList<>();
    
    // Numero giocatori per questo anno
    private int numeroGiocatori = 4;  // Default 4 giocatori
    
    private Boolean contieneDadi = false;
    private Boolean contieneCompetenze = false;
    private Boolean contieneHorcrux = false;
    
    public void aggiornaMeccaniche(Meccanica m) {
        if(m.getContieneDadi() == true) {
            this.contieneDadi = true;
        }
        if(m.getContieneCompetenze() == true) {
            this.contieneCompetenze = true;
        }
        if(m.getContieneHorcrux() == true) {
            this.contieneHorcrux = true;
        }
    }
    
    /**
     * Crea la lista di luoghi per un anno specifico.
     * I luoghi sono già ordinati nel JSON.
     */
    public static List<Luogo> caricaLuoghiPerAnno(List<String> idLuoghi) {
        List<Luogo> luoghi = new ArrayList<>();
        
        for (String id : idLuoghi) {
            try {
                Luogo luogo = LocationFactory.creaLuogo(id);
                luoghi.add(luogo);
                
            } catch (IllegalArgumentException e) {
                System.err.println("Luogo non trovato: " + id);
            }
        }
        
        return luoghi;
    }
    
    // ============================================
    // METODI EROI CON SUPPORTO VERSIONI
    // ============================================
    
    /**
     * Ottiene la lista di eroi disponibili per questo anno
     * FIX: Passa l'anno corrente a HeroFactory per ottenere la versione corretta
     */
    public List<Eroe> getEroiDisponibili() {
        List<Eroe> eroi = new ArrayList<>();
        
        // Se non ci sono ID specificati, carica tutti gli eroi di base
        if (eroiDisponibiliId.isEmpty()) {
            System.out.println("Nessun eroe specificato nel JSON, carico i 4 eroi di base per anno " + anno);
            
            // Carica i 4 eroi principali CON L'ANNO CORRETTO
            try {
                eroi.add(HeroFactory.creaEroe("Harry Potter", anno));
                eroi.add(HeroFactory.creaEroe("Hermione Granger", anno));
                eroi.add(HeroFactory.creaEroe("Ron Weasley", anno));
                eroi.add(HeroFactory.creaEroe("Neville Longbottom", anno));
                
                System.out.println("Caricati 4 eroi di base (versione anno " + anno + ")");
                
            } catch (Exception e) {
                System.err.println("Errore caricamento eroi di default: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Carico " + eroiDisponibiliId.size() + " eroi specificati per anno " + anno);
            
            // Carica eroi specificati CON L'ANNO CORRETTO
            for (String eroId : eroiDisponibiliId) {
                try {
                    // Converte ID in nome per HeroFactory
                    String nomeEroe = convertiIdInNome(eroId);
                    Eroe eroe = HeroFactory.creaEroe(nomeEroe, anno);
                    eroi.add(eroe);
                    
                    System.out.println("Caricato: " + nomeEroe + " (anno " + anno + ")");
                    
                } catch (IllegalArgumentException e) {
                    System.err.println("Eroe non trovato: " + eroId);
                } catch (Exception e) {
                    System.err.println("Errore caricamento eroe " + eroId + ": " + e.getMessage());
                }
            }
        }
        
        return eroi;
    }
    
    /**
     * Converte un ID eroe (snake_case o camelCase) in nome completo
     * 
     * Esempi:
     * "harry_potter" in "Harry Potter"
     * "harryPotter" in "Harry Potter"
     * "hermione_granger" in "Hermione Granger"
     */
    private String convertiIdInNome(String id) {
        // Rimuovi underscore e sostituisci con spazio
        String nome = id.replace("_", " ");
        
        String[] parole = nome.split(" ");
        StringBuilder nomeFormattato = new StringBuilder();
        
        for (String parola : parole) {
            if (parola.length() > 0) {
                nomeFormattato.append(Character.toUpperCase(parola.charAt(0)))
                             .append(parola.substring(1).toLowerCase())
                             .append(" ");
            }
        }
        
        return nomeFormattato.toString().trim();
    }
    
    /**
     * Ottiene il numero di giocatori per questo anno
     */
    public int getNumeroGiocatori() {
        return numeroGiocatori;
    }
    
    /**
     * Imposta il numero di giocatori
     */
    public void setNumeroGiocatori(int numeroGiocatori) {
        this.numeroGiocatori = numeroGiocatori;
    }
    
    /**
     * Imposta gli ID degli eroi disponibili
     */
    public void setEroiDisponibiliId(List<String> eroiDisponibiliId) {
        this.eroiDisponibiliId = eroiDisponibiliId;
    }
    
    /**
     * Ottiene gli ID degli eroi disponibili
     */
    public List<String> getEroiDisponibiliId() {
        return eroiDisponibiliId;
    }
    
    
    // GETTERS E SETTERS
    
    
    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
        System.out.println("📅 Anno impostato: " + anno);
    }

    public List<String> getCarteNegozioId() {
        return carteNegozioId;
    }

    public List<String> getMalvagiId() {
        return malvagiId;
    }

    public List<String> getArtiOscureId() {
        return artiOscureId;
    }

    public List<String> getLuoghiId() {
        return luoghiId;
    }

    public void setLuoghiId(List<String> luoghiId) {
        this.luoghiId = luoghiId;
    }

    public List<String> getHorcruxId() {
		return horcruxId;
	}

	public void setHorcruxId(List<String> horcruxId) {
		this.horcruxId = horcruxId;
	}

	public Boolean getContieneDadi() {
        return contieneDadi;
    }

    public Boolean getContieneCompetenze() {
        return contieneCompetenze;
    }

    public Boolean getContieneHorcrux() {
        return contieneHorcrux;
    }
}