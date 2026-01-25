package gestoreEffetti;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import carte.*;
import gioco.Giocatore;
import gioco.StatoDiGioco;
import gioco.InputController;
import grafica.panels.MessagePanel;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Esecutore centralizzato per tutti gli effetti del gioco. VERSIONE COMPLETA
 * con InputController per scelte utente
 */
public class EsecutoreEffetti {

	private static final Random random = new Random();

	/**
	 * Punto di ingresso principale per l'esecuzione di un effetto.
	 */
	public static void eseguiEffetto(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto == null) {
			System.err.println("⚠️ ATTENZIONE: Tentativo di eseguire effetto null");
			return;
		}

		switch (effetto.getType()) {
		// === RISORSE ===
		case GUADAGNARE_ATTACCO:
			guadagnareAttacco(effetto, stato, giocatore);
			break;
		case GUADAGNARE_INFLUENZA:
			guadagnareInfluenza(effetto, stato, giocatore);
			break;
		case GUADAGNARE_VITA:
			guadagnareVita(effetto, stato, giocatore);
			break;

		// === VITA E DANNO ===
		case PERDERE_VITA:
			perdereVita(effetto, stato, giocatore);
			break;
		case LIMITARE_DANNO:
			break;

		// === CARTE ===
		case PESCARE_CARTA:
			pescaCarta(effetto, stato, giocatore);
			break;
		case SCARTARE_CARTA:
			scartareCarta(effetto, stato, giocatore);
			break;
		case SCARTA_INCANTESIMO:
			scartareTipoCarta(effetto, stato, giocatore, Incantesimo.class);
			break;
		case SCARTA_OGGETTO:
			scartareTipoCarta(effetto, stato, giocatore, Oggetto.class);
			break;
		case SCARTA_ALLEATO:
			scartareTipoCarta(effetto, stato, giocatore, Alleato.class);
			break;

		// === LUOGHI ===
		case AGGIUNGERE_MARCHIO_NERO:
			aggiungereMarchioNero(effetto, stato, giocatore);
			break;
		case RIMUOVERE_MARCHIO_NERO:
			rimuovereMarchioNero(effetto, stato, giocatore);
			break;

		// === MALVAGI ===
		case RIMUOVERE_ATTACCO:
			rimuovereAttacco(effetto, stato, giocatore);
			break;
		case CURARE_MALVAGI:
			curareMalvagi(effetto, stato, giocatore);
			break;

		// === SCELTE ===
		case SCELTA:
			gestisciScelta(effetto, stato, giocatore);
			break;
		case SCELTA_MULTIPLA:
			gestisciSceltaMultipla(effetto, stato, giocatore);
			break;

		// === DADI ===
		case DADO_GRIFONDORO:
			tiraDado(effetto, stato, giocatore, "grifondoro");
			break;
		case DADO_SERPEVERDE:
			tiraDado(effetto, stato, giocatore, "serpeverde");
			break;
		case DADO_CORVONERO:
			tiraDado(effetto, stato, giocatore, "corvonero");
			break;
		case DADO_TASSOROSSO:
			tiraDado(effetto, stato, giocatore, "tassorosso");
			break;
		case DADO_MALVAGIO:
			tiraDadoMalvagio(effetto, stato, giocatore);
			break;
		case RITIRA_DADO:
			break;

		// === MAZZO ===
		case ALLEATO_IN_MAZZO:
			mettiCartaInCimaMazzo(effetto, stato, giocatore, Alleato.class);
			break;
		case INCANTESIMO_IN_MAZZO:
			mettiCartaInCimaMazzo(effetto, stato, giocatore, Incantesimo.class);
			break;
		case OGGETTO_IN_MAZZO:
			mettiCartaInCimaMazzo(effetto, stato, giocatore, Oggetto.class);
			break;
		case MISCHIA_MAZZO:
			Collections.shuffle(giocatore.getMazzo().getCarte());
			System.out.println("🔀 Mazzo mischiato");
			break;

		// === RICERCA ===
		case CERCA_ALLEATO:
			cercaCartaNellaDiscardPile(effetto, stato, giocatore, Alleato.class);
			break;
		case CERCA_INCANTESIMO:
			cercaCartaNellaDiscardPile(effetto, stato, giocatore, Incantesimo.class);
			break;
		case CERCA_OGGETTO:
			cercaCartaNellaDiscardPile(effetto, stato, giocatore, Oggetto.class);
			break;
		case CERCA_CARTA_MAZZO:
			cercaCartaNelMazzo(effetto, stato, giocatore);
			break;

		// === RIVELAZIONE ===
		case RIVELA_CARTA:
			rivelaCarta(effetto, stato, giocatore);
			break;
		case RIVELA_NUOVO_EVENTO:
			rivelaEventoArtOscure(effetto, stato, giocatore);
			break;
		case GUARDA_CARTA_CIMA:
			guardaCartaCima(effetto, stato, giocatore);
			break;
		case LASCIA_CARTA_CIMA:
			System.out.println("📌 Carta lasciata in cima al mazzo");
			break;
		case SCARTA_CARTA_CIMA:
			scartaCartaCima(effetto, stato, giocatore);
			break;

		// === CONDIZIONI ===
		case ALLEATO_IN_MANO:
			verificaAlleatoInMano(effetto, stato, giocatore);
			break;
		case ACQUISTO_CARTA:
			break;

		// === EFFETTI SPECIALI ===
		case COPIA_EFFETTO:
			copiaEffetto(effetto, stato, giocatore);
			break;
		case SCARTA_INFLUENZA:
			scartaInfluenza(effetto, stato, giocatore);
			break;
		case SCONTO_ACQUISTO_CASATA:
			System.out.println("💰 Sconto attivo per carte con dado casata");
			break;

		// === VITTORIA/SCONFITTA ===
		case VITTORIA:
			stato.setVictory(true);
			System.out.println("🎉 VITTORIA! Gli eroi hanno trionfato!");
			break;

		// === BLOCCHI ===
		case NON_GUADAGNARE_ATTACCHI:
		case NON_GUADAGNARE_GETTONI:
		case NON_GUADAGNARE_VITA:
		case NON_PESCARE_CARTE:
		case NON_RIMUOVERE_MARCHI:
		case NON_ATTACCARE_VOLDY:
		case LIMITA_ATTACCO:
			System.out.println("🚫 Effetto di blocco attivato: " + effetto.getType());
			break;

		default:
			System.err.println("⚠️ Effetto non implementato: " + effetto.getType());
		}
	}

	// ============================================================================
	// IMPLEMENTAZIONE METODI SPECIFICI
	// ============================================================================

	private static void guadagnareAttacco(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto.getQta() == null || effetto.getQta() <= 0)
			return;

		List<Giocatore> bersagli = determinaBersagli(effetto, stato, giocatore);

		for (Giocatore g : bersagli) {
			if (stato.getGestoreEffetti().regolaAttiva(TipoEffetto.NON_GUADAGNARE_ATTACCHI)) {
				if (g != stato.getGiocatori().get(stato.getGiocatoreCorrente())) {
					System.out.println("⚠️ " + g.getEroe().getNome() + " non può guadagnare attacchi");
					continue;
				}
			}

			int vecchioValore = g.getAttacco();
			g.setAttacco(vecchioValore + effetto.getQta());
			System.out.println("⚔️ " + g.getEroe().getNome() + " guadagna " + effetto.getQta() + " attacco");

			stato.getGestoreTrigger().attivaTrigger(TipoTrigger.ATTACCHI_ASSEGNATI, stato, g);
		}
	}

	private static void guadagnareInfluenza(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto.getQta() == null || effetto.getQta() <= 0)
			return;

		List<Giocatore> bersagli = determinaBersagli(effetto, stato, giocatore);

		for (Giocatore g : bersagli) {
			if (stato.getGestoreEffetti().regolaAttiva(TipoEffetto.NON_GUADAGNARE_GETTONI)) {
				if (g != stato.getGiocatori().get(stato.getGiocatoreCorrente())) {
					System.out.println("⚠️ " + g.getEroe().getNome() + " non può guadagnare influenza");
					continue;
				}
			}

			int vecchioValore = g.getGettone();
			g.setGettone(vecchioValore + effetto.getQta());
			System.out.println("🪙 " + g.getEroe().getNome() + " guadagna " + effetto.getQta() + " influenza");
		}
	}

	private static void guadagnareVita(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto.getQta() == null || effetto.getQta() <= 0)
			return;

		List<Giocatore> bersagli = determinaBersagli(effetto, stato, giocatore);

		for (Giocatore g : bersagli) {
			// stato.getGestoreTrigger().attivaTrigger(TipoTrigger.TENTA_GUADAGNA_VITA,
			// stato, g);

			if (stato.getGestoreEffetti().regolaAttiva(TipoEffetto.NON_GUADAGNARE_VITA)) {
				System.out.println("⚠️ " + g.getEroe().getNome() + " non può guadagnare vita");
				continue;
			}

			int vecchiaVita = g.getSalute();
			int nuovaVita = Math.min(vecchiaVita + effetto.getQta(), g.getSaluteMax());
			g.setSalute(nuovaVita);
			int vitaGuadagnata = nuovaVita - vecchiaVita;

			System.out.println("❤️ " + g.getEroe().getNome() + " guadagna " + vitaGuadagnata + " vita");

			if (vitaGuadagnata > 0) {
				stato.getGestoreTrigger().attivaTrigger(TipoTrigger.GUADAGNA_VITA, stato, g);
				if (vitaGuadagnata >= 2) {
					stato.getGestoreTrigger().attivaTrigger(TipoTrigger.GUADAGNA_VITA_QUORUM, stato, g);
				}

				if (g.getSalute() == g.getSaluteMax()) {
					stato.getGestoreTrigger().attivaTrigger(TipoTrigger.SALUTE_MASSIMA, stato, g);
				}
			}
		}
	}

	/**
	 * ⭐ GESTIONE PERDERE VITA CON STORDIMENTO E PASSAGGIO LUOGO Quando un giocatore
	 * scende a 0 vita: 1. Aggiunge 1 marchio nero al luogo 2. Controlla se il luogo
	 * è perso (marchi neri >= max) 3. Se perso, passa al prossimo luogo 4. Verifica
	 * se tutti i luoghi sono persi (sconfitta) 5. Scarta tutte le carte e azzera
	 * segnalini 6. Ripristina vita al massimo 7. Il turno passa al giocatore
	 * successivo
	 */
	private static void perdereVita(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto.getQta() == null || effetto.getQta() <= 0)
			return;

		List<Giocatore> bersagli = determinaBersagli(effetto, stato, giocatore);

		for (Giocatore g : bersagli) {
			int dannoEffettivo = effetto.getQta();

			if (stato.getGestoreEffetti().regolaAttiva(TipoEffetto.LIMITARE_DANNO)) {
				dannoEffettivo = Math.max(1, dannoEffettivo - 1);
				System.out.println("🛡️ Danno ridotto a " + dannoEffettivo);
			}

			int vecchiaVita = g.getSalute();
			g.setSalute(Math.max(0, vecchiaVita - dannoEffettivo));

			System.out.println("💔 " + g.getEroe().getNome() + " perde " + dannoEffettivo + " vita");

			stato.getGestoreTrigger().attivaTrigger(TipoTrigger.RICEVI_DANNO, stato, g);

			// ⭐ GESTIONE STORDIMENTO
			if (g.getSalute() == 0) {
				System.out.println("\n😵 " + g.getEroe().getNome() + " è STORDITO!");

				// 1. Aggiungi marchio nero
				int marchiAttuali = stato.getLuogoAttuale().getNumeroMarchiNeri();
				int marchiMax = stato.getLuogoAttuale().getMarchiNeriMax();

				System.out.println("  🌑 Aggiunto 1 Marchio Nero al luogo");
				stato.getLuogoAttuale().setNumeroMarchiNeri(marchiAttuali + 1);

				// 2. Controlla se il luogo è perso
				if (stato.getLuogoAttuale().getNumeroMarchiNeri() >= marchiMax) {
					// ⭐ Passa al prossimo luogo
					stato.passaAlProssimoLuogo();

					// La verifica sconfitta è già dentro passaAlProssimoLuogo()
				} else {
					System.out.println(
							"  🌑 Marchi Neri: " + stato.getLuogoAttuale().getNumeroMarchiNeri() + "/" + marchiMax);
				}

				// 3. Scarta tutte le carte dalla mano
				System.out.println("  🗑️ Scartate tutte le carte dalla mano (" + g.getMano().size() + ")");
				while (!g.getMano().isEmpty()) {
					Carta carta = g.getMano().get(0);
					g.getMano().remove(0);
					g.getScarti().aggiungiCarta(carta);
				}

				// 4. Azzera segnalini
				System.out.println("  💰 Azzerati attacco e gettone");
				g.setAttacco(0);
				g.setGettone(0);

				// 5. Ripristina vita al massimo
				g.setSalute(g.getSaluteMax());
				System.out.println("  ❤️ Vita ripristinata a " + g.getSaluteMax());

				// 6. Attiva trigger stordimento (per eventuali effetti)
				stato.getGestoreTrigger().attivaTrigger(TipoTrigger.STORDIMENTO, stato, g);

				// 7. Il turno passa automaticamente
				System.out.println("  ⏭️ Il turno passa al giocatore successivo\n");
			}
		}
	}

	private static void pescaCarta(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto.getQta() == null || effetto.getQta() <= 0)
			return;

		List<Giocatore> bersagli = determinaBersagli(effetto, stato, giocatore);

		for (Giocatore g : bersagli) {
			if (stato.getGestoreEffetti().regolaAttiva(TipoEffetto.NON_PESCARE_CARTE)) {
				System.out.println("⚠️ " + g.getEroe().getNome() + " non può pescare carte");
				continue;
			}

			for (int i = 0; i < effetto.getQta(); i++) {
				Carta carta = g.getMazzo().pescaCarta();
				if (carta != null) {
					g.getMano().add(carta);
					System.out.println("🎴 " + g.getEroe().getNome() + " pesca: " + carta.getNome());
				}
			}

			if (effetto.getQta() > 1) {
				stato.getGestoreTrigger().attivaTrigger(TipoTrigger.PESCA_CARTA_BONUS, stato, g);
			}
		}
	}

	private static void scartareCarta(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto.getQta() == null || effetto.getQta() <= 0)
			return;

		List<Giocatore> bersagli = determinaBersagli(effetto, stato, giocatore);

		for (Giocatore g : bersagli) {
			int carteScartate = 0;

			for (int i = 0; i < effetto.getQta() && !g.getMano().isEmpty(); i++) {
				// ⭐ USA InputController per far scegliere la carta
				Carta carta = InputController.scegliCarta(new ArrayList<>(g.getMano()), "Scarta Carta",
						"Scegli una carta da scartare (" + (i + 1) + "/" + effetto.getQta() + ")");

				if (carta != null) {
					g.getMano().remove(carta);
					g.getScarti().aggiungiCarta(carta);
					carteScartate++;
					System.out.println("🗑️ " + g.getEroe().getNome() + " scarta: " + carta.getNome());

					stato.getGestoreTrigger().attivaTrigger(TipoTrigger.AUTO_SCARTO, stato, g);
				}
			}

			if (carteScartate > 0) {
				stato.getGestoreTrigger().attivaTrigger(TipoTrigger.SCARTA_CARTA_MALUS, stato, g);
				stato.getGestoreTrigger().attivaTrigger(TipoTrigger.SCARTA_CARTA_BONUS, stato, g);
			}
		}
	}

	private static void scartareTipoCarta(Effetto effetto, StatoDiGioco stato, Giocatore giocatore,
			Class<? extends Carta> tipo) {
		List<Giocatore> bersagli = determinaBersagli(effetto, stato, giocatore);

		for (Giocatore g : bersagli) {
			List<Carta> carteTipo = new ArrayList<>();
			for (Carta c : g.getMano()) {
				if (tipo.isInstance(c)) {
					carteTipo.add(c);
				}
			}

			if (!carteTipo.isEmpty()) {
				// ⭐ USA InputController
				Carta daScartare = InputController.scegliCarta(carteTipo, "Scarta " + tipo.getSimpleName(),
						"Scegli una carta " + tipo.getSimpleName() + " da scartare");

				if (daScartare != null) {
					g.getMano().remove(daScartare);
					g.getScarti().aggiungiCarta(daScartare);
					System.out.println("🗑️ " + g.getEroe().getNome() + " scarta: " + daScartare.getNome());

					if (tipo == Incantesimo.class) {
						stato.getGestoreTrigger().attivaTrigger(TipoTrigger.SCARTA_INCANTESIMO, stato, g);
					}
				}
			} else {
				System.out.println("⚠️ Nessuna carta " + tipo.getSimpleName() + " in mano");
			}
		}
	}

	private static void aggiungereMarchioNero(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto.getQta() == null || effetto.getQta() <= 0)
			return;

		int vecchioValore = stato.getLuogoAttuale().getNumeroMarchiNeri();
		stato.getLuogoAttuale().setNumeroMarchiNeri(vecchioValore + effetto.getQta());
		System.out.println("⚫ Aggiunti " + effetto.getQta() + " marchi neri");

		stato.getGestoreTrigger().attivaTrigger(TipoTrigger.AGGIUNTA_MARCHIO_NERO, stato, giocatore);

		if (stato.getLuogoAttuale().getNumeroMarchiNeri() >= stato.getLuogoAttuale().getMarchiNeriMax()) {
			stato.passaAlProssimoLuogo();
		}
	}

	private static void rimuovereMarchioNero(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto.getQta() == null || effetto.getQta() <= 0)
			return;

		stato.getGestoreTrigger().attivaTrigger(TipoTrigger.TENTA_RIMUOVI_MARCHIO_NERO, stato, giocatore);

		if (stato.getGestoreEffetti().regolaAttiva(TipoEffetto.NON_RIMUOVERE_MARCHI)) {
			System.out.println("⚠️ Non è possibile rimuovere marchi neri");
			return;
		}

		int vecchioValore = stato.getLuogoAttuale().getNumeroMarchiNeri();
		stato.getLuogoAttuale().setNumeroMarchiNeri(Math.max(0, vecchioValore - effetto.getQta()));
		int rimosse = vecchioValore - stato.getLuogoAttuale().getNumeroMarchiNeri();

		System.out.println("⚪ Rimossi " + rimosse + " marchi neri");

		if (rimosse > 0) {
			stato.getGestoreTrigger().attivaTrigger(TipoTrigger.RIMOZIONE_MARCHIO_NERO, stato, giocatore);
		}
	}

	private static void rimuovereAttacco(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto.getQta() == null || effetto.getQta() <= 0)
			return;

		for (Malvagio malvagio : stato.getMalvagiAttivi()) {
			int dannoAttuale = malvagio.getDanno();
			malvagio.setDanno(Math.max(0, dannoAttuale - effetto.getQta()));
			System.out.println("🔻 Rimossi " + effetto.getQta() + " attacchi da " + malvagio.getNome());
		}
	}

	private static void curareMalvagi(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto.getQta() == null || effetto.getQta() <= 0)
			return;

		for (Malvagio malvagio : stato.getMalvagiAttivi()) {
			int vecchiaVita = malvagio.getVita();
			malvagio.setDanno(Math.min(malvagio.getDanno() - effetto.getQta(), malvagio.getVita()));
			System.out
					.println("💚 " + malvagio.getNome() + " recupera " + (vecchiaVita - malvagio.getDanno()) + " vita");
		}
	}

	private static void gestisciScelta(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		List<Effetto> opzioni = effetto.getOpzioni();

		if (opzioni == null || opzioni.size() < 2) {
			System.out.println("⚠️ SCELTA richiede almeno 2 opzioni!");
			return;
		}

		Effetto opzioneA = opzioni.get(0);
		Effetto opzioneB = opzioni.get(1);

		// ⭐ NUOVO: Verifica se opzione A è possibile
		boolean opzioneAPossibile = verificaSceltaPossibile(opzioneA, giocatore);

		if (!opzioneAPossibile) {
			System.out.println("⚡ Scelta automatica: opzione 1 impossibile, eseguo opzione 2");

			if (grafica.GameController.getInstance() != null) {
				grafica.GameController.getInstance().getGameUI().getMessagePanel().mostraMessaggio(
						giocatore.getEroe().getNome() + " non può scartare → effetto alternativo",
						MessagePanel.TipoMessaggio.INFO);
			}

			eseguiEffetto(opzioneB, stato, giocatore);
			return;
		}

		// ⭐ MODIFICATO: Mostra dialog con nome giocatore
		mostraDialogScelta(opzioneA, opzioneB, stato, giocatore);
	}

	private static void gestisciSceltaMultipla(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (effetto.getOpzioni() == null || effetto.getOpzioni().isEmpty()) {
			System.err.println("⚠️ Scelta multipla senza opzioni");
			return;
		}

		for (Effetto opzione : effetto.getOpzioni()) {
			eseguiEffetto(opzione, stato, giocatore);
		}
	}

	private static void mostraDialogScelta(Effetto opzioneA, Effetto opzioneB, StatoDiGioco stato,
			Giocatore giocatore) {
		javafx.application.Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

// ⭐ NUOVO: Titolo con nome giocatore
			alert.setTitle("Scelta - " + giocatore.getEroe().getNome());
			alert.setHeaderText(giocatore.getEroe().getNome() + ", fai la tua scelta:");

			String descrizioneA = getDescrizioneEffetto(opzioneA);
			String descrizioneB = getDescrizioneEffetto(opzioneB);

			alert.setContentText("Opzione 1: " + descrizioneA + "\n\n" + "Opzione 2: " + descrizioneB);

			ButtonType btnOpzioneA = new ButtonType("Opzione 1");
			ButtonType btnOpzioneB = new ButtonType("Opzione 2");

			alert.getButtonTypes().setAll(btnOpzioneA, btnOpzioneB);

			alert.showAndWait().ifPresent(risposta -> {
				if (risposta == btnOpzioneA) {
					eseguiEffetto(opzioneA, stato, giocatore);
				} else {
					eseguiEffetto(opzioneB, stato, giocatore);
				}

				if (grafica.GameController.getInstance() != null) {
					grafica.GameController.getInstance().getGameUI().aggiorna();
				}
			});
		});
	}

	private static String getDescrizioneEffetto(Effetto effetto) {
		switch (effetto.getType()) {
		case SCARTA_INCANTESIMO:
			return "Scarta " + (effetto.getQta() != null ? effetto.getQta() : 1) + " incantesimo/i";
		case SCARTA_OGGETTO:
			return "Scarta " + (effetto.getQta() != null ? effetto.getQta() : 1) + " oggetto/i";
		case SCARTA_ALLEATO:
			return "Scarta " + (effetto.getQta() != null ? effetto.getQta() : 1) + " alleato/i";
		case PERDERE_VITA:
			return "Perdi " + (effetto.getQta() != null ? effetto.getQta() : 1) + " ❤️ vita";
		case AGGIUNGERE_MARCHIO_NERO:
			return "Aggiungi " + (effetto.getQta() != null ? effetto.getQta() : 1) + " 🌑 marchio/i";
		default:
			return effetto.getType().toString();
		}
	}

	/**
	 * Verifica se una scelta è possibile (ha le carte necessarie)
	 */
	private static boolean verificaSceltaPossibile(Effetto effetto, Giocatore giocatore) {
		switch (effetto.getType()) {
		case SCARTA_INCANTESIMO:
			return giocatore.getMano().stream().anyMatch(c -> c.getClasse().equalsIgnoreCase("Incantesimo"));

		case SCARTA_OGGETTO:
			return giocatore.getMano().stream().anyMatch(c -> c.getClasse().equalsIgnoreCase("Oggetto"));

		case SCARTA_ALLEATO:
			return giocatore.getMano().stream().anyMatch(c -> c.getClasse().equalsIgnoreCase("Alleato"));

		default:
			return true;
		}
	}

	private static void tiraDado(Effetto effetto, StatoDiGioco stato, Giocatore giocatore, String tipoDado) {
		Dado dado = stato.getDadi().get(tipoDado);
		if (dado == null) {
			System.err.println("⚠️ Dado non trovato: " + tipoDado);
			return;
		}

		List<Effetto> opzioniDado = effetto.getOpzioni();
		if (opzioniDado == null || opzioniDado.size() < 6) {
			System.err.println("⚠️ Dado deve avere 6 opzioni");
			return;
		}

		stato.getGestoreTrigger().attivaTrigger(TipoTrigger.DADO_TIRATO, stato, giocatore);

		dado.tiraDado(stato, giocatore, opzioniDado);
	}

	private static void tiraDadoMalvagio(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		tiraDado(effetto, stato, giocatore, "serpeverde");
	}

	private static void mettiCartaInCimaMazzo(Effetto effetto, StatoDiGioco stato, Giocatore giocatore,
			Class<? extends Carta> tipo) {
		System.out.println("📚 Le prossime carte " + tipo.getSimpleName() + " andranno in cima al mazzo");
	}

	private static void cercaCartaNellaDiscardPile(Effetto effetto, StatoDiGioco stato, Giocatore giocatore,
			Class<? extends Carta> tipo) {
		List<Carta> carteTrovate = new ArrayList<>();
		for (Carta c : giocatore.getScarti().getCarte()) {
			if (tipo.isInstance(c)) {
				carteTrovate.add(c);
			}
		}

		if (carteTrovate.isEmpty()) {
			System.out.println("⚠️ Nessuna carta " + tipo.getSimpleName() + " nello scarto");
			return;
		}

		// ⭐ USA InputController
		Carta scelta = InputController.scegliCarta(carteTrovate, "Cerca nello Scarto",
				"Scegli una carta " + tipo.getSimpleName() + " dallo scarto");

		if (scelta != null) {
			giocatore.getScarti().getCarte().remove(scelta);
			giocatore.getMano().add(scelta);
			System.out.println("🔍 " + giocatore.getEroe().getNome() + " recupera " + scelta.getNome());
		}
	}

	private static void cercaCartaNelMazzo(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		int valoreMax = effetto.getQtaTarget() != null ? effetto.getQtaTarget() : 99;

		List<Carta> carteIdonee = new ArrayList<>();
		for (Carta c : giocatore.getMazzo().getCarte()) {
			if (c.getCosto() <= valoreMax) {
				carteIdonee.add(c);
			}
		}

		if (carteIdonee.isEmpty()) {
			System.out.println("⚠️ Nessuna carta con valore ≤ " + valoreMax);
			Collections.shuffle(giocatore.getMazzo().getCarte());
			return;
		}

		// ⭐ USA InputController
		Carta scelta = InputController.scegliCarta(carteIdonee, "Cerca nel Mazzo",
				"Scegli una carta con costo ≤ " + valoreMax);

		if (scelta != null) {
			giocatore.getMazzo().getCarte().remove(scelta);
			giocatore.getMano().add(scelta);
			System.out.println("🔍 " + giocatore.getEroe().getNome() + " trova " + scelta.getNome());
		}

		Collections.shuffle(giocatore.getMazzo().getCarte());
	}

	private static void rivelaCarta(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (giocatore.getMazzo().isEmpty()) {
			System.out.println("⚠️ Mazzo vuoto, rimescolo scarti...");

			if (!giocatore.getScarti().getCarte().isEmpty()) {
				giocatore.getMazzo().getCarte().addAll(giocatore.getScarti().getCarte());
				giocatore.getScarti().getCarte().clear();
				Collections.shuffle(giocatore.getMazzo().getCarte());
			} else {
				return;
			}
		}

		Carta primaCarta = giocatore.getMazzo().get(0);
		System.out.println("👁️ Carta rivelata: " + primaCarta.getNome());

		int valoreMinimo = effetto.getQtaTarget() != null ? effetto.getQtaTarget() : 0;
		if (primaCarta.getCosto() > valoreMinimo) {
			giocatore.getMazzo().pescaCarta();
			giocatore.getScarti().aggiungiCarta(primaCarta);

			if (effetto.getRipetuto() != null) {
				eseguiEffetto(effetto.getRipetuto(), stato, giocatore);
			}
		}
	}

	private static void rivelaEventoArtOscure(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		int numEventi = effetto.getQta() != null ? effetto.getQta() : 1;

		for (int i = 0; i < numEventi; i++) {
			ArteOscura evento = stato.pescaArteOscura();
			if (evento != null) {
				System.out.println("🌑 Arti Oscure: " + evento.getNome());
				evento.applicaEffetto(stato, giocatore);
			}
		}
	}

	private static void guardaCartaCima(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (giocatore.getMazzo().isEmpty()) {
			System.out.println("⚠️ Mazzo vuoto");
			return;
		}

		Carta primaCarta = giocatore.getMazzo().get(0);
		if (primaCarta != null) {
			InputController.mostraMessaggio("Carta in Cima", "Carta in cima al mazzo: " + primaCarta.getNome());
		}
	}

	private static void scartaCartaCima(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		Carta primaCarta = giocatore.getMazzo().pescaCarta();
		if (primaCarta != null) {
			giocatore.getScarti().aggiungiCarta(primaCarta);
			System.out.println("🗑️ Scartata: " + primaCarta.getNome());
		}
	}

	private static void copiaEffetto(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		System.out.println("📋 Copia effetto alleato...");
	}

	private static void scartaInfluenza(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		int qtaDaScartare = effetto.getQta() != null ? effetto.getQta() : 0;

		if (giocatore.getGettone() >= qtaDaScartare) {
			giocatore.setGettone(giocatore.getGettone() - qtaDaScartare);
			System.out.println("🔵 Scartati " + qtaDaScartare + " influenza");

			if (effetto.getRipetuto() != null) {
				eseguiEffetto(effetto.getRipetuto(), stato, giocatore);
			}
		} else {
			System.out.println("⚠️ Influenza insufficiente");
		}
	}

	private static void verificaAlleatoInMano(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		int contatore = 0;

		for (Carta c : giocatore.getMano()) {
			if (c instanceof Alleato) {
				contatore++;
			}
		}

		System.out.println("👥 Alleati in mano: " + contatore);

		if (contatore > 0 && effetto.getOpzioni() != null) {
			int indiceOpzione = Math.min(contatore - 1, effetto.getOpzioni().size() - 1);
			Effetto effettoDaEseguire = effetto.getOpzioni().get(indiceOpzione);
			eseguiEffetto(effettoDaEseguire, stato, giocatore);
		}
	}

	private static List<Giocatore> determinaBersagli(Effetto effetto, StatoDiGioco stato, Giocatore attivo) {
		List<Giocatore> bersagli = new ArrayList<>();

		if (effetto.getTarget() == null) {
			effetto.setTarget(BersaglioEffetto.EROE_ATTIVO);
		}

		switch (effetto.getTarget()) {
		case EROE_ATTIVO:
		case SE_STESSO:
			bersagli.add(stato.getGiocatori().get(stato.getGiocatoreCorrente()));
			break;

		case TUTTI_GLI_EROI:
			bersagli.addAll(stato.getGiocatori());
			break;

		case EROI_NON_ATTIVI:
			for (Giocatore g : stato.getGiocatori()) {
				if (g != stato.getGiocatori().get(stato.getGiocatoreCorrente())) {
					bersagli.add(g);
				}
			}
			break;

		case EROE_ATTIVANTE:
			bersagli.add(attivo);
			break;

		case EROE_SCELTO:
			// ⭐ USA InputController
			int indice = InputController.scegliGiocatore(stato.getGiocatori(), "Scegli Eroe",
					"Scegli un eroe come bersaglio");
			if (indice >= 0 && indice < stato.getGiocatori().size()) {
				bersagli.add(stato.getGiocatori().get(indice));
			} else {
				bersagli.add(stato.getGiocatori().get(stato.getGiocatoreCorrente()));
			}
			break;

		default:
			bersagli.add(stato.getGiocatori().get(stato.getGiocatoreCorrente()));
		}

		return bersagli;
	}

}