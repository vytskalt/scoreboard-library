package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.objective;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.PacketAdapterProviderImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.util.ComponentProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class PaperObjectivePacketAdapter extends AbstractObjectivePacketAdapter {
  public PaperObjectivePacketAdapter(@NotNull PacketAdapterProviderImpl provider, @NotNull String objectiveName) {
    super(provider, objectiveName);
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players, @NotNull String entry, int value, @Nullable Component display, @Nullable ScoreFormat scoreFormat) {
    Object nmsDisplay = display == null ? null : ComponentProvider.fromAdventure(display, null);
    Object numberFormat = ScoreFormatConverter.convert(null, scoreFormat);
    Object packet = createScorePacket(entry, value, nmsDisplay, numberFormat);
    provider.packetSender().sendPacket(players, packet);
  }

  @Override
  public void sendProperties(
    @NotNull Collection<Player> players,
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType,
    @Nullable ScoreFormat scoreFormat
  ) {
    Object nmsValue = PacketAccessors.fromAdventureComponent(value);
    Object numberFormat = ScoreFormatConverter.convert(null, scoreFormat);
    Object packet = createObjectivePacket(packetType, nmsValue, renderType, numberFormat);
    provider.packetSender().sendPacket(players, packet);
  }
}
