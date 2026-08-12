package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.util;

import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.connection.UserConnection;
import io.netty.channel.Channel;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.NmsAccessors;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public final class PacketSenderImpl implements PacketSender<Object> {
  private static final MethodHandle GET_HANDLE;
  private static final MethodHandle PLAYER_CONNECTION;
  private static final MethodHandle SEND_PACKET;

  private final ViaAPI<Player> via;

  public PacketSenderImpl(final ViaAPI<Player> via) {
    this.via = via;
  }

  static {
    MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    MethodType methodType = MethodType.methodType(NmsAccessors.SERVER_PLAYER_CLASS);
    try {
      GET_HANDLE = lookup.findVirtual(NmsAccessors.CRAFT_PLAYER_CLASS, "getHandle", methodType);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }

    MethodHandle playerConnection = null;
    for (Field field : NmsAccessors.SERVER_PLAYER_CLASS.getFields()) {
      if (field.getType() == NmsAccessors.PLAYER_CONNECTION_CLASS) {
        try {
          playerConnection = lookup.unreflectGetter(field);
        } catch (IllegalAccessException e) {
          throw new ExceptionInInitializerError(e);
        }
      }
    }

    if (playerConnection == null) {
      throw new ExceptionInInitializerError("failed to find player connection field");
    }
    PLAYER_CONNECTION = playerConnection;

    MethodType sendMethodType = MethodType.methodType(void.class, NmsAccessors.PKT_CLASS);
    MethodHandle sendPacket = null;

    String[] sendPacketNames = {"a", "sendPacket", "b", "send"};
    for (String name : sendPacketNames) {
      try {
        sendPacket = lookup.findVirtual(NmsAccessors.PLAYER_CONNECTION_CLASS, name, sendMethodType);
      } catch (NoSuchMethodException ignored) {
      } catch (IllegalAccessException e) {
        throw new ExceptionInInitializerError(e);
      }
    }

    if (sendPacket == null) {
      throw new ExceptionInInitializerError(new RuntimeException("Couldn't find send packet method"));
    }

    SEND_PACKET = sendPacket;
  }

  @Override
  public void sendPacket(Player player, Object packet) {
    if (this.via != null) {
      final UserConnection conn = this.via.getConnection(player.getUniqueId());
      if (conn != null) {
        final Channel channel = conn.getChannel();
        if (ViaConnectionGuard.isCurrentPlayConnection(this.via, player, conn)) {
          assert channel != null; // checked by ViaConnectionGuard

          // Paper has some "network optimization" patch that makes the NMS sendPacket method
          // not always send the packet immediately to the player's netty channel,
          // and this is a problem when we send some packets using ViaVersion API because it always sends
          // the packet to the channel immediately, so the packet order between NMS and ViaVersion API can
          // get messed up. So for ViaVersion players we send the packet directly to the player's channel
          // bypassing Paper logic to work around this. Fortunately ViaVersion provides easy access to the player's channel.
          channel.eventLoop().execute(() -> {
            // The player can disconnect or enter configuration while this write is queued. The
            // channel remains open in that state, but its encoder no longer accepts game packets.
            // Also guard against a reconnect replacing ViaVersion's UUID mapping in the meantime.
            if (ViaConnectionGuard.isCurrentPlayConnection(this.via, player, conn)) {
              channel.writeAndFlush(packet);
            }
          });
          return;
        }
      }
    }

    try {
      Object handle = GET_HANDLE.invoke(player);
      Object connection = PLAYER_CONNECTION.invoke(handle);
      SEND_PACKET.invoke(connection, packet);
    } catch (Throwable e) {
      throw new IllegalStateException("couldn't send packet to player", e);
    }
  }
}
