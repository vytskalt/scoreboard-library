package net.megavex.scoreboardlibrary.implementation.packetAdapter.util;

import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.commons.LocaleProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class LocalePacketUtil {
  private LocalePacketUtil() {
  }

  public static <P> void sendLocalePackets(
    @NotNull PacketSender<P> sender,
    @NotNull Collection<Player> players,
    @NotNull Function<Locale, P> packetFunction
  ) {
    if (players.isEmpty()) {
      return;
    }

    if (players.size() == 1) {
      Player player = players.iterator().next();
      P packet = packetFunction.apply(LocaleProvider.locale(player));
      sender.sendPacket(player, packet);
      return;
    }

    Map<Locale, P> map = CollectionProvider.map(1);
    for (Player player : players) {
      Locale locale = LocaleProvider.locale(player);
      P packet = map.computeIfAbsent(locale, i -> packetFunction.apply(locale));
      sender.sendPacket(player, packet);
    }
  }
}
