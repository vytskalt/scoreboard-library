package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import com.google.gson.JsonElement;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

import static net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson;

public final class ScoreFormatConverter {
  private static final Object STYLE_CODEC;
  private static final Object BLANK;
  private static final ConstructorAccessor<?> STYLED_CONSTRUCTOR;
  private static final ConstructorAccessor<?> FIXED_CONSTRUCTOR;

  static {
    if (PacketAccessors.IS_1_20_3_OR_ABOVE) {
      STYLE_CODEC = ReflectUtil.findFieldUnchecked(PacketAccessors.STYLE_SERIALIZER_CLASS, 0, PacketAccessors.CODEC_CLASS, true).get(null);

      Class<?> blankFormatClass = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.numbers.BlankFormat");
      BLANK = ReflectUtil.findFieldUnchecked(blankFormatClass, 0, blankFormatClass, true).get(null);

      Class<?> styledFormatClass = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.numbers.StyledFormat");
      STYLED_CONSTRUCTOR = ReflectUtil.findConstructor(styledFormatClass, PacketAccessors.STYLE_CLASS);

      Class<?> fixedFormatClass = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.numbers.FixedFormat");
      FIXED_CONSTRUCTOR = ReflectUtil.findConstructor(fixedFormatClass, PacketAccessors.COMPONENT_CLASS);
    } else {
      STYLE_CODEC = null;
      BLANK = null;
      STYLED_CONSTRUCTOR = null;
      FIXED_CONSTRUCTOR = null;
    }
  }

  private ScoreFormatConverter() {
  }

  public static @Nullable Object convert(@Nullable Locale locale, @Nullable ScoreFormat format) {
    if (format == null || !PacketAccessors.IS_1_20_3_OR_ABOVE) {
      return null;
    }

    if (format == ScoreFormat.blank()) {
      return BLANK;
    } else if (format instanceof ScoreFormat.Styled) {
      JsonElement json = gson().serializer().toJsonTree(((ScoreFormat.Styled) format).style());
      Object result = PacketAccessors.CODEC_PARSE.invoke(STYLE_CODEC, PacketAccessors.JSON_OPS, json);
      //noinspection OptionalGetWithoutIsPresent
      Object style = ((Optional<?>) PacketAccessors.RESULT_UNWRAP_METHOD.invoke(result)).get();
      return STYLED_CONSTRUCTOR.invoke(style);
    } else if (format instanceof ScoreFormat.Fixed) {
      return FIXED_CONSTRUCTOR.invoke(ModernComponentProvider.fromAdventure(((ScoreFormat.Fixed) format).content(), locale));
    } else {
      throw new IllegalArgumentException("Invalid score format: " + format);
    }
  }
}
