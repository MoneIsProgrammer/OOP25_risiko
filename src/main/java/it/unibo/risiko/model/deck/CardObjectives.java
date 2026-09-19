package it.unibo.risiko.model.deck;

public enum CardObjectives {
    OBJECTIVE1("Conquer 18 territories, the territories must have atleast two troops each"),
    OBJECTIVE2("Conquer 24 territories"),
    OBJECTIVE3("Conquer all of North America and Africa"),
    OBJECTIVE4("Conquer all of North America and Oceania"),
    OBJECTIVE5("Conquer all of Asia and South America"),
    OBJECTIVE6("Conquer all of Asia and Africa"),
    OBJECTIVE7("Conquer all of Europe, South America and a third continent of your choice"),
    OBJECTIVE8("Conquer all of Europe, Oceania and a third continent of your choice"),
    OBJECTIVE9("Destroy all the Yellow troops. Note: If the Yellow troops are not present " +
               "in the game, or if your own troops are Yellow, or if the Yellow troops are " +
               "eliminated by another player, your objective becomes conquering 24 territories."),
    OBJECTIVE10("Destroy all the Red troops. Note: If the Red troops are not present " +
                "in the game, or if your own troops are Red, or if the Red troops are " +
                "eliminated by another player, your objective becomes conquering 24 territories."),
    OBJECTIVE11("Destroy all the Green troops. Note: If the Green troops are not present " +
                "in the game, or if your own troops are Green, or if the Green troops are " +
                "eliminated by another player, your objective becomes conquering 24 territories."),
    OBJECTIVE12("Destroy all the Blue troops. Note: If the Blue troops are not present " + 
                "in the game, or if your own troops are Blue, or if the Blue troops are " + 
                "eliminated by another player, your objective becomes conquering 24 territories."),
    OBJECTIVE13("Destroy all the Pink troops. Note: If the Pink troops are not present " +
                "in the game, or if your own troops are Pink, or if the Pink troops are " + 
                "eliminated by another player, your objective becomes conquering 24 territories."),
    OBJECTIVE14("Destroy all the Black troops. Note: If the Black troops are not present " +  
                "in the game, or if your own troops are Black, or if the Black troops are " +  
                "eliminated by another player, your objective becomes conquering 24 territories."),
    OBJECTIVEX("Your last objective could not be met. New Objective: Conquer 24 territories");

    private final String objectiveDescription;

    // Constructor
    private CardObjectives (String objectiveDescription) {
        this.objectiveDescription = objectiveDescription;
    }

    // Getter
    public String getObjectiveDescription () {
        return this.objectiveDescription;
    }
}
