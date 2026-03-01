package net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.invoke.MethodHandle;

public class ConstructorAccessor<T> {
  private final MethodHandle handle;

  public ConstructorAccessor(@NotNull MethodHandle handle) {
    this.handle = handle;
  }

  public @NotNull T invoke(@UnknownNullability Object... args) {
    try {
      //noinspection unchecked
      return (T) handle.invokeExact(args);
    } catch (Throwable e) {
      throw new IllegalStateException("couldn't instantiate constructor", e);
    }
  }
}
