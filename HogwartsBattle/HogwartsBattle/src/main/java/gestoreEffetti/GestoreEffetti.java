package gestoreEffetti;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import carte.Carta;

/**
 * Gestisce gli effetti attivi nel gioco.
 * Tiene traccia di quali effetti sono attualmente applicati e da quali carte.
 * 
 * Supporta effetti:
 * - Permanenti (finché la carta è in gioco)
 * - Temporanei (fino a fine turno)
 */
public class GestoreEffetti {
    private final Map<TipoEffetto, Set<Carta>> effettiAttivi = new EnumMap<>(TipoEffetto.class);
    private final Set<Carta> carteTemporanee = new HashSet<>();
    
    /**
     * Aggiunge un effetto permanente al gestore.
     * L'effetto rimane attivo finché la carta non viene rimossa.
     * 
     * @param effetto Tipo di effetto da aggiungere
     * @param carta Carta che applica l'effetto
     */
    public void aggiungiEffetto(TipoEffetto effetto, Carta carta) {
        this.effettiAttivi.computeIfAbsent(effetto, _ -> new HashSet<>()).add(carta);
    }
    
    /**
     * Aggiunge un effetto temporaneo al gestore.
     * L'effetto viene rimosso automaticamente a fine turno.
     * 
     * @param effetto Tipo di effetto da aggiungere
     * @param carta Carta che applica l'effetto
     */
    public void aggiungiEffettoTemporaneo(TipoEffetto effetto, Carta carta) {
        this.aggiungiEffetto(effetto, carta);
        this.carteTemporanee.add(carta);
    }
    
    /**
     * Rimuove tutti gli effetti applicati da una carta specifica.
     * 
     * @param carta Carta i cui effetti devono essere rimossi
     */
    public void rimuoviEffetto(Carta carta) {
        for (Set<Carta> carte : this.effettiAttivi.values()) {
            carte.remove(carta);
        }
        this.carteTemporanee.remove(carta);
    }
    
    /**
     * Pulisce gli effetti temporanei a fine turno.
     * Chiamato automaticamente dal TurnManager.
     */
    public void fineTurno() {
        for (Carta cartaTemporanea : carteTemporanee) {
            this.rimuoviEffetto(cartaTemporanea);
        }
        
        this.carteTemporanee.clear();
    }
    
    /**
     * Verifica se una regola/effetto è attualmente attiva.
     * 
     * @param effetto Tipo di effetto da verificare
     * @return true se almeno una carta sta applicando questo effetto
     */
    public Boolean regolaAttiva(TipoEffetto effetto) {
        return this.effettiAttivi.containsKey(effetto) && !this.effettiAttivi.get(effetto).isEmpty();
    }
    
    /**
     * Ottiene tutte le carte che stanno applicando un certo effetto.
     * 
     * @param effetto Tipo di effetto
     * @return Set di carte che applicano l'effetto (può essere vuoto)
     */
    public Set<Carta> getCarteConEffetto(TipoEffetto effetto) {
        return this.effettiAttivi.getOrDefault(effetto, new HashSet<>());
    }
    
    /**
     * Pulisce completamente tutti gli effetti.
     * Usato per reset del gioco o situazioni speciali.
     */
    public void pulisciTutto() {
        this.effettiAttivi.clear();
        this.carteTemporanee.clear();
    }
}
