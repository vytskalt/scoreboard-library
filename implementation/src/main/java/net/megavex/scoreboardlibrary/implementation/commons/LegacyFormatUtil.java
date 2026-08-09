package net.megavex.scoreboardlibrary.implementation.commons;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyFormat;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.kyori.adventure.text.Component.empty;
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

    // Legacy format serializer ignores empty components, so it's impossible to set player color on 1.8 via team prefix
    // Need to manually add the missing legacy format of the last component

    Component processed = processLastTextComponent(translated);
    String legacyFormat = legacySection().serialize(processed);

    if (processed != translated) {
      legacyFormat = legacyFormat.substring(0, legacyFormat.length() - 1);
    }

    return legacyFormat;
  }

  private static Component processLastTextComponent(Component component) {
    List<Component> children = component.children();
    if (children.isEmpty()) {
      if (component instanceof TextComponent) {
        TextComponent textComponent = (TextComponent) component;
        String content = textComponent.content();
        if (content.isEmpty()) {
          // Add space at the last component if it's empty, forces legacy serializer to include formatting for its style
          return textComponent.content(" ");
        }
      }

      return component;
    }

    Component last = children.get(children.size() - 1);
    Component processed = processLastTextComponent(last);
    if (processed == last) {
      return component;
    }
    children = new ArrayList<>(children);
    children.set(children.size() - 1, processed);
    return component.children(children);
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
