package gestoreEffetti;

public enum TipoTrigger {
	/**
	 * Trigger che viene attivato quando un malvagio viene sconfitto
	 */
	NEMICO_SCONFITTO, 
	/**
	 * Trigger che viene attivato quando si scarta una carta che
	 * ha uno o più effetti quando viene scartata 
	 */
	AUTO_SCARTO, 
	/**
	 * Trigger della carta Legilmanzia che, in caso la carta in cima al mazzo sia
	 * un incantesimo, la fa scartare e fa perdere 2 vite 
	 */
	SCARTA_INCANTESIMO, 
	/**
	 * Attiva un effetto aggiuntivo in caso si giochi un altro alleato
	 */
	GIOCA_ALLEATO, 
	/**
	 * Trigger delle carte Fred e George Weasley che attivano
	 * un effetto aggiuntivo nel caso in cui un altro giocatore abbia
	 * un Weasley in mano
	 */
	WEASLEY_IN_MANO, 
	/**
	 * Attiva un effetto aggiuntivo nel caso si giochi un altro oggetto
	 */
	GIOCA_OGGETTO,
	/**
	 * Trigger della carta Pozioni avanzate che permette agli eroi
	 * con vita massima di guadagnare un segnalino attacco e di pescare una carta
	 */
	SALUTE_MASSIMA,
	/**
	 * Trigger della carta eroe Harry che si attiva quando si rimuove
	 * per la prima volta un marchio nero dal luogo
	 */
	RIMOZIONE_MARCHIO_NERO, 
	/**
	 * Trigger della carta eroe Hermione che si attiva quando si giocano 4
	 * o più incantesimi
	 */
	INCANTESIMI_GIOCATI, 
	/**
	 * Trigger della carta eroe Neville che si attiva quando si guadagnano
	 * vite durante il turno del giocatore che ha Neville come eroe
	 */
	GUADAGNA_VITA, 
	/**
	 * Trigger della carta eroe Ron che si attiva quando si assegnano più
	 * di 3 attacchi ai malvagi
	 */
	ATTACCHI_ASSEGNATI,
	/**
	 * Trigger della carta arti oscure Avada Kedavra che fa aggiungere
	 * un ulteriore marchio nero al luogo in caso l'eroe attivo venga
	 * stordito
	 */
	STORDIMENTO, 
	/**
	 * Trigger delle carte malvagio Draco e Lucius Malfoy che provocano
	 * effetti negativi quando viene aggiunto un marchio nero al luogo
	 */
	AGGIUNTA_MARCHIO_NERO, 
	/**
	 * Trigger della carta malvagio Tiger e Goyle, che si attiva quando
	 * un giocatore è costretto a scartare una carta
	 */
	SCARTA_CARTA_MALUS,
	/**
	 * Trigger della carta competenza Difesa contro le arti oscure,
	 * che si distingue da SCARTA_CARTA_MALUS per il fatto che 
	 * questo trigger comporta un bonus.
	 */
	SCARTA_CARTA_BONUS,
	/**
	 * Trigger della carta Horcrux Anello, che fa perdere vite quando
	 * si assegnano più attacchi a un singolo malvagio
	 */
	ASSEGNA_PIU_ATTACCHI, 
	/**
	 * Trigger della carta Horcrux Diadema, che fa perdere due vite quando
	 * all'inizio del turno sono presenti in mano almeno un alleato, un oggetto
	 * e un incantesimo
	 */
	TUTTE_TIPOLOGIE, 
	/**
	 * Trigger della carta competenza Pozioni che si attiva
	 * quando si gioca almeno un alleato, un incantesimo e un oggetto
	 */
	GIOCA_TUTTO, 
	/**
	 * Trigger della carta competenza Storia della magia 
	 * che si attiva ogni volta che si acquista un incantesimo
	 */
	ACQUISTA_INCANTESIMO, 
	/**
	 * Trigger della carta malvagio Mangiamorte che si attiva
	 * quando viene rivelato un nuovo malvagio mentre questa carta
	 * è in gioco
	 */
	RIVELA_NEMICO, 
	/**
	 * Trigger della carta malvagio Mangiamorte che si attiva
	 * quando viene rivelato l'evento arti oscure Morsmordre
	 */
	RIVELA_MORSMORDRE, 
	/**
	 * Trigger della carta competenza Artimanzia che si attiva
	 * quando si acquista una carta che ha come effetto tirare
	 * il dado casata
	 */
	COSTO_MINORE_1;
}
