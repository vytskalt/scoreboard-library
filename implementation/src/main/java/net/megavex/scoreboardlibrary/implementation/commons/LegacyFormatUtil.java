package net.megavex.scoreboardlibrary.implementation.commons;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyFormat;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.parseChar;

public final class LegacyFormatUtil {
  private static final Map<NamedTextColor, ChatColor> chatColorMap;

  static {
    ChatColor[] values = ChatColor.values();
    chatColorMap = new HashMap<>(values.length);
    for (ChatColor value : values) {
      if (!value.isColor()) continue;

      LegacyFormat format = Objects.requireNonNull(parseChar(value.getChar()));
      chatColorMap.put((NamedTextColor) format.color(), value);
    }
  }

  private LegacyFormatUtil() {
  }

  public static String limitLegacyText(String text, int limit) {
    if (text.length() <= limit) {
      return text;
    }

    int lastNotColorCharIndex = limit - 1;
    while (text.charAt(lastNotColorCharIndex) == LegacyComponentSerializer.SECTION_CHAR) {
      lastNotColorCharIndex--;
    }

    return text.substring(0, lastNotColorCharIndex + 1);
  }

  public static String serialize(@Nullable Component component, @Nullable Locale locale) {
    if (component == null || component == empty()) return "";

    Component translated;
    if (locale != null) {
      translated = GlobalTranslator.render(component, locale);
    } else {
      translated = component;
    }

    String legacyFormat = legacySection().serialize(translated);

    // Legacy format serializer ignores empty components, so it's impossible to set player color on 1.8 via team prefix
    // Need to manually add the missing legacy format of the last component
    Component lastChild = translated;
    while (!lastChild.children().isEmpty()) {
      lastChild = lastChild.children().get(0);
    }

    if (lastChild instanceof TextComponent) {
      String content = ((TextComponent) lastChild).content();
      if (content.isEmpty()) {
        String ending = legacySection().serialize(lastChild.append(space()));
        legacyFormat += ending.substring(0, ending.length() - 1);
      }
    }

    return legacyFormat;
  }

  public static char getChar(@Nullable NamedTextColor color) {
    if (color == null) return 'r';

    ChatColor chatColor = chatColorMap.getOrDefault(color, ChatColor.WHITE);
    return chatColor.getChar();
  }

  public static int getIndex(@Nullable NamedTextColor color) {
    ChatColor chatColor = chatColorMap.getOrDefault(color == null ? NamedTextColor.WHITE : color, ChatColor.WHITE);
    return chatColor.ordinal();
  }
}
