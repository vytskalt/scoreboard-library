package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public final class PacketUtil {
  private static final MethodHandle GET_HANDLE;
  private static final MethodHandle PLAYER_CONNECTION;
  private static final MethodHandle SEND_PACKET;

  private PacketUtil() {
  }

  static {
    String cbPackage = Bukkit.getServer().getClass().getPackage().getName();

    Class<?> craftPlayer;
    try {
      craftPlayer = Class.forName(cbPackage + ".entity.CraftPlayer");
    } catch (ClassNotFoundException e) {
      throw new ExceptionInInitializerError(e);
    }

    MethodHandles.Lookup lookup = MethodHandles.publicLookup();
    MethodType methodType = MethodType.methodType(ServerPlayer.class);
    try {
      GET_HANDLE = lookup.findVirtual(craftPlayer, "getHandle", methodType);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }

    MethodHandle playerConnection = null;
    for (Field field : ServerPlayer.class.getFields()) {
      if (field.getType() == ServerGamePacketListenerImpl.class) {
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

    MethodType sendMethodType = MethodType.methodType(void.class, Packet.class);
    MethodHandle sendPacket = null;

    String[] sendPacketNames = {"a", "sendPacket", "b", "send"};
    for (String name : sendPacketNames) {
      try {
        sendPacket = lookup.findVirtual(ServerGamePacketListenerImpl.class, name, sendMethodType);
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

  public static void sendPacket(Player player, Packet<?> packet) {
    try {
      ServerPlayer handle = (ServerPlayer) GET_HANDLE.invoke(player);
      ServerGamePacketListenerImpl connection = (ServerGamePacketListenerImpl) PLAYER_CONNECTION.invoke(handle);
      SEND_PACKET.invoke(connection, packet);
    } catch (Throwable e) {
      throw new IllegalStateException("couldn't send packet to player", e);
    }
  }
}
