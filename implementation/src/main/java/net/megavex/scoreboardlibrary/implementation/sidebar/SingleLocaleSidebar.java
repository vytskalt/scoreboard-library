package net.megavex.scoreboardlibrary.implementation.sidebar;

import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.sidebar.line.LocaleLineHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

public class SingleLocaleSidebar extends AbstractSidebar {
  private final Locale locale;
  private final LocaleLineHandler sidebar;
  private final Set<Player> internalPlayers = new HashSet<>(8);

  public SingleLocaleSidebar(@NotNull ScoreboardLibraryImpl scoreboardLibrary, int size, @NotNull String objectiveName, @NotNull Locale locale) {
    super(scoreboardLibrary, size, objectiveName);
    this.locale = locale;
    this.sidebar = new LocaleLineHandler(this, locale);
  }

  @Override
  public @Nullable Locale locale() {
    return locale;
  }

  @Override
  protected @NotNull Set<Player> internalPlayers() {
    return internalPlayers;
  }

  @Override
  protected @Nullable LocaleLineHandler addPlayer0(@NotNull Player player) {
    if (!internalPlayers.add(player)) return null;
    return sidebar;
  }

  @Override
  protected @Nullable LocaleLineHandler removePlayer0(@NotNull Player player) {
    if (!internalPlayers.remove(player)) return null;
    return sidebar;
  }

  @Override
  protected void forEachLineHandler(@NotNull Consumer<LocaleLineHandler> consumer) {
    consumer.accept(sidebar);
  }
}
