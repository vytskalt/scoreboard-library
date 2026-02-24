package net.megavex.scoreboardlibrary.implementation.team;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class TeamManagerTask {
  private TeamManagerTask() {
  }

  public static final class Close extends TeamManagerTask {
    public static final Close INSTANCE = new Close();

    private Close() {
    }
  }

  public static final class AddPlayer extends TeamManagerTask {
    private final Player player;

    public AddPlayer(@NotNull Player player) {
      this.player = player;
    }

    public @NotNull Player player() {
      return player;
    }
  }

  public static final class RemovePlayer extends TeamManagerTask {
    private final Player player;
    private final CompletableFuture<Void> future;

    public RemovePlayer(@NotNull Player player, @Nullable CompletableFuture<Void> future) {
      this.player = player;
      this.future = future;
    }

    public @NotNull Player player() {
      return player;
    }

    public @Nullable CompletableFuture<Void> future() {
      return future;
    }
  }

  public static final class ReloadPlayer extends TeamManagerTask {
    private final Player player;

    public ReloadPlayer(@NotNull Player player) {
      this.player = player;
    }

    public @NotNull Player player() {
      return player;
    }
  }

  public static final class AddTeam extends TeamManagerTask {
    private final ScoreboardTeamImpl team;

    public AddTeam(@NotNull ScoreboardTeamImpl team) {
      this.team = team;
    }

    public @NotNull ScoreboardTeamImpl team() {
      return team;
    }
  }

  public static final class RemoveTeam extends TeamManagerTask {
    private final ScoreboardTeamImpl team;

    public RemoveTeam(@NotNull ScoreboardTeamImpl team) {
      this.team = team;
    }

    public @NotNull ScoreboardTeamImpl team() {
      return team;
    }
  }

  public static final class UpdateTeamDisplay extends TeamManagerTask {
    private final TeamDisplayImpl teamDisplay;

    public UpdateTeamDisplay(@NotNull TeamDisplayImpl teamDisplay) {
      this.teamDisplay = teamDisplay;
    }

    public @NotNull TeamDisplayImpl teamDisplay() {
      return teamDisplay;
    }
  }

  public static final class AddEntries extends TeamManagerTask {
    private final TeamDisplayImpl teamDisplay;
    private final Collection<String> entry;

    public AddEntries(@NotNull TeamDisplayImpl teamDisplay, @NotNull Collection<String> entries) {
      this.teamDisplay = teamDisplay;
      this.entry = entries;
    }

    public @NotNull TeamDisplayImpl teamDisplay() {
      return teamDisplay;
    }

    public @NotNull Collection<String> entries() {
      return entry;
    }
  }

  public static final class RemoveEntries extends TeamManagerTask {
    private final TeamDisplayImpl teamDisplay;
    private final Collection<String> entry;

    public RemoveEntries(@NotNull TeamDisplayImpl teamDisplay, @NotNull Collection<String> entries) {
      this.teamDisplay = teamDisplay;
      this.entry = entries;
    }

    public @NotNull TeamDisplayImpl teamDisplay() {
      return teamDisplay;
    }

    public @NotNull Collection<String> entries() {
      return entry;
    }
  }

  public static final class ChangeTeamDisplay extends TeamManagerTask {
    private final Player player;
    private final ScoreboardTeamImpl team;
    private final TeamDisplayImpl oldTeamDisplay;
    private final TeamDisplayImpl newTeamDisplay;

    public ChangeTeamDisplay(@NotNull Player player, @NotNull ScoreboardTeamImpl team, @NotNull TeamDisplayImpl oldTeamDisplay, @NotNull TeamDisplayImpl newTeamDisplay) {
      this.player = player;
      this.team = team;
      this.oldTeamDisplay = oldTeamDisplay;
      this.newTeamDisplay = newTeamDisplay;
    }

    public @NotNull Player player() {
      return player;
    }

    public @NotNull ScoreboardTeamImpl team() {
      return team;
    }

    public @NotNull TeamDisplayImpl oldTeamDisplay() {
      return oldTeamDisplay;
    }

    public @NotNull TeamDisplayImpl newTeamDisplay() {
      return newTeamDisplay;
    }
  }
}
