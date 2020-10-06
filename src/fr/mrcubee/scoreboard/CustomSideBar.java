package fr.mrcubee.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CustomSideBar {

    private Objective objective;
    private Map<Integer, String> lines;

    protected CustomSideBar(String name, String displayName) {
        this.objective = Objective.create(name, displayName);
        this.lines = new HashMap<Integer, String>();
    }

    public ConcurrentLinkedQueue<OfflinePlayer> getReceivers() {
        return this.objective.getReceivers();
    }

    public boolean setLine(int number, String line) {
        if (line == null)
            return false;
        if (this.lines.containsKey(number))
            this.objective.removeScore(this.lines.remove(number));
        this.lines.put(number, line);
        return this.objective.setScore(line, number);
    }

    public String getLine(int number, String line) {
        return this.lines.get(number);
    }

    public boolean removeLine(int number) {
        String line = this.lines.remove(number);

        if (line == null)
            return false;
        return this.objective.removeScore(line);
    }

    public boolean setDisplayName(String displayName) {
        return this.objective.setDisplayName(displayName);
    }

    public String getDisplayName() {
        return this.objective.getDisplayName();
    }

    public static CustomSideBar create(String name, String displayName) {
        if (name == null || displayName == null)
            return null;
        return new CustomSideBar(name, displayName);
    }
}
