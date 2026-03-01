package net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.invoke.MethodHandle;

public class MethodAccessor {
  private final MethodHandle handle;

  public MethodAccessor(@NotNull MethodHandle handle) {
    this.handle = handle;
  }

  public Object invoke(@UnknownNullability Object... args) {
    try {
      return handle.invokeExact(args);
    } catch (Throwable e) {
      throw new IllegalStateException("couldn't set value of field", e);
    }
  }
}
