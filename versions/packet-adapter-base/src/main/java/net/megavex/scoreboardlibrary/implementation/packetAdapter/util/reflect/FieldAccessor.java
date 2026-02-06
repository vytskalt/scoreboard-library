package net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.invoke.MethodHandle;

public class FieldAccessor<T, V> {
  private final MethodHandle setter;

  public FieldAccessor(@NotNull MethodHandle setter) {
    this.setter = setter;
  }

  public void set(@NotNull T instance, @UnknownNullability V value) {
    try {
      setter.invokeExact(instance, value);
    } catch (Throwable e) {
      throw new IllegalStateException("couldn't set value of field", e);
    }
  }
}
