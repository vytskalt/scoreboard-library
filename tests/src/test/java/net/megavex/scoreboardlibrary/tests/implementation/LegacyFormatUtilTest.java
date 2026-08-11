package net.megavex.scoreboardlibrary.tests.implementation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.tests.Assert;

import static net.kyori.adventure.text.Component.text;

public final class LegacyFormatUtilTest {
  private LegacyFormatUtilTest() {
  }

  public static void serialize() {
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

  private static void assertSerialization(final Component component, final String expected) {
    String legacy = LegacyFormatUtil.serialize(component, null);
    Assert.equals(expected, legacy);
  }
}
