package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.util;

import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.State;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;

public final class ViaConnectionGuard {
  private ViaConnectionGuard() {
  }

  public static boolean isCurrentPlayConnection(
    ViaAPI<Player> via,
    Player player,
    UserConnection connection
  ) {
    final Channel channel = connection.getChannel();
    final ProtocolInfo protocolInfo = connection.getProtocolInfo();
    return channel != null
      && player.isOnline()
      && channel.isActive()
      && !connection.isPendingDisconnect()
      && protocolInfo != null
      // for some reason these states are LOGIN for native players?
      && protocolInfo.getServerState() == State.PLAY
      && protocolInfo.getClientState() == State.PLAY
      && via.getConnection(player.getUniqueId()) == connection;
  }
}
