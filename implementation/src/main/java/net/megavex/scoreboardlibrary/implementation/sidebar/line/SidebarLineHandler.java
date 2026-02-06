package net.megavex.scoreboardlibrary.implementation.sidebar.line;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LegacyLocaleLine;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.LocaleLine;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.ModernLocaleLine;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.locale.PostModernLocaleLine;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class SidebarLineHandler {
  private final LineRenderingStrategy strategy;
  private final LocaleLineHandler localeLineHandler;
  private final Set<Player> players = CollectionProvider.set(1);
  private final LocaleLine[] lines;

  public SidebarLineHandler(@NotNull LineRenderingStrategy strategy, @NotNull LocaleLineHandler localeLineHandler) {
    this.strategy = strategy;
    this.localeLineHandler = localeLineHandler;
    this.lines = new LocaleLine[localeLineHandler.sidebar().maxLines()];

    for (GlobalLineInfo line : localeLineHandler.sidebar().lines()) {
      if (line != null) {
        Component value = line.value();
        if (value != null) {
          setLine(line.line(), GlobalTranslator.render(value, localeLineHandler.locale()));
        }
      }
    }
  }

  public @NotNull LocaleLineHandler localeLineHandler() {
    return localeLineHandler;
  }

  public @NotNull Set<Player> players() {
    return players;
  }

  public void updateScores() {
    for (LocaleLine line : lines) {
      if (line != null && line.info().updateScore()) {
        line.sendScore(players);
      }
    }
  }

  public void updateScore(int line) {
    LocaleLine localeLine = lines[line];
    localeLine.sendScore(players);
  }

  public void setLine(int line, Component renderedLine) {
    LocaleLine localeLine = lines[line];
    if (renderedLine == null && localeLine == null) {
      return;
    }

    boolean newlyCreated = false;
    if (localeLine == null) {
      lines[line] = localeLine = createLine(strategy, localeLineHandler.sidebar().lines()[line]);
      newlyCreated = true;
    }

    if (renderedLine == null) {
      lines[line] = null;
    } else {
      localeLine.value(renderedLine);
    }

    if (renderedLine == null) {
      localeLine.hide(players);
    } else if (newlyCreated) {
      localeLine.resetOldPlayer();
      localeLine.show(players);
    } else {
      localeLine.updateTeam();
    }
  }

  public void show(Collection<Player> players) {
    for (LocaleLine line : lines) {
      if (line != null) {
        line.show(players);
      }
    }
  }

  public void hide(Collection<Player> players) {
    for (LocaleLine line : lines) {
      if (line != null) {
        line.hide(players);
      }
    }
  }

  private @NotNull LocaleLine createLine(@NotNull LineRenderingStrategy strategy, @NotNull GlobalLineInfo line) {
    switch (strategy) {
      case LEGACY:
        return new LegacyLocaleLine(line, this);
      case MODERN:
        return new ModernLocaleLine(line, this);
      case POST_MODERN:
        return new PostModernLocaleLine(line, this);
      default:
        throw new IllegalArgumentException();
    }
  }
}
