package net.megavex.scoreboardlibrary.implementation.commons;

/**
 * Represents different strategies of rendering sidebar lines.
 */
public enum LineRenderingStrategy {
  /**
   * For versions older than 1.13, where team properties are stored as strings and have a limit of 16 characters.
   */
  LEGACY,
  /**
   * For versions 1.13-1.20.2, where team properties are stored as components with no limits.
   */
  MODERN,
  /**
   * For versions 1.20.3+, where the score display name can be used instead of team prefix team.
   * Not currently being used.
   */
  POST_MODERN
}
