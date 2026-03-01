package net.megavex.scoreboardlibrary.implementation.packetAdapter.team;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import org.jetbrains.annotations.NotNull;

public final class TeamConstants {
  public static final int DISPLAY_NAME_LEGACY_LIMIT = 32;
  public static final int PREFIX_SUFFIX_LEGACY_LIMIT = 16;

  public static final int MODE_CREATE = 0,
    MODE_REMOVE = 1,
    MODE_UPDATE = 2,
    MODE_ADD_ENTRIES = 3,
    MODE_REMOVE_ENTRIES = 4;

  private TeamConstants() {
  }

  public static int mode(@NotNull PropertiesPacketType packetType) {
    switch (packetType) {
      case CREATE:
        return MODE_CREATE;
      case UPDATE:
        return MODE_UPDATE;
      default:
        throw new IllegalStateException();
    }
  }

  public static int mode(@NotNull EntriesPacketType packetType) {
    switch (packetType) {
      case ADD:
        return MODE_ADD_ENTRIES;
      case REMOVE:
        return MODE_REMOVE_ENTRIES;
      default:
        throw new IllegalStateException();
    }
  }
}
