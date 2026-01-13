package java;
abstract class Wizard{
    String name;
    int powerLevel;
    public Wizard(String name, int powerLevel){
        this.name = name;
        this.powerLevel = powerLevel;
    }
    abstract int castSpell(String spellName);
    public String getName() {
        return name;
    }
}
class DarkWizard extends Wizard {
    public DarkWizard(String name, int powerLevel) {
        super(name,powerLevel);
    }
    @Override
    int castSpell(String spellName) {
        switch (spellName) {
            case "Crucio":
                return powerLevel * 3;
            case "AvadaKedavra":
                return powerLevel * 5;
            default:
                return powerLevel;
        }
    }
}
class HealingWizard extends Wizard {
    public HealingWizard(String name, int powerLevel) {
        super(name, powerLevel);
    }
    @Override
    int castSpell(String spellName) {
        switch (spellName) {
            case "Heal":
                return powerLevel * 2;
            case "Revive":
                return powerLevel * 4;
            default:
                return 0;
        }
    }
}

class ElementWizard extends Wizard {
    public ElementWizard(String name, int powerLevel) {
        super(name, powerLevel);
    }

    @Override
    int castSpell(String spellName) {
        switch (spellName) {
            case "Fireball":
                return powerLevel * 2;
            case "Lightning":
                return powerLevel * 3;
            default:
                return powerLevel;
        }
    }
}


public class task4 {

    public static void main(String args[]){

        Wizard wizard = new DarkWizard("Voldemort", 80);

        String spell = "AvadaKedavra";
        int damage = wizard.castSpell(spell);

        System.out.println("Wizard: " + wizard.getName());
        System.out.println("Spell Casted: " + spell);
        System.out.println("Damage Dealt: " + damage);
    }

}