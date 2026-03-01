package net.megavex.scoreboardlibrary.implementation.commons;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.*;

public final class CollectionProvider {
  private static final MethodHandle mapConstructor, setConstructor, listConstructor;

  static {
    Lookup lookup = MethodHandles.publicLookup();
    MethodType parameters = MethodType.methodType(void.class, int.class);
    mapConstructor = getConstructor(lookup, "it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap", HashMap.class, parameters);
    setConstructor = getConstructor(lookup, "it.unimi.dsi.fastutil.objects.ObjectOpenHashSet", HashSet.class, parameters);
    listConstructor = getConstructor(lookup, "it.unimi.dsi.fastutil.objects.ObjectArrayList", ArrayList.class, parameters);
  }

  private CollectionProvider() {
  }

  private static MethodHandle getConstructor(Lookup lookup, String fastUtilClass, Class<?> fallback, MethodType parameters) {
    Class<?> clazz;
    try {
      clazz = Class.forName(fastUtilClass);
    } catch (ClassNotFoundException e) {
      clazz = fallback;
    }

    try {
      return lookup.findConstructor(clazz, parameters);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public static <K, V> @NotNull Map<K, V> map(int capacity) {
    try {
      // noinspection unchecked
      return (Map<K, V>) mapConstructor.invokeWithArguments(capacity);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public static <E> @NotNull Set<E> set(int capacity) {
    try {
      // noinspection unchecked
      return (Set<E>) setConstructor.invokeWithArguments(capacity);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  public static <E> @NotNull List<E> list(int capacity) {
    try {
      // noinspection unchecked
      return (List<E>) listConstructor.invokeWithArguments(capacity);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
