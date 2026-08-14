package net.megavex.scoreboardlibrary.implementation.packetAdapter.util;

import net.megavex.scoreboardlibrary.implementation.commons.LocaleProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsPacketSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class LocalePacketUtil {
  private LocalePacketUtil() {
  }

  public static void sendLocalePackets(
    @NotNull NmsPacketSender sender,
    @NotNull Collection<Player> players,
    @NotNull Function<Locale, Object> packetFunction
  ) {
    if (players.isEmpty()) {
      return;
    }

    if (players.size() == 1) {
      Player player = players.iterator().next();
      Object packet = packetFunction.apply(LocaleProvider.locale(player));
      sender.sendPacket(player, packet);
      return;
    }

    Map<Locale, Object> map = new HashMap<>(1);
    for (Player player : players) {
      Locale locale = LocaleProvider.locale(player);
      Object packet = map.computeIfAbsent(locale, i -> packetFunction.apply(locale));
      sender.sendPacket(player, packet);
    }
  }
}
