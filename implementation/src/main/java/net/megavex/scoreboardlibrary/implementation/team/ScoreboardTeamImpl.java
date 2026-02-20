package net.megavex.scoreboardlibrary.implementation.team;

import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamDisplay;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardTeamImpl implements ScoreboardTeam {
  private final TeamManagerImpl teamManager;
  private final String name;
  private final TeamsPacketAdapter packetAdapter;

  private final TeamDisplayImpl defaultDisplay;
  private final Map<Player, TeamDisplayImpl> displayMap = new ConcurrentHashMap<>();

  public ScoreboardTeamImpl(@NotNull TeamManagerImpl teamManager, @NotNull String name) {
    this.teamManager = teamManager;
    this.name = name;
    this.packetAdapter = teamManager.scoreboardLibrary().packetAdapter().createTeamPacketAdapter(name);
    this.defaultDisplay = new TeamDisplayImpl(this);
  }

  @Override
  public @NotNull TeamManagerImpl teamManager() {
    return teamManager;
  }

  @Override
  public @NotNull String name() {
    return name;
  }

  @Override
  public @NotNull TeamDisplayImpl defaultDisplay() {
    return defaultDisplay;
  }

  @Override
  public @NotNull TeamDisplay display(@NotNull Player player) {
    Preconditions.checkNotNull(player);

    if (!teamManager.players().contains(player)) {
      throw new IllegalArgumentException("player not in TeamManager");
    }

    return Objects.requireNonNull(displayMap.get(player));
  }

  @Override
  public void display(@NotNull Player player, @NotNull TeamDisplay teamDisplay) {
    Preconditions.checkNotNull(player);
    Preconditions.checkNotNull(teamDisplay);

    if (!teamManager.players().contains(player)) {
      throw new IllegalArgumentException("player not in TeamManager");
    }

    if (teamDisplay.team() != this || !(teamDisplay instanceof TeamDisplayImpl)) {
      throw new IllegalArgumentException("invalid TeamDisplay");
    }

    TeamDisplayImpl oldTeamDisplay = Objects.requireNonNull(displayMap.put(player, (TeamDisplayImpl) teamDisplay));
    if (oldTeamDisplay == teamDisplay) {
      return;
    }

    teamManager.taskQueue().add(new TeamManagerTask.ChangeTeamDisplay(player, this, oldTeamDisplay, (TeamDisplayImpl) teamDisplay));
  }

  @Override
  public @NotNull TeamDisplay createDisplay() {
    return new TeamDisplayImpl(this);
  }

  public @NotNull TeamsPacketAdapter packetAdapter() {
    return packetAdapter;
  }

  public @NotNull Map<Player, TeamDisplayImpl> displayMap() {
    return displayMap;
  }

  public void addPlayer(@NotNull Player player) {
    TeamDisplayImpl teamDisplay = Objects.requireNonNull(displayMap.get(player));
    if (teamDisplay.players().add(player)) {
      teamDisplay.packetAdapter().sendProperties(PropertiesPacketType.CREATE, Collections.singleton(player));
    }
  }

  public void removePlayer(@NotNull Player player) {
    TeamDisplayImpl teamDisplay = Objects.requireNonNull(displayMap.remove(player));
    if (teamDisplay.players().remove(player)) {
      teamDisplay.packetAdapter().removeTeam(Collections.singleton(player));
    }
  }

  public void changeTeamDisplay(@NotNull Player player, @NotNull TeamDisplayImpl oldTeamDisplay, @NotNull TeamDisplayImpl newTeamDisplay) {
    if (!oldTeamDisplay.players().remove(player)) {
      return;
    }

    newTeamDisplay.players().add(player);

    Collection<Player> singleton = Collections.singleton(player);
    newTeamDisplay.packetAdapter().sendProperties(PropertiesPacketType.UPDATE, singleton);

    Collection<String> oldEntries = oldTeamDisplay.syncedEntries();
    Collection<String> newEntries = newTeamDisplay.syncedEntries();

    if (oldEntries.isEmpty()) {
      newTeamDisplay.packetAdapter().sendEntries(EntriesPacketType.ADD, singleton, newEntries);
      return;
    }

    List<String> entries = new ArrayList<>(oldEntries);
    entries.removeAll(newEntries);
    if (!entries.isEmpty()) {
      newTeamDisplay.packetAdapter().sendEntries(EntriesPacketType.REMOVE, singleton, entries);
    }

    entries = new ArrayList<>(newEntries);
    entries.removeAll(oldEntries);
    if (!entries.isEmpty()) {
      newTeamDisplay.packetAdapter().sendEntries(EntriesPacketType.ADD, singleton, entries);
    }
  }
}
