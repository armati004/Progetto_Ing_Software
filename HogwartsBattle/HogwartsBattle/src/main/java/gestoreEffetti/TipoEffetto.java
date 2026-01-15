package gestoreEffetti;

public enum TipoEffetto {
	/**
	 * Effetto che fa perdere una o più vite
	 */
	PERDERE_VITA, 
	/**
	 * Effetto che si attiva per limitare il danno subito
	 */
	LIMITARE_DANNO,
	/**
	 * Effetto che fa guadagnare una o più vite
	 */
	GUADAGNARE_VITA, 
	/**
	 * Effetto che fa aggiungere uno o più marchi neri al luogo
	 */
	AGGIUNGERE_MARCHIO_NERO, 
	/**
	 * Effetto che fa rimuovere uno o più marchi neri dal luogo
	 */
	RIMUOVERE_MARCHIO_NERO,
	/**
	 * Effetto che costringe a scartare una o più carte
	 */
	SCARTARE_CARTA, 
	/**
	 * Effetto che cura i malvagi
	 */
	CURARE_MALVAGI,
	/**
	 * Effetto che permette di pescare una o più carte
	 */
	PESCARE_CARTA, 
	/**
	 * Effetto che fa guadagnare uno o più gettoni
	 */
	GUADAGNARE_INFLUENZA, 
	/**
	 * Effetto che fa guadagnare uno o più segnalini attacco
	 */
	GUADAGNARE_ATTACCO, 
	/**
	 * Effetto che fa rimuovere uno o più segnalini attacco 
	 * da uno o più malvagi 
	 */
	RIMUOVERE_ATTACCO, 
	/**
	 * Effetto che contiene delle opzioni tra cui scegliere
	 */
	SCELTA,
	DADO_TASSOROSSO, DADO_GRIFONDORO, DADO_CORVONERO, DADO_SERPEVERDE, 
	/**
	 * Tira il dado di Serpeverde e, in base al risultato,
	 * viene applicato un effetto negativo
	 */
	DADO_MALVAGIO,
	/**
	 * Effetto relativo alla carta competenza Artimanzia che riduce il costo
	 * delle carte che hanno come effetto tirare un dado casata. 
	 */
	SCONTO_ACQUISTO_CASATA,
	/**
	 * Permette di tirare il dado due volte.
	 */
	RITIRA_DADO,
	/**
	 * Effetto della carta Cappello Parlante 
	 * che permette di mettere gli alleati acquistati in cima al mazzo
	 */
	ALLEATO_IN_MAZZO, 
	/**
	 * Effetto della carta Giratempo dello starter pack di Hermione,
	 * che permette di mettere gli incantesimi acquistati in cima al mazzo
	 */
	INCANTESIMO_IN_MAZZO,
	/**
	 * Effetto della carta Wingardium Leviosa
	 * che permette di mettere gli oggetti acquistati in cima al mazzo
	 */
	OGGETTO_IN_MAZZO,
	/**
	 * Effetto della carta pozione polisucco, che copia gli effetti di un alleato
	 * giocato in quel turno.
	 */
	COPIA_EFFETTO,
	/**
	 * Effetto della carta malvagio basilisco e della carta arti oscure pietrificazione 
	 * che proibisce di pescare carte extra
	 */
	NON_PESCARE_CARTE,
	/**
	 * Effetto della carta malvagio Fenrir Greyback e della carta arti oscure Sectumsempra
	 * che impedisce di guadagnare vite
	 */
	NON_GUADAGNARE_VITA,
	/**
	 * Effetto della carta malvagio Barty Crouch JR
	 * che impedisce di rimuovere marchi neri dal luogo
	 */
	NON_RIMUOVERE_MARCHI,
	/**
	 * Effetto della carta Horcrux medaglione
	 * che impedisce di guadagnare segnalini attacco
	 * ai giocatori che NON sono l'eroe attivo
	 */
	NON_GUADAGNARE_ATTACCHI,
	/**
	 * Effetto della carta Horcrux medaglione
	 * che impedisce di guadagnare gettoni (segnalini influenza)
	 * ai giocatori che NON sono l'eroe attivo
	 */
	NON_GUADAGNARE_GETTONI, 
	/**
	 * Effetto che obbliga i giocatori a scartare una carta
	 * di tipo incantesimo
	 */
	SCARTA_INCANTESIMO, 
	/**
	 * Effetto che obbliga i giocatori a scartare una carta
	 * di tipo oggetto
	 */
	SCARTA_OGGETTO, 
	/**
	 * Effetto che obbliga i giocatori a scartare una carta
	 * di tipo alleato
	 */
	SCARTA_ALLEATO, 
	/**
	 * Effetto che costringe il giocatore a rivelare la propria carta
	 * in cima al mazzo. Se questa ha un valore superiore a quello indicato
	 * dall'effetto, viene scartata e si attiva un altro effetto a seconda della carta
	 * che implementa questo effetto
	 */
	RIVELA_CARTA,
	/**
	 * Effetto della carta malvagio Bellatrix Lestrange
	 * e delle carte arti oscure Avada Kedavra, Imperio e Crucio,
	 * che fanno rivelare un evento arti oscure aggiuntivo
	 */
	RIVELA_NUOVO_EVENTO, 
	/**
	 * Effetto della carta malvagio Dolores Umbridge
	 * che fa perdere vite in caso di acquisto di una carta
	 * con un costo di 4 o più.
	 */
	ACQUISTO_CARTA, 
	/**
	 * Effetto sconfitta di un nemico che permette di cercare
	 * nella pila degli scarti un alleato e metterlo in mano
	 */
	CERCA_ALLEATO, 
	/**
	 * Effetto sconfitta di un nemico che permette di cercare
	 * nella pila degli scarti un incantesimo e metterlo in mano
	 */
	CERCA_INCANTESIMO,
	/**
	 * Effetto che blocca le abilità di un malvagio
	 * legato all'effetto del petrificus
	 */
	BLOCCA_ABILITA_MALVAGIO,
	/**
	 * Effetto sconfitta di un nemico che permette di cercare
	 * nella pila degli scarti un oggetto e metterlo in mano
	 */
	CERCA_OGGETTO, 
	/**
	 * Effetto sconfitta di Voldemort che fa vincere gli eroi
	 * quando viene sconfitto
	 */
	VITTORIA, 
	/**
	 * Effetto che impedisce di attaccare Voldemort quando
	 * sono presenti altri malvagi
	 */
	NON_ATTACCARE_VOLDY,
	/**
	 * Effetto della carta malvagio Tom Riddle, 
	 * che conta quanti alleati ci sono in mano e,
	 * se il numero è diverso da 0, attiva la scelta
	 * in base al numero di alleati
	 */
	ALLEATO_IN_MANO,
	/**
	 * Effetto della carta competenza lezioni di volo,
	 * che permette di rimuovere x influenza come requisito
	 * per il secondo effetto (x è definito nel JSON)
	 */
	SCARTA_INFLUENZA,
	/**
	 * Effetto della carta divinazione,
	 * Permette di vedere la carta in cima al mazzo
	 */
	GUARDA_CARTA_CIMA,
	/**
	 * Seguito della carta divinazione,
	 * Permette di lasciare la carta in cima al mazzo
	 */
	LASCIA_CARTA_CIMA,
	/**
	 * Seguito della carta divinazione,
	 * Permette di scartare la carta in cima al mazzo
	 */
	SCARTA_CARTA_CIMA,
	/**
	 * Effetto della carta Trasfigurazione,
	 * Permette di cercare una carta nel mazzo con un valore massimo definito nel json
	 */
	CERCA_CARTA_MAZZO,
	/**
	 * Mischia il mazzo
	 */
	MISCHIA_MAZZO, 
	/**
	 * Effetto che non permette di mettere più di un Segnalino Attacco ad un Malvagio
	 */
	LIMITA_ATTACCO,
	SCELTA_MULTIPLA;

}
