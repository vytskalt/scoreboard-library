package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util;

import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class NativeAdventureUtil {
  private NativeAdventureUtil() {
  }

  public static @NotNull AdventureComponent fromAdventureComponent(@NotNull Component component) {
    return new AdventureComponent(component);
  }
}
