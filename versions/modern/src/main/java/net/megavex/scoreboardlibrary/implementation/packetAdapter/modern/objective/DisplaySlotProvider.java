package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective;

import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectiveConstants;
import net.minecraft.world.scores.DisplaySlot;
import org.jetbrains.annotations.NotNull;

// DisplaySlot was added in 1.20.2 so this has to be a separate class
public final class DisplaySlotProvider {
  private static final DisplaySlot[] VALUES = DisplaySlot.values();

  private DisplaySlotProvider() {
  }

  public static @NotNull DisplaySlot toNms(@NotNull ObjectiveDisplaySlot slot) {
    return VALUES[ObjectiveConstants.displaySlotIndex(slot)];
  }
}
