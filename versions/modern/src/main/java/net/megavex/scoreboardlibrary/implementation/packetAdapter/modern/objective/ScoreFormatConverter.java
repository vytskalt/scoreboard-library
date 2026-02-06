package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.chat.numbers.FixedFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Locale;
import java.util.Optional;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public final class ScoreFormatConverter {
  private static final MethodHandle RESULT_UNWRAP_METHOD;

  static {
    try {
      RESULT_UNWRAP_METHOD = MethodHandles.lookup().findVirtual(DataResult.class, "result", MethodType.methodType(Optional.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private ScoreFormatConverter() {
  }

  public static @Nullable Object convert(@NotNull ComponentProvider componentProvider, @Nullable Locale locale, @Nullable ScoreFormat format) {
    if (format == null || !PacketAccessors.IS_1_20_3_OR_ABOVE) {
      return null;
    }

    if (format == ScoreFormat.blank()) {
      return BlankFormat.INSTANCE;
    } else if (format instanceof ScoreFormat.Styled) {
      JsonElement json = gson().serializer().toJsonTree(((ScoreFormat.Styled) format).style());
      Object result = Style.Serializer.CODEC.parse(JsonOps.INSTANCE, json);
      Style style;
      try {
        //noinspection unchecked,rawtypes
        style = (Style) ((Optional) RESULT_UNWRAP_METHOD.invokeExact((DataResult) result)).orElseThrow(RuntimeException::new);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
      return new StyledFormat(style);
    } else if (format instanceof ScoreFormat.Fixed) {
      return new FixedFormat(componentProvider.fromAdventure(((ScoreFormat.Fixed) format).content(), locale));
    } else {
      throw new IllegalArgumentException("Invalid score format: " + format);
    }
  }
}
