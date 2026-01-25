package net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.invoke.MethodHandle;

public class FieldAccessor<T, V> {
  private final MethodHandle getter, setter;

  public FieldAccessor(@NotNull MethodHandle getter, @NotNull MethodHandle setter) {
    this.getter = getter;
    this.setter = setter;
  }

  public Object get(T instance) {
    try {
      return getter.invokeExact(instance);
    } catch (Throwable e) {
      throw new IllegalStateException("couldn't set value of field", e);
    }
  }

  public void set(T instance, @UnknownNullability V value) {
    try {
      setter.invokeExact(instance, value);
    } catch (Throwable e) {
      throw new IllegalStateException("couldn't set value of field", e);
    }
  }
}
