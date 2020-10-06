package fr.mrcubee.scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScoreBoardManager {

    private final ScheduledExecutorService scheduledExecutorMonoThread;
    private final Map<UUID, Object> scoreboards;

    public ScoreBoardManager() {
        this.scheduledExecutorMonoThread = Executors.newScheduledThreadPool(1);
        this.scoreboards = new HashMap<UUID, Object>();
    }



}
