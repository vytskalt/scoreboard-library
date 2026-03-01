package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public final class ChatColorUtil {
  private static final MethodHandle FROM_NAME_METHOD, GET_INDEX_METHOD;

  static {
    Class<?> enumChatFormatClass = ReflectUtil.getClassOrThrow(LegacyMinecraftClasses.server("EnumChatFormat"));
    MethodHandles.Lookup lookup = MethodHandles.lookup();

    try {
      Method fromNameMethod = enumChatFormatClass.getMethod("b", String.class);
      FROM_NAME_METHOD = lookup.unreflect(fromNameMethod);

      Method getIndexMethod = enumChatFormatClass.getMethod("b");
      GET_INDEX_METHOD = lookup.unreflect(getIndexMethod);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private ChatColorUtil() {
  }

  public static int getColorIndex(@NotNull NamedTextColor color) {
    String name = NamedTextColor.NAMES.key(color);
    try {
      Object format = FROM_NAME_METHOD.invoke(name);
      return (int) GET_INDEX_METHOD.invoke(format);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
