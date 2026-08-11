package net.megavex.scoreboardlibrary.tests;

public final class AssertFailedException extends RuntimeException {
  public AssertFailedException(final String message) {
    super("ASSERTION FAILED: " + message);
  }
}
