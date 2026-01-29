package gestoreEffetti;

public enum TipoTrigger {
	NEMICO_SCONFITTO, 
	EROE_ATTIVO,
	AUTO_SCARTO, 
	SCARTA_INCANTESIMO, 
	GIOCA_ALLEATO, 
	ACQUISTA_ALLEATO,
	WEASLEY_IN_MANO, 
	PESCA_CARTA_BONUS,
	GIOCA_OGGETTO,
	SALUTE_MASSIMA,
	RIMOZIONE_MARCHIO_NERO, 
	INCANTESIMI_GIOCATI, 
	GIOCA_INCANTESIMO,
	GUADAGNA_VITA, 
	ATTACCHI_ASSEGNATI,
	STORDIMENTO, 
	AGGIUNTA_MARCHIO_NERO, 
	SCARTA_CARTA_MALUS,
	SCARTA_CARTA_BONUS,
	ASSEGNA_PIU_ATTACCHI, 
	ALLEATO_IN_MANO,
	TUTTE_TIPOLOGIE, 
	GIOCA_TUTTO, 
	ACQUISTA_INCANTESIMO, 
	RIVELA_NEMICO, 
	TENTA_RIMUOVI_MARCHIO_NERO,
	RIVELA_MORSMORDRE_O_MALVAGIO, 
	COSTO_MINORE_1,
	DADO_TIRATO,
	ACQUISTA_CARTA_UMBRIDGE,
	ATTACCA_VOLDY,
	INIZIO_TURNO_GENERALE,
	ASSEGNA_SEGNALINO_CREATURE,
	GUADAGNA_VITA_QUORUM,
	RICEVI_DANNO,
	TENTA_GUADAGNA_VITA,
	
	// ===== ESPANSIONE CHARMS & POTIONS =====
	
	// --- POZIONI (Pack 2+) ---
	/** Trigger quando si brewa una pozione */
	BREW_POZIONE,
	/** Trigger quando si raccolgono ingredienti */
	RACCOLTA_INGREDIENTI,
	/** Trigger quando si usa effetto banish pozione */
	BANISH_POZIONE,
	
	// --- DARK ARTS POTIONS (Pack 3+) ---
	/** Trigger quando si pesca Dark Arts Potion */
	PESCA_DARK_ARTS_POTION,
	/** Trigger quando si brewa Dark Arts Potion */
	BREW_DARK_ARTS_POTION,
	/** Trigger all'inizio turno con Dark Arts Potion attiva */
	INIZIO_TURNO_DARK_ARTS,
	
	// --- ENCOUNTER (Pack 1-4) ---
	/** Trigger quando si completa un Encounter */
	COMPLETA_ENCOUNTER,
	/** Trigger quando si giocano carte specifiche per Encounter */
	GIOCA_CARTE_TIPO,
	/** Trigger quando si guadagna influenza per Encounter */
	ACQUISTA_INFLUENZA,
	/** Trigger quando si giocano carte con valore specifico */
	GIOCA_CARTE_VALORE,
	/** Trigger quando si assegna a Voldemort */
	ASSEGNA_A_VOLDEMORT,
	/** Trigger quando si distrugge Horcrux */
	DISTRUGGI_HORCRUX,
	/** Trigger quando si sconfiggono malvagi multipli */
	SCONFIGGI_MALVAGI,
	/** Trigger quando si sconfigge creatura */
	SCONFIGGI_CREATURA;
}
