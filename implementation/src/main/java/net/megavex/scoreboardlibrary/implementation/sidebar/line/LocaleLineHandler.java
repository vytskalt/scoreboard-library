package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Locale;

public final class LocaleLineHandler {
  private final AbstractSidebar sidebar;
  private final Locale locale;
  private final SidebarLineHandler[] lineHandlers;

  public LocaleLineHandler(@NotNull AbstractSidebar sidebar, @NotNull Locale locale) {
    this.sidebar = sidebar;
    this.locale = locale;
    this.lineHandlers = new SidebarLineHandler[LineRenderingStrategy.values().length];
  }

  public @NotNull AbstractSidebar sidebar() {
    return sidebar;
  }

  public @NotNull Locale locale() {
    return locale;
  }

  public boolean hasPlayers() {
    for (SidebarLineHandler lineHandler : lineHandlers) {
      if (lineHandler != null && !lineHandler.players().isEmpty()) {
        return true;
      }
    }
    return false;
  }

  public void addPlayer(@NotNull Player player) {
    LineRenderingStrategy strategy = sidebar.scoreboardLibrary().packetAdapter().lineRenderingStrategy(player);
    lineHandler(strategy).players().add(player);
  }

  public void removePlayer(@NotNull Player player) {
    for (SidebarLineHandler lineHandler : lineHandlers) {
      if (lineHandler != null && lineHandler.players().remove(player)) {
        return;
      }
    }
  }

  public @NotNull SidebarLineHandler lineHandler(@NotNull LineRenderingStrategy lineType) {
    SidebarLineHandler lineHandler = lineHandlers[lineType.ordinal()];
    if (lineHandler == null) {
      lineHandler = lineHandlers[lineType.ordinal()] = new SidebarLineHandler(lineType, this);
    }
    return lineHandler;
  }

  public void updateScoreFormat(int lineIndex) {
    SidebarLineHandler lineHandler = lineHandlers[LineRenderingStrategy.MODERN.ordinal()];
    if (lineHandler != null) {
      lineHandler.updateScore(lineIndex);
    }
  }

  public void updateLine(int lineIndex, Component renderedValue) {
    for (SidebarLineHandler lineHandler : lineHandlers) {
      if (lineHandler != null) {
        lineHandler.setLine(lineIndex, renderedValue);
      }
    }
  }

  public void updateScores() {
    for (SidebarLineHandler lineHandler : lineHandlers) {
      if (lineHandler != null) {
        lineHandler.updateScores();
      }
    }
  }

  public void show(Player player) {
    for (SidebarLineHandler lineHandler : lineHandlers) {
      if (lineHandler != null && lineHandler.players().contains(player)) {
        lineHandler.show(Collections.singleton(player));
        return;
      }
    }
  }

  public void hide(Player player) {
    for (SidebarLineHandler lineHandler : lineHandlers) {
      if (lineHandler != null && lineHandler.players().contains(player)) {
        lineHandler.hide(Collections.singleton(player));
        return;
      }
    }
  }

  public void hide() {
    for (SidebarLineHandler lineHandler : lineHandlers) {
      if (lineHandler != null) {
        lineHandler.hide(lineHandler.players());
      }
    }
  }
}
