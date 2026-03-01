package net.megavex.scoreboardlibrary.implementation.sidebar;

import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.player.PlayerDisplayable;
import net.megavex.scoreboardlibrary.implementation.player.ScoreboardLibraryPlayer;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.GlobalLineInfo;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.LocaleLineHandler;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import static net.kyori.adventure.text.Component.empty;

public abstract class AbstractSidebar implements Sidebar, PlayerDisplayable {
  private final ScoreboardLibraryImpl scoreboardLibrary;
  private final ObjectivePacketAdapter packetAdapter;

  private final GlobalLineInfo[] lines;
  private Component title = empty();

  private final Set<Player> players = new HashSet<>(8);

  private boolean closed;

  private final Queue<SidebarTask> taskQueue = new ConcurrentLinkedQueue<>();

  public AbstractSidebar(@NotNull ScoreboardLibraryImpl scoreboardLibrary, int maxLines, @NotNull String objectiveName) {
    this.scoreboardLibrary = scoreboardLibrary;
    this.packetAdapter = scoreboardLibrary.packetAdapter().createObjectiveAdapter(objectiveName);
    this.lines = new GlobalLineInfo[maxLines];
  }

  protected abstract @NotNull Set<Player> internalPlayers();

  protected abstract void forEachLineHandler(@NotNull Consumer<LocaleLineHandler> consumer);

  protected abstract @Nullable LocaleLineHandler addPlayer0(@NotNull Player player);

  protected abstract @Nullable LocaleLineHandler removePlayer0(@NotNull Player player);

  @Override
  public final void close() {
    if (!closed) {
      closed = true;
      taskQueue.add(SidebarTask.Close.INSTANCE);
    }
  }

  @Override
  public final boolean closed() {
    return closed;
  }

  @Override
  public final @NotNull Collection<Player> players() {
    return closed ? Collections.emptySet() : Collections.unmodifiableSet(players);
  }

  @Override
  public final boolean addPlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    checkClosed();

    if (players.add(player)) {
      taskQueue.add(new SidebarTask.AddPlayer(player));
      return true;
    }

