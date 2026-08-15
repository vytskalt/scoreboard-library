package net.megavex.scoreboardlibrary.api.exception;

/**
 * Exception thrown by {@link net.megavex.scoreboardlibrary.api.ScoreboardLibrary#loadScoreboardLibrary} indicating that the current server version is unsupported.
 * As a fallback, consider using the {@link net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary} implementation.
 */
public final class NoPacketAdapterAvailableException extends Exception {
  public NoPacketAdapterAvailableException() {
  }
}
