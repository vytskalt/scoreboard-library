package net.megavex.scoreboardlibrary.implementation.objective;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveScore;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.kyori.adventure.text.Component.empty;

public class ScoreboardObjectiveImpl implements ScoreboardObjective {
  private final ObjectivePacketAdapter packetAdapter;
  private final Queue<ObjectiveManagerTask> taskQueue;
  private final String name;

  private final Map<String, ObjectiveScore> scores = new HashMap<>();
  private Component value = empty();
  private ObjectiveRenderType renderType = ObjectiveRenderType.INTEGER;
  private ScoreFormat defaultScoreFormat;
  private boolean closed;
  private boolean wasDisplayed;

  public ScoreboardObjectiveImpl(@NotNull ObjectivePacketAdapter packetAdapter, @NotNull Queue<ObjectiveManagerTask> taskQueue, @NotNull String name) {
    this.packetAdapter = packetAdapter;
    this.taskQueue = taskQueue;
    this.name = name;
  }

  public ObjectivePacketAdapter packetAdapter() {
    return packetAdapter;
  }

  public @NotNull Map<String, ObjectiveScore> scores() {
    return scores;
  }

  public @NotNull String name() {
    return name;
  }

  public void close() {
    closed = true;
  }

  @Override
  public @NotNull Component value() {
    return value;
  }

  @Override
  public @NotNull ScoreboardObjective value(@NotNull ComponentLike value) {
    Component component = value.asComponent();
    if (!this.value.equals(component)) {
      this.value = component;
      if (!closed) {
        taskQueue.add(new ObjectiveManagerTask.UpdateObjective(this));
      }
    }
    return this;
  }

  @Override
  public @NotNull ObjectiveRenderType renderType() {
    return renderType;
  }

  @Override
  public @NotNull ScoreboardObjective renderType(@NotNull ObjectiveRenderType renderType) {
    if (this.renderType != renderType) {
      this.renderType = renderType;
      if (!closed) {
        taskQueue.add(new ObjectiveManagerTask.UpdateObjective(this));
      }
    }
    return this;
  }

  @Override
  public @Nullable ScoreFormat defaultScoreFormat() {
    return defaultScoreFormat;
  }

  @Override
  public void defaultScoreFormat(@Nullable ScoreFormat defaultScoreFormat) {
    if (!Objects.equals(this.defaultScoreFormat, defaultScoreFormat)) {
      this.defaultScoreFormat = defaultScoreFormat;
      taskQueue.add(new ObjectiveManagerTask.UpdateObjective(this));
    }
  }

  @Override
  public void refreshProperties() {
    if (!closed) {
      taskQueue.add(new ObjectiveManagerTask.UpdateObjective(this));
    }
  }

  @Override
  public @Nullable ObjectiveScore scoreInfo(@NotNull String entry) {
    return scores.get(entry);
  }

  @Override
  public @NotNull ScoreboardObjective score(@NotNull String entry, ObjectiveScore score) {
    ObjectiveScore oldScore = scores.put(entry, score);
    if (!Objects.equals(oldScore, score)) {
      taskQueue.add(new ObjectiveManagerTask.UpdateScore(this, entry, score));
    }
    return this;
  }

  @Override
  public @NotNull ScoreboardObjective removeScore(@NotNull String entry) {
    if (scores.remove(entry) != null) {
      taskQueue.add(new ObjectiveManagerTask.UpdateScore(this, entry, null));
    }
    return this;
  }

  @Override
  public void refreshScore(@NotNull String entry) {
    ObjectiveScore score = scores.get(entry);
    if (score != null) {
      taskQueue.add(new ObjectiveManagerTask.UpdateScore(this, entry, score));
    }
  }

  public void sendProperties(@NotNull Collection<Player> players, @NotNull PropertiesPacketType packetType) {
    packetAdapter.sendProperties(players, packetType, value, renderType, defaultScoreFormat);
  }

  public boolean wasDisplayed() {
    return wasDisplayed;
  }

  public void wasDisplayed(boolean wasDisplayed) {
    this.wasDisplayed = wasDisplayed;
  }
}
