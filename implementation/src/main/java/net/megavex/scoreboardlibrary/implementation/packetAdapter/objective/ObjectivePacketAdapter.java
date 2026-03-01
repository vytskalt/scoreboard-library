package net.megavex.scoreboardlibrary.implementation.packetAdapter.objective;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ObjectivePacketAdapter {
  @NotNull String objectiveName();

  void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot);

  void sendProperties(
    @NotNull Collection<Player> players,
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType,
    @Nullable ScoreFormat scoreFormat
  );

  void remove(@NotNull Collection<Player> players);

  void sendScore(
    @NotNull Collection<Player> players,
    @NotNull String entry,
    int value,
    @Nullable Component display,
    @Nullable ScoreFormat scoreFormat
  );

  void removeScore(@NotNull Collection<Player> players, @NotNull String entry);
}
