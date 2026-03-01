package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.MinecraftClasses;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class LegacyPacketSender implements PacketSender<Object> {
  public static final LegacyPacketSender INSTANCE = new LegacyPacketSender();

  private static final MethodHandle GET_HANDLE_METHOD, PLAYER_CONNECTION_FIELD, SEND_PACKET_METHOD;

  static {
    Class<?> packetClass = ReflectUtil.getClassOrThrow(LegacyMinecraftClasses.server("Packet"));
    Class<?> craftPlayerClass = ReflectUtil.getClassOrThrow(MinecraftClasses.craftBukkit("entity.CraftPlayer"));
    MethodHandles.Lookup lookup = MethodHandles.lookup();

    try {
      Method getHandleMethod = craftPlayerClass.getMethod("getHandle");
      GET_HANDLE_METHOD = lookup.unreflect(getHandleMethod);

      Field playerConnectionField = getHandleMethod.getReturnType().getField("playerConnection");
      PLAYER_CONNECTION_FIELD = lookup.unreflectGetter(playerConnectionField);

      Method sendPacketMethod = playerConnectionField.getType().getMethod("sendPacket", packetClass);
      SEND_PACKET_METHOD = lookup.unreflect(sendPacketMethod);
    } catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private LegacyPacketSender() {
  }

  @Override
  public void sendPacket(Player player, Object packet) {
    try {
      Object handle = GET_HANDLE_METHOD.invoke(player);
      Object playerConnection = PLAYER_CONNECTION_FIELD.invoke(handle);
      SEND_PACKET_METHOD.invoke(playerConnection, packet);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