    return false;
  }

  @Override
  public final boolean removePlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    checkClosed();

    if (players.remove(player)) {
      taskQueue.add(new SidebarTask.RemovePlayer(player));
      return true;
    }

    return false;
  }

  @Override
  public final @Range(from = 1, to = MAX_LINES) int maxLines() {
    return lines.length;
  }

  @Override
  public @NotNull String objectiveName() {
    return packetAdapter.objectiveName();
  }

  @Override
  public final void line(@Range(from = 0, to = MAX_LINES - 1) int index, @Nullable ComponentLike value, @Nullable ScoreFormat scoreFormat) {
    checkClosed();

    Component component = value == null ? null : value.asComponent();
    GlobalLineInfo globalLineInfo = getLineInfo(index);
    boolean updateValue = !Objects.equals(globalLineInfo.value(), component);
    boolean updateScore = !Objects.equals(globalLineInfo.scoreFormat(), scoreFormat);

    if (updateValue || updateScore) {
      globalLineInfo.value(component);
      globalLineInfo.scoreFormat(scoreFormat);
      taskQueue.add(new SidebarTask.UpdateLine(index, updateValue, updateScore));
      updateScores();
    }
  }

  @Override
  public void refreshLine(@Range(from = 0, to = Integer.MAX_VALUE - 1) final int index) {
    checkClosed();
    GlobalLineInfo line = lines[index];
    if (line != null) {
      boolean updateValue = line.value() != null;
      boolean updateScore = line.scoreFormat() != null;
      taskQueue.add(new SidebarTask.UpdateLine(index, updateValue, updateScore));
    }
  }

  @Override
  public final @Nullable Component line(@Range(from = 0, to = MAX_LINES - 1) int line) {
    checkClosed();

    GlobalLineInfo globalLineInfo = lines[line];
    return globalLineInfo == null ? null : globalLineInfo.value();
  }

  @Override
  public final @NotNull Component title() {
    return title;
  }

  @Override
  public final void title(@NotNull ComponentLike title) {
    Preconditions.checkNotNull(title);
    checkClosed();

    Component component = title.asComponent();
    if (!Objects.equals(this.title, component)) {
      this.title = component;
      taskQueue.add(SidebarTask.UpdateTitle.INSTANCE);
    }
  }

  @Override
  public void refreshTitle() {
    checkClosed();
    taskQueue.add(SidebarTask.UpdateTitle.INSTANCE);
  }

  public final @NotNull ScoreboardLibraryImpl scoreboardLibrary() {
    return scoreboardLibrary;
  }

  public final ObjectivePacketAdapter packetAdapter() {
    return packetAdapter;
  }

  public final @Nullable GlobalLineInfo[] lines() {
    return lines;
  }

  public final @NotNull Queue<SidebarTask> taskQueue() {
    return taskQueue;
  }

  @Override
  public final void display(@NotNull Player player) {
    Collection<Player> singleton = Collections.singleton(player);
    packetAdapter.sendProperties(singleton, PropertiesPacketType.CREATE, title, ObjectiveRenderType.INTEGER, ScoreFormat.blank());

    LocaleLineHandler lineHandler = Objects.requireNonNull(addPlayer0(player));
    lineHandler.addPlayer(player);
    lineHandler.show(player);
    packetAdapter.display(singleton, ObjectiveDisplaySlot.sidebar());
  }

  public final boolean tick() {
    while (true) {
      SidebarTask task = taskQueue.poll();
      if (task == null) {
        break;
      }

      if (task instanceof SidebarTask.Close) {
        forEachLineHandler(LocaleLineHandler::hide);
        packetAdapter.remove(internalPlayers());

        for (Player player : internalPlayers()) {
          Objects.requireNonNull(scoreboardLibrary.getPlayer(player)).sidebarQueue().remove(this);
        }

        return false;
      } else if (task instanceof SidebarTask.AddPlayer) {
        SidebarTask.AddPlayer addPlayerTask = (SidebarTask.AddPlayer) task;
        ScoreboardLibraryPlayer slPlayer = scoreboardLibrary.getOrCreatePlayer(addPlayerTask.player());
        slPlayer.sidebarQueue().add(this);
      } else if (task instanceof SidebarTask.RemovePlayer) {
        SidebarTask.RemovePlayer removePlayerTask = (SidebarTask.RemovePlayer) task;

        LocaleLineHandler lineHandler = removePlayer0(removePlayerTask.player());
        if (lineHandler != null) {
          lineHandler.hide(removePlayerTask.player());
          lineHandler.removePlayer(removePlayerTask.player());
          packetAdapter.remove(Collections.singleton(removePlayerTask.player()));
        }

        Objects.requireNonNull(scoreboardLibrary.getPlayer(removePlayerTask.player())).sidebarQueue().remove(this);
      } else if (task instanceof SidebarTask.ReloadPlayer) {
        SidebarTask.ReloadPlayer reloadPlayerTask = (SidebarTask.ReloadPlayer) task;
        LocaleLineHandler lineHandler = removePlayer0(reloadPlayerTask.player());
        if (lineHandler == null) {
          continue;
        }

        packetAdapter.remove(Collections.singleton(reloadPlayerTask.player()));
        lineHandler.hide(reloadPlayerTask.player());
        lineHandler.removePlayer(reloadPlayerTask.player());

        display(reloadPlayerTask.player());
      } else if (task instanceof SidebarTask.UpdateLine) {
        SidebarTask.UpdateLine updateLineTask = (SidebarTask.UpdateLine) task;
        forEachLineHandler(sidebar -> {
          if (updateLineTask.updateValue()) {
            Component value = lines[updateLineTask.line()].value();
            Component renderedValue = value == null ? null : GlobalTranslator.render(value, sidebar.locale());
            sidebar.updateLine(updateLineTask.line(), renderedValue);
          }

          if (updateLineTask.updateScore()) {
            sidebar.updateScoreFormat(updateLineTask.line());
          }
        });
      } else if (task instanceof SidebarTask.UpdateScores) {
        forEachLineHandler(LocaleLineHandler::updateScores);
        for (GlobalLineInfo line : lines) {
          if (line != null) {
            line.updateScore(false);
          }
        }
      } else if (task instanceof SidebarTask.UpdateTitle) {
        packetAdapter.sendProperties(internalPlayers(), PropertiesPacketType.UPDATE, title, ObjectiveRenderType.INTEGER, ScoreFormat.blank());
      }
    }
    return true;
  }

  private void updateScores() {
    int size = 0;
    for (GlobalLineInfo line : lines) {
      if (line != null && line.value() != null) {
        size++;
      }
    }

    boolean changed = false;
    int i = 0;

    for (GlobalLineInfo line : lines) {
      if (line != null && line.value() != null) {
        int newScore = size - i - 1;
        if (line.objectiveScore() != newScore) {
          changed = true;
          line.updateScore(true);
          line.objectiveScore(newScore);
        }

        i++;
      }
    }

    if (changed) {
      taskQueue.add(SidebarTask.UpdateScores.INSTANCE);
    }
  }

  private @NotNull GlobalLineInfo getLineInfo(int line) {
    GlobalLineInfo globalLineInfo = lines[line];
    if (globalLineInfo == null) {
      char c = Character.toChars(128 + line)[0];
      String playerName = "" + LegacyComponentSerializer.SECTION_CHAR + c;

      globalLineInfo = lines[line] = new GlobalLineInfo(this, playerName, line);
      updateScores();
    }

    return globalLineInfo;
  }

  private void checkClosed() {
    if (closed) {
      throw new IllegalStateException("Sidebar is closed");
    }
  }
}
