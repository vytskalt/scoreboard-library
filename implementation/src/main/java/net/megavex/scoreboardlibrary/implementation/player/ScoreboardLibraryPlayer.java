package net.megavex.scoreboardlibrary.implementation.player;

import net.megavex.scoreboardlibrary.implementation.objective.ObjectiveManagerImpl;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.team.TeamManagerImpl;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class ScoreboardLibraryPlayer {
  private final DisplayableQueue<TeamManagerImpl> teamManagerQueue;
  private final DisplayableQueue<ObjectiveManagerImpl> objectiveManagerQueue;
  private final DisplayableQueue<AbstractSidebar> sidebarQueue;

  public ScoreboardLibraryPlayer(@NotNull Player player) {
    UUID uuid = player.getUniqueId();
    this.teamManagerQueue = new DisplayableQueue<>(uuid);
    this.objectiveManagerQueue = new DisplayableQueue<>(uuid);
    this.sidebarQueue = new DisplayableQueue<>(uuid);
  }

  public @NotNull DisplayableQueue<TeamManagerImpl> teamManagerQueue() {
    return teamManagerQueue;
  }

  public @NotNull DisplayableQueue<ObjectiveManagerImpl> objectiveManagerQueue() {
    return objectiveManagerQueue;
  }

  public @NotNull DisplayableQueue<AbstractSidebar> sidebarQueue() {
    return sidebarQueue;
  }
}
