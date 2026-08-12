package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.MethodAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodType;
import java.util.Objects;
import java.util.Optional;

import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.*;

public final class NmsAccessors {
  private NmsAccessors() {
  }

  // --- CODEC ---

  public static final MethodAccessor RESULT_UNWRAP_METHOD;
  public static final Object JSON_OPS;
  public static final MethodAccessor CODEC_PARSE;

  static {
    if (DATA_RESULT_CLASS != null) {
      RESULT_UNWRAP_METHOD = ReflectUtil.findMethod(DATA_RESULT_CLASS, false, MethodType.methodType(Optional.class), "result");
      try {
        JSON_OPS = JSON_OPS_CLASS.getField("INSTANCE").get(null);
      } catch (IllegalAccessException | NoSuchFieldException e) {
        throw new RuntimeException(e);
      }
      CODEC_PARSE = ReflectUtil.findMethod(CODEC_CLASS, false, MethodType.methodType(DATA_RESULT_CLASS, DYNAMIC_OPS_CLASS, Object.class), "parse");
    } else {
      RESULT_UNWRAP_METHOD = null;
      JSON_OPS = null;
      CODEC_PARSE = null;
    }
  }

  // --- ??? ---

  public static final ConstructorAccessor<?> ADVENTURE_COMPONENT_CONSTRUCTOR =
    ADVENTURE_COMPONENT_CLASS != null ? ReflectUtil.findOptionalConstructor(ADVENTURE_COMPONENT_CLASS, Component.class) : null;

  public static @NotNull Object fromAdventureComponent(@NotNull Component component) {
    return Objects.requireNonNull(NmsAccessors.ADVENTURE_COMPONENT_CONSTRUCTOR).invoke(component);
  }
}
