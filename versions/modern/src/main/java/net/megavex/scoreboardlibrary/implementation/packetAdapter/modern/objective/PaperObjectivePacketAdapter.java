package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAdapterProviderImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.NativeAdventureUtil;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PaperObjectivePacketAdapter extends AbstractObjectivePacketAdapter {
  public PaperObjectivePacketAdapter(@NotNull PacketAdapterProviderImpl packetAdapter, @NotNull ComponentProvider componentProvider, @NotNull String objectiveName) {
    super(packetAdapter, componentProvider, objectiveName);
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players, @NotNull String entry, int value, @Nullable Component display, @Nullable ScoreFormat scoreFormat) {
    net.minecraft.network.chat.Component nmsDisplay = display == null ? null : componentProvider.fromAdventure(display, null);
    Object numberFormat = ScoreFormatConverter.convert(componentProvider, null, scoreFormat);
    ClientboundSetScorePacket packet = createScorePacket(entry, value, nmsDisplay, numberFormat);
    sender.sendPacket(players, packet);
  }

  @Override
  public void sendProperties(
    @NotNull Collection<Player> players,
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType,
    @Nullable ScoreFormat scoreFormat
  ) {
    AdventureComponent nmsValue = NativeAdventureUtil.fromAdventureComponent(value);
    Object numberFormat = ScoreFormatConverter.convert(componentProvider, null, scoreFormat);
    ClientboundSetObjectivePacket packet = createObjectivePacket(packetType, nmsValue, renderType, numberFormat);
    sender.sendPacket(players, packet);
  }
}
