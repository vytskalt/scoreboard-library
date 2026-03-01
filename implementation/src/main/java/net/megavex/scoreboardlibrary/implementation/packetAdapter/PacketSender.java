package net.megavex.scoreboardlibrary.implementation.packetAdapter;

import org.bukkit.entity.Player;

public interface PacketSender<T> {
  void sendPacket(Player player, T packet);

  default void sendPacket(Iterable<Player> players, T packet) {
    for (Player player : players) {
      sendPacket(player, packet);
    }
  }
}
