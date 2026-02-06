package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.NativeAdventureUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.RegistryUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Locale;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public class ComponentProviderImpl implements ComponentProvider {
  private static final MethodHandle FROM_JSON_METHOD;

  static {
    if (!PacketAccessors.IS_1_21_6_OR_ABOVE) {
      Class<?> serializerClass = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.Component$Serializer", "net.minecraft.network.chat.IChatBaseComponent$ChatSerializer");
      MethodHandle handle = null;
      for (Method method : serializerClass.getMethods()) {
        if (method.getReturnType() == MutableComponent.class &&
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
    } else {
      FROM_JSON_METHOD = null;
    }
  }

  private final boolean isNativeAdventure;

  public ComponentProviderImpl(boolean isNativeAdventure) {
    this.isNativeAdventure = isNativeAdventure;
  }

  @Override
  public net.minecraft.network.chat.@NotNull Component fromAdventure(@NotNull Component adventure, @Nullable Locale locale) {
    if (isNativeAdventure) {
      return NativeAdventureUtil.fromAdventureComponent(adventure);
    }

    Component translated = adventure;
    if (locale != null) {
      translated = GlobalTranslator.render(adventure, locale);
    }
    JsonElement json = gson().serializeToTree(translated);

    if (FROM_JSON_METHOD == null) {
      // 1.21.6+
      return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
    }

    Object[] args;
    if (PacketAccessors.IS_1_20_5_OR_ABOVE) {
      args = new Object[]{json, RegistryUtil.MINECRAFT_REGISTRY};
    } else {
      args = new Object[]{json};
    }

    try {
      return (net.minecraft.network.chat.Component) FROM_JSON_METHOD.invokeWithArguments(args);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
