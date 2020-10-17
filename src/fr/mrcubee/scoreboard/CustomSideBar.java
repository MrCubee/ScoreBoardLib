package fr.mrcubee.scoreboard;

import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CustomSideBar {

    private final Objective objective;
    private final Map<Integer, Integer> hash;
    private final Map<Integer, String> lines;
    private int oldSize;

    protected CustomSideBar(String name, String displayName) {
        this.objective = Objective.create(name, displayName);
        this.hash = new HashMap<Integer, Integer>();
        this.lines = new HashMap<Integer, String>();
        this.oldSize = 0;
    }

    public ConcurrentLinkedQueue<OfflinePlayer> getReceivers() {
        return this.objective.getReceivers();
    }

    public boolean setLine(int number, String line) {
        if (line == null)
            return false;
        else if (this.hash.containsKey(number) && this.hash.get(number) == line.hashCode())
            return true;
        if (this.lines.containsKey(number)) {
            this.objective.removeScore(this.lines.remove(number));
            this.hash.remove(number);
        }
        this.lines.put(number, line);
        this.hash.put(number, line.hashCode());
        return this.objective.setScore(line, number);
    }

    public String getLine(int number, String line) {
        return this.lines.get(number);
    }

    public boolean removeLine(int number) {
        String line = this.lines.remove(number);

        this.hash.remove(number);
        if (line == null)
            return false;
        return this.objective.removeScore(line);
    }

    public void setLines(Collection<String> lines) {
        int size;
        int currentSize;

        if (lines == null || lines.isEmpty())
            currentSize = 0;
        else {
            size = lines.size();
            currentSize = size;
            for (String line : lines)
                setLine(size--, line);
        }
        for (int i = currentSize + 1; i <= this.oldSize; i++)
            removeLine(i);
        this.oldSize = currentSize;
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
