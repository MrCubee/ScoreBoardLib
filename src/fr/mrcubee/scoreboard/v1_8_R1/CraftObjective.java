package fr.mrcubee.scoreboard.v1_8_R1;

import fr.mrcubee.scoreboard.Objective;
import fr.mrcubee.scoreboard.ObjectiveFormat;
import fr.mrcubee.scoreboard.ObjectiveLocation;
import fr.mrcubee.util.Reflection;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CraftObjective extends Objective {

    public CraftObjective(String name, String displayName) {
        super(name, displayName);
    }

    @Override
    public void setObjectiveFormat(ObjectiveFormat objectiveFormat) {
        super.setObjectiveFormat(objectiveFormat);
        this.getReceivers().forEach(offlinePlayer -> this.update(offlinePlayer.getPlayer()));
    }

    @Override
    public void setLocation(ObjectiveLocation location) {
        super.setLocation(location);
        this.getReceivers().forEach(offlinePlayer -> this.update(offlinePlayer.getPlayer()));
    }

    @Override
    public boolean setDisplayName(String displayName) {
        if (!super.setDisplayName(displayName))
            return false;
        this.getReceivers().forEach(offlinePlayer -> this.update(offlinePlayer.getPlayer()));
        return true;
    }

    @Override
    public boolean setScore(String playerName, int value) {
        if (!super.setScore(playerName, value))
            return false;
        this.getReceivers().forEach(offlinePlayer -> this.updateScore(offlinePlayer.getPlayer(), playerName, value));
        return true;
    }

    @Override
    public boolean removeScore(String playerName) {
        PacketPlayOutScoreboardScore packet;

        if (!super.removeScore(playerName))
            return false;
        packet = new PacketPlayOutScoreboardScore();
        Reflection.setValue(packet, "a", playerName);
        Reflection.setValue(packet, "b", this.getName());
        Reflection.setValue(packet, "c", 0);
        Reflection.setValue(packet, "d", EnumScoreboardAction.REMOVE);
        this.getReceivers().forEach(offlinePlayer -> {
            Player player = offlinePlayer.getPlayer();

            if (player != null)
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        });
        return true;
    }

    @Override
    protected void remove(Player player) {
        PacketPlayOutScoreboardObjective packet;

        if (player == null)
            return;
        packet = new PacketPlayOutScoreboardObjective();
        Reflection.setValue(packet, "a", this.getName());
        Reflection.setValue(packet, "b", this.getDisplayName());
        Reflection.setValue(packet, "c",
                EnumScoreboardHealthDisplay.values()[this.getObjectiveFormat().ordinal()]);
        Reflection.setValue(packet, "d", 1);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    protected void create(Player player) {
        PacketPlayOutScoreboardObjective packet;

        if (player == null)
            return;
        packet = new PacketPlayOutScoreboardObjective();
        Reflection.setValue(packet, "a", this.getName());
        Reflection.setValue(packet, "b", this.getDisplayName());
        Reflection.setValue(packet, "c",
                EnumScoreboardHealthDisplay.values()[this.getObjectiveFormat().ordinal()]);
        Reflection.setValue(packet, "d", 0);
        remove(player);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    protected void displayTo(Player player, ObjectiveLocation objectiveLocation) {
        PacketPlayOutScoreboardDisplayObjective packet;

        if (player == null || objectiveLocation == null)
            return;
        packet = new PacketPlayOutScoreboardDisplayObjective();
        Reflection.setValue(packet, "a", this.getLocation().ordinal());
        Reflection.setValue(packet, "b", this.getName());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    protected void update(Player player) {
        PacketPlayOutScoreboardObjective packet;

        if (player == null)
            return;
        packet = new PacketPlayOutScoreboardObjective();
        Reflection.setValue(packet, "a", this.getName());
        Reflection.setValue(packet, "b", this.getDisplayName());
        Reflection.setValue(packet, "c",
                EnumScoreboardHealthDisplay.values()[this.getObjectiveFormat().ordinal()]);
        Reflection.setValue(packet, "d", 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private void updateScore(Player player, String playerName, int value) {
        PacketPlayOutScoreboardScore packet;

        if (player == null || playerName == null)
            return;
        packet = new PacketPlayOutScoreboardScore();
        Reflection.setValue(packet, "a", playerName);
        Reflection.setValue(packet, "b", this.getName());
        Reflection.setValue(packet, "c", value);
        Reflection.setValue(packet, "d", EnumScoreboardAction.CHANGE);
    }

    private void updateScores(Player player) {
        if (player == null)
            return;
        this.getScores().forEach(score -> updateScore(player, score.getPlayerName(), score.getScore()));
    }

    @Override
    protected void updateScores(boolean force) {
        String old;
        PacketPlayOutScoreboardObjective packet;

        if (force) {
            old = toggleName();
            packet = new PacketPlayOutScoreboardObjective();
            Reflection.setValue(packet, "a", old);
            Reflection.setValue(packet, "b", this.getDisplayName());
            Reflection.setValue(packet, "c",
                    EnumScoreboardHealthDisplay.values()[this.getObjectiveFormat().ordinal()]);
            Reflection.setValue(packet, "d", 1);
            this.getReceivers().forEach(offlinePlayer -> {
                Player player = offlinePlayer.getPlayer();

                if (player != null) {
                    create(player);
                    updateScores(player);
                    displayTo(player, this.getLocation());
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                }
            });
        } else
            this.getReceivers().forEach(offlinePlayer -> updateScores(offlinePlayer.getPlayer()));
    }
}
