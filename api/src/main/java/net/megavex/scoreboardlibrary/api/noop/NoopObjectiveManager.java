package net.megavex.scoreboardlibrary.api.noop;

import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveManager;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

class NoopObjectiveManager implements ObjectiveManager {
  private final Map<String, ScoreboardObjective> objectives = new HashMap<>();
  private final Set<Player> players = new HashSet<>();
  private boolean isClosed = true;

  @Override
  public @NotNull ScoreboardObjective create(@NotNull String name) {
    return objectives.computeIfAbsent(name, i -> new NoopScoreboardObjective());
  }

  @Override
  public void remove(@NotNull ScoreboardObjective objective) {
    objectives.values().remove(objective);
  }

  @Override
  public void display(@NotNull ObjectiveDisplaySlot displaySlot, @NotNull ScoreboardObjective objective) {
  }

  @Override
  public @NotNull Collection<Player> players() {
    return Collections.unmodifiableSet(players);
  }

  @Override
  public boolean addPlayer(@NotNull Player player) {
    return players.add(player);
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    return players.remove(player);
  }

  @Override
  public CompletableFuture<Void> removePlayerFuture(@NotNull Player player) {
    players.remove(player);
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void close() {
    isClosed = true;
  }

  @Override
  public boolean closed() {
    return isClosed;
  }
}
