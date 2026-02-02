package net.megavex.scoreboardlibrary.implementation.objective;

import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveManager;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveScore;
import net.megavex.scoreboardlibrary.api.objective.ScoreboardObjective;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.player.PlayerDisplayable;
import net.megavex.scoreboardlibrary.implementation.player.ScoreboardLibraryPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ObjectiveManagerImpl implements ObjectiveManager, PlayerDisplayable {
  private final ScoreboardLibraryImpl library;
  private final Map<String, ScoreboardObjectiveImpl> objectives = new ConcurrentHashMap<>();
  private final Map<ObjectiveDisplaySlot, ScoreboardObjectiveImpl> displaySlots = new HashMap<>();

  private final Set<Player> players = CollectionProvider.set(8);
  private final Set<Player> displayingPlayers = CollectionProvider.set(8);
  private final Queue<ObjectiveManagerTask> taskQueue = new ConcurrentLinkedQueue<>();
  private boolean closed;

  public ObjectiveManagerImpl(@NotNull ScoreboardLibraryImpl library) {
    this.library = library;
  }

  public @NotNull Queue<ObjectiveManagerTask> taskQueue() {
    return taskQueue;
  }

  @Override
  public @NotNull ScoreboardObjective create(@NotNull String name) {
    Preconditions.checkNotNull(name);
    checkClosed();
    return objectives.computeIfAbsent(name, i -> {
      ObjectivePacketAdapter packetAdapter = library.packetAdapter().createObjectiveAdapter(name);
      ScoreboardObjectiveImpl objective = new ScoreboardObjectiveImpl(packetAdapter, taskQueue, name);
      taskQueue.add(new ObjectiveManagerTask.AddObjective(objective));
      return objective;
    });
  }

  @Override
  public void remove(@NotNull ScoreboardObjective objective) {
    Preconditions.checkNotNull(objective);
    checkClosed();
    if (!(objective instanceof ScoreboardObjectiveImpl)) {
      return;
    }

    ScoreboardObjectiveImpl impl = (ScoreboardObjectiveImpl) objective;
    if (objectives.values().remove(impl)) {
      displaySlots.values().removeIf(e -> e == impl);
      impl.close();
      taskQueue.add(new ObjectiveManagerTask.RemoveObjective(impl));
    }
  }

  @Override
  public void display(@NotNull ObjectiveDisplaySlot displaySlot, @NotNull ScoreboardObjective objective) {
    Preconditions.checkNotNull(displaySlot);
    Preconditions.checkNotNull(objective);
    checkClosed();

    if (!(objective instanceof ScoreboardObjectiveImpl)) {
      throw new IllegalArgumentException("Invalid objective implementation");
    }

    ScoreboardObjectiveImpl impl = (ScoreboardObjectiveImpl) objective;
    if (objectives.get(impl.name()) != impl) {
      throw new IllegalArgumentException("Objective is from a different manager");
    }

    displaySlots.put(displaySlot, impl);
    taskQueue.add(new ObjectiveManagerTask.UpdateDisplaySlot(displaySlot, impl));
  }

  @Override
  public @NotNull Collection<Player> players() {
    return Collections.unmodifiableSet(players);
  }

  @Override
  public boolean addPlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    checkClosed();
    if (!players.add(player)) {
      return false;
    }

    taskQueue.add(new ObjectiveManagerTask.AddPlayer(player));
    return true;
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    checkClosed();
    if (!players.remove(player)) {
      return false;
    }

    taskQueue.add(new ObjectiveManagerTask.RemovePlayer(player));
    return true;
  }

  @Override
  public void close() {
    if (!closed) {
      closed = true;
      taskQueue.add(ObjectiveManagerTask.Close.INSTANCE);
    }
  }

  @Override
  public boolean closed() {
    return closed;
  }

  public boolean tick() {
    while (true) {
      ObjectiveManagerTask task = taskQueue.poll();
      if (task == null) {
        break;
      }

      if (task instanceof ObjectiveManagerTask.Close) {
        for (ScoreboardObjectiveImpl objective : objectives.values()) {
          objective.packetAdapter().remove(displayingPlayers);
        }

        for (Player player : players) {
          Objects.requireNonNull(library.getPlayer(player)).objectiveManagerQueue().remove(this);
        }
        return false;
      } else if (task instanceof ObjectiveManagerTask.AddPlayer) {
        Player player = ((ObjectiveManagerTask.AddPlayer) task).player();
        @NotNull ScoreboardLibraryPlayer slPlayer = library.getOrCreatePlayer(player);
        slPlayer.objectiveManagerQueue().add(this);
      } else if (task instanceof ObjectiveManagerTask.RemovePlayer) {
        Player player = ((ObjectiveManagerTask.RemovePlayer) task).player();
        Collection<Player> singleton = Collections.singleton(player);
        for (ScoreboardObjectiveImpl objective : objectives.values()) {
          objective.packetAdapter().remove(singleton);
        }

        displayingPlayers.remove(player);
        Objects.requireNonNull(library.getPlayer(player)).objectiveManagerQueue().remove(this);
      } else if (task instanceof ObjectiveManagerTask.ReloadPlayer) {
        Player player = ((ObjectiveManagerTask.ReloadPlayer) task).player();
        Collection<Player> singleton = Collections.singleton(player);
        for (ScoreboardObjectiveImpl objective : objectives.values()) {
          objective.sendProperties(singleton, PropertiesPacketType.UPDATE);

          for (Map.Entry<String, ObjectiveScore> entry : objective.scores().entrySet()) {
            ObjectiveScore score = entry.getValue();
            objective.packetAdapter().sendScore(singleton, entry.getKey(), score.value(), score.displayName(), score.format());
          }
        }
      } else if (task instanceof ObjectiveManagerTask.AddObjective) {
        ScoreboardObjectiveImpl objective = ((ObjectiveManagerTask.AddObjective) task).objective();
        objective.sendProperties(displayingPlayers, PropertiesPacketType.CREATE);
        objective.wasDisplayed(true);
      } else if (task instanceof ObjectiveManagerTask.RemoveObjective) {
        ScoreboardObjectiveImpl objective = ((ObjectiveManagerTask.RemoveObjective) task).objective();
        objective.packetAdapter().remove(displayingPlayers);
      } else if (task instanceof ObjectiveManagerTask.UpdateObjective) {
        ScoreboardObjectiveImpl objective = ((ObjectiveManagerTask.UpdateObjective) task).objective();
        objective.sendProperties(displayingPlayers, PropertiesPacketType.UPDATE);
      } else if (task instanceof ObjectiveManagerTask.UpdateScore) {
        ObjectiveManagerTask.UpdateScore updateScoreTask = ((ObjectiveManagerTask.UpdateScore) task);
        ScoreboardObjectiveImpl objective = updateScoreTask.objective();
        String entry = updateScoreTask.entry();
        ObjectiveScore score = updateScoreTask.score();
        if (score != null) {
          objective.packetAdapter().sendScore(displayingPlayers, entry, score.value(), score.displayName(), score.format());
        } else {
          objective.packetAdapter().removeScore(displayingPlayers, entry);
        }
      } else if (task instanceof ObjectiveManagerTask.UpdateDisplaySlot) {
        ObjectiveManagerTask.UpdateDisplaySlot updateDisplaySlotTask = (ObjectiveManagerTask.UpdateDisplaySlot) task;
        ObjectiveDisplaySlot slot = updateDisplaySlotTask.displaySlot();
        ScoreboardObjectiveImpl objective = updateDisplaySlotTask.objective();
        objective.packetAdapter().display(displayingPlayers, slot);
      }
    }
    return true;
  }

  @Override
  public void display(@NotNull Player player) {
    displayingPlayers.add(player);
    Collection<Player> singleton = Collections.singleton(player);

    for (ScoreboardObjectiveImpl objective : objectives.values()) {
      if (!objective.wasDisplayed()) {
        continue;
      }

      objective.sendProperties(singleton, PropertiesPacketType.CREATE);
      for (Map.Entry<String, ObjectiveScore> entry : objective.scores().entrySet()) {
        ObjectiveScore score = entry.getValue();
        objective.packetAdapter().sendScore(singleton, entry.getKey(), score.value(), score.displayName(), score.format());
      }
    }

    for (Map.Entry<ObjectiveDisplaySlot, ScoreboardObjectiveImpl> entry : displaySlots.entrySet()) {
      if (!entry.getValue().wasDisplayed()) {
        continue;
      }
      entry.getValue().packetAdapter().display(singleton, entry.getKey());
    }
  }

  private void checkClosed() {
    if (closed) {
      throw new IllegalStateException("ObjectiveManager is closed");
    }
  }
}
