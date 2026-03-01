package net.megavex.scoreboardlibrary.implementation.scheduler;

import net.kyori.adventure.util.Ticks;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

final class FoliaTaskScheduler implements TaskScheduler {
  private static final Class<?> asyncSchedulerClass;
  private static final Class<?> scheduledTaskClass;
  private static final Class<?> cancelledStateClass;
  private static final MethodHandle getAsyncSchedulerMethod;
  private static final MethodHandle cancelScheduledTaskMethod;
  private static final MethodHandle runAtFixedRateMethod;
  private static final MethodHandle runDelayedMethod;

  static {
    try {
      asyncSchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
      scheduledTaskClass = Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
      cancelledStateClass = Class.forName("io.papermc.paper.threadedregions.scheduler.ScheduledTask$CancelledState");

      MethodHandles.Lookup lookup = MethodHandles.lookup();
      getAsyncSchedulerMethod = lookup.findVirtual(
        Server.class,
        "getAsyncScheduler",
        MethodType.methodType(asyncSchedulerClass)
      );

      cancelScheduledTaskMethod = lookup.findVirtual(scheduledTaskClass, "cancel", MethodType.methodType(cancelledStateClass));

      runAtFixedRateMethod = lookup.findVirtual(
        asyncSchedulerClass,
        "runAtFixedRate",
        MethodType.methodType(
          scheduledTaskClass,
          Plugin.class,
          Consumer.class,
          long.class,
          long.class,
          TimeUnit.class
        )
      );

      runDelayedMethod = lookup.findVirtual(
        asyncSchedulerClass,
        "runDelayed",
        MethodType.methodType(
          scheduledTaskClass,
          Plugin.class,
          Consumer.class,
          long.class,
          TimeUnit.class
        )
      );
    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private final Plugin plugin;
  private final Object asyncScheduler;

  public FoliaTaskScheduler(@NotNull Plugin plugin) {
    this.plugin = plugin;
    try {
      this.asyncScheduler = getAsyncSchedulerMethod.invoke(plugin.getServer());
    } catch (Throwable e) {
      throw new RuntimeException("couldn't get async scheduler", e);
    }
  }

  @Override
  public @NotNull RunningTask runEveryTick(@NotNull Runnable runnable) {
    Object scheduledTask;
    try {
      Consumer<Object> task = t -> runnable.run();
      scheduledTask = runAtFixedRateMethod.invoke(
        asyncScheduler,
        plugin,
        task,
        Ticks.SINGLE_TICK_DURATION_MS,
        Ticks.SINGLE_TICK_DURATION_MS,
        TimeUnit.MILLISECONDS
      );
    } catch (Throwable e) {
      throw new RuntimeException("couldn't schedule repeating task", e);
    }

    return () -> {
      try {
        cancelScheduledTaskMethod.invoke(scheduledTask);
      } catch (Throwable e) {
        throw new RuntimeException("couldn't cancel scheduled task", e);
      }
    };
  }

  @Override
  public void runNextTick(@NotNull Runnable runnable) {
    Consumer<Object> task = t -> runnable.run();
    try {
      runDelayedMethod.invoke(asyncScheduler, plugin, task, Ticks.SINGLE_TICK_DURATION_MS, TimeUnit.MILLISECONDS);
    } catch (Throwable e) {
      throw new RuntimeException("couldn't schedule delayed task", e);
    }
  }
}
