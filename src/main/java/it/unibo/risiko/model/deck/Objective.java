package it.unibo.risiko.model.deck;

/**
 * All of the classical objectives from Risiko.
 */
public enum Objective {
    OBJECTIVE1("Conquer 18 territories, the territories must have atleast two troops each", "objective1"),
    OBJECTIVE2("Conquer 24 territories", "objective2"),
    OBJECTIVE3("Conquer all of North America and Africa", "objective3"),
    OBJECTIVE4("Conquer all of North America and Oceania", "objective4"),
    OBJECTIVE5("Conquer all of Asia and South America", "objective5"),
    OBJECTIVE6("Conquer all of Asia and Africa", "objective6"),
    OBJECTIVE7("Conquer all of Europe, South America and a third continent of your choice", "objective7"),
    OBJECTIVE8("Conquer all of Europe, Oceania and a third continent of your choice", "objective8"),
    OBJECTIVE9("Destroy all the Yellow troops. Note: If the Yellow troops are not present "
               + "in the game, or if your own troops are Yellow, or if the Yellow troops are "
               + suffix(), "objective9"),
    OBJECTIVE10("Destroy all the Red troops. Note: If the Red troops are not present "
                + "in the game, or if your own troops are Red, or if the Red troops are "
                + suffix(), "objective10"),
    OBJECTIVE11("Destroy all the Green troops. Note: If the Green troops are not present "
                + "in the game, or if your own troops are Green, or if the Green troops are "
                + suffix(), "objective11"),
    OBJECTIVE12("Destroy all the Blue troops. Note: If the Blue troops are not present "
                + "in the game, or if your own troops are Blue, or if the Blue troops are "
                + suffix(), "objective12"),
    OBJECTIVE13("Destroy all the Pink troops. Note: If the Pink troops are not present "
                + "in the game, or if your own troops are Pink, or if the Pink troops are "
                + suffix(), "objective13"),
    OBJECTIVE14("Destroy all the Black troops. Note: If the Black troops are not present "
                + "in the game, or if your own troops are Black, or if the Black troops are " 
                + suffix(), "objective14"),
    OBJECTIVEX("Your last objective could not be met. New Objective: Conquer 24 territories", "objectivex");

    private final String objectiveDescription;
    private final String id;

    /* Constructor */
    Objective(final String objectiveDescription, final String id) {
        this.objectiveDescription = objectiveDescription;
        this.id = id;
    }

    // hack to remove checkstyle repetition error
    private static String suffix() {
        return "eliminated by another player, your objective becomes conquering 24 territories.";
    }

    /**
     * @return the description of the objective
     */
    public String getObjectiveDescription() {
        return this.objectiveDescription;
    }

    /**
     * @return the id of the Objective
     */
    public String getObjectiveId() {
        return this.id;
    }
}
