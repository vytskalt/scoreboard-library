package net.megavex.scoreboardlibrary.implementation.sidebar;

import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.commons.LocaleProvider;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.LocaleLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class PlayerDependantLocaleSidebar extends AbstractSidebar {
  private final Map<Player, LocaleLineHandler> playerMap = new HashMap<>();
  private final Map<Locale, LocaleLineHandler> localeMap = new HashMap<>();

  public PlayerDependantLocaleSidebar(@NotNull ScoreboardLibraryImpl scoreboardLibrary, int maxLines, @NotNull String objectiveName) {
    super(scoreboardLibrary, maxLines, objectiveName);
  }

  @Override
  public @Nullable Locale locale() {
    return null;
  }

  @Override
  protected @NotNull Set<Player> internalPlayers() {
    return playerMap.keySet();
  }

  @Override
  protected void forEachLineHandler(@NotNull Consumer<LocaleLineHandler> consumer) {
    if (localeMap != null) {
      for (LocaleLineHandler value : localeMap.values()) {
        consumer.accept(value);
      }
    }
  }

  @Override
  protected @Nullable LocaleLineHandler addPlayer0(@NotNull Player player) {
    LocaleLineHandler sidebar = playerMap.get(player);
    if (sidebar != null) {
      return null;
    }

    Locale locale = LocaleProvider.locale(player);

    sidebar = localeMap.get(locale);
    if (sidebar != null) {
      playerMap.put(player, sidebar);
      return sidebar;
    }

    sidebar = new LocaleLineHandler(this, locale);

    localeMap.put(locale, sidebar);
    playerMap.put(player, sidebar);

    return sidebar;
  }

  @Override
  protected @Nullable LocaleLineHandler removePlayer0(@NotNull Player player) {
    if (playerMap == null) return null;

    LocaleLineHandler lineHandler = playerMap.remove(player);
    if (lineHandler == null) return null;

    if (!lineHandler.hasPlayers() && localeMap != null) {
      localeMap.remove(lineHandler.locale());
    }

    return lineHandler;
  }
}
