package fr.mrcubee.scoreboard;

import fr.mrcubee.util.MinecraftVersion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public abstract class Objective {

    private final String originName;
    private String name;
    private String displayName;
    private ObjectiveFormat objectiveFormat;
    private ObjectiveLocation location;
    private final ConcurrentLinkedQueue<OfflinePlayer> receivers;
    private final ConcurrentLinkedQueue<Score> scores;

    protected Objective(String name, String displayName) {
        this.originName = name;
        this.name = name;
        this.displayName = displayName;
        this.objectiveFormat = ObjectiveFormat.INTEGER;
        this.location = ObjectiveLocation.SIDEBAR;
        this.receivers = new ConcurrentLinkedQueue<OfflinePlayer>() {
            @Override
            public boolean add(OfflinePlayer offlinePlayer) {
                if (offlinePlayer == null || !offlinePlayer.isOnline())
                    return false;
                init(offlinePlayer.getPlayer());
                return super.add(offlinePlayer);
            }

            @Override
            public boolean addAll(Collection<? extends OfflinePlayer> collection) {
                if (collection == null)
                    return false;
                collection.stream().forEach(this::add);
                return true;
            }

            @Override
            public OfflinePlayer remove() {
                OfflinePlayer offlinePlayer = super.remove();

                if (offlinePlayer.isOnline())
                    Objective.this.remove(offlinePlayer.getPlayer());
                return offlinePlayer;
            }

            @Override
            public boolean remove(Object o) {
                if (!super.remove(o))
                    return false;
                if (o instanceof OfflinePlayer)
                    Objective.this.remove(((OfflinePlayer) o).getPlayer());
                return true;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                if (collection == null)
                    return false;
                collection.stream().forEach(this::remove);
                return true;
            }
        };
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
        return true;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    protected ConcurrentLinkedQueue<Score> getScores() {
        return scores;
    }

    public void setObjectiveFormat(ObjectiveFormat objectiveFormat) {
        this.objectiveFormat = (objectiveFormat != null) ? objectiveFormat : ObjectiveFormat.INTEGER;
    }

    public ObjectiveFormat getObjectiveFormat() {
        if (this.objectiveFormat == null)
            this.objectiveFormat = ObjectiveFormat.INTEGER;
        return this.objectiveFormat;
    }

    public void setLocation(ObjectiveLocation location) {
        this.location = (location != null) ? location : ObjectiveLocation.SIDEBAR;
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
        return true;
    }

    public boolean removeScore(String playerName) {
        Score score;

        if (playerName == null)
            return false;
        score = this.getScore(playerName);
        if (score == null)
            return false;
        this.scores.remove(score);
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

    protected abstract void create(Player player);
    protected abstract void remove(Player player);
    protected abstract void displayTo(Player player, ObjectiveLocation objectiveLocation);
    protected abstract void update(Player player);
    protected abstract void updateScores(boolean force);
    
    public static Objective create(String name, String displayName) {
        String nmsVersion;
        Class<? extends Objective> objectiveClass;
        Constructor<? extends Objective> constructor;
        
        if (name == null || displayName == null)
            return null;
        nmsVersion = MinecraftVersion.getNMSVersion();
        try {
            objectiveClass = (Class<? extends Objective>) Class.forName("fr.mrcubee.scoreboard." + nmsVersion + ".CraftObjective");
            constructor = objectiveClass.getConstructor(String.class, String.class);
            return constructor.newInstance(name, displayName);
        } catch (Exception ignored) {}
        return null;
    }
}
