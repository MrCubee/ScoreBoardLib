package fr.mrcubee.scoreboard;

public class Score {

    private final String playerName;
    private int score;

    protected Score(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    protected Score(String playerName) {
        this.playerName = playerName;
        this.score = 0;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
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

    public static Score create(String playerName, int score) {
        if (playerName == null)
            return null;
        return new Score(playerName, score);
    }
}
