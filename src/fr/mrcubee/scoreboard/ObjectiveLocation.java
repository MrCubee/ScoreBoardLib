package fr.mrcubee.scoreboard;

public enum ObjectiveLocation {

    LIST,
    SIDEBAR,
    BELOW_NAME;

    public int getLocation() {
        return this.ordinal();
    }

}
