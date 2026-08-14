package net.megavex.slib.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;

public final class TeamProperties {
  private final Component displayName;
  private final Component prefix;
  private final Component suffix;
  private final boolean isFriendlyFire;
  private final boolean canSeeFriendlyInvisibles;
  private final NameTagVisibility nameTagVisibility;
  private final CollisionRule collisionRule;
  private final NamedTextColor playerColor;

  public TeamProperties(
    final Component displayName,
    final Component prefix,
    final Component suffix,
    final boolean isFriendlyFire,
    final boolean canSeeFriendlyInvisibles,
    final NameTagVisibility nameTagVisibility,
    final CollisionRule collisionRule,
    final NamedTextColor playerColor) {
    this.displayName = displayName;
    this.prefix = prefix;
    this.suffix = suffix;
    this.isFriendlyFire = isFriendlyFire;
    this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
    this.nameTagVisibility = nameTagVisibility;
    this.collisionRule = collisionRule;
    this.playerColor = playerColor;
  }

  public Component getDisplayName() {
    return displayName;
  }

  public Component getPrefix() {
    return prefix;
  }

  public Component getSuffix() {
    return suffix;
  }

  public boolean isFriendlyFire() {
    return isFriendlyFire;
  }

  public boolean isCanSeeFriendlyInvisibles() {
    return canSeeFriendlyInvisibles;
  }

  public NameTagVisibility getNameTagVisibility() {
    return nameTagVisibility;
  }

  public CollisionRule getCollisionRule() {
    return collisionRule;
  }

  public NamedTextColor getPlayerColor() {
    return playerColor;
  }
}
