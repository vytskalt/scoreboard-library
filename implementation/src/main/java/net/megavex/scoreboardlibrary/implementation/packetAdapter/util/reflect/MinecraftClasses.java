package net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class MinecraftClasses {
  private static final String CB_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();

  private MinecraftClasses() {
  }

  public static @NotNull String craftBukkit(String className) {
    return CB_PACKAGE + '.' + className;
  }
}
