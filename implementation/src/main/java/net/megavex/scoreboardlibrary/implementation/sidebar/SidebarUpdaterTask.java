package net.megavex.scoreboardlibrary.implementation.sidebar;

import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.scheduler.RunningTask;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.logging.Level;

public final class SidebarUpdaterTask implements Runnable {
  private final ScoreboardLibraryImpl scoreboardLibrary;
  private final RunningTask task;
  private final Object lock = new Object();

  public SidebarUpdaterTask(@NotNull ScoreboardLibraryImpl scoreboardLibrary) {
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
      Iterator<AbstractSidebar> iterator = scoreboardLibrary.sidebars().iterator();
      while (iterator.hasNext()) {
        AbstractSidebar sidebar = iterator.next();
        boolean result;
        try {
          result = sidebar.tick();
        } catch (Exception e) {
          scoreboardLibrary.plugin().getLogger().log(Level.WARNING, "an error occurred while updating a Sidebar instance", e);
          continue;
        }

        if (!result) {
          iterator.remove();
        }
      }
    }
  }
}
