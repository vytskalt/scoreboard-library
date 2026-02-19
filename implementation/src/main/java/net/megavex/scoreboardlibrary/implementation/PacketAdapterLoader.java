package net.megavex.scoreboardlibrary.implementation;

import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public final class PacketAdapterLoader {
  private static final String MODERN = "modern", LEGACY = "legacy";

  private PacketAdapterLoader() {
  }

  public static @NotNull PacketAdapterProvider loadPacketAdapter(Plugin plugin) throws NoPacketAdapterAvailableException {
    Class<?> nmsClass = findAndLoadImplementationClass();
    if (nmsClass == null) {
      throw new NoPacketAdapterAvailableException();
    }

    try {
      return (PacketAdapterProvider) nmsClass.getConstructors()[0].newInstance(plugin);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("couldn't initialize packet adapter", e);
    }
  }

  private static @Nullable Class<?> findAndLoadImplementationClass() {
    String version = Bukkit.getServer().getBukkitVersion();
    int dashIndex = version.indexOf('-');
    if (dashIndex != -1) {
      version = version.substring(0, dashIndex);
    }

    return tryLoadVersion(version);
  }

  private static @Nullable Class<?> tryLoadVersion(@NotNull String serverVersion) {
    // https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-legacy/
    // https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-10-1-15/
    // https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-16/
    // https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-21/
    switch (serverVersion) {
      case "1.7.10":
      case "1.8":
      case "1.8.3":
      case "1.8.4":
      case "1.8.5":
      case "1.8.6":
      case "1.8.7":
      case "1.8.8":
      case "1.9":
      case "1.9.2":
      case "1.9.4":
      case "1.10.2":
      case "1.11":
      case "1.11.2":
      case "1.12":
      case "1.12.1":
      case "1.12.2":
        return tryLoadImplementationClass(LEGACY);
      case "1.13":
      case "1.13.1":
      case "1.13.2":
      case "1.14":
      case "1.14.1":
      case "1.14.2":
      case "1.14.3":
      case "1.14.4":
      case "1.15":
      case "1.15.1":
      case "1.15.2":
      case "1.16":
      case "1.16.1":
      case "1.16.2":
      case "1.16.3":
      case "1.16.4":
      case "1.16.5":
      case "1.17":
      case "1.17.1":
      case "1.18":
      case "1.18.1":
      case "1.18.2":
      case "1.19":
      case "1.19.1":
      case "1.19.2":
      case "1.19.3":
      case "1.19.4":
      case "1.20":
      case "1.20.1":
      case "1.20.2":
      case "1.20.3":
      case "1.20.4":
      case "1.20.5":
      case "1.20.6":
      case "1.21":
      case "1.21.1":
      case "1.21.2":
      case "1.21.3":
      case "1.21.4":
      case "1.21.5":
      case "1.21.6":
      case "1.21.7":
      case "1.21.8":
      case "1.21.9":
      case "1.21.10":
      case "1.21.11":
        return tryLoadImplementationClass(MODERN);
      default:
        // Hide from relocation checkers
        String property = "net.mega".concat("vex.scoreboardlibrary.forceModern");
        if (System.getProperty(property, "").equalsIgnoreCase("true")) {
          return tryLoadImplementationClass(MODERN);
        }

        return null;
    }
  }

  private static @Nullable Class<?> tryLoadImplementationClass(@NotNull String name) {
    try {
      String path = "net.megavex.scoreboardlibrary.implementation.packetAdapter." + name + ".PacketAdapterProviderImpl";
      return Class.forName(path);
    } catch (ClassNotFoundException ignored) {
      return null;
    }
  }
}
