package fr.mrcubee.scoreboard;

public class Score {

    private final Objective objective;
    private final String playerName;
    private int score;

    protected Score(Objective objective, String playerName, int score) {
        this.objective = objective;
        this.playerName = playerName;
        this.score = score;
    }

    protected Score(Objective objective, String playerName) {
        this.objective = objective;
        this.playerName = playerName;
        this.score = 0;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
        this.objective.updateScores(false);
    }

    public void removeScore(int score) {
        this.score -= score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Objective getObjective() {
        return objective;
    }
}
