package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.FieldAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;

import java.util.Collection;

public final class PacketAccessors {
  public static final Class<?> HEALTH_DISPLAY_CLASS =
    ReflectUtil.getOptionalClass(LegacyMinecraftClasses.server("IScoreboardCriteria$EnumScoreboardHealthDisplay"));
  public static final Object HEALTH_DISPLAY_INTEGER = HEALTH_DISPLAY_CLASS != null ? ReflectUtil.getEnumInstance(HEALTH_DISPLAY_CLASS, "INTEGER") : null;
  public static final Object HEALTH_DISPLAY_HEARTS = HEALTH_DISPLAY_CLASS != null ? ReflectUtil.getEnumInstance(HEALTH_DISPLAY_CLASS, "HEARTS") : null;

  private static final boolean IS_AT_LEAST_1_8 = HEALTH_DISPLAY_INTEGER != null;
  private static final boolean IS_AT_LEAST_1_9 = ReflectUtil.getOptionalClass(LegacyMinecraftClasses.server("ScoreboardTeamBase$EnumTeamPush")) != null;

  public static final Class<?> OBJECTIVE_CLASS =
    ReflectUtil.getClassOrThrow(LegacyMinecraftClasses.server("PacketPlayOutScoreboardObjective"));
  public static final ConstructorAccessor<?> OBJECTIVE_CONSTRUCTOR = ReflectUtil.findConstructor(OBJECTIVE_CLASS);
  public static final FieldAccessor<Object, Integer> OBJECTIVE_MODE_FIELD =
    ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, int.class);
  public static final FieldAccessor<Object, String> OBJECTIVE_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, String.class);
  public static final FieldAccessor<Object, String> OBJECTIVE_DISPLAY_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 1, String.class);
  public static final FieldAccessor<Object, Object> OBJECTIVE_HEALTH_DISPLAY_FIELD =
    HEALTH_DISPLAY_CLASS != null ? ReflectUtil.findFieldUnchecked(OBJECTIVE_CLASS, 0, HEALTH_DISPLAY_CLASS) : null;

  public static final Class<?> DISPLAY_OBJECTIVE_CLASS =
    ReflectUtil.getClassOrThrow(LegacyMinecraftClasses.server("PacketPlayOutScoreboardDisplayObjective"));
  public static final ConstructorAccessor<?> DISPLAY_OBJECTIVE_CONSTRUCTOR = ReflectUtil.findConstructor(DISPLAY_OBJECTIVE_CLASS);
  public static final FieldAccessor<Object, Integer> DISPLAY_OBJECTIVE_POSITION =
    ReflectUtil.findFieldUnchecked(DISPLAY_OBJECTIVE_CLASS, 0, int.class);
  public static final FieldAccessor<Object, String> DISPLAY_OBJECTIVE_NAME =
    ReflectUtil.findFieldUnchecked(DISPLAY_OBJECTIVE_CLASS, 0, String.class);

  public static final Class<?> SCORE_CLASS =
    ReflectUtil.getClassOrThrow(LegacyMinecraftClasses.server("PacketPlayOutScoreboardScore"));
  public static final ConstructorAccessor<?> SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(SCORE_CLASS, String.class);
  public static final FieldAccessor<Object, String> SCORE_OBJECTIVE_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(SCORE_CLASS, 1, String.class);
  public static final FieldAccessor<Object, Integer> SCORE_VALUE_FIELD =
    ReflectUtil.findFieldUnchecked(SCORE_CLASS, 0, int.class);
  public static final Class<?> SCORE_ACTION_CLASS =
    ReflectUtil.getOptionalClass(LegacyMinecraftClasses.server("PacketPlayOutScoreboardScore$EnumScoreboardAction"));
  public static final FieldAccessor<Object, Object> SCORE_ACTION_FIELD_1_8 =
    SCORE_ACTION_CLASS != null ? ReflectUtil.findFieldUnchecked(SCORE_CLASS, 0, SCORE_ACTION_CLASS) : null;
  public static final Object SCORE_ACTION_CHANGE_1_8 = SCORE_ACTION_FIELD_1_8 != null ? ReflectUtil.getEnumInstance(SCORE_ACTION_CLASS, "CHANGE") : null;
  public static final FieldAccessor<Object, Integer> SCORE_ACTION_FIELD_1_7 =
    SCORE_ACTION_CLASS == null ? ReflectUtil.findFieldUnchecked(SCORE_CLASS, 1, int.class) : null;

  public static final Class<?> TEAM_CLASS =
    ReflectUtil.getClassOrThrow(LegacyMinecraftClasses.server("PacketPlayOutScoreboardTeam"));
  public static final ConstructorAccessor<?> TEAM_CONSTRUCTOR = ReflectUtil.findConstructor(TEAM_CLASS);
  public static final FieldAccessor<Object, String> TEAM_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 0, String.class);
  public static final FieldAccessor<Object, String> TEAM_DISPLAY_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 1, String.class);
  public static final FieldAccessor<Object, String> TEAM_PREFIX_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 2, String.class);
  public static final FieldAccessor<Object, String> TEAM_SUFFIX_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 3, String.class);
  public static final FieldAccessor<Object, String> TEAM_NAME_TAG_VISIBILITY_FIELD =
    IS_AT_LEAST_1_8 ? ReflectUtil.findFieldUnchecked(TEAM_CLASS, 4, String.class) : null;
  public static final FieldAccessor<Object, String> TEAM_COLLISION_RULE_FIELD =
    IS_AT_LEAST_1_9 ? ReflectUtil.findFieldUnchecked(TEAM_CLASS, 5, String.class) : null;
  public static final FieldAccessor<Object, Integer> TEAM_COLOR_FIELD =
    IS_AT_LEAST_1_8 ? ReflectUtil.findFieldUnchecked(TEAM_CLASS, 0, int.class) : null;
  @SuppressWarnings("rawtypes")
  public static final FieldAccessor<Object, Collection> TEAM_ENTRIES_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, 0, Collection.class);
  public static final FieldAccessor<Object, Integer> TEAM_MODE_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, IS_AT_LEAST_1_8 ? 1 : 0, int.class);
  public static final FieldAccessor<Object, Integer> TEAM_RULES_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_CLASS, IS_AT_LEAST_1_8 ? 2 : 1, int.class);

  private PacketAccessors() {
  }
}
