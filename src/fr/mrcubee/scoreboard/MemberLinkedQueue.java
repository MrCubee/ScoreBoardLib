package fr.mrcubee.scoreboard;

import org.bukkit.OfflinePlayer;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MemberLinkedQueue extends ConcurrentLinkedQueue<OfflinePlayer> {

    private final Objective objective;

    public MemberLinkedQueue(Objective objective) {
        this.objective = objective;
    }

    @Override
    public boolean add(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null || !offlinePlayer.isOnline())
            return false;
        objective.init(offlinePlayer.getPlayer());
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
            objective.remove(offlinePlayer.getPlayer());
        return offlinePlayer;
    }

    @Override
    public boolean remove(Object o) {
        if (!super.remove(o))
            return false;
        if (o instanceof OfflinePlayer)
            objective.remove(((OfflinePlayer) o).getPlayer());
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        if (collection == null)
            return false;
        collection.forEach(this::remove);
        return true;
    }
}
