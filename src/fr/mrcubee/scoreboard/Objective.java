package fr.mrcubee.scoreboard;

import fr.mrcubee.bukkit.packet.GenericPacketPlayOutScoreboardDisplayObjective;
import fr.mrcubee.bukkit.packet.GenericPacketPlayOutScoreboardObjective;
import fr.mrcubee.bukkit.packet.GenericPacketPlayOutScoreboardScore;
import fr.mrcubee.bukkit.scoreboard.ObjectiveAction;
import fr.mrcubee.bukkit.scoreboard.ObjectiveFormat;
import fr.mrcubee.bukkit.scoreboard.ObjectiveLocation;
import fr.mrcubee.bukkit.scoreboard.ScoreAction;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class Objective {

    private final String originName;
    private String name;
    private String displayName;
    private ObjectiveFormat objectiveFormat;
    private ObjectiveLocation location;
    private final ConcurrentLinkedQueue<OfflinePlayer> receivers;
    private final ConcurrentLinkedQueue<Score> scores;

    private Objective(String name, String displayName) {
        this.originName = name;
        this.name = name;
        this.displayName = displayName;
        this.objectiveFormat = ObjectiveFormat.INTEGER;
        this.location = ObjectiveLocation.SIDEBAR;
        this.receivers = new MemberLinkedQueue(this);
        this.scores = new ConcurrentLinkedQueue<Score>() {
            @Override
            public boolean add(Score score) {
                if (score == null)
                    return false;
                return super.add(score);
            }

            @Override
            public boolean addAll(Collection<? extends Score> collection) {
                return super.addAll(collection.stream().filter(Objects::nonNull).collect(Collectors.toList()));
            }
        };
    }

    public String getOriginName() {
        return this.originName;
    }

    public String getName() {
        return this.name;
    }

    public String toggleName() {
        String old = this.name;

        if (this.name.endsWith("1"))
            this.name = this.name.substring(0, this.name.length() - 1);
        else
            this.name += "1";
        return old;
    }

    public boolean setDisplayName(String displayName) {
        if (displayName == null)
            return false;
        this.displayName = displayName;
        updateScores(false);
        return true;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    protected ConcurrentLinkedQueue<Score> getScores() {
        return this.scores;
    }

    public void setObjectiveFormat(ObjectiveFormat objectiveFormat) {
        this.objectiveFormat = (objectiveFormat != null) ? objectiveFormat : ObjectiveFormat.INTEGER;
        updateScores(false);
    }

    public ObjectiveFormat getObjectiveFormat() {
        if (this.objectiveFormat == null)
            this.objectiveFormat = ObjectiveFormat.INTEGER;
        return this.objectiveFormat;
    }

    public void setLocation(ObjectiveLocation location) {
        this.location = (location != null) ? location : ObjectiveLocation.SIDEBAR;
        updateScores(false);
    }

    public ObjectiveLocation getLocation() {
        if (this.location == null)
            this.location = ObjectiveLocation.SIDEBAR;
        return this.location;
    }

    public ConcurrentLinkedQueue<OfflinePlayer> getReceivers() {
        return this.receivers;
    }

    protected void init(Player player) {
        this.create(player);
        this.displayTo(player, this.getLocation());
    }

    public boolean setScore(String playerName, int value) {
        Score score;

        if (playerName == null)
            return false;
        score = this.getScore(playerName);
        if (score != null)
            score.setScore(value);
        else {
            score = new Score(this, playerName, value);
            this.scores.add(score);
        }
        this.getReceivers().forEach(offlinePlayer -> updateScore(offlinePlayer.getPlayer(), playerName, value));
        return true;
    }

    public boolean removeScore(String playerName) {
        GenericPacketPlayOutScoreboardScore packet;
        Score score;

        if (playerName == null)
            return false;
        score = this.getScore(playerName);
        if (score == null)
            return false;
        this.scores.remove(score);
        packet = GenericPacketPlayOutScoreboardScore.create();
        if (packet == null)
            return true;
        packet.setObjectiveName(this.name);
        packet.setPlayerName(playerName);
        packet.setScoreValue(0);
        packet.setScoreAction(ScoreAction.REMOVE);
        this.getReceivers().forEach(offlinePlayer -> packet.sendPlayer(offlinePlayer.getPlayer()));
        return true;
    }

    public Score getScore(String playerName) {
        if (playerName == null)
            return null;
        for (Score score : this.scores)
            if (score.getPlayerName().equals(playerName))
                return score;
        return null;
    }

    protected void create(Player player) {
        GenericPacketPlayOutScoreboardObjective packet;

        if (player == null)
            return;
        packet = GenericPacketPlayOutScoreboardObjective.create();
        if (packet == null)
            return;
        packet.setObjectiveName(this.name);
        packet.setObjectiveDisplayName(this.displayName);
        packet.setObjectiveFormat(this.objectiveFormat);
        packet.setAction(ObjectiveAction.CREATE);
        remove(player);
        packet.sendPlayer(player);
    }

    protected void remove(Player player) {
        GenericPacketPlayOutScoreboardObjective packet;

        if (player == null)
            return;
        packet = GenericPacketPlayOutScoreboardObjective.create();
        if (packet == null)
            return;
        packet.setObjectiveName(this.name);
        packet.setObjectiveDisplayName(this.displayName);
        packet.setObjectiveFormat(this.objectiveFormat);
        packet.setAction(ObjectiveAction.REMOVE);
        packet.sendPlayer(player);
    }

    protected void displayTo(Player player, ObjectiveLocation objectiveLocation) {
        GenericPacketPlayOutScoreboardDisplayObjective packet;

        if (player == null || objectiveLocation == null)
            return;
        packet = GenericPacketPlayOutScoreboardDisplayObjective.create();
        if (packet == null)
            return;
        packet.setObjectiveName(this.name);
        packet.setObjectiveLocation(objectiveLocation);
        packet.sendPlayer(player);
    }

    protected void update(Player player) {
        GenericPacketPlayOutScoreboardObjective packet;

        if (player == null)
            return;
        packet = GenericPacketPlayOutScoreboardObjective.create();
        if (packet == null)
            return;
        packet.setObjectiveName(this.name);
        packet.setObjectiveDisplayName(this.displayName);
        packet.setObjectiveFormat(this.objectiveFormat);
        packet.setAction(ObjectiveAction.UPDATE);
        packet.sendPlayer(player);
    }

    private void updateScore(Player player, String playerName, int value) {
        GenericPacketPlayOutScoreboardScore packet;

        if (player == null || playerName == null)
            return;
        packet = GenericPacketPlayOutScoreboardScore.create();
        if (packet == null)
            return;
        packet.setPlayerName(playerName);
        packet.setObjectiveName(this.name);
        packet.setScoreValue(value);
        packet.setScoreAction(ScoreAction.CHANGE);
        packet.sendPlayer(player);
    }

    public void updateScores(Player player) {
        if (player == null)
            return;
        this.getScores().forEach(score -> updateScore(player, score.getPlayerName(), score.getScore()));
    }

    public void updateScores(boolean force) {
        GenericPacketPlayOutScoreboardObjective packet = GenericPacketPlayOutScoreboardObjective.create();
        String old;

        if (packet == null)
            return;
        if (force) {
            old = toggleName();
            packet.setObjectiveName(old);
            packet.setObjectiveDisplayName(this.displayName);
            packet.setObjectiveFormat(this.objectiveFormat);
            packet.setAction(ObjectiveAction.REMOVE);
            getReceivers().forEach(offlinePlayer -> {
                Player player = offlinePlayer.getPlayer();

                if (player != null) {
                    create(player);
                    updateScores(player);
                    displayTo(player, this.getLocation());
                    packet.sendPlayer(player);
                }
            });
        } else
            this.getReceivers().forEach(offlinePlayer -> updateScores(offlinePlayer.getPlayer()));
    }

    public static Objective create(String name, String displayName) {
        return new Objective(name, displayName);
    }
}
