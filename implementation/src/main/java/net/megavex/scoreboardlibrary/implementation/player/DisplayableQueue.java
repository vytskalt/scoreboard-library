package net.megavex.scoreboardlibrary.implementation.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DisplayableQueue<T extends PlayerDisplayable> {
  private final UUID playerUuid;
  private final List<T> queue = new ArrayList<>(1);

  public DisplayableQueue(@NotNull UUID playerUuid) {
    this.playerUuid = playerUuid;
  }

  public synchronized @Nullable T current() {
    if (queue.isEmpty()) {
      return null;
    } else {
      return queue.get(0);
    }
  }

  public synchronized void add(@NotNull T displayable) {
    if (queue.contains(displayable)) {
      throw new IllegalStateException("displayable already registered");
    }

    queue.add(displayable);

    if (current() == displayable) {
      Player player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        displayable.display(player);
      }
    }
  }

  public synchronized void remove(@NotNull T displayable) {
    boolean wasCurrent = displayable == current();
    if (!queue.remove(displayable)) {
      throw new IllegalStateException("displayable not registered");
    }

    if (!wasCurrent) {
      return;
    }

    T newDisplayable = current();
    if (newDisplayable != null) {
      Player player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        newDisplayable.display(player);
      }
    }
  }
}
