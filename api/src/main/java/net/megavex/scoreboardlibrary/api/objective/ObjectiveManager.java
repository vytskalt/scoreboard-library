package net.megavex.scoreboardlibrary.api.objective;

import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a group of {@link ScoreboardObjective}s.
 * To get an instance of this interface, use {@link ScoreboardLibrary#createObjectiveManager()}
 * Note: this interface is not thread-safe, meaning you can only use it from one thread at a time,
 * although it does not have to be the main thread.
 */
public interface ObjectiveManager {
  /**
   * Creates an objective with a name if one doesn't already exist and returns it.
   *
   * @param name name of the objective
   * @return objective
   */
  @NotNull ScoreboardObjective create(@NotNull String name);

  /**
   * Removes an objective.
   *
   * @param objective objective to remove
   */
  void remove(@NotNull ScoreboardObjective objective);

  /**
   * Updates which objective is shown in a display slot.
   *
   * @param displaySlot display slot value to show objective in
   * @param objective   objective to display in that display slot
   */
  void display(@NotNull ObjectiveDisplaySlot displaySlot, @NotNull ScoreboardObjective objective);

  /**
   * @return unmodifiable collection of viewers in this ObjectiveManager
   * @see #addPlayer
   * @see #removePlayer
   */
  @NotNull Collection<Player> players();

  /**
   * Adds a viewer to this ObjectiveManager.
   * Note that a player can only see a single ObjectiveManager at a time.
   * The ObjectiveManager will internally be added to a queue for this player who
   * will start seeing it once they are removed from all previous ObjectiveManagers.
   *
   * @param player player to add
   * @return whether the player was added
   */
  boolean addPlayer(@NotNull Player player);

  /**
   * Adds multiple viewers to this ObjectiveManager.
   *
   * @param players viewers to add
   * @see #addPlayer
   */
  default void addPlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      addPlayer(player);
    }
  }

  /**
   * Removes a viewer from this ObjectiveManager.
   *
   * @param player viewer to remove
   * @return whether the viewer was removed
   */
  boolean removePlayer(@NotNull Player player);

  /**
   * Removes multiple viewers from this ObjectiveManager
   *
   * @param players viewers to remove
   */
  default void removePlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      removePlayer(player);
    }
  }

  /**
   * Removes a viewer from this ObjectiveManager.
   *
   * @param player viewer to remove
   * @return future that will be completed once all removal packets have been sent in an async task
   */
  CompletableFuture<Void> removePlayerFuture(@NotNull Player player);

  /**
   * Closes this ObjectiveManager.
   * This must be called once you no longer need this ObjectiveManager to prevent a memory leak.
   */
  void close();

  /**
   * @return whether this ObjectiveManager is closed
   * @see #close
   */
  boolean closed();
}
