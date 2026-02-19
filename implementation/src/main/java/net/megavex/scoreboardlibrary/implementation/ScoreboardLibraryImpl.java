package net.megavex.scoreboardlibrary.implementation;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveManager;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.objective.ObjectiveManagerImpl;
import net.megavex.scoreboardlibrary.implementation.objective.ObjectiveUpdaterTask;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.player.LocaleListener;
import net.megavex.scoreboardlibrary.implementation.player.ScoreboardLibraryPlayer;
import net.megavex.scoreboardlibrary.implementation.scheduler.TaskScheduler;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.PlayerDependantLocaleSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.SidebarUpdaterTask;
import net.megavex.scoreboardlibrary.implementation.sidebar.SingleLocaleSidebar;
import net.megavex.scoreboardlibrary.implementation.team.TeamManagerImpl;
import net.megavex.scoreboardlibrary.implementation.team.TeamUpdaterTask;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardLibraryImpl implements ScoreboardLibrary {
  private final Plugin plugin;
  private final PacketAdapterProvider packetAdapter;
  private final TaskScheduler taskScheduler;

  private final Map<Player, ScoreboardLibraryPlayer> playerMap = new MapMaker().weakKeys().makeMap();

  private volatile Set<TeamManagerImpl> teamManagers;
  private volatile Set<ObjectiveManagerImpl> objectiveManagers;
  private volatile Set<AbstractSidebar> sidebars;

  private final LocaleListener localeListener;
  private TeamUpdaterTask teamTask;
  private ObjectiveUpdaterTask objectiveTask;
  private SidebarUpdaterTask sidebarTask;

  private final Object lock = new Object();
  private volatile boolean closed;

  public ScoreboardLibraryImpl(@NotNull Plugin plugin) throws NoPacketAdapterAvailableException {
    Preconditions.checkNotNull(plugin, "plugin");

    try {
      Class.forName("net.kyori.adventure.Adventure");
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Adventure is not in the classpath");
    }

    this.plugin = plugin;
    this.packetAdapter = PacketAdapterLoader.loadPacketAdapter(plugin);
    this.taskScheduler = TaskScheduler.create(plugin);

    boolean localeEventExists = false;
    try {
      Class.forName("org.bukkit.event.player.PlayerLocaleChangeEvent");
      localeEventExists = true;
    } catch (ClassNotFoundException ignored) {
    }

    if (localeEventExists) {
      localeListener = new LocaleListener(this);
      plugin.getServer().getPluginManager().registerEvents(localeListener, plugin);
    } else {
      localeListener = null;
    }
  }

  public @NotNull Plugin plugin() {
    return plugin;
  }

  public @NotNull PacketAdapterProvider packetAdapter() {
    return packetAdapter;
  }

  public @NotNull TaskScheduler taskScheduler() {
    return taskScheduler;
  }

  @Override
  public @NotNull Sidebar createSidebar(int maxLines, @Nullable Locale locale, @NotNull String objectiveName) {
    checkClosed();

    if (maxLines <= 0) {
      throw new IllegalArgumentException("invalid maxLines value: " + maxLines);
    }

    AbstractSidebar sidebar;
    if (locale == null) {
      sidebar = new PlayerDependantLocaleSidebar(this, maxLines, objectiveName);
    } else {
      sidebar = new SingleLocaleSidebar(this, maxLines, objectiveName, locale);
    }

    sidebars().add(sidebar);
    return sidebar;
  }

  @Override
  public @NotNull TeamManagerImpl createTeamManager() {
    checkClosed();

    TeamManagerImpl teamManager = new TeamManagerImpl(this);
    teamManagers().add(teamManager);
    return teamManager;
  }

  @Override
  public @NotNull ObjectiveManager createObjectiveManager() {
    checkClosed();

    ObjectiveManagerImpl objectiveManager = new ObjectiveManagerImpl(this);
    objectiveManagers().add(objectiveManager);
    return objectiveManager;
  }

  @Override
  public void close() {
    if (closed) {
      return;
    }

    synchronized (lock) {
      if (closed) {
        return;
      }

      closed = true;
    }

    HandlerList.unregisterAll(localeListener);

    if (teamManagers != null) {
      teamTask.task().cancel();
      synchronized (teamTask.lock()) {
        for (TeamManagerImpl teamManager : teamManagers) {
          teamManager.close();
          teamManager.tick();
        }
      }
    }

    if (objectiveManagers != null) {
      objectiveTask.task().cancel();
      synchronized (objectiveTask.lock()) {
        for (ObjectiveManagerImpl objectiveManager : objectiveManagers) {
          objectiveManager.close();
          objectiveManager.tick();
        }
      }
    }

    if (sidebars != null) {
      sidebarTask.task().cancel();
      synchronized (sidebarTask.lock()) {
        for (AbstractSidebar sidebar : sidebars) {
          sidebar.close();
          sidebar.tick();
        }
      }
    }
  }

  @Override
  public boolean closed() {
    return closed;
  }

  public Set<TeamManagerImpl> teamManagers() {
    if (teamManagers == null) {
      synchronized (lock) {
        if (teamManagers == null) {
          teamManagers = Collections.newSetFromMap(new ConcurrentHashMap<>(4, 0.75f, 2));
          teamTask = new TeamUpdaterTask(this);
        }
      }
    }

    return teamManagers;
  }

  public Set<ObjectiveManagerImpl> objectiveManagers() {
    if (objectiveManagers == null) {
      synchronized (lock) {
        if (objectiveManagers == null) {
          objectiveManagers = Collections.newSetFromMap(new ConcurrentHashMap<>(4, 0.75f, 2));
          objectiveTask = new ObjectiveUpdaterTask(this);
        }
      }
    }

    return objectiveManagers;
  }

  public Set<AbstractSidebar> sidebars() {
    if (sidebars == null) {
      synchronized (lock) {
        if (sidebars == null) {
          sidebars = Collections.newSetFromMap(new ConcurrentHashMap<>(4, 0.75f, 2));
          sidebarTask = new SidebarUpdaterTask(this);
        }
      }
    }

    return this.sidebars;
  }

  public @NotNull ScoreboardLibraryPlayer getOrCreatePlayer(@NotNull Player player) {
    return playerMap.computeIfAbsent(player, ScoreboardLibraryPlayer::new);
  }

  public @Nullable ScoreboardLibraryPlayer getPlayer(@NotNull Player player) {
    return playerMap.get(player);
  }

  private void checkClosed() {
    if (closed) {
      throw new IllegalStateException("ScoreboardLibrary is closed");
    }
  }
}
