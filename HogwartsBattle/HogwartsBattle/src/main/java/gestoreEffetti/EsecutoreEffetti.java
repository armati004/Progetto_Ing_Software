package gestoreEffetti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import carte.*;
import gioco.Giocatore;
import gioco.StatoDiGioco;
import gioco.InputController;
import grafica.Entita;
import grafica.GameController;
import grafica.panels.DialogHelper;
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
		case BLOCCA_ABILITA_MALVAGIO:
			bloccaMalvagio(effetto, stato, giocatore);
			break;

		// === SCELTE ===
		case SCELTA:
			scelta(effetto, stato, giocatore);
			break;
		case SCELTA_MULTIPLA:
			sceltaPerTuttiIGiocatori(effetto.getOpzioni().get(0), effetto.getOpzioni().get(1), stato);
			break;

		// === DADI ===
		case DADO_GRIFONDORO:
			tiraDadoCasata(effetto, stato, giocatore, TipoEffetto.DADO_GRIFONDORO); // ✅ Specifico
			break;
		case DADO_SERPEVERDE:
			tiraDadoCasata(effetto, stato, giocatore, TipoEffetto.DADO_SERPEVERDE); // ✅ Specifico
			break;
		case DADO_CORVONERO:
			tiraDadoCasata(effetto, stato, giocatore, TipoEffetto.DADO_CORVONERO); // ✅ Specifico
			break;
		case DADO_TASSOROSSO:
			tiraDadoCasata(effetto, stato, giocatore, TipoEffetto.DADO_TASSOROSSO); // ✅ Specifico
			break;
		case DADO_MALVAGIO:
			tiraDadoMalvagio(effetto, stato, giocatore, TipoEffetto.DADO_SERPEVERDE);
			break;
		case SCEGLI_DADO:
		    scegliDadoDaTirare(effetto, stato, giocatore);
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

			if (giocatore.getEroe().getNome().contains("Ron") && giocatore.getAttacco() >= 3
					&& giocatore.getEroe().getTriggers() != null) {
				if (giocatore.getEroe().getTriggers().get(0).getAttivato1Volta() == false) {
					stato.getGestoreTrigger().attivaTrigger(TipoTrigger.ATTACCHI_ASSEGNATI, stato, g);
					giocatore.getEroe().getTriggers().get(0).setAttivato1Volta(true);
				}
			}
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
		int qta = effetto.getQta() != null ? effetto.getQta() : 1;

		if (giocatore.getMano().isEmpty()) {
			System.out.println("⚠️ " + giocatore.getEroe().getNome() + " non ha carte da scartare");
			return;
		}

		// ⭐ MODIFICATO: Passa giocatore per mostrare nome
		InputController.getInstance().mostraSelezioneCartePerScartare(giocatore, // ⭐ Giocatore (per nome)
				TipoEffetto.SCARTARE_CARTA, qta, carteSelezionate -> {
					for (Carta c : carteSelezionate) {
						giocatore.getMano().remove(c);
						giocatore.getScarti().aggiungiCarta(c);
						System.out.println("  📤 " + giocatore.getEroe().getNome() + " scarta: " + c.getNome());
					}

					if (grafica.GameController.getInstance() != null) {
						grafica.GameController.getInstance().getGameUI().aggiorna();
					}
				});
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

		// stato.getGestoreTrigger().attivaTrigger(TipoTrigger.TENTA_RIMUOVI_MARCHIO_NERO,
		// stato, giocatore);

		if (stato.getGestoreEffetti().regolaAttiva(TipoEffetto.NON_RIMUOVERE_MARCHI)) {
			System.out.println("⚠️ Non è possibile rimuovere marchi neri");
			return;
		}

		int vecchioValore = stato.getLuogoAttuale().getNumeroMarchiNeri();
		stato.getLuogoAttuale().setNumeroMarchiNeri(Math.max(0, vecchioValore - effetto.getQta()));
		int rimosse = vecchioValore - stato.getLuogoAttuale().getNumeroMarchiNeri();

		System.out.println("⚪ Rimossi " + rimosse + " marchi neri");

		if (rimosse > 0) {
			for (Giocatore g : stato.getGiocatori()) {
				if (g.getEroe().getNome().contains("Harry") && g.getEroe().getTriggers() != null) {
					if (g.getEroe().getTriggers().get(0).getAttivato1Volta() == false) {
						stato.getGestoreTrigger().attivaTrigger(TipoTrigger.RIMOZIONE_MARCHIO_NERO, stato, giocatore);
						g.getEroe().getTriggers().get(0).setAttivato1Volta(true);
					}
				}
			}
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
			int dannoAttuale = malvagio.getDanno();
			malvagio.setDanno(Math.max(0, dannoAttuale - effetto.getQta()));
			System.out.println(
					"💚 " + malvagio.getNome() + " recupera " + (dannoAttuale - malvagio.getDanno()) + " vita");
		}
	}

	private static void bloccaMalvagio(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		Malvagio selezionato = InputController.scegliMalvagio(stato.getMalvagiAttivi(), giocatore.getEroe().getNome(),
				"Scegli un malvagio da bloccare");
		if (selezionato != null) {
			selezionato.setBloccoAbilita(true);
			selezionato.setGiocatoreBloccante(giocatore);
		}
	}

	private static void scelta(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		List<Effetto> opzioni = effetto.getOpzioni();

		if (opzioni == null || opzioni.size() < 2) {
			System.out.println("⚠️ SCELTA richiede almeno 2 opzioni!");
			return;
		}

		Effetto opzioneA = opzioni.get(0);
		Effetto opzioneB = opzioni.get(1);

		// ⭐ NUOVO: Controlla se scelta riguarda tutti i giocatori
		if (effetto.getTarget() == BersaglioEffetto.TUTTI_GLI_EROI) {
			// Ogni giocatore deve scegliere individualmente
			sceltaPerTuttiIGiocatori(opzioneA, opzioneB, stato);
		} else {
			// Scelta per un solo giocatore (normale)
			sceltaPerSingoloGiocatore(opzioneA, opzioneB, stato, giocatore);
		}
	}

	/**
	 * Gestisce scelta quando ogni giocatore deve decidere individualmente
	 */
	private static void sceltaPerTuttiIGiocatori(Effetto opzioneA, Effetto opzioneB, StatoDiGioco stato) {
		System.out.println("🎲 Scelta multipla: ogni giocatore sceglie per sé");

		// ⭐ NUOVO: Coda di giocatori che devono scegliere
		// List<Giocatore> giocatoriDaScegliere = new ArrayList<>(stato.getGiocatori());

		// Mostra dialog per il primo giocatore
		mostraDialogScelta(opzioneA, opzioneB, stato, stato.getGiocatori().get(stato.getGiocatoreCorrente()));
	}

	/**
	 * Mostra dialog di scelta in sequenza per ogni giocatore
	 */
	private static void mostraDialogSceltaSequenziale(int indiceCorrente, List<Giocatore> giocatori, Effetto opzioneA,
			Effetto opzioneB, StatoDiGioco stato) {
		if (indiceCorrente >= giocatori.size()) {
			// Tutti hanno scelto
			System.out.println("✅ Tutti i giocatori hanno fatto la loro scelta");

			// Aggiorna UI
			if (grafica.GameController.getInstance() != null) {
				grafica.GameController.getInstance().getGameUI().aggiorna();
			}
			return;
		}

		Giocatore giocatoreCorrente = giocatori.get(indiceCorrente);

		// Verifica se opzione A è possibile per questo giocatore
		boolean opzioneAPossibile = verificaSceltaPossibile(opzioneA, giocatoreCorrente);

		if (!opzioneAPossibile) {
			// Auto-esegue opzione B
			System.out.println(
					"⚡ " + giocatoreCorrente.getEroe().getNome() + ": opzione 1 impossibile, eseguo opzione 2");
			eseguiEffetto(opzioneB, stato, giocatoreCorrente);

			// Prossimo giocatore
			mostraDialogSceltaSequenziale(indiceCorrente + 1, giocatori, opzioneA, opzioneB, stato);
			return;
		}

		// ⭐ MOSTRA DIALOG per questo giocatore specifico
		javafx.application.Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

			// ⭐ IMPORTANTE: Nome del giocatore nel titolo E header
			alert.setTitle("Scelta - " + giocatoreCorrente.getEroe().getNome());
			alert.setHeaderText(giocatoreCorrente.getEroe().getNome() + ", fai la tua scelta:");

			String descrizioneA = getDescrizioneEffetto(opzioneA, giocatoreCorrente);
			String descrizioneB = getDescrizioneEffetto(opzioneB, giocatoreCorrente);

			alert.setContentText("Opzione 1: " + descrizioneA + "\n\n" + "Opzione 2: " + descrizioneB);

			ButtonType btnOpzioneA = new ButtonType("Opzione 1");
			ButtonType btnOpzioneB = new ButtonType("Opzione 2");

			alert.getButtonTypes().setAll(btnOpzioneA, btnOpzioneB);

			// ⭐ IMPORTANTE: Dialog MODALE (blocca fino a risposta)
			alert.showAndWait().ifPresent(risposta -> {
				if (risposta == btnOpzioneA) {
					System.out.println("✓ " + giocatoreCorrente.getEroe().getNome() + " sceglie opzione 1");
					eseguiEffetto(opzioneA, stato, giocatoreCorrente);
				} else {
					System.out.println("✓ " + giocatoreCorrente.getEroe().getNome() + " sceglie opzione 2");
					eseguiEffetto(opzioneB, stato, giocatoreCorrente);
				}

				// ⭐ RICORSIONE: Mostra dialog per prossimo giocatore
				mostraDialogSceltaSequenziale(indiceCorrente + 1, giocatori, opzioneA, opzioneB, stato);
			});
		});
	}

	/**
	 * Gestisce scelta per un singolo giocatore (normale)
	 */
	private static void sceltaPerSingoloGiocatore(Effetto opzioneA, Effetto opzioneB, StatoDiGioco stato,
			Giocatore giocatore) {
		// Verifica se opzione A è possibile
		boolean opzioneAPossibile = verificaSceltaPossibile(opzioneA, giocatore);

		if (!opzioneAPossibile) {
			// Auto-esegue opzione B
			System.out.println("⚡ Scelta automatica: opzione 1 impossibile, eseguo opzione 2");

			if (grafica.GameController.getInstance() != null) {
				grafica.GameController.getInstance().getGameUI().getMessagePanel().mostraMessaggio(
						giocatore.getEroe().getNome() + " non può scartare → effetto alternativo",
						MessagePanel.TipoMessaggio.INFO);
			}

			eseguiEffetto(opzioneB, stato, giocatore);
			return;
		}

		// Mostra dialog normale
		mostraDialogScelta(opzioneA, opzioneB, stato, giocatore);
	}

	private static void mostraDialogScelta(Effetto opzioneA, Effetto opzioneB, StatoDiGioco stato,
			Giocatore giocatore) {
		javafx.application.Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

			// ⭐ NUOVO: Titolo con nome giocatore
			if (opzioneA.getTarget().equals(BersaglioEffetto.EROE_ATTIVANTE)
					&& opzioneB.getTarget().equals(BersaglioEffetto.EROE_ATTIVANTE)) {
				for (Giocatore g : stato.getGiocatori()) {
					alert.setTitle("Scelta - " + g.getEroe().getNome());
					alert.setHeaderText(g.getEroe().getNome() + ", fai la tua scelta:");

					String descrizioneA = getDescrizioneEffetto(opzioneA, g);
					String descrizioneB = getDescrizioneEffetto(opzioneB, g);

					alert.setContentText("Opzione 1: " + descrizioneA + "\n\n" + "Opzione 2: " + descrizioneB);

					ButtonType btnOpzioneA = new ButtonType("Opzione 1");
					ButtonType btnOpzioneB = new ButtonType("Opzione 2");

					alert.getButtonTypes().setAll(btnOpzioneA, btnOpzioneB);

					alert.showAndWait().ifPresent(risposta -> {
						if (risposta == btnOpzioneA) {
							eseguiEffetto(opzioneA, stato, g);
						} else {
							eseguiEffetto(opzioneB, stato, g);
						}

						if (grafica.GameController.getInstance() != null) {
							grafica.GameController.getInstance().getGameUI().aggiorna();
						}
					});
				}
			} else {
				alert.setTitle("Scelta - " + giocatore.getEroe().getNome());
				alert.setHeaderText(giocatore.getEroe().getNome() + ", fai la tua scelta:");

				String descrizioneA = getDescrizioneEffetto(opzioneA, giocatore);
				String descrizioneB = getDescrizioneEffetto(opzioneB, giocatore);

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
			}
		});
	}

	private static String getDescrizioneEffetto(Effetto effetto, Giocatore giocatore) {
		StringBuilder descrizione = new StringBuilder();

		switch (effetto.getType()) {
		case SCARTA_INCANTESIMO:
			int qtaIncantesimi = effetto.getQta() != null ? effetto.getQta() : 1;
			descrizione.append("Scarta ").append(qtaIncantesimi).append(" incantesimo/i");

			// ✅ NUOVO: Mostra incantesimi disponibili
			List<Carta> incantesimi = giocatore.getMano().stream()
					.filter(c -> c.getClasse().equalsIgnoreCase("Incantesimo"))
					.collect(java.util.stream.Collectors.toList());

			if (!incantesimi.isEmpty()) {
				descrizione.append("\n   Incantesimi in mano: ");
				for (int i = 0; i < incantesimi.size(); i++) {
					if (i > 0)
						descrizione.append(", ");
					descrizione.append(incantesimi.get(i).getNome());
				}
			}
			break;

		case SCARTA_OGGETTO:
			int qtaOggetti = effetto.getQta() != null ? effetto.getQta() : 1;
			descrizione.append("Scarta ").append(qtaOggetti).append(" oggetto/i");

			// ✅ NUOVO: Mostra incantesimi disponibili
			List<Carta> oggetti = giocatore.getMano().stream().filter(c -> c.getClasse().equalsIgnoreCase("Oggetto"))
					.collect(java.util.stream.Collectors.toList());

			if (!oggetti.isEmpty()) {
				descrizione.append("\n   Oggetti in mano: ");
				for (int i = 0; i < oggetti.size(); i++) {
					if (i > 0)
						descrizione.append(", ");
					descrizione.append(oggetti.get(i).getNome());
				}
			}
			break;
		case SCARTA_ALLEATO:
			int qtaAlleati = effetto.getQta() != null ? effetto.getQta() : 1;
			descrizione.append("Scarta ").append(qtaAlleati).append(" alleato/i");

			// ✅ NUOVO: Mostra incantesimi disponibili
			List<Carta> alleati = giocatore.getMano().stream().filter(c -> c.getClasse().equalsIgnoreCase("Alleato"))
					.collect(java.util.stream.Collectors.toList());

			if (!alleati.isEmpty()) {
				descrizione.append("\n   Incantesimi in mano: ");
				for (int i = 0; i < alleati.size(); i++) {
					if (i > 0)
						descrizione.append(", ");
					descrizione.append(alleati.get(i).getNome());
				}
			}
			break;
		case PERDERE_VITA:
			int qtaVita = effetto.getQta() != null ? effetto.getQta() : 1;
			descrizione.append("Perdi ").append(qtaVita).append(" ❤️ vita");
			// ✅ NUOVO: Mostra vita attuale
			descrizione.append("\n   Vita attuale: ").append(giocatore.getSalute());
			break;
		default:
			descrizione.append(effetto.getType().toString()).append(" ").append(effetto.getQta()).append(" ")
					.append(effetto.getTarget());
		}

		return descrizione.toString();
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

	/**
	 * Tira un dado di una casata specifica
	 */
	private static void tiraDadoCasata(Effetto effetto, StatoDiGioco stato, Giocatore giocatore, TipoEffetto tipoDado) {
		if (stato.getDadi() == null || stato.getDadi().isEmpty()) {
			System.out.println("⚠️ Nessun dado disponibile!");
			return;
		}

		// STEP 1: Trova il dado corretto
		Dado dadoSelezionato = null;
		String idDadoCercato = getIdDadoDaTipo(tipoDado);

		// Cerca prima per ID esatto nella Map
		dadoSelezionato = stato.getDadi().get(idDadoCercato); // ✅ Usa Map.get()

		// Se non trovato, cerca per nome
		if (dadoSelezionato == null) {
			String nomeDadoCercato = getNomeDadoDaTipo(tipoDado);
			for (Dado dado : stato.getDadi().values()) { // ✅ Usa Map.values()
				if (dado.getNome().equalsIgnoreCase(nomeDadoCercato)) {
					dadoSelezionato = dado;
					break;
				}
			}
		}

		// STEP 2: Usa le opzioni integrate nel dado
		List<Effetto> opzioniDado = dadoSelezionato.getOpzioni(); // ✅ Opzioni dal dado

		// STEP 3: Tira il dado
		Effetto effettoEseguito = dadoSelezionato.tiraDado(stato, giocatore, opzioniDado);

		// ✅ STEP 4: Applica risultato agli Horcrux (se Anno 7)
		if (effettoEseguito != null && stato.getHorcruxAttivi() != null && !stato.getHorcruxAttivi().isEmpty()) {
			applicaRisultatoDadoAHorcrux(effettoEseguito, stato, giocatore);
		}

		// ✅ STEP 5: Trigger DADO_TIRATO
		stato.getGestoreTrigger().attivaTrigger(TipoTrigger.DADO_TIRATO, stato, giocatore);
	}

	private static String getIdDadoDaTipo(TipoEffetto tipo) {
	    switch (tipo) {
	        case DADO_GRIFONDORO:
	            return "dadoGrifondoro";  // ✅ camelCase
	        case DADO_SERPEVERDE:
	            return "dadoSerpeverde";  // ✅ camelCase
	        case DADO_CORVONERO:
	            return "dadoCorvonero";  // ✅ camelCase
	        case DADO_TASSOROSSO:
	            return "dadoTassorosso";  // ✅ camelCase
	        default:
	            return "dado";
	    }
	}

	/**
	 * Permette al giocatore di scegliere quale dado tirare (Storia di Hogwarts)
	 * Costruisce dinamicamente la lista dei dadi disponibili dalla Map
	 */
	private static void scegliDadoDaTirare(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		if (stato.getDadi() == null || stato.getDadi().isEmpty()) {
			System.out.println("⚠️ Nessun dado disponibile!");
			return;
		}
		
		// Costruisci lista dadi disponibili dinamicamente dalla Map
		List<String> dadoIds = new ArrayList<>();
		List<String> opzioniDadi = new ArrayList<>();
		List<TipoEffetto> tipiDado = new ArrayList<>();
		
		// ✅ ID corretti dal JSON (camelCase)
		String[] idsDaCercare = {"dadoGrifondoro", "dadoSerpeverde", "dadoCorvonero", "dadoTassorosso"};
		String[] icone = {"🦁", "🐍", "🦅", "🦡"};
		TipoEffetto[] tipi = {
			TipoEffetto.DADO_GRIFONDORO, 
			TipoEffetto.DADO_SERPEVERDE, 
			TipoEffetto.DADO_CORVONERO, 
			TipoEffetto.DADO_TASSOROSSO
		};
		
		for (int i = 0; i < idsDaCercare.length; i++) {
			String idDado = idsDaCercare[i];
			
			// Controlla se questo dado esiste nella Map
			if (stato.getDadi().containsKey(idDado)) {
				Dado dado = stato.getDadi().get(idDado);
				dadoIds.add(idDado);
				opzioniDadi.add(icone[i] + " " + dado.getNome());
				tipiDado.add(tipi[i]);
				
				System.out.println("✓ Dado disponibile: " + dado.getNome() + " (ID: " + idDado + ")");
			}
		}
		
		// Verifica che ci siano dadi disponibili
		if (opzioniDadi.isEmpty()) {
			System.out.println("⚠️ Nessun dado delle casate disponibile!");
			System.out.println("   Dadi presenti nella Map: " + stato.getDadi().keySet());
			return;
		}
		
		System.out.println("📜 Storia di Hogwarts attivata - " + opzioniDadi.size() + " dadi disponibili");
		
		// Mostra dialog con i dadi effettivamente disponibili
		grafica.panels.DialogHelper.mostraSceltaMultipla(
			"Storia di Hogwarts",
			giocatore.getEroe().getNome() + ", scegli quale dado tirare:",
			"Seleziona uno dei dadi delle casate disponibili:",
			opzioniDadi,
			indiceScelta -> {
				if (indiceScelta >= 0 && indiceScelta < tipiDado.size()) {
					TipoEffetto dadoScelto = tipiDado.get(indiceScelta);
					String nomeDadoScelto = opzioniDadi.get(indiceScelta);
					
					System.out.println("✅ " + giocatore.getEroe().getNome() + " ha scelto: " + nomeDadoScelto);
					
					// Tira il dado scelto
					tiraDadoCasata(effetto, stato, giocatore, dadoScelto);
				}
				
				// Aggiorna UI
				if (grafica.GameController.getInstance() != null) {
					grafica.GameController.getInstance().getGameUI().aggiorna();
				}
			},
			grafica.panels.DialogHelper.DialogStyle.DADO()
		);
	}

	/**
	 * Converte il tipo effetto nel nome del dado
	 */
	private static String getNomeDadoDaTipo(TipoEffetto tipo) {
		switch (tipo) {
		case DADO_GRIFONDORO:
			return "Dado Grifondoro";
		case DADO_SERPEVERDE:
			return "Dado Serpeverde";
		case DADO_CORVONERO:
			return "Dado Corvonero";
		case DADO_TASSOROSSO:
			return "Dado Tassorosso";
		default:
			return "Dado";
		}
	}

	/**
	 * Ottiene la lista di 6 effetti per il dado specifico
	 */
	private static List<Effetto> getOpzioniDado(Effetto effetto, TipoEffetto tipoDado) {
		// Se l'effetto ha già le opzioni definite, usale
		if (effetto.getOpzioni() != null && effetto.getOpzioni().size() >= 6) {
			return effetto.getOpzioni();
		}

		// Altrimenti, avvisa che le opzioni devono essere configurate
		System.out.println("⚠️ Le opzioni del dado devono essere definite nell'effetto!");
		return null;
	}

	/**
	 * Applica il risultato del dado agli Horcrux attivi
	 */
	private static void applicaRisultatoDadoAHorcrux(Effetto effetto, StatoDiGioco stato, Giocatore giocatore) {
		// Converti effetto in Entità
		Entita risultatoDado = convertiEffettoInEntita(effetto);

		if (risultatoDado == null) {
			return; // Effetto non rilevante per Horcrux
		}

		System.out.println("🎲 Risultato dado per Horcrux: " + risultatoDado);

		// Applica a tutti gli Horcrux attivi
		List<Horcrux> horcruxDaDistruggere = new ArrayList<>();

		for (Horcrux horcrux : stato.getHorcruxAttivi()) {
			boolean segnalinoAssegnato = horcrux.applicaRisultatoDado(risultatoDado);

			if (segnalinoAssegnato) {
				System.out.println("  ✓ Segnalino " + risultatoDado + " → " + horcrux.getNome());
			}

			if (horcrux.horcruxDistrutto()) {
				System.out.println("  🔥 HORCRUX DISTRUTTO: " + horcrux.getNome());
				horcruxDaDistruggere.add(horcrux);
			}
		}

		// Distruggi Horcrux completati
		for (Horcrux h : horcruxDaDistruggere) {
			// Applica ricompensa
			h.applicaRicompensa(stato, giocatore);

			// Rimuovi
			stato.getHorcruxAttivi().remove(h);

			// Messaggio
			if (GameController.getInstance() != null) {
				GameController.getInstance().getGameUI().getMessagePanel()
						.mostraMessaggio("🔥 " + h.getNome() + " DISTRUTTO!", MessagePanel.TipoMessaggio.MALVAGIO);
			}
		}
	}

	/**
	 * Converte tipo effetto in Entità per Horcrux
	 */
	private static Entita convertiEffettoInEntita(Effetto effetto) {
		switch (effetto.getType()) {
		case GUADAGNARE_ATTACCO:
			return Entita.ATTACCO;

		case GUADAGNARE_INFLUENZA:
			return Entita.INFLUENZA;

		case GUADAGNARE_VITA:
		case PERDERE_VITA:
			return Entita.VITA;

		case PESCARE_CARTA:
		case SCARTARE_CARTA:
			return Entita.CARTA;

		default:
			return null;
		}
	}

	private static void tiraDadoMalvagio(Effetto effetto, StatoDiGioco stato, Giocatore giocatore,
			TipoEffetto tipoDado) {
		if (stato.getDadi() == null || stato.getDadi().isEmpty()) {
			System.out.println("⚠️ Nessun dado disponibile!");
			return;
		}

		// ✅ STEP 1: Trova il dado corretto in base al tipo
		Dado dadoSelezionato = null;
		String nomeDadoCercato = getNomeDadoDaTipo(tipoDado);

		for (Dado dado : stato.getDadi().values()) {
			if (dado.getNome().equalsIgnoreCase(nomeDadoCercato)
					|| dado.getId().toLowerCase().contains(nomeDadoCercato.toLowerCase())) {
				dadoSelezionato = dado;
				break;
			}
		}

		if (dadoSelezionato == null) {
			System.out.println("⚠️ Dado " + nomeDadoCercato + " non trovato!");
			return;
		}

		// ✅ STEP 2: Ottieni le 6 opzioni per questo dado
		List<Effetto> opzioniDado = getOpzioniDado(effetto, tipoDado);

		if (opzioniDado == null || opzioniDado.size() < 6) {
			System.out.println("⚠️ Effetti del dado non configurati correttamente!");
			return;
		}

		// ✅ STEP 3: Tira il dado con le opzioni corrette
		Effetto effettoEseguito = dadoSelezionato.tiraDado(stato, giocatore, opzioniDado);

		switch (convertiEffettoInEntita(effettoEseguito)) {
		case ATTACCO:
			perdereVita(effettoEseguito, stato, giocatore);
			break;
		case VITA:
			rimuovereAttacco(effettoEseguito, stato, giocatore);
			break;
		case INFLUENZA:
			aggiungereMarchioNero(effettoEseguito, stato, giocatore);
			break;
		case CARTA:
			scartareCarta(effettoEseguito, stato, giocatore);
			break;
		}
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
			if (evento.getNome().contains("Morsmordre")) {
				stato.getGestoreTrigger().attivaTrigger(TipoTrigger.RIVELA_MORSMORDRE_O_MALVAGIO, stato, giocatore);
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
			for (int i = 0; i < effetto.getQtaTarget(); i++) {
				int indice = InputController.scegliGiocatore(stato.getGiocatori(), "Scegli Eroe",
						"Scegli un eroe come bersaglio");
				if (indice >= 0 && indice < stato.getGiocatori().size()) {
					bersagli.add(stato.getGiocatori().get(indice));
				} else {
					bersagli.add(stato.getGiocatori().get(stato.getGiocatoreCorrente()));
				}
			}
			break;

		default:
			bersagli.add(stato.getGiocatori().get(stato.getGiocatoreCorrente()));
		}

		return bersagli;
	}

}