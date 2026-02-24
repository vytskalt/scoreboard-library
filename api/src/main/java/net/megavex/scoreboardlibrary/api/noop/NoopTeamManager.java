package net.megavex.scoreboardlibrary.api.noop;

import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamDisplay;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

class NoopTeamManager implements TeamManager {
  private final Set<Player> players = new HashSet<>();
  private final Map<String, NoopScoreboardTeam> teams = new HashMap<>();
  private boolean closed;

  @Override
  public void close() {
    closed = true;
  }

  @Override
  public boolean closed() {
    return closed;
  }

  @Override
  public @NotNull Collection<Player> players() {
    return closed ? Collections.emptySet() : Collections.unmodifiableSet(players);
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    checkClosed();

    if (!players.remove(player)) {
      return false;
    }

    for (NoopScoreboardTeam team : teams.values()) {
      team.displayMap().remove(player);
    }

    return true;
  }

  @Override
  public CompletableFuture<Void> removePlayerFuture(@NotNull Player player) {
    this.removePlayer(player);
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public @NotNull Collection<ScoreboardTeam> teams() {
    return closed ? Collections.emptySet() : Collections.unmodifiableCollection(teams.values());
  }

  @Override
  public @Nullable ScoreboardTeam team(@NotNull String name) {
    return teams.get(name.toLowerCase());
  }

  @Override
  public boolean teamExists(@NotNull String name) {
    return teams.containsKey(name.toLowerCase());
  }

  @Override
  public @NotNull ScoreboardTeam createIfAbsent(@NotNull String name, @Nullable BiFunction<Player, ScoreboardTeam, TeamDisplay> teamDisplayFunction) {
    Preconditions.checkNotNull(name);
    checkClosed();

    name = name.toLowerCase();
    NoopScoreboardTeam team = teams.get(name);
    if (team != null) {
      return team;
    }

    team = new NoopScoreboardTeam(this, name);
    for (Player player : players) {
      TeamDisplay teamDisplay = teamDisplayFunction == null ? team.defaultDisplay() : teamDisplayFunction.apply(player, team);
      validateTeamDisplay(team, teamDisplay);
      team.displayMap().put(player, teamDisplay);
    }
    teams.put(name, team);

    return team;
  }

  @Override
  public boolean removeTeam(@NotNull String name) {
    Preconditions.checkNotNull(name);
    checkClosed();

    return teams.remove(name.toLowerCase()) != null;
  }

  @Override
  public void removeTeam(@NotNull ScoreboardTeam team) {
    Preconditions.checkNotNull(team);
    Preconditions.checkArgument(team.teamManager() == this);
    checkClosed();

    teams.remove(team.name(), (NoopScoreboardTeam) team);
  }

  @Override
  public boolean addPlayer(@NotNull Player player, @Nullable Function<ScoreboardTeam, TeamDisplay> teamDisplayFunction) {
    Preconditions.checkNotNull(player);
    checkClosed();

    if (!players.add(player)) {
      return false;
    }

    for (NoopScoreboardTeam team : teams.values()) {
      TeamDisplay teamDisplay = teamDisplayFunction == null ? team.defaultDisplay() : teamDisplayFunction.apply(team);
      validateTeamDisplay(team, teamDisplay);
      team.displayMap().put(player, teamDisplay);
    }

    return true;
  }

  private void validateTeamDisplay(@NotNull ScoreboardTeam team, @Nullable TeamDisplay teamDisplay) {
    if (teamDisplay == null || teamDisplay.team() != team) {
      throw new IllegalArgumentException("invalid TeamDisplay");
    }

    if (!(teamDisplay instanceof NoopTeamDisplay)) {
      throw new IllegalArgumentException("must be TeamDisplayImpl");
    }
  }

  private void checkClosed() {
    if (closed) {
      throw new IllegalStateException("NoopTeamManager is closed");
    }
  }
}
