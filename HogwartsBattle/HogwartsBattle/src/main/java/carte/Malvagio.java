package carte;
import java.util.List;

import gioco.Giocatore;
import gioco.StatoDiGioco;
import gestoreEffetti.DurataEffetto;
import gestoreEffetti.Effetto;
import gestoreEffetti.EsecutoreEffetti;
import gestoreEffetti.TipoTrigger;
import gestoreEffetti.Trigger;


//classe effetto
/*public class Effect {
    private EffectType type;
    private Integer quantity;
    private TargetType target;

    public Effect (EffectType type, Integer quantity, TargetType target) {
    this.type = type;
    this. quantity = quantity;
    this.target = target;
    }
    public EffectType getType() {return type;}
    public EffectType getQuantity() {return Quantity;}
    public EffectType getTarget() {return target;}

    public void execute() {
        System.out.println("Eseguo effetto: " + type + " su " + target + " valore: " + quantity);
    }
}

//scelta opzioni

public class ChoiceEffect extends Effect {

    private List<Effect> options;

    public ChoiceEffect(List<Effect> options) {
        super(null, null, null);
        this.options = options;
    }

    public List<Effect> getOptions() {
        return options;
    }

    public void choose(int index) {
        options.get(index).execute();
    }
}

//condizioni
public class ConditionalEffect {

    private int minValue;
    private List<Effect> effectsIfTrue;

    public ConditionalEffect(int minValue, List<Effect> effectsIfTrue) {
        this.minValue = minValue;
        this.effectsIfTrue = effectsIfTrue;
    }

    public void checkAndExecute(int value) {
        if (value >= minValue) {
            effectsIfTrue.forEach(Effect::execute);
        }
    }
}

//trigger
public class Trigger {

    private TriggerType type;
    private List<String> cause;
    private String repeat;  
    private Integer quorum; 
    private List<Effect> effects;

    public Trigger(TriggerType type, List<Effect> effects) {
        this.type = type;
        this.effects = effects;
    }

    public TriggerType getType() {
        return type;
    }

    public void execute() {
        for (Effect e : effects) {
            e.execute();
        }
    }
}*/

//classe malvagio
public class Malvagio extends Carta{
	
	private final int vita;
	private int danno;
    private List<Effetto> reward;
    private Boolean bloccoAbilita;
    private Giocatore giocatoreBloccante;
    private Boolean attaccoassegnato;
    
	
    public Malvagio(String nome, String id, String classe, String descrizione, int costo, String pathImmagine,
			List<Effetto> effetti, List<Trigger> triggers, List<Effetto> reward, int vita) {
		super(nome, id, classe, descrizione, costo, pathImmagine, effetti, triggers);
		this.reward = reward;
		this.vita = vita;
		this.danno = 0;
		this.setBloccoAbilita(false);
		this.giocatoreBloccante = null;
		this.setAttaccoassegnato(false);
	}

	/*private String name;
    private String id;
    private String description;
    private String imagePath;
    
    private List<Trigger> triggers;*/
    

    /*public void activateTrigger(TipoTrigger type) {
        for (Trigger t : triggers) {
            if (t.getType() == type) {
                t.execute();
            }
        }
    }*/
    
    public void defeat(StatoDiGioco stato, Giocatore g) {
        System.out.println("Sconfitto " + this.getNome());
        for(Effetto e : this.getReward()) {
        	EsecutoreEffetti.eseguiEffetto(e, stato, g, this);
        }
    }
    
    public int getDanno() {
		return danno;
	}

	public void setDanno(int danno) {
		this.danno = danno;
	}

	public int getVita() {
		return vita;
	}

	public List<Effetto> getReward() {
		return reward;
	}

	public boolean isSconfitto() {
		if(this.getDanno() >= this.getVita()) {
			return true;
		}
		else {
			return false;
		}
	}

	public Boolean getBloccoAbilita() {
		return bloccoAbilita;
	}

	public void setBloccoAbilita(Boolean bloccoAbilita) {
		this.bloccoAbilita = bloccoAbilita;
	}

	public Giocatore getGiocatoreBloccante() {
		return giocatoreBloccante;
	}

	public void setGiocatoreBloccante(Giocatore giocatoreBloccante) {
		this.giocatoreBloccante = giocatoreBloccante;
	}

	public Boolean getAttaccoassegnato() {
		return attaccoassegnato;
	}

	public void setAttaccoassegnato(Boolean attaccoassegnato) {
		this.attaccoassegnato = attaccoassegnato;
	}

}

//classe gioco
/*public class Game {

    private String name;
    private List<Malvagi> malvagio;

    public Game(String name, List<Malvagi> malvagi) {
        this.name = name;
        this.malvagi = malvagi;
    }

    public List<Malvagio> getMalvagi() {
        return malvagi;
    }
}*/
