package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms;

import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ConstructorAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.FieldAccessor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;

import java.util.Collection;
import java.util.Optional;

import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses.*;
import static net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsEnums.*;

public final class NmsTeams {
  private NmsTeams() {
  }

  public static final ConstructorAccessor<?> PARAMETERS_CONSTRUCTOR; // full record constructor on 26.2+, empty constructor for below
  public static final ConstructorAccessor<?> TEAM_PACKET_CONSTRUCTOR;

  public static final FieldAccessor<Object, String> TEAM_NAME_FIELD;
  public static final FieldAccessor<Object, Integer> TEAM_MODE_FIELD;
  public static final FieldAccessor<Object, Collection<String>> TEAM_ENTRIES_FIELD;

  static {
    if (IS_26_2_OR_ABOVE) {
      PARAMETERS_CONSTRUCTOR = ReflectUtil.findConstructor(TEAM_PARAMETERS_PKT_CLASS, COMPONENT_CLASS, COMPONENT_CLASS, COMPONENT_CLASS, TEAM_VISIBILITY_CLASS, TEAM_COLLISION_RULE_CLASS, Optional.class, byte.class);
      TEAM_PACKET_CONSTRUCTOR = ReflectUtil.findConstructor(SET_PLAYER_TEAM_PKT_CLASS, String.class, int.class, Optional.class, Collection.class); // same as below

      TEAM_NAME_FIELD = null;
      TEAM_MODE_FIELD = null;
      TEAM_ENTRIES_FIELD = null;
    } else if (IS_1_17_OR_ABOVE) {
      assert TEAM_PARAMETERS_PKT_CLASS != null;
      PARAMETERS_CONSTRUCTOR = ReflectUtil.getEmptyConstructor(TEAM_PARAMETERS_PKT_CLASS);
      TEAM_PACKET_CONSTRUCTOR = ReflectUtil.findConstructor(SET_PLAYER_TEAM_PKT_CLASS, String.class, int.class, Optional.class, Collection.class);

      TEAM_NAME_FIELD = null;
      TEAM_MODE_FIELD = null;
      TEAM_ENTRIES_FIELD = null;
    } else {
      PARAMETERS_CONSTRUCTOR = null;
      TEAM_PACKET_CONSTRUCTOR = ReflectUtil.findConstructor(SET_PLAYER_TEAM_PKT_CLASS);

      TEAM_NAME_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 0, String.class);
      TEAM_MODE_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 0, int.class);
      TEAM_ENTRIES_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 0, Collection.class);
    }
  }

  public static final FieldAccessor<Object, Object> DISPLAY_NAME_FIELD;
  public static final FieldAccessor<Object, Object> PREFIX_FIELD;
  public static final FieldAccessor<Object, Object> SUFFIX_FIELD;

  public static final FieldAccessor<Object, Object> NAME_TAG_VISIBILITY_FIELD;
  public static final FieldAccessor<Object, Object> COLLISION_RULE_FIELD;

  public static final FieldAccessor<Object, Object> COLOR_FIELD;
  public static final FieldAccessor<Object, Integer> OPTIONS_FIELD;

  static {
    if (IS_26_2_OR_ABOVE) {
      DISPLAY_NAME_FIELD = null;
      PREFIX_FIELD = null;
      SUFFIX_FIELD = null;
      NAME_TAG_VISIBILITY_FIELD = null;
      COLLISION_RULE_FIELD = null;
      COLOR_FIELD = null;
      OPTIONS_FIELD = null;
    } else if (IS_1_17_OR_ABOVE) {
      DISPLAY_NAME_FIELD = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, COMPONENT_CLASS);
      PREFIX_FIELD = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 1, COMPONENT_CLASS);
      SUFFIX_FIELD = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 2, COMPONENT_CLASS);

      if (IS_1_21_5_OR_ABOVE) {
        NAME_TAG_VISIBILITY_FIELD = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, TEAM_VISIBILITY_CLASS);
        COLLISION_RULE_FIELD = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, TEAM_COLLISION_RULE_CLASS);
      } else {
        NAME_TAG_VISIBILITY_FIELD = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, String.class);
        COLLISION_RULE_FIELD = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 1, String.class);
      }

      COLOR_FIELD = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, TEAM_COLOR_OR_CHAT_FORMATTING_CLASS);
      OPTIONS_FIELD = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, int.class);
    } else if (IS_1_13_OR_ABOVE) {
      DISPLAY_NAME_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 0, COMPONENT_CLASS);
      PREFIX_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 1, COMPONENT_CLASS);
      SUFFIX_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 2, COMPONENT_CLASS);

      NAME_TAG_VISIBILITY_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 1, String.class);
      COLLISION_RULE_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 2, String.class);

      COLOR_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 0, TEAM_COLOR_OR_CHAT_FORMATTING_CLASS);
      OPTIONS_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 1, int.class);
    } else {
      DISPLAY_NAME_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 1, String.class);
      PREFIX_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 2, String.class);
      SUFFIX_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 3, String.class);

      NAME_TAG_VISIBILITY_FIELD = IS_1_8_OR_ABOVE ? ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 4, String.class) : null;
      COLLISION_RULE_FIELD = IS_1_9_OR_ABOVE ? ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 5, String.class) : null;

      COLOR_FIELD = IS_1_8_OR_ABOVE ? ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 0, int.class) : null;
      OPTIONS_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, IS_1_8_OR_ABOVE ? 2 : 1, int.class);
    }
  }

  public static Object createTeamParameters(Object displayName, Object playerPrefix, Object playerSuffix, ImmutableTeamProperties<?> otherProps) {
    if (IS_26_2_OR_ABOVE) {
      Object chatFormattingColor = null;
      if (otherProps.playerColor() != null) {
        chatFormattingColor = ADVENTURE_TO_NMS_CHAT_FORMATTING.get(otherProps.playerColor());
      }
      Object nmsNameTagVisibility = nameTagVisibility(otherProps.nameTagVisibility());
      Object nmsCollisionRule = collisionRule(otherProps.collisionRule());
      return PARAMETERS_CONSTRUCTOR.invoke(displayName, playerPrefix, playerSuffix, nmsNameTagVisibility, nmsCollisionRule, Optional.ofNullable(chatFormattingColor), (byte) otherProps.packOptions());
    }

    Object parameters = PARAMETERS_CONSTRUCTOR.invoke();
    setupOldTeamPropertiesFields(parameters, displayName, playerPrefix, playerSuffix, otherProps);
    return parameters;
  }

  public static void setupOldTeamPropertiesFields(Object teamPacket, Object displayName, Object playerPrefix, Object playerSuffix, ImmutableTeamProperties<?> otherProps) {
    Object nmsNameTagVisibility;
    Object nmsCollisionRule;
    if (IS_1_21_5_OR_ABOVE) {
      nmsNameTagVisibility = nameTagVisibility(otherProps.nameTagVisibility());
      nmsCollisionRule = collisionRule(otherProps.collisionRule());
    } else {
      nmsNameTagVisibility = otherProps.nameTagVisibility().key();
      nmsCollisionRule = otherProps.collisionRule().key();
    }

    Object chatFormattingColor = ADVENTURE_TO_NMS_CHAT_FORMATTING.get(otherProps.playerColor() != null ? otherProps.playerColor() : NamedTextColor.WHITE);

    DISPLAY_NAME_FIELD.set(teamPacket, displayName);
    PREFIX_FIELD.set(teamPacket, playerPrefix);
    SUFFIX_FIELD.set(teamPacket, playerSuffix);
    NAME_TAG_VISIBILITY_FIELD.set(teamPacket, nmsNameTagVisibility);
    COLLISION_RULE_FIELD.set(teamPacket, nmsCollisionRule);
    COLOR_FIELD.set(teamPacket, chatFormattingColor);
    OPTIONS_FIELD.set(teamPacket, otherProps.packOptions());
  }
}
