package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util;

import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.MinecraftClasses;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public final class ModernComponentProvider {
  public static final boolean IS_NATIVE_ADVENTURE;

  private static final Object MINECRAFT_REGISTRY;
  private static final Object CODEC;

  private static final MethodHandle FROM_JSON_METHOD;

  private ModernComponentProvider() {
  }

  static {
    // Hide from relocation checkers
    String notRelocatedPackage = "net.ky".concat("ori.adventure.text");

    // The native adventure optimisations only work when the adventure library isn't relocated
    IS_NATIVE_ADVENTURE = PacketAccessors.ADVENTURE_COMPONENT_CLASS != null && Component.class.getPackage().getName().equals(notRelocatedPackage);

    if (PacketAccessors.IS_1_20_5_OR_ABOVE) {
      Class<?> craftRegistry;
      try {
        craftRegistry = Class.forName(MinecraftClasses.craftBukkit("CraftRegistry"));
      } catch (ClassNotFoundException e) {
        throw new ExceptionInInitializerError(e);
      }

      try {
        Method method = craftRegistry.getMethod("getMinecraftRegistry");
        MINECRAFT_REGISTRY = method.invoke(null);
      } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    } else {
      MINECRAFT_REGISTRY = null;
    }

    if (PacketAccessors.IS_1_21_6_OR_ABOVE) {
      CODEC = ReflectUtil.findFieldUnchecked(PacketAccessors.COMPONENT_SERIALIZATION_CLASS, 0, PacketAccessors.CODEC_CLASS, true).get(null);
      FROM_JSON_METHOD = null;
    } else {
      MethodHandle handle = null;
      for (Method method : PacketAccessors.COMPONENT_SERIALIZATION_CLASS.getMethods()) {
        if (method.getReturnType().isAssignableFrom(PacketAccessors.COMPONENT_CLASS) &&
          method.getParameterCount() >= 1 &&
          method.getParameterCount() <= 2 &&
          method.getParameterTypes()[0] == JsonElement.class
        ) {
          try {
            handle = MethodHandles.lookup().unreflect(method);
            break;
          } catch (IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
          }
        }
      }

      if (handle == null) {
        throw new ExceptionInInitializerError("failed to find chat component fromJson method");
      }

      FROM_JSON_METHOD = handle;
      CODEC = null;
    }
  }

  public static @NotNull Object fromAdventure(@NotNull Component adventure, @Nullable Locale locale) {
    if (IS_NATIVE_ADVENTURE) {
      return PacketAccessors.fromAdventureComponent(adventure);
    }

    Component translated = adventure;
    if (locale != null) {
      translated = GlobalTranslator.render(adventure, locale);
    }
    JsonElement json = gson().serializeToTree(translated);

    if (FROM_JSON_METHOD == null) {
      // 1.21.6+
      Object result = PacketAccessors.CODEC_PARSE.invoke(CODEC, PacketAccessors.JSON_OPS, json);
      //noinspection OptionalGetWithoutIsPresent
      return ((Optional<?>) PacketAccessors.RESULT_UNWRAP_METHOD.invoke(result)).get();
    }

    Object[] args;
    if (MINECRAFT_REGISTRY != null) {
      args = new Object[]{json, MINECRAFT_REGISTRY};
    } else {
      args = new Object[]{json};
    }

    try {
      return FROM_JSON_METHOD.invokeWithArguments(args);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
