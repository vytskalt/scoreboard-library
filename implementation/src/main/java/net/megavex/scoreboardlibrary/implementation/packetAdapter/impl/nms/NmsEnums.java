package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms;

import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.FieldAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;

import java.util.HashMap;
import java.util.Map;

import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.*;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.DISPLAY_SLOT_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.SCORE_ACTION_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.TEAM_COLLISION_RULE_CLASS;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.TEAM_VISIBILITY_CLASS;

public final class NmsEnums {
  private NmsEnums() {
  }

  public static final Object SCORE_ACTION_CHANGE;
  public static final Object SCORE_ACTION_REMOVE;

  static {
    if (IS_1_8_OR_ABOVE && !IS_1_20_3_OR_ABOVE) {
      SCORE_ACTION_CHANGE = ReflectUtil.getEnumInstance(SCORE_ACTION_CLASS, "CHANGE", "a");
      SCORE_ACTION_REMOVE = ReflectUtil.getEnumInstance(SCORE_ACTION_CLASS, "REMOVE", "b");
    } else {
      SCORE_ACTION_CHANGE = null;
      SCORE_ACTION_REMOVE = null;
    }
  }

  public static final Map<NamedTextColor, Enum<?>> ADVENTURE_TO_NMS_CHAT_FORMATTING = new HashMap<>();

  static {
    Object[] chatFormattings = NmsClasses.TEAM_COLOR_OR_CHAT_FORMATTING_CLASS.getEnumConstants();
    FieldAccessor<Object, Object> nameField = ReflectUtil.findFieldUnchecked(NmsClasses.TEAM_COLOR_OR_CHAT_FORMATTING_CLASS, 0, String.class);

    outer:
    for (NamedTextColor color : NamedTextColor.NAMES.values()) {
      for (Object chatFormatting : chatFormattings) {
        if (color.toString().equalsIgnoreCase((String) nameField.get(chatFormatting))) {
          ADVENTURE_TO_NMS_CHAT_FORMATTING.put(color, (Enum<?>) chatFormatting);
          continue outer;
        }
      }
      throw new RuntimeException("No chat formatting enum constant found for " + color.toString());
    }
  }

  public static final Object NAME_TAG_VISIBILITY_ALWAYS = ReflectUtil.getEnumInstance(TEAM_VISIBILITY_CLASS, "ALWAYS");
  public static final Object NAME_TAG_VISIBILITY_NEVER = ReflectUtil.getEnumInstance(TEAM_VISIBILITY_CLASS, "NEVER");
  public static final Object NAME_TAG_VISIBILITY_HIDE_FOR_OTHER_TEAMS = ReflectUtil.getEnumInstance(TEAM_VISIBILITY_CLASS, "HIDE_FOR_OTHER_TEAMS");
  public static final Object NAME_TAG_VISIBILITY_HIDE_FOR_OWN_TEAM = ReflectUtil.getEnumInstance(TEAM_VISIBILITY_CLASS, "HIDE_FOR_OWN_TEAM");

  public static final Object COLLISION_RULE_ALWAYS;
  public static final Object COLLISION_RULE_NEVER;
  public static final Object COLLISION_RULE_PUSH_OTHER_TEAMS;
  public static final Object COLLISION_RULE_PUSH_OWN_TEAM;

  static {
    if (TEAM_COLLISION_RULE_CLASS != null) {
      COLLISION_RULE_ALWAYS = ReflectUtil.getEnumInstance(TEAM_COLLISION_RULE_CLASS, "ALWAYS");
      COLLISION_RULE_NEVER = ReflectUtil.getEnumInstance(TEAM_COLLISION_RULE_CLASS, "NEVER");
      COLLISION_RULE_PUSH_OTHER_TEAMS = ReflectUtil.getEnumInstance(TEAM_COLLISION_RULE_CLASS, "PUSH_OTHER_TEAMS");
      COLLISION_RULE_PUSH_OWN_TEAM = ReflectUtil.getEnumInstance(TEAM_COLLISION_RULE_CLASS, "PUSH_OWN_TEAM");
    } else {
      COLLISION_RULE_ALWAYS = null;
      COLLISION_RULE_NEVER = null;
      COLLISION_RULE_PUSH_OTHER_TEAMS = null;
      COLLISION_RULE_PUSH_OWN_TEAM = null;
    }
  }

  public static final Object RENDER_TYPE_INTEGER = ReflectUtil.getEnumInstance(OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS, "INTEGER");
  public static final Object RENDER_TYPE_HEARTS = ReflectUtil.getEnumInstance(OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS, "HEARTS");

  public static final Object[] DISPLAY_SLOT_VALUES = DISPLAY_SLOT_CLASS == null ? null : DISPLAY_SLOT_CLASS.getEnumConstants();

  public static Object nameTagVisibility(NameTagVisibility value) {
    switch (value) {
      case NEVER:
        return NAME_TAG_VISIBILITY_NEVER;
      case ALWAYS:
        return NAME_TAG_VISIBILITY_ALWAYS;
      case HIDE_FOR_OTHER_TEAMS:
        return NAME_TAG_VISIBILITY_HIDE_FOR_OTHER_TEAMS;
      case HIDE_FOR_OWN_TEAM:
        return NAME_TAG_VISIBILITY_HIDE_FOR_OWN_TEAM;
      default:
        throw new IllegalStateException("unknown name tag visibility " + value.name());
    }
  }

  public static Object collisionRule(CollisionRule value) {
    switch (value) {
      case NEVER:
        return COLLISION_RULE_NEVER;
      case ALWAYS:
        return COLLISION_RULE_ALWAYS;
      case PUSH_OTHER_TEAMS:
        return COLLISION_RULE_PUSH_OTHER_TEAMS;
      case PUSH_OWN_TEAM:
        return COLLISION_RULE_PUSH_OWN_TEAM;
      default:
        throw new IllegalStateException("unknown collision rule " + value.name());
    }
  }

  public static Object renderType(ObjectiveRenderType renderType) {
    switch (renderType) {
      case INTEGER:
        return RENDER_TYPE_INTEGER;
      case HEARTS:
        return RENDER_TYPE_HEARTS;
      default:
        throw new IllegalStateException("unknown render type " + renderType);
    }
  }
}
