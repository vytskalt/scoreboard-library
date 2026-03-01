package net.megavex.scoreboardlibrary.implementation.packetAdapter.objective;

import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public final class ObjectiveConstants {
  public static final int MODE_CREATE = 0,
    MODE_REMOVE = 1,
    MODE_UPDATE = 2,
    LEGACY_VALUE_CHAR_LIMIT = 32;

  private ObjectiveConstants() {
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

  public static int displaySlotIndex(@NotNull ObjectiveDisplaySlot slot) {
    return displaySlotIndex(slot, true);
  }

  public static int displaySlotIndex(@NotNull ObjectiveDisplaySlot slot, boolean isAbove1_8) {
    if (slot instanceof ObjectiveDisplaySlot.PlayerList) {
      return 0;
    }

    if (slot instanceof ObjectiveDisplaySlot.Sidebar || (!isAbove1_8 && slot instanceof ObjectiveDisplaySlot.TeamSidebar)) {
      return 1;
    }

    if (slot instanceof ObjectiveDisplaySlot.BelowName) {
      return 2;
    }

    NamedTextColor color = ((ObjectiveDisplaySlot.TeamSidebar) slot).teamColor();
    char legacyChar = LegacyFormatUtil.getChar(color);
    ChatColor chatColor = ChatColor.getByChar(legacyChar);
    return 3 + chatColor.ordinal();
  }
}
