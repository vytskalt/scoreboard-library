package net.megavex.scoreboardlibrary.implementation.player;

import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.objective.ObjectiveManagerImpl;
import net.megavex.scoreboardlibrary.implementation.objective.ObjectiveManagerTask;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.PlayerDependantLocaleSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.SidebarTask;
import net.megavex.scoreboardlibrary.implementation.team.TeamManagerImpl;
import net.megavex.scoreboardlibrary.implementation.team.TeamManagerTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.jetbrains.annotations.NotNull;

public final class LocaleListener implements Listener {
  private final ScoreboardLibraryImpl scoreboardLibrary;

  public LocaleListener(@NotNull ScoreboardLibraryImpl scoreboardLibrary) {
    this.scoreboardLibrary = scoreboardLibrary;
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerLocaleChanged(PlayerLocaleChangeEvent event) {
    Player player = event.getPlayer();

    // Need to wait a tick because the locale didn't update yet
    scoreboardLibrary.taskScheduler().runNextTick(() -> {
      ScoreboardLibraryPlayer slPlayer = scoreboardLibrary.getPlayer(player);
      if (slPlayer != null) {
        TeamManagerImpl teamManager = slPlayer.teamManagerQueue().current();
        if (teamManager != null) {
          teamManager.taskQueue().add(new TeamManagerTask.ReloadPlayer(player));
        }

        ObjectiveManagerImpl objectiveManager = slPlayer.objectiveManagerQueue().current();
        if (objectiveManager != null) {
          objectiveManager.taskQueue().add(new ObjectiveManagerTask.ReloadPlayer(player));
        }

        AbstractSidebar sidebar = slPlayer.sidebarQueue().current();
        if (sidebar instanceof PlayerDependantLocaleSidebar) {
          sidebar.taskQueue().add(new SidebarTask.ReloadPlayer(player));
        }
      }
    });
  }
}
