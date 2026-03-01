package net.megavex.scoreboardlibrary.implementation.objective;

import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.scheduler.RunningTask;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.logging.Level;

public final class ObjectiveUpdaterTask implements Runnable {
  private final ScoreboardLibraryImpl scoreboardLibrary;
  private final RunningTask task;
  private final Object lock = new Object();

  public ObjectiveUpdaterTask(@NotNull ScoreboardLibraryImpl scoreboardLibrary) {
    this.scoreboardLibrary = scoreboardLibrary;
    this.task = scoreboardLibrary.taskScheduler().runEveryTick(this);
  }

  public @NotNull RunningTask task() {
    return task;
  }

  public @NotNull Object lock() {
    return lock;
  }

  @Override
  public void run() {
    synchronized (lock) {
      Iterator<ObjectiveManagerImpl> iterator = scoreboardLibrary.objectiveManagers().iterator();
      while (iterator.hasNext()) {
        ObjectiveManagerImpl objectiveManager = iterator.next();
        boolean result;
        try {
          result = objectiveManager.tick();
        } catch (Exception e) {
          scoreboardLibrary.plugin().getLogger().log(Level.WARNING, "an error occurred while updating an ObjectiveManager instance", e);
          continue;
        }

        if (!result) {
          iterator.remove();
        }
      }
    }
  }
}
