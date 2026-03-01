package net.megavex.scoreboardlibrary.implementation.scheduler;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

final class BukkitTaskScheduler implements TaskScheduler {
  private final Plugin plugin;

  public BukkitTaskScheduler(@NotNull Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public @NotNull RunningTask runEveryTick(@NotNull Runnable runnable) {
    org.bukkit.scheduler.BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, 1, 1);
    return task::cancel;
  }

  @Override
  public void runNextTick(@NotNull Runnable runnable) {
    plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, 1);
  }
}
