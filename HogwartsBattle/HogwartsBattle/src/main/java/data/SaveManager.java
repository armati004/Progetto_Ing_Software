package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import carte.Carta;
import gioco.Giocatore;
import gioco.StatoDiGioco;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manager per il salvataggio e caricamento delle partite
 */
public class SaveManager {
    
    private static final String SAVE_DIRECTORY = "saves";
    private static final String SAVE_FILE_EXTENSION = ".json";
    private static final String DEFAULT_SAVE_NAME = "autosave";
    
    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
    
    /**
     * Salva lo stato corrente del gioco
     * 
     * @param stato Stato di gioco da salvare
     * @param nomeSalvataggio Nome del file di salvataggio (senza estensione)
     * @return true se salvato con successo
     */
    public static boolean salvaPartita(StatoDiGioco stato, String nomeSalvataggio) {
        try {
            createSaveDirectory();
            
            // ⭐ NUOVO: Se non è autosave, genera nome unico
            String nomeFinal = nomeSalvataggio;
            if (!nomeSalvataggio.equals(DEFAULT_SAVE_NAME)) {
                nomeFinal = generaNomeUnico(nomeSalvataggio);
            }
            
            // Estrai dati giocatori
            List<PlayerSaveData> playerData = new ArrayList<>();
            for (Giocatore g : stato.getGiocatori()) {
                String nomeEroe = g.getEroe().getNome();
                String idCompetenza = g.getCompetenza() != null ? g.getCompetenza().getId() : null;
                
                // ⭐ NUOVO: Salva carte acquisite
                List<String> carteIds = new ArrayList<>();
                carteIds.addAll(getCarteIds(g.getMazzo().getCarte()));
                carteIds.addAll(getCarteIds(g.getScarti().getCarte()));
                carteIds.addAll(getCarteIds(g.getMano()));
                
                PlayerSaveData psd = new PlayerSaveData(nomeEroe, idCompetenza, carteIds);
                psd.setCarteNelMazzo(carteIds); // ⭐ Salva IDs
                playerData.add(psd);
            }
            
            // ⭐ NUOVO: Aggiungi carte acquisite globali
            List<String> carteNegozio = new ArrayList<>();
            if (stato.getMazzoNegozio() != null) {
                carteNegozio.addAll(getCarteIds(new ArrayList<>(stato.getMazzoNegozio())));
            }
            if (stato.getMercato() != null) {
                carteNegozio.addAll(getCarteIds(stato.getMercato()));
            }

            //saveData.setCarteNegozioRimaste(carteNegozio);
            System.out.println("💾 Salvate " + carteNegozio.size() + " carte del negozio");
            
            // Crea oggetto salvataggio
            GameSaveData saveData = new GameSaveData(
                stato.getAnnoCorrente(),
                stato.getGiocatori().size(),
                playerData,
                stato.getGiocatoreCorrente(),
                stato.isVictory(),
                nomeFinal,
                new ArrayList<>(carteNegozio) // ⭐ NUOVO parametro
            );
            
            // Serializza e salva
            String json = gson.toJson(saveData);
            Path filePath = Paths.get(SAVE_DIRECTORY, nomeFinal + SAVE_FILE_EXTENSION);
            Files.writeString(filePath, json);
            
            System.out.println("💾 Partita salvata: " + nomeFinal);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Errore salvataggio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ⭐ NUOVO METODO: Genera nome unico per salvataggio
     */
    private static String generaNomeUnico(String nomeBase) {
        Path filePath = Paths.get(SAVE_DIRECTORY, nomeBase + SAVE_FILE_EXTENSION);
        
        // Se file non esiste, usa nome base
        if (!Files.exists(filePath)) {
            return nomeBase;
        }
        
        // Altrimenti aggiungi numero progressivo
        int counter = 1;
        String nomeNuovo;
        do {
            nomeNuovo = nomeBase + "_" + counter;
            filePath = Paths.get(SAVE_DIRECTORY, nomeNuovo + SAVE_FILE_EXTENSION);
            counter++;
        } while (Files.exists(filePath));
        
        return nomeNuovo;
    }

    /**
     * ⭐ HELPER: Estrae IDs da lista carte
     */
    private static List<String> getCarteIds(List<Carta> carte) {
        List<String> ids = new ArrayList<>();
        for (Carta c : carte) {
            if (c != null && c.getId() != null) {
                ids.add(c.getId());
            }
        }
        return ids;
    }
    
    /**
     * Carica una partita salvata
     * 
     * @param nomeSalvataggio Nome del file di salvataggio (senza estensione)
     * @return GameSaveData caricato, o null se errore
     */
    public static GameSaveData caricaPartita(String nomeSalvataggio) {
        try {
            String filename = SAVE_DIRECTORY + "/" + nomeSalvataggio + SAVE_FILE_EXTENSION;
            File file = new File(filename);
            
            if (!file.exists()) {
                System.err.println("❌ File di salvataggio non trovato: " + filename);
                return null;
            }
            
            // Leggi JSON
            String json = new String(Files.readAllBytes(Paths.get(filename)));
            
            // Deserializza
            GameSaveData saveData = gson.fromJson(json, GameSaveData.class);
            
            System.out.println("📂 Partita caricata: " + filename);
            System.out.println("   Anno: " + saveData.getAnnoCorrente());
            System.out.println("   Giocatori: " + saveData.getNumeroGiocatori());
            System.out.println("   Data salvataggio: " + saveData.getDataOra());
            
            return saveData;
            
        } catch (IOException e) {
            System.err.println("❌ Errore nel caricamento: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lista tutti i salvataggi disponibili
     * 
     * @return Lista dei nomi dei salvataggi (senza estensione)
     */
    public static List<String> listaSalvataggi() {
        try {
            createSaveDirectory();
            
            File saveDir = new File(SAVE_DIRECTORY);
            File[] files = saveDir.listFiles((dir, name) -> name.endsWith(SAVE_FILE_EXTENSION));
            
            if (files == null || files.length == 0) {
                return new ArrayList<>();
            }
            
            List<String> saves = new ArrayList<>();
            for (File file : files) {
                String name = file.getName();
                // Rimuovi estensione
                saves.add(name.substring(0, name.length() - SAVE_FILE_EXTENSION.length()));
            }
            
            return saves;
            
        } catch (Exception e) {
            System.err.println("❌ Errore nella lista salvataggi: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Elimina un salvataggio
     * 
     * @param nomeSalvataggio Nome del file da eliminare
     * @return true se eliminato con successo
     */
    public static boolean eliminaSalvataggio(String nomeSalvataggio) {
        try {
            String filename = SAVE_DIRECTORY + "/" + nomeSalvataggio + SAVE_FILE_EXTENSION;
            File file = new File(filename);
            
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("🗑️ Salvataggio eliminato: " + filename);
                }
                return deleted;
            } else {
                System.err.println("❌ File non trovato: " + filename);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Errore nell'eliminazione: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Salvataggio automatico
     */
    public static boolean autosave(StatoDiGioco stato) {
        return salvaPartita(stato, DEFAULT_SAVE_NAME);
    }
    
    /**
     * Carica salvataggio automatico
     */
    public static GameSaveData caricaAutosave() {
        return caricaPartita(DEFAULT_SAVE_NAME);
    }
    
    /**
     * Verifica se esiste un salvataggio automatico
     */
    public static boolean esisteAutosave() {
        String filename = SAVE_DIRECTORY + "/" + DEFAULT_SAVE_NAME + SAVE_FILE_EXTENSION;
        return new File(filename).exists();
    }
    
    /**
     * Crea la directory saves se non esiste
     */
    private static void createSaveDirectory() {
        File saveDir = new File(SAVE_DIRECTORY);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
            System.out.println("📁 Creata directory: " + SAVE_DIRECTORY);
        }
    }
    
    /**
     * Ottiene informazioni su un salvataggio senza caricarlo completamente
     */
    public static GameSaveData getInfoSalvataggio(String nomeSalvataggio) {
        return caricaPartita(nomeSalvataggio);
    }
}