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
    private List<String> encounterId = new ArrayList<>();
    private List<String> luoghiId = new ArrayList<>();
    
    // NUOVO: Pozioni (Pack 2+)
    private List<String> pozioniId = new ArrayList<>();
    private String potionShelfSide;  // "A" o "B"
    
    // NUOVO: Dark Arts Potions (Pack 3+)
    private List<String> darkArtsPotionId = new ArrayList<>();
    
    // NUOVO: Competenze (Pack 2+)
    private List<String> competenzeId = new ArrayList<>();
    
    // NUOVO: Horcrux (Gioco 7)
    private List<String> horcruxId = new ArrayList<>();
    
    // âœ… Lista ID eroi disponibili per questo anno
    private List<String> eroiDisponibiliId = new ArrayList<>();
    
    // âœ… Numero giocatori per questo anno
    private int numeroGiocatori = 4;  // Default 4 giocatori
    
    private Boolean contieneDadi = false;
    private Boolean contieneCompetenze = false;
    private Boolean contieneHorcrux = false;
    private Boolean contieneEncounter = false;
    private Boolean contienePozioni = false;
    private Boolean contieneDarkArtsPozioni = false;
    
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
        if(m.getContieneEncounter() == true) {
            this.contieneEncounter = true;
        }
        if(m.getContienePozioni() == true) {
            this.contienePozioni = true;
        }
        if(m.getContieneDarkArtsPozioni() == true) {
            this.contieneDarkArtsPozioni = true;
        }
    }
    
    /**
     * Crea la lista di luoghi per un anno specifico.
     * I luoghi sono giÃ  ordinati nel JSON.
     */
    public static List<Luogo> caricaLuoghiPerAnno(List<String> idLuoghi) {
        List<Luogo> luoghi = new ArrayList<>();
        
        for (String id : idLuoghi) {
            try {
                Luogo luogo = LocationFactory.creaLuogo(id);
                luoghi.add(luogo);
                
            } catch (IllegalArgumentException e) {
                System.err.println("âš ï¸ Luogo non trovato: " + id);
            }
        }
        
        return luoghi;
    }
    
    // ============================================
    // METODI EROI CON SUPPORTO VERSIONI
    // ============================================
    
    /**
     * Ottiene la lista di eroi disponibili per questo anno
     * âœ… AGGIORNATO: Passa l'anno corrente a HeroFactory per ottenere la versione corretta
     */
    public List<Eroe> getEroiDisponibili() {
        List<Eroe> eroi = new ArrayList<>();
        
        // Se non ci sono ID specificati, carica tutti gli eroi di base
        if (eroiDisponibiliId.isEmpty()) {
            System.out.println("ðŸ“‹ Nessun eroe specificato nel JSON, carico i 4 eroi di base per anno " + anno);
            
            // Carica i 4 eroi principali CON L'ANNO CORRETTO
            try {
                eroi.add(HeroFactory.creaEroe("Harry Potter", anno));        // âœ… Con anno
                eroi.add(HeroFactory.creaEroe("Hermione Granger", anno));    // âœ… Con anno
                eroi.add(HeroFactory.creaEroe("Ron Weasley", anno));         // âœ… Con anno
                eroi.add(HeroFactory.creaEroe("Neville Longbottom", anno));  // âœ… Con anno
                
                System.out.println("âœ“ Caricati 4 eroi di base (versione anno " + anno + ")");
                
            } catch (Exception e) {
                System.err.println("âŒ Errore caricamento eroi di default: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("ðŸ“‹ Carico " + eroiDisponibiliId.size() + " eroi specificati per anno " + anno);
            
            // Carica eroi specificati CON L'ANNO CORRETTO
            for (String eroId : eroiDisponibiliId) {
                try {
                    // âœ… IMPORTANTE: Converte ID in nome per HeroFactory
                    String nomeEroe = convertiIdInNome(eroId);
                    Eroe eroe = HeroFactory.creaEroe(nomeEroe, anno);  // âœ… Con anno
                    eroi.add(eroe);
                    
                    System.out.println("  âœ“ Caricato: " + nomeEroe + " (anno " + anno + ")");
                    
                } catch (IllegalArgumentException e) {
                    System.err.println("  âš ï¸ Eroe non trovato: " + eroId);
                } catch (Exception e) {
                    System.err.println("  âŒ Errore caricamento eroe " + eroId + ": " + e.getMessage());
                }
            }
        }
        
        return eroi;
    }
    
    /**
     * Converte un ID eroe (snake_case o camelCase) in nome completo
     * 
     * Esempi:
     * - "harry_potter" â†’ "Harry Potter"
     * - "harryPotter" â†’ "Harry Potter"
     * - "hermione_granger" â†’ "Hermione Granger"
     */
    private String convertiIdInNome(String id) {
        // Rimuovi underscore e sostituisci con spazio
        String nome = id.replace("_", " ");
        
        // Capitalizza ogni parola
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
    
    // ============================================
    // GETTERS E SETTERS ESISTENTI
    // ============================================
    
    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
        System.out.println("ðŸ“… Anno impostato: " + anno);
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

    public Boolean getContieneDadi() {
        return contieneDadi;
    }

    public Boolean getContieneCompetenze() {
        return contieneCompetenze;
    }

    public Boolean getContieneHorcrux() {
        return contieneHorcrux;
    }

    public List<String> getEncounterId() {
        return encounterId;
    }

    public Boolean getContieneEncounter() {
        return contieneEncounter;
    }
    
    // ============================================
    // GETTER NUOVI CAMPI ESPANSIONE
    // ============================================
    
    public List<String> getPozioniId() {
        return pozioniId;
    }
    
    public void setPozioniId(List<String> pozioniId) {
        this.pozioniId = pozioniId;
    }
    
    public String getPotionShelfSide() {
        return potionShelfSide;
    }
    
    public void setPotionShelfSide(String potionShelfSide) {
        this.potionShelfSide = potionShelfSide;
    }
    
    public List<String> getDarkArtsPotionId() {
        return darkArtsPotionId;
    }
    
    public void setDarkArtsPotionId(List<String> darkArtsPotionId) {
        this.darkArtsPotionId = darkArtsPotionId;
    }
    
    public List<String> getCompetenzeId() {
        return competenzeId;
    }
    
    public void setCompetenzeId(List<String> competenzeId) {
        this.competenzeId = competenzeId;
    }
    
    public List<String> getHorcruxId() {
        return horcruxId;
    }
    
    public void setHorcruxId(List<String> horcruxId) {
        this.horcruxId = horcruxId;
    }
    
    public Boolean getContienePozioni() {
        return contienePozioni;
    }
    
    public Boolean getContieneDarkArtsPozioni1() {
        return contieneDarkArtsPozioni;
    }

    public void setContieneDarkArtsPozioni1(boolean contieneDarkArtsPozioni) {
        this.contieneDarkArtsPozioni = contieneDarkArtsPozioni;
    }

    public void setContieneEncounter1(boolean contieneEncounter) {
        this.contieneEncounter = contieneEncounter;

    }
    
    public Boolean getContieneDarkArtsPozioni() {
        return contieneDarkArtsPozioni;
    }

    public void setContieneDarkArtsPozioni11(boolean contieneDarkArtsPozioni) {
        this.contieneDarkArtsPozioni = contieneDarkArtsPozioni;
    }

    public void setContieneEncounter(boolean contieneEncounter) {
        this.contieneEncounter = contieneEncounter;
    }
    
    public void setContieneDarkArtsPozioni(boolean contieneDarkArtsPozioni) {
        this.contieneDarkArtsPozioni = contieneDarkArtsPozioni;
    }

	public void setContienePozioni(boolean b) {
		// TODO Auto-generated method stub
		
	}
}
