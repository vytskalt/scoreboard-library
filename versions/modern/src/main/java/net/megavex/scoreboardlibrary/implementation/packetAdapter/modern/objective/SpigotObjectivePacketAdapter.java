package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class SpigotObjectivePacketAdapter extends AbstractObjectivePacketAdapter {
  public SpigotObjectivePacketAdapter(@NotNull String objectiveName) {
    super(objectiveName);
  }

  @Override
  public void sendScore(@NotNull Collection<Player> players, @NotNull String entry, int value, @Nullable Component display, @Nullable ScoreFormat scoreFormat) {
    LocalePacketUtil.sendLocalePackets(ModernPacketSender.INSTANCE, players, locale -> {
      Object nmsDisplay = display == null ? null : ModernComponentProvider.fromAdventure(display, locale);
      Object numberFormat = ScoreFormatConverter.convert(locale, scoreFormat);
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
      ModernPacketSender.INSTANCE,
      players,
      locale -> {
        Object numberFormat = ScoreFormatConverter.convert(locale, scoreFormat);
        return createObjectivePacket(packetType, ModernComponentProvider.fromAdventure(value, locale), renderType, numberFormat);
      }
    );
  }
}
