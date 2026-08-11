package net.megavex.scoreboardlibrary.tests;

import java.util.Objects;

public final class Assert {
  private Assert() {
  }

  public static void equals(final Object expected, final Object actual) {
    isTrue(Objects.equals(expected, actual), "Expected '" + expected + "', but got '" + actual + "'");
  }

  public static void isNull(final Object value) {
    isTrue(value == null, "Expected null, got " + value);
  }

  public static void isTrue(final boolean value, final String msg) {
    if (!value) {
      throw new AssertFailedException(msg);
    }
  }
}
