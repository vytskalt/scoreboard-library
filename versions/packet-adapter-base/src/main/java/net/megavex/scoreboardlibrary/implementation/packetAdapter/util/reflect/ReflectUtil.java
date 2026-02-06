package net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectUtil {
  // Inspired by
  // https://github.com/dmulloy2/ProtocolLib/blob/02e917cd08cf5b37a52052e22b223272a040e0df/src/main/java/com/comphenix/protocol/reflect/accessors/MethodHandleHelper.java

  private static final Object UNSAFE;
  private static final MethodHandles.Lookup LOOKUP;
  private static final MethodHandle ALLOCATE_INSTANCE_HANDLE;
  private static final MethodType VOID_METHOD_TYPE = MethodType.methodType(void.class);
  private static final MethodType VIRTUAL_FIELD_SETTER = MethodType.methodType(void.class, Object.class, Object.class);

  static {
    MethodHandles.Lookup normalLookup = MethodHandles.lookup();

    try {
      Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
      Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
      theUnsafe.setAccessible(true);
      UNSAFE = theUnsafe.get(null);
      ALLOCATE_INSTANCE_HANDLE = normalLookup.findVirtual(UNSAFE.getClass(), "allocateInstance", MethodType.methodType(Object.class, Class.class));
    } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
      throw new ExceptionInInitializerError(e);
    }

    MethodHandles.Lookup lookup;
    try {
      MethodHandle staticFieldOffset = normalLookup.findVirtual(UNSAFE.getClass(), "staticFieldOffset", MethodType.methodType(long.class, Field.class));
      MethodHandle staticFieldBase = normalLookup.findVirtual(UNSAFE.getClass(), "staticFieldBase", MethodType.methodType(Object.class, Field.class));
      MethodHandle getObject = normalLookup.findVirtual(UNSAFE.getClass(), "getObject", MethodType.methodType(Object.class, Object.class, long.class));

      Field trustedLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
      long offset = (long) staticFieldOffset.invoke(UNSAFE, trustedLookup);
      Object baseValue = staticFieldBase.invoke(UNSAFE, trustedLookup);

      lookup = (MethodHandles.Lookup) getObject.invoke(UNSAFE, baseValue, offset);
    } catch (Throwable e) {
      e.printStackTrace();
      lookup = normalLookup;
    }

    LOOKUP = lookup;
  }

  private ReflectUtil() {
  }

  public static @NotNull Class<?> getClassOrThrow(String... names) {
    Class<?> c = getOptionalClass(names);
    if (c == null) {
      throw new IllegalStateException("Class with names either of " + String.join(", ", names) + " not found");
    }
    return c;
  }

  public static @Nullable Class<?> getOptionalClass(String... names) {
    for (String name : names) {
      if (name == null) continue;
      try {
        return Class.forName(name);
      } catch (ClassNotFoundException ignored) {
      }
    }
    return null;
  }

  public static @NotNull Object getEnumInstance(@NotNull Class<?> clazz, @NotNull String... names) {
    for (String name : names) {
      try {
        //noinspection unchecked,rawtypes
        return Enum.valueOf((Class<? extends Enum>) clazz, name);
      } catch (IllegalArgumentException ignored) {
      }
    }
    throw new IllegalStateException("Enum " + clazz.getName() + " instance with names either of  " + String.join(",", names) + " not found");
  }

  public static <T, V> @NotNull FieldAccessor<T, V> findFieldUnchecked(@NotNull Class<?> clazz, int index, @NotNull Class<?> valueClass) {
    return findFieldUnchecked(clazz, index, valueClass, false);
  }

  public static <T, V> @NotNull FieldAccessor<T, V> findFieldUnchecked(@NotNull Class<?> clazz, int index, @NotNull Class<?> valueClass, boolean isStatic) {
    int i = 0;
    for (Field field : clazz.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers()) != isStatic || field.getType() != valueClass) {
        continue;
      }

      if (i == index) {
        try {
          MethodHandle getter = LOOKUP.unreflectGetter(field);
          MethodHandle setter = !isStatic || !Modifier.isFinal(field.getModifiers()) ? LOOKUP.unreflectSetter(field).asType(VIRTUAL_FIELD_SETTER) : null;
          return new FieldAccessor<>(getter, setter);
        } catch (IllegalAccessException e) {
          throw new RuntimeException("failed to unreflect field setter", e);
        }
      }

      i++;
    }

    throw new IllegalStateException(
      "couldn't find field with class " + valueClass.getSimpleName() + " on " + clazz.getSimpleName() + " at index " + index
    );
  }

  public static <T> @Nullable ConstructorAccessor<T> findOptionalConstructor(@NotNull Class<T> clazz, @NotNull Class<?>... args) {
    try {
      MethodHandle handle = LOOKUP.findConstructor(clazz, MethodType.methodType(void.class, args));
      return new ConstructorAccessor<>(convertToGeneric(handle));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      return null;
    }
  }

  public static <T> @NotNull ConstructorAccessor<T> findConstructor(@NotNull Class<T> clazz, @NotNull Class<?>... args) {
    ConstructorAccessor<T> accessor = findOptionalConstructor(clazz, args);
    if (accessor == null) {
      throw new RuntimeException("Constructor for class " + clazz.getName() + " not found");
    }
    return accessor;
  }

  public static <T> @NotNull PacketConstructor<T> getEmptyConstructor(@NotNull Class<T> packetClass) {
    try {
      MethodHandle constructor = LOOKUP.findConstructor(packetClass, VOID_METHOD_TYPE);
      return () -> {
        try {
          // noinspection unchecked
          return (T) constructor.invoke();
        } catch (Throwable e) {
          throw new IllegalStateException("couldn't create packet instance", e);
        }
      };
    } catch (NoSuchMethodException | IllegalAccessException ignored) {
    }

    return () -> {
      try {
        // noinspection unchecked
        return (T) ALLOCATE_INSTANCE_HANDLE.invoke(UNSAFE, packetClass);
      } catch (Throwable e) {
        throw new IllegalStateException("couldn't allocate packet instance using Unsafe", e);
      }
    };
  }

  public static @NotNull MethodAccessor findMethod(@NotNull Class<?> clazz, boolean isStatic, @NotNull MethodType methodType, @NotNull String... names) {
    MethodAccessor accessor = findOptionalMethod(clazz, isStatic, methodType, names);
    if (accessor == null) {
      throw new RuntimeException("Method " + String.join(",", names) + " for class " + clazz.getName() + " not found");
    }
    return accessor;
  }

  public static <T> @Nullable MethodAccessor findOptionalMethod(@NotNull Class<T> clazz, boolean isStatic, @NotNull MethodType methodType, @NotNull String... names) {
    if (names.length == 0) {
      throw new IllegalArgumentException("no method names passed");
    }
    for (String name : names) {
      MethodHandle handle;
      try {
        if (isStatic) {
          handle = LOOKUP.findStatic(clazz, name, methodType);
        } else {
          handle = LOOKUP.findVirtual(clazz, name, methodType);
        }
        return new MethodAccessor(convertToGeneric(handle));
      } catch (NoSuchMethodException ignored) {
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  private static @NotNull MethodHandle convertToGeneric(@NotNull MethodHandle handle) {
    MethodHandle target = handle.asFixedArity();
    MethodType methodType = MethodType.genericMethodType(0, true);
    target = target.asSpreader(Object[].class, handle.type().parameterCount());
    return target.asType(methodType);
  }
}
