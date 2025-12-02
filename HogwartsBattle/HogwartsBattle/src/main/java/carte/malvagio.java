package carte;
import java.until.List;
import gioco.StatoDiGioco;
import gestoreEffetti.DurataEffetto;
import gestoreEffetti.Effetto;
import gestoreEffetti.EsecutoreEffetti;
import gestoreEffetti.Trigger;


//classe effetto
public class Effect {
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
}

//classe malvagio
public class malvagio {

    private String name;
    private String id;
    private String description;
    private String imagePath;
    private int life;
    
    private List<Trigger> triggers;
    private List<Effect> rewards;

    public malvagio(String name, String id, String description, String imagePath,
                   int life, List<Trigger> triggers, List<Effect> rewards) {

        this.name = name;
        this.id = id;
        this.description = description;
        this.imagePath = imagePath;
        this.life = life;
        this.triggers = triggers;
        this.rewards = rewards;
    }

    public void activateTrigger(TriggerType type) {
        for (Trigger t : triggers) {
            if (t.getType() == type) {
                t.execute();
            }
        }
    }

    public void defeat() {
        System.out.println("Sconfitto " + name);
        rewards.forEach(Effect::execute);
    }
}

//classe gioco
public class Game {

    private String name;
    private List<Malvagi> malvagio;

    public Game(String name, List<Malvagi> malvagi) {
        this.name = name;
        this.malvagi = malvagi;
    }

    public List<Malvagio> getMalvagi() {
        return malvagi;
    }
}
