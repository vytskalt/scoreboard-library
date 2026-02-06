package net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.invoke.MethodHandle;

public class FieldAccessor<T, V> {
  private final MethodHandle getter, setter;

  public FieldAccessor(@NotNull MethodHandle getter, @Nullable MethodHandle setter) {
    this.getter = getter;
    this.setter = setter;
  }

  public Object get(@Nullable T instance) {
    try {
      if (instance == null) {
        return getter.invoke();
      } else {
        return getter.invoke(instance);
      }
    } catch (Throwable e) {
      throw new IllegalStateException("couldn't set value of field", e);
    }
  }

  public void set(T instance, @UnknownNullability V value) {
    if (this.setter == null) {
      throw new IllegalStateException("cannot set static final field");
    }
    try {
      setter.invokeExact(instance, value);
    } catch (Throwable e) {
      throw new IllegalStateException("couldn't set value of field", e);
    }
  }
}
