package net.megavex.scoreboardlibrary.implementation.team;

import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamDisplay;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.player.PlayerDisplayable;
import net.megavex.scoreboardlibrary.implementation.player.ScoreboardLibraryPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TeamManagerImpl implements TeamManager, PlayerDisplayable {
  private final ScoreboardLibraryImpl scoreboardLibrary;

  private final Set<Player> players = CollectionProvider.set(8);
  private final List<Player> internalPlayers = CollectionProvider.list(8);

  private final Map<String, ScoreboardTeamImpl> teams = new ConcurrentHashMap<>();
  private final Queue<TeamManagerTask> taskQueue = new ConcurrentLinkedQueue<>();
  private boolean closed;

  public TeamManagerImpl(@NotNull ScoreboardLibraryImpl scoreboardLibrary) {
    this.scoreboardLibrary = scoreboardLibrary;
  }

  @Override
  public void close() {
    if (!closed) {
      closed = true;
      taskQueue.add(TeamManagerTask.Close.INSTANCE);
    }
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

    ScoreboardTeamImpl team = teams.get(name);
    if (team != null) {
      return team;
    }

    team = new ScoreboardTeamImpl(this, name);
    teams.put(name, team);

    for (Player player : players) {
      TeamDisplay teamDisplay = teamDisplayFunction == null ? team.defaultDisplay() : teamDisplayFunction.apply(player, team);
      validateTeamDisplay(team, teamDisplay);
      team.displayMap().put(player, (TeamDisplayImpl) teamDisplay);
    }

    TeamManagerTask.AddTeam task = new TeamManagerTask.AddTeam(team);
    taskQueue.add(task);
    return team;
  }

  @Override
  public boolean removeTeam(@NotNull String name) {
    Preconditions.checkNotNull(name);
    checkClosed();

    ScoreboardTeamImpl team = teams.remove(name.toLowerCase());
    if (team != null) {
      taskQueue.add(new TeamManagerTask.RemoveTeam(team));
      return true;
    }

    return false;
  }

  @Override
  public void removeTeam(@NotNull ScoreboardTeam team) {
    Preconditions.checkNotNull(team);
    Preconditions.checkArgument(team.teamManager() == this);
    checkClosed();

    if (teams.remove(team.name(), (ScoreboardTeamImpl) team)) {
      taskQueue.add(new TeamManagerTask.RemoveTeam((ScoreboardTeamImpl) team));
    }
  }

  @Override
  public boolean addPlayer(@NotNull Player player, @Nullable Function<ScoreboardTeam, TeamDisplay> teamDisplayFunction) {
    Preconditions.checkNotNull(player);
    checkClosed();

    if (!players.add(player)) {
      return false;
    }

    for (ScoreboardTeamImpl team : teams.values()) {
      TeamDisplay teamDisplay = teamDisplayFunction == null ? team.defaultDisplay() : teamDisplayFunction.apply(team);
      validateTeamDisplay(team, teamDisplay);
      team.displayMap().put(player, (TeamDisplayImpl) teamDisplay);
    }

    taskQueue.add(new TeamManagerTask.AddPlayer(player));
    return true;
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    checkClosed();

    if (!players.remove(player)) {
      return false;
    }

    TeamManagerTask.RemovePlayer task = new TeamManagerTask.RemovePlayer(player);
    taskQueue.add(task);
    return true;
  }

  public @NotNull ScoreboardLibraryImpl scoreboardLibrary() {
    return scoreboardLibrary;
  }

  public @NotNull Queue<TeamManagerTask> taskQueue() {
    return taskQueue;
  }

  @Override
  public void display(@NotNull Player player) {
    for (ScoreboardTeamImpl team : teams.values()) {
      team.addPlayer(player);
    }
  }

  public boolean tick() {
    while (true) {
      TeamManagerTask task = taskQueue.poll();
      if (task == null) {
        break;
      }

      if (task instanceof TeamManagerTask.Close) {
        for (ScoreboardTeamImpl team : teams.values()) {
          for (TeamDisplayImpl value : team.displayMap().values()) {
            value.packetAdapter().removeTeam(value.players());
          }
        }

        for (Player player : internalPlayers) {
          Objects.requireNonNull(scoreboardLibrary.getPlayer(player)).teamManagerQueue().remove(this);
        }
        return false;
      } else if (task instanceof TeamManagerTask.AddPlayer) {
        TeamManagerTask.AddPlayer addPlayerTask = (TeamManagerTask.AddPlayer) task;
        @NotNull ScoreboardLibraryPlayer slPlayer = scoreboardLibrary.getOrCreatePlayer(addPlayerTask.player());
        slPlayer.teamManagerQueue().add(this);
        internalPlayers.add(addPlayerTask.player());
      } else if (task instanceof TeamManagerTask.RemovePlayer) {
        TeamManagerTask.RemovePlayer removePlayerTask = (TeamManagerTask.RemovePlayer) task;
        for (ScoreboardTeamImpl team : teams.values()) {
          team.removePlayer(removePlayerTask.player());
        }

        internalPlayers.remove(removePlayerTask.player());
        Objects.requireNonNull(scoreboardLibrary.getPlayer(removePlayerTask.player())).teamManagerQueue().remove(this);
      } else if (task instanceof TeamManagerTask.ReloadPlayer) {
        TeamManagerTask.ReloadPlayer reloadPlayerTask = (TeamManagerTask.ReloadPlayer) task;
        for (ScoreboardTeamImpl team : teams.values()) {
          TeamDisplayImpl teamDisplay = team.displayMap().get(reloadPlayerTask.player());
          if (teamDisplay != null) {
            teamDisplay.packetAdapter().sendProperties(PropertiesPacketType.UPDATE, Collections.singleton(reloadPlayerTask.player()));
          }
        }
      } else if (task instanceof TeamManagerTask.AddTeam) {
        TeamManagerTask.AddTeam addTeamTask = (TeamManagerTask.AddTeam) task;
        ScoreboardTeamImpl team = addTeamTask.team();
        for (Player player : team.displayMap().keySet()) {
          ScoreboardLibraryPlayer slPlayer = scoreboardLibrary.getPlayer(player);
          if (slPlayer != null && slPlayer.teamManagerQueue().current() == this) {
            team.addPlayer(player);
          }
        }
      } else if (task instanceof TeamManagerTask.RemoveTeam) {
        TeamManagerTask.RemoveTeam removeTeamTask = (TeamManagerTask.RemoveTeam) task;
        for (TeamDisplayImpl value : removeTeamTask.team().displayMap().values()) {
          value.packetAdapter().removeTeam(value.players());
        }
      } else if (task instanceof TeamManagerTask.UpdateTeamDisplay) {
        TeamManagerTask.UpdateTeamDisplay updateTeamDisplayTask = (TeamManagerTask.UpdateTeamDisplay) task;
        @NotNull TeamDisplayImpl teamDisplay = updateTeamDisplayTask.teamDisplay();
        teamDisplay.handleUpdateDisplay();
      } else if (task instanceof TeamManagerTask.AddEntries) {
        TeamManagerTask.AddEntries addEntriesTask = (TeamManagerTask.AddEntries) task;
        @NotNull TeamDisplayImpl teamDisplay = addEntriesTask.teamDisplay();
        teamDisplay.handleAddEntries(addEntriesTask.entries());
      } else if (task instanceof TeamManagerTask.RemoveEntries) {
        TeamManagerTask.RemoveEntries removeEntriesTask = (TeamManagerTask.RemoveEntries) task;
        @NotNull TeamDisplayImpl teamDisplay = removeEntriesTask.teamDisplay();
        teamDisplay.handleRemoveEntries(removeEntriesTask.entries());
      } else if (task instanceof TeamManagerTask.ChangeTeamDisplay) {
        TeamManagerTask.ChangeTeamDisplay changeTeamDisplayTask = (TeamManagerTask.ChangeTeamDisplay) task;
        changeTeamDisplayTask.team().changeTeamDisplay(changeTeamDisplayTask.player(), changeTeamDisplayTask.oldTeamDisplay(), changeTeamDisplayTask.newTeamDisplay());
      }
    }
    return true;
  }

  private void checkClosed() {
    if (closed) {
      throw new IllegalStateException("TeamManager is closed");
    }
  }

  private void validateTeamDisplay(@NotNull ScoreboardTeam team, @Nullable TeamDisplay teamDisplay) {
    if (teamDisplay == null || teamDisplay.team() != team) {
      throw new IllegalArgumentException("invalid TeamDisplay");
    }

    if (!(teamDisplay instanceof TeamDisplayImpl)) {
      throw new IllegalArgumentException("must be TeamDisplayImpl");
    }
  }
}
