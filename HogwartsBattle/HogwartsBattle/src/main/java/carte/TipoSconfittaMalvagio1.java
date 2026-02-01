package carte;

/**
 * Enum che definisce il tipo di sconfitta richiesta per un Malvagio.
 * Usato nell'espansione Charms & Potions per supportare malvagi
 * che possono essere sconfitti in modi diversi.
 */
public enum TipoSconfittaMalvagio1 {
    SOLO_ATTACCO,        // Sconfitto solo con Attacco
    SOLO_INFLUENZA,      // Sconfitto solo con Influenza
    ATTACCO_O_INFLUENZA  // Sconfitto con Attacco oppure Influenza
}
