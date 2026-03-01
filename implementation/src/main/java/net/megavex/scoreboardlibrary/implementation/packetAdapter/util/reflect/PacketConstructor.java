package net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect;

import org.jetbrains.annotations.NotNull;

public interface PacketConstructor<T> {
  @NotNull T invoke();
}
