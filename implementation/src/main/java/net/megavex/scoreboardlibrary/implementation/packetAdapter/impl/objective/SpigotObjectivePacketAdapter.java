package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.objective;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.PacketAdapterProviderImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsComponent;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public final class SpigotObjectivePacketAdapter extends AbstractObjectivePacketAdapter {
  public SpigotObjectivePacketAdapter(@NotNull PacketAdapterProviderImpl provider, @NotNull String objectiveName) {
    super(provider, objectiveName);
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players, @NotNull String entry, int value, @Nullable Component display, @Nullable ScoreFormat scoreFormat) {
    LocalePacketUtil.sendLocalePackets(provider.packetSender(), players, locale -> {
      Object nmsDisplay = display == null ? null : NmsComponent.fromAdventure(display, locale);
      Object numberFormat = NmsScoreFormat.convert(locale, scoreFormat);
      return createScorePacket(entry, value, nmsDisplay, numberFormat);
    });
  }

  @Override
  public void sendProperties(
    @NotNull Collection<Player> players,
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType,
    @Nullable ScoreFormat scoreFormat
  ) {
    LocalePacketUtil.sendLocalePackets(
      provider.packetSender(),
      players,
      locale -> {
        Object numberFormat = NmsScoreFormat.convert(locale, scoreFormat);
        return createObjectivePacket(packetType, NmsComponent.fromAdventure(value, locale), renderType, numberFormat);
      }
    );
  }
}
