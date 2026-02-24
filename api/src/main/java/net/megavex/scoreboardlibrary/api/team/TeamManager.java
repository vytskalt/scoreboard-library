package net.megavex.scoreboardlibrary.api.team;

import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a group of {@link ScoreboardTeam}s.
 * To get an instance of this interface, use {@link ScoreboardLibrary#createTeamManager()}.
 * Note: this interface is not thread-safe, meaning you can only use it from one thread at a time,
 * although it does not have to be the main thread.
 */
@ApiStatus.NonExtendable
public interface TeamManager {
  // Teams

  /**
   * @return unmodifiable collection of teams in this TeamManager
   */
  @NotNull Collection<ScoreboardTeam> teams();

  /**
   * Gets a team by its name.
   *
   * @param name name of team
   * @return team with the name, or null if one doesn't exist
   */
  @Nullable ScoreboardTeam team(@NotNull String name);

  /**
   * Check whether a team with a name exists.
   *
   * @param name name of team
   * @return whether a team by the name exists
   */
  boolean teamExists(@NotNull String name);

  /**
   * Creates a team with a name if one doesn't already exist and returns it.
   *
   * @param name name of team
   * @return team
   */
  default @NotNull ScoreboardTeam createIfAbsent(@NotNull String name) {
    return createIfAbsent(name, null);
  }

  /**
   * Creates a team with a name if one doesn't already exist and returns it.
   *
   * @param name                name of team
   * @param teamDisplayFunction a function that provides the team display to set for each player
   * @return team
   */
  @NotNull ScoreboardTeam createIfAbsent(@NotNull String name, @Nullable BiFunction<Player, ScoreboardTeam, TeamDisplay> teamDisplayFunction);

  /**
   * Removes a team by its name.
   *
   * @param name name of team
   * @return if there was a team with than name
   */
  boolean removeTeam(@NotNull String name);

  /**
   * Removes a team.
   *
   * @param team team to remove
   * @throws IllegalArgumentException if the provided team is not owned by this TeamManager
   */
  void removeTeam(@NotNull ScoreboardTeam team);

  // Players

  /**
   * @return unmodifiable collection of viewers in this TeamManager
   * @see #addPlayer
   * @see #removePlayer
   */
  @NotNull Collection<Player> players();

  /**
   * Adds a viewer to this TeamManager.
   * Note that a player can only see a single TeamManager at a time.
   * The TeamManager will internally be added to a queue for this player who
   * will start seeing it once they are removed from all previous TeamManagers.
   *
   * @param player player to add
   * @return whether the player was added
   */
  default boolean addPlayer(@NotNull Player player) {
    return addPlayer(player, null);
  }

  /**
   * Adds a viewer to this TeamManager.
   * Note that a player can only see a single TeamManager at a time.
   * The TeamManager will internally be added to a queue for this player who
   * will start seeing it once they are removed from all previous TeamManagers.
   *
   * @param player              player to add
   * @param teamDisplayFunction a function that provides the team display to set for each team
   * @return whether the player was added
   */
  boolean addPlayer(@NotNull Player player, @Nullable Function<ScoreboardTeam, TeamDisplay> teamDisplayFunction);

  /**
   * Adds multiple viewers to this TeamManager.
   *
   * @param players viewers to add
   * @see #addPlayer
   */
  default void addPlayers(@NotNull Collection<Player> players) {
    addPlayers(players, null);
  }

  /**
   * Adds multiple viewers to this TeamManager.
   *
   * @param players viewers to add
   * @param teamDisplayFunction a function that provides the team display to set for each team
   * @see #addPlayer
   */
  default void addPlayers(@NotNull Collection<Player> players, @Nullable Function<ScoreboardTeam, TeamDisplay> teamDisplayFunction) {
    for (Player player : players) {
      addPlayer(player, teamDisplayFunction);
    }
  }

  /**
   * Removes a viewer from this TeamManager.
   *
   * @param player viewer to remove
   * @return whether the viewer was removed
   */
  boolean removePlayer(@NotNull Player player);

  /**
   * Removes multiple viewers from this TeamManager.
   *
   * @param players viewers to remove
   */
  default void removePlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      removePlayer(player);
    }
  }

  /**
   * Removes a viewer from this TeamManager.
   *
   * @param player viewer to remove
   * @return future that will be completed once all removal packets have been sent in an async task
   */
  CompletableFuture<Void> removePlayerFuture(@NotNull Player player);

  // Close

  /**
   * Closes this TeamManager.
   * This must be called once you no longer need this TeamManager to prevent a memory leak.
   */
  void close();

  /**
   * @return Whether this TeamManager is closed
   * @see #close
   */
  boolean closed();
}
