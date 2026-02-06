package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util;

import net.minecraft.core.RegistryAccess;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class RegistryUtil {
  public static final RegistryAccess MINECRAFT_REGISTRY;

  static {
    String cbPackage = Bukkit.getServer().getClass().getPackage().getName();
    Class<?> craftRegistry;
    try {
      craftRegistry = Class.forName(cbPackage + ".CraftRegistry");
    } catch (ClassNotFoundException e) {
      throw new ExceptionInInitializerError(e);
    }

    try {
      Method method = craftRegistry.getDeclaredMethod("getMinecraftRegistry");
      MINECRAFT_REGISTRY = (RegistryAccess) method.invoke(null);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private RegistryUtil() {
  }
}
