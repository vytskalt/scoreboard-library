package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GlobalLineInfo {
  private final String player;
  private final int line;
  private final TeamsPacketAdapter packetAdapter;
  private Component value;
  private ScoreFormat scoreFormat;
  private int objectiveScore;
  private boolean updateScore;

  public GlobalLineInfo(@NotNull AbstractSidebar sidebar, @NotNull String player, int line) {
    this.player = player;
    this.line = line;
    this.packetAdapter = sidebar
      .scoreboardLibrary()
      .packetAdapter()
      .createTeamPacketAdapter("sidebar_line_" + line);
  }

  public @NotNull String player() {
    return player;
  }

  public int line() {
    return line;
  }

  public @NotNull TeamsPacketAdapter packetAdapter() {
    return packetAdapter;
  }

  public @Nullable Component value() {
    return value;
  }

  public void value(@Nullable Component value) {
    this.value = value;
  }

  public @Nullable ScoreFormat scoreFormat() {
    return scoreFormat;
  }

  public void scoreFormat(@Nullable ScoreFormat scoreFormat) {
    this.scoreFormat = scoreFormat;
  }

  public int objectiveScore() {
    return objectiveScore;
  }

  public void objectiveScore(int objectiveScore) {
    this.objectiveScore = objectiveScore;
  }

  public boolean updateScore() {
    return updateScore;
  }

  public void updateScore(boolean updateScore) {
    this.updateScore = updateScore;
  }
}
