package net.megavex.scoreboardlibrary.implementation.objective;

import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveScore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ObjectiveManagerTask {
  private ObjectiveManagerTask() {
  }

  public static final class Close extends ObjectiveManagerTask {
    public static final Close INSTANCE = new Close();

    private Close() {
    }
  }

  public static final class AddPlayer extends ObjectiveManagerTask {
    private final Player player;

    public AddPlayer(@NotNull Player player) {
      this.player = player;
    }

    public @NotNull Player player() {
      return player;
    }
  }

  public static final class RemovePlayer extends ObjectiveManagerTask {
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

  public static final class ReloadPlayer extends ObjectiveManagerTask {
    private final Player player;

    public ReloadPlayer(@NotNull Player player) {
      this.player = player;
    }

    public @NotNull Player player() {
      return player;
    }
  }

  public static final class AddObjective extends ObjectiveManagerTask {
    private final ScoreboardObjectiveImpl objective;

    public AddObjective(@NotNull ScoreboardObjectiveImpl team) {
      this.objective = team;
    }

    public @NotNull ScoreboardObjectiveImpl objective() {
      return objective;
    }
  }

  public static final class RemoveObjective extends ObjectiveManagerTask {
    private final ScoreboardObjectiveImpl objective;

    public RemoveObjective(@NotNull ScoreboardObjectiveImpl team) {
      this.objective = team;
    }

    public @NotNull ScoreboardObjectiveImpl objective() {
      return objective;
    }
  }

  public static final class UpdateObjective extends ObjectiveManagerTask {
    private final ScoreboardObjectiveImpl objective;

    public UpdateObjective(@NotNull ScoreboardObjectiveImpl objective) {
      this.objective = objective;
    }

    public @NotNull ScoreboardObjectiveImpl objective() {
      return objective;
    }
  }

  public static final class UpdateScore extends ObjectiveManagerTask {
    private final ScoreboardObjectiveImpl objective;
    private final String entry;
    private final ObjectiveScore score;

    public UpdateScore(@NotNull ScoreboardObjectiveImpl objective, @NotNull String entry, @Nullable ObjectiveScore score) {
      this.objective = objective;
      this.entry = entry;
      this.score = score;
    }

    public @NotNull ScoreboardObjectiveImpl objective() {
      return objective;
    }

    public @NotNull String entry() {
      return entry;
    }

    public @Nullable ObjectiveScore score() {
      return score;
    }
  }

  public static final class UpdateDisplaySlot extends ObjectiveManagerTask {
    private final ObjectiveDisplaySlot displaySlot;
    private final ScoreboardObjectiveImpl objective;

    public UpdateDisplaySlot(@NotNull ObjectiveDisplaySlot displaySlot, @NotNull ScoreboardObjectiveImpl objective) {
      this.displaySlot = displaySlot;
      this.objective = objective;
    }

    public @NotNull ObjectiveDisplaySlot displaySlot() {
      return displaySlot;
    }

    public @NotNull ScoreboardObjectiveImpl objective() {
      return objective;
    }
  }
}
