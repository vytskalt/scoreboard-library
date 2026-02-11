package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.api.team.enums.CollisionRule;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodType;
import java.util.*;

public final class PacketAccessors {
  private PacketAccessors() {
  }

  public static final boolean IS_1_17_OR_ABOVE, IS_1_20_2_OR_ABOVE, IS_1_20_3_OR_ABOVE, IS_1_20_5_OR_ABOVE, IS_1_21_5_OR_ABOVE, IS_1_21_6_OR_ABOVE;
  private static final String OLD_NMS_VERSION_STRING;

  static {
    boolean is1_17OrAbove = false;
    try {
      Class.forName("net.minecraft.world.item.BundleItem");
      is1_17OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_17_OR_ABOVE = is1_17OrAbove;

    if (!is1_17OrAbove) {
      OLD_NMS_VERSION_STRING = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    } else {
      OLD_NMS_VERSION_STRING = null;
    }

    boolean is1_20_2OrAbove = false;
    try {
      Class.forName("net.minecraft.world.scores.DisplaySlot");
      is1_20_2OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_20_2_OR_ABOVE = is1_20_2OrAbove;

    boolean is1_20_3OrAbove = false;
    try {
      Class.forName("net.minecraft.network.chat.numbers.NumberFormat");
      is1_20_3OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_20_3_OR_ABOVE = is1_20_3OrAbove;

    boolean is1_20_5OrAbove = false;
    try {
      Class.forName("net.minecraft.network.protocol.common.ClientboundTransferPacket");
      is1_20_5OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_20_5_OR_ABOVE = is1_20_5OrAbove;

    boolean is1_21_5OrAbove = false;
    try {
      Class.forName("net.minecraft.world.item.component.BlocksAttacks");
      is1_21_5OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_21_5_OR_ABOVE = is1_21_5OrAbove;

    boolean is1_21_6OrAbove = false;
    try {
      Class.forName("net.minecraft.server.dialog.Dialog");
      is1_21_6OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_21_6_OR_ABOVE = is1_21_6OrAbove;
  }

  public static final Class<?> PKT_CLASS,
    SET_OBJECTIVE_PKT_CLASS,
    SET_DISPLAY_OBJECTIVE_PKT_CLASS,
    SET_SCORE_PKT_CLASS,
    RESET_SCORE_PKT_CLASS,
    SET_PLAYER_TEAM_PKT_CLASS,
    TEAM_PARAMETERS_PKT_CLASS, // 1.17
    COMPONENT_CLASS,
    MUTABLE_COMPONENT_CLASS,
    COMPONENT_SERIALIZATION_CLASS,
    STYLE_CLASS,
    STYLE_SERIALIZER_CLASS,
    NUMBER_FORMAT_CLASS,
    DISPLAY_SLOT_CLASS, // 1.20.2
    OBJECTIVE_CLASS,
    TEAM_VISIBILITY_CLASS,
    TEAM_COLLISION_RULE_CLASS,
    CHAT_FORMATTING_CLASS,
    OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS,
    DATA_RESULT_CLASS,
    DYNAMIC_OPS_CLASS,
    JSON_OPS_CLASS,
    CODEC_CLASS,
    SERVER_PLAYER_CLASS,
    PLAYER_CONNECTION_CLASS,
    ADVENTURE_COMPONENT_CLASS;

  static {
    PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.Packet", oldSpigotClassName("Packet"));
    SET_OBJECTIVE_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetObjectivePacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective", oldSpigotClassName("PacketPlayOutScoreboardObjective"));
    SET_DISPLAY_OBJECTIVE_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective", oldSpigotClassName("PacketPlayOutScoreboardDisplayObjective"));
    SET_SCORE_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetScorePacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore", oldSpigotClassName("PacketPlayOutScoreboardScore"));
    RESET_SCORE_PKT_CLASS = ReflectUtil.getOptionalClass("net.minecraft.network.protocol.game.ClientboundResetScorePacket", "net.minecraft.network.protocol.game.ClientboundResetScorePacket");
    SET_PLAYER_TEAM_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam", oldSpigotClassName("PacketPlayOutScoreboardTeam"));
    TEAM_PARAMETERS_PKT_CLASS = ReflectUtil.getOptionalClass("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket$Parameters", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$b");
    COMPONENT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.Component", "net.minecraft.network.chat.IChatBaseComponent", oldSpigotClassName("IChatBaseComponent"), oldSpigotClassName("ChatBaseComponent"));
    MUTABLE_COMPONENT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.MutableComponent", "net.minecraft.network.chat.IChatMutableComponent", oldSpigotClassName("IChatMutableComponent"));
    COMPONENT_SERIALIZATION_CLASS = ReflectUtil.getOptionalClass("net.minecraft.network.chat.ComponentSerialization", "net.minecraft.network.chat.Component$Serializer", "net.minecraft.network.chat.IChatBaseComponent$ChatSerializer", oldSpigotClassName("IChatBaseComponent$ChatSerializer"));
    STYLE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.Style", "net.minecraft.network.chat.ChatModifier", oldSpigotClassName("ChatModifier"));
    STYLE_SERIALIZER_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.Style$Serializer", "net.minecraft.network.chat.ChatModifier$ChatModifierSerializer", oldSpigotClassName("ChatModifier$ChatModifierSerializer"));
    NUMBER_FORMAT_CLASS = ReflectUtil.getOptionalClass("net.minecraft.network.chat.numbers.NumberFormat");
    DISPLAY_SLOT_CLASS = ReflectUtil.getOptionalClass("net.minecraft.world.scores.DisplaySlot");
    OBJECTIVE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.world.scores.Objective", "net.minecraft.world.scores.ScoreboardObjective", oldSpigotClassName("ScoreboardObjective"));
    TEAM_VISIBILITY_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.world.scores.Team$Visibility", "net.minecraft.world.scores.ScoreboardTeamBase$EnumNameTagVisibility", oldSpigotClassName("ScoreboardTeamBase$EnumNameTagVisibility"));
    TEAM_COLLISION_RULE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.world.scores.Team$CollisionRule", "net.minecraft.world.scores.ScoreboardTeamBase$EnumTeamPush", oldSpigotClassName("ScoreboardTeamBase$EnumTeamPush"));
    CHAT_FORMATTING_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.ChatFormatting", "net.minecraft.EnumChatFormat", oldSpigotClassName("EnumChatFormat"));
    OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.world.scores.criteria.ObjectiveCriteria$RenderType", "net.minecraft.world.scores.criteria.IScoreboardCriteria$EnumScoreboardHealthDisplay", oldSpigotClassName("IScoreboardCriteria$EnumScoreboardHealthDisplay"));
    DATA_RESULT_CLASS = ReflectUtil.getClassOrThrow("com.mojang.serialization.DataResult");
    DYNAMIC_OPS_CLASS = ReflectUtil.getClassOrThrow("com.mojang.serialization.DynamicOps");
    JSON_OPS_CLASS = ReflectUtil.getClassOrThrow("com.mojang.serialization.JsonOps");
    CODEC_CLASS = ReflectUtil.getClassOrThrow("com.mojang.serialization.Codec");
    SERVER_PLAYER_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.server.level.ServerPlayer", "net.minecraft.server.level.EntityPlayer", oldSpigotClassName("EntityPlayer"));
    PLAYER_CONNECTION_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.server.network.ServerGamePacketListenerImpl", "net.minecraft.server.network.PlayerConnection", oldSpigotClassName("PlayerConnection"));
    ADVENTURE_COMPONENT_CLASS = ReflectUtil.getOptionalClass("io.papermc.paper.adventure.AdventureComponent");
  }

  static {
    if (IS_1_20_3_OR_ABOVE) {
      RESET_SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(Objects.requireNonNull(RESET_SCORE_PKT_CLASS), String.class, String.class);
    } else {
      RESET_SCORE_CONSTRUCTOR = null;
    }

    if (IS_1_20_5_OR_ABOVE) {
      OBJECTIVE_NUMBER_FORMAT_FIELD = ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, Optional.class);
      SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(SET_SCORE_PKT_CLASS, String.class, String.class, int.class, Optional.class, Optional.class);
      SCORE_1_20_2_METHOD_CHANGE = null;
      SCORE_1_20_2_METHOD_REMOVE = null;
    } else if (IS_1_20_3_OR_ABOVE) {
      OBJECTIVE_NUMBER_FORMAT_FIELD = ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, NUMBER_FORMAT_CLASS);
      SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(SET_SCORE_PKT_CLASS, String.class, String.class, int.class, COMPONENT_CLASS, NUMBER_FORMAT_CLASS);
      SCORE_1_20_2_METHOD_CHANGE = null;
      SCORE_1_20_2_METHOD_REMOVE = null;
    } else {
      OBJECTIVE_NUMBER_FORMAT_FIELD = null;

      Class<?> methodClass = ReflectUtil.getClassOrThrow("net.minecraft.server.ServerScoreboard$Method", "net.minecraft.server.ScoreboardServer$Action", oldSpigotClassName("ScoreboardServer$Action"));
      SCORE_1_20_2_METHOD_CHANGE = ReflectUtil.getEnumInstance(methodClass, "CHANGE", "a");
      SCORE_1_20_2_METHOD_REMOVE = ReflectUtil.getEnumInstance(methodClass, "REMOVE", "b");
      SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(SET_SCORE_PKT_CLASS, methodClass, String.class, String.class, int.class);
    }

    RESULT_UNWRAP_METHOD = ReflectUtil.findMethod(PacketAccessors.DATA_RESULT_CLASS, false, MethodType.methodType(Optional.class), "result");
    try {
      JSON_OPS = PacketAccessors.JSON_OPS_CLASS.getField("INSTANCE").get(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    CODEC_PARSE = ReflectUtil.findMethod(PacketAccessors.CODEC_CLASS, false, MethodType.methodType(PacketAccessors.DATA_RESULT_CLASS, PacketAccessors.DYNAMIC_OPS_CLASS, Object.class), "parse");
  }

  public static final MethodAccessor RESULT_UNWRAP_METHOD;
  public static final MethodAccessor CODEC_PARSE;
  public static final Object JSON_OPS;

  public static final Map<NamedTextColor, Object> NMS_CHAT_FORMATTING_MAP = new HashMap<>();

  static {
    Object[] chatFormattings = CHAT_FORMATTING_CLASS.getEnumConstants();
    FieldAccessor<Object, Object> charField = ReflectUtil.findFieldUnchecked(CHAT_FORMATTING_CLASS, 0, char.class);

    outer: for (NamedTextColor color : NamedTextColor.NAMES.values()) {
      char c = LegacyFormatUtil.getChar(color);
      for (Object chatFormatting : chatFormattings) {
        if (c == (char)charField.get(chatFormatting)) {
          NMS_CHAT_FORMATTING_MAP.put(color, chatFormatting);
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

  public static final Object COLLISION_RULE_ALWAYS = ReflectUtil.getEnumInstance(TEAM_COLLISION_RULE_CLASS, "ALWAYS");
  public static final Object COLLISION_RULE_NEVER = ReflectUtil.getEnumInstance(TEAM_COLLISION_RULE_CLASS, "NEVER");
  public static final Object COLLISION_RULE_PUSH_OTHER_TEAMS = ReflectUtil.getEnumInstance(TEAM_COLLISION_RULE_CLASS, "PUSH_OTHER_TEAMS");
  public static final Object COLLISION_RULE_PUSH_OWN_TEAM = ReflectUtil.getEnumInstance(TEAM_COLLISION_RULE_CLASS, "PUSH_OWN_TEAM");

  public static final Object RENDER_TYPE_INTEGER = ReflectUtil.getEnumInstance(OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS, "INTEGER");
  public static final Object RENDER_TYPE_HEARTS = ReflectUtil.getEnumInstance(OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS, "HEARTS");

  public static final List<Object> DISPLAY_SLOT_VALUES = DISPLAY_SLOT_CLASS == null ? null : Arrays.asList(DISPLAY_SLOT_CLASS.getEnumConstants());

  public static final ConstructorAccessor<?> ADVENTURE_COMPONENT_CONSTRUCTOR =
    ADVENTURE_COMPONENT_CLASS != null ? ReflectUtil.findOptionalConstructor(ADVENTURE_COMPONENT_CLASS, Component.class) : null;

  // --- OBJECTIVES ---

  public static final PacketConstructor<?> OBJECTIVE_PACKET_CONSTRUCTOR =
    ReflectUtil.getEmptyConstructor(SET_OBJECTIVE_PKT_CLASS);
  public static final FieldAccessor<Object, String> OBJECTIVE_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, String.class);
  public static final FieldAccessor<Object, Object> OBJECTIVE_VALUE_FIELD =
    ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, COMPONENT_CLASS);
  public static final FieldAccessor<Object, Object> OBJECTIVE_RENDER_TYPE_FIELD =
    ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS);
  // Optional<NumberFormat> for 1.20.5+, NumberFormat for below
  public static final FieldAccessor<Object, Object> OBJECTIVE_NUMBER_FORMAT_FIELD;
  public static final FieldAccessor<Object, Integer> OBJECTIVE_MODE_FIELD =
    ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, int.class);

  public static final ConstructorAccessor<?> DISPLAY_CONSTRUCTOR;
  public static final FieldAccessor<Object, String> DISPLAY_OBJECTIVE_NAME =
    ReflectUtil.findFieldUnchecked(SET_DISPLAY_OBJECTIVE_PKT_CLASS, 0, String.class);
  public static final FieldAccessor<Object, Object> DISPLAY_SLOT;

  static {
    if (IS_1_20_2_OR_ABOVE) {
      DISPLAY_CONSTRUCTOR = ReflectUtil.findConstructor(SET_DISPLAY_OBJECTIVE_PKT_CLASS, DISPLAY_SLOT_CLASS, OBJECTIVE_CLASS);
      DISPLAY_SLOT = null;
    } else if (IS_1_17_OR_ABOVE) {
      DISPLAY_CONSTRUCTOR = ReflectUtil.findConstructor(SET_DISPLAY_OBJECTIVE_PKT_CLASS, int.class, OBJECTIVE_CLASS);
      DISPLAY_SLOT = null;
    } else {
      DISPLAY_CONSTRUCTOR = ReflectUtil.findConstructor(SET_DISPLAY_OBJECTIVE_PKT_CLASS);
      DISPLAY_SLOT = ReflectUtil.findFieldUnchecked(SET_DISPLAY_OBJECTIVE_PKT_CLASS, 0, int.class);
    }
  }

  public static final ConstructorAccessor<?> RESET_SCORE_CONSTRUCTOR; // 1.20.3+

  public static final ConstructorAccessor<?> SCORE_CONSTRUCTOR;

  public static final Object SCORE_1_20_2_METHOD_CHANGE, SCORE_1_20_2_METHOD_REMOVE;

  // --- TEAMS ---

  public static final PacketConstructor<?> PARAMETERS_CONSTRUCTOR;
  public static final ConstructorAccessor<?> TEAM_PACKET_CONSTRUCTOR;

  public static final FieldAccessor<Object, String> TEAM_NAME_FIELD;
  public static final FieldAccessor<Object, Integer> TEAM_MODE_FIELD;
  public static final FieldAccessor<Object, Collection<String>> TEAM_ENTRIES_FIELD;

  public static final FieldAccessor<Object, Object> DISPLAY_NAME_FIELD;
  public static final FieldAccessor<Object, Object> PREFIX_FIELD;
  public static final FieldAccessor<Object, Object> SUFFIX_FIELD;

  public static final FieldAccessor<Object, Object> NAME_TAG_VISIBILITY_FIELD;
  public static final FieldAccessor<Object, Object> COLLISION_RULE_FIELD;

  public static final FieldAccessor<Object, Object> COLOR_FIELD;
  public static final FieldAccessor<Object, Integer> OPTIONS_FIELD;

  static {
    if (IS_1_17_OR_ABOVE) {
      assert TEAM_PARAMETERS_PKT_CLASS != null;
      PARAMETERS_CONSTRUCTOR = ReflectUtil.getEmptyConstructor(TEAM_PARAMETERS_PKT_CLASS);
      TEAM_PACKET_CONSTRUCTOR = ReflectUtil.findConstructor(SET_PLAYER_TEAM_PKT_CLASS, String.class, int.class, Optional.class, Collection.class);

      TEAM_NAME_FIELD = null;
      TEAM_MODE_FIELD = null;
      TEAM_ENTRIES_FIELD = null;

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

      COLOR_FIELD = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, CHAT_FORMATTING_CLASS);
      OPTIONS_FIELD = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, int.class);
    } else {
      PARAMETERS_CONSTRUCTOR = null;
      TEAM_PACKET_CONSTRUCTOR = ReflectUtil.findConstructor(SET_PLAYER_TEAM_PKT_CLASS);

      TEAM_NAME_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 0, String.class);
      TEAM_MODE_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 0, int.class);
      TEAM_ENTRIES_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 0, Collection.class);

      DISPLAY_NAME_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 0, COMPONENT_CLASS);
      PREFIX_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 1, COMPONENT_CLASS);
      SUFFIX_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 2, COMPONENT_CLASS);

      NAME_TAG_VISIBILITY_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 1, String.class);
      COLLISION_RULE_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 2, String.class);

      COLOR_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 0, CHAT_FORMATTING_CLASS);
      OPTIONS_FIELD = ReflectUtil.findFieldUnchecked(SET_PLAYER_TEAM_PKT_CLASS, 1, int.class);
    }
  }

  public static Object nameTagVisibility(NameTagVisibility value) {
    switch (value) {
      case NEVER:
        return PacketAccessors.NAME_TAG_VISIBILITY_NEVER;
      case ALWAYS:
        return PacketAccessors.NAME_TAG_VISIBILITY_ALWAYS;
      case HIDE_FOR_OTHER_TEAMS:
        return PacketAccessors.NAME_TAG_VISIBILITY_HIDE_FOR_OTHER_TEAMS;
      case HIDE_FOR_OWN_TEAM:
        return PacketAccessors.NAME_TAG_VISIBILITY_HIDE_FOR_OWN_TEAM;
      default:
        throw new IllegalStateException("unknown name tag visibility " + value.name());
    }
  }

  public static Object collisionRule(CollisionRule value) {
    switch (value) {
      case NEVER:
        return PacketAccessors.COLLISION_RULE_NEVER;
      case ALWAYS:
        return PacketAccessors.COLLISION_RULE_ALWAYS;
      case PUSH_OTHER_TEAMS:
        return PacketAccessors.COLLISION_RULE_PUSH_OTHER_TEAMS;
      case PUSH_OWN_TEAM:
        return PacketAccessors.COLLISION_RULE_PUSH_OWN_TEAM;
      default:
        throw new IllegalStateException("unknown collision rule " + value.name());
    }
  }

  public static @NotNull Object fromAdventureComponent(@NotNull Component component) {
    return Objects.requireNonNull(PacketAccessors.ADVENTURE_COMPONENT_CONSTRUCTOR).invoke(component);
  }

  public static @Nullable String oldSpigotClassName(String clazz) {
    if (OLD_NMS_VERSION_STRING != null) {
      return "net.minecraft.server." + OLD_NMS_VERSION_STRING + "." + clazz;
    }
    return null;
  }
}
