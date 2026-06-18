package net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.invoke.MethodHandle;

public interface ConstructorAccessor<T> {
  @NotNull T invoke(@UnknownNullability Object... args);
}
