package net.megavex.scoreboardlibrary.api.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Represents the display properties of a scoreboard team.
 * Note: this interface is not thread-safe, meaning you can only use it from one thread at a time,
 * although it does not have to be the main thread.
 */
@ApiStatus.NonExtendable
public interface TeamDisplay {
  /**
   * @return team which this team display is assigned to
   */
  @NotNull ScoreboardTeam team();

  // Entries

  /**
   * Gets the entries. The entries specify which players are members of the team.
   *
   * @return entries of the team.
   */
  @NotNull Collection<String> entries();

  /**
   * Adds an entry.
   *
   * @param entry entry to add
   * @return whether the entry was added
   * @see #entries
   */
  boolean addEntry(@NotNull String entry);

  /**
   * Adds multiple entries.
   *
   * @param entries entries to add
   * @return whether any entry was added
   * @see #entries
   */
  boolean addEntries(@NotNull Collection<String> entries);

  /**
   * Removes an entry.
   *
   * @param entry entry to remove
   * @return whether the entry was removed
   * @see #entries
   */
  boolean removeEntry(@NotNull String entry);

  /**
   * Removes multiple entries.
   *
   * @param entries entries to remove
   * @return whether any entry was removed
   * @see #entries
   */
  boolean removeEntries(@NotNull Collection<String> entries);

  // Properties

  /**
   * @return display name, defaults to {@link Component#empty()}
   */
  @NotNull Component displayName();

  /**
   * Updates the display name.
   *
   * @param displayName new display name
   */
  @NotNull TeamDisplay displayName(@NotNull ComponentLike displayName);

  /**
   * Gets the prefix, which defaults to {@link Component#empty()}.
   * The suffix is prepended to all team members' names.
   *
   * @return prefix, defaults to {@link Component#empty()}
   */
  @NotNull Component prefix();

  /**
   * Updates the prefix.
   *
   * @param prefix new prefix
   * @see #prefix()
   */
  @NotNull TeamDisplay prefix(@NotNull ComponentLike prefix);

  /**
   * Gets the suffix, which defaults to {@link Component#empty()}.
   * The suffix is appended to all team members' names.
   *
   * @return suffix
   */
  @NotNull Component suffix();

  /**
   * Updates the suffix.
   *
   * @param suffix new suffix
   * @see #suffix()
   */
  @NotNull TeamDisplay suffix(@NotNull ComponentLike suffix);

  /**
   * @return friendly fire rule value, defaults to false
   */
  boolean friendlyFire();

  /**
   * Updates the friendly fire rule.
   *
   * @param friendlyFire new rule value
   */
  @NotNull TeamDisplay friendlyFire(boolean friendlyFire);

  /**
   * Gets the can see friendly invisibles rule value, which is false by default.
   * If true, team members can see other members with the invisibility effect.
   *
   * @return rule value
   */
  boolean canSeeFriendlyInvisibles();

  /**
   * Updates the can see friendly invisibles rule.
   *
   * @param canSeeFriendlyInvisibles new rule value
   * @see #canSeeFriendlyInvisibles()
   */
  @NotNull TeamDisplay canSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles);

  /**
   * @return name tag visibility rule value
   * @since Minecraft 1.8
   */
  @NotNull NameTagVisibility nameTagVisibility();

  /**
   * Updates the name tag visibility rule.
   *
   * @param nameTagVisibility new rule value
   * @since Minecraft 1.8
   */
  @NotNull TeamDisplay nameTagVisibility(@NotNull NameTagVisibility nameTagVisibility);

  /**
   * @return collision rule
   * @since Minecraft 1.9
   */
  @NotNull CollisionRule collisionRule();

  /**
   * Updates the collision rule.
   *
   * @param collisionRule new rule value
   * @since Minecraft 1.9
   */
  @NotNull TeamDisplay collisionRule(@NotNull CollisionRule collisionRule);

  /**
   * Gets the player color. The player color is the color player name tags are displayed in.
   * It is also used for displaying team specific sidebars with the {@link ObjectiveDisplaySlot.TeamSidebar}} display slot.
   *
   * @return player color
   * @since Minecraft 1.8
   */
  @Nullable NamedTextColor playerColor();

  /**
   * Updates the player color.
   *
   * @param playerColor new player color
   * @see #playerColor()
   * @since Minecraft 1.8
   */
  @NotNull TeamDisplay playerColor(@Nullable NamedTextColor playerColor);

  /**
   * Refreshes the team display, re-rendering any {@link net.kyori.adventure.text.TranslatableComponent}s.
   */
  void refresh();
}
