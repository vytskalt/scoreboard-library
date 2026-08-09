package net.megavex.scoreboardlibrary.implementation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;

class LegacyFormatUtilTest {
  @Test
  void serializeTest() {
    assertSerialization(text(""), "");
    assertSerialization(text("Regular", NamedTextColor.RED), "§cRegular");
    assertSerialization(text("", NamedTextColor.RED), "§c");

    assertSerialization(text("Text", NamedTextColor.AQUA, TextDecoration.BOLD), "§b§lText");
    Component nested = text("Text ", NamedTextColor.AQUA, TextDecoration.BOLD)
      .append(text("", NamedTextColor.AQUA)
        .append(text("", NamedTextColor.RED)));
    assertSerialization(nested, "§b§lText §c§l");

    Component decorated = text("Text ", NamedTextColor.AQUA)
      .append(text("", NamedTextColor.AQUA, TextDecoration.BOLD).append(text("", NamedTextColor.RED)));
    assertSerialization(decorated, "§bText §c§l");
  }

  private void assertSerialization(Component component, String expected) {
    String legacy = LegacyFormatUtil.serialize(component, null);
    Assertions.assertEquals(expected, legacy);
  }
}
