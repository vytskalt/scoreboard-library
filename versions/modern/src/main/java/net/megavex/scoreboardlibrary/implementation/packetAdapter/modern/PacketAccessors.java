package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.*;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodType;
import java.util.*;

public final class PacketAccessors {
  public static final Class<?> PKT_CLASS,
    SET_OBJECTIVE_PKT_CLASS,
    SET_DISPLAY_OBJECTIVE_PKT_CLASS,
    SET_SCORE_PKT_CLASS,
    RESET_SCORE_PKT_CLASS,
    SET_PLAYER_TEAM_PKT_CLASS,
    TEAM_PARAMETERS_PKT_CLASS,
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
    // TODO: spigot mapping names
    PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.Packet");
    SET_OBJECTIVE_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetObjectivePacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective");
    SET_DISPLAY_OBJECTIVE_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective");
    SET_SCORE_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetScorePacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore");
    RESET_SCORE_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundResetScorePacket", "net.minecraft.network.protocol.game.ClientboundResetScorePacket");
    SET_PLAYER_TEAM_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam");
    TEAM_PARAMETERS_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket$Parameters", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$b");
    COMPONENT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.Component", "net.minecraft.network.chat.IChatBaseComponent");
    MUTABLE_COMPONENT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.MutableComponent", "net.minecraft.network.chat.IChatMutableComponent");
    COMPONENT_SERIALIZATION_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.ComponentSerialization");
    STYLE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.Style", "net.minecraft.network.chat.ChatModifier");
    STYLE_SERIALIZER_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.Style$Serializer", "net.minecraft.network.chat.ChatModifier$ChatModifierSerializer");
    NUMBER_FORMAT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.numbers.NumberFormat");
    DISPLAY_SLOT_CLASS = ReflectUtil.getOptionalClass("net.minecraft.world.scores.DisplaySlot");
    OBJECTIVE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.world.scores.Objective", "net.minecraft.world.scores.ScoreboardObjective");
    TEAM_VISIBILITY_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.world.scores.Team$Visibility", "net.minecraft.world.scores.ScoreboardTeamBase$EnumNameTagVisibility");
    TEAM_COLLISION_RULE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.world.scores.Team$CollisionRule", "net.minecraft.world.scores.ScoreboardTeamBase$EnumTeamPush");
    CHAT_FORMATTING_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.ChatFormatting", "net.minecraft.EnumChatFormat");
    OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.world.scores.criteria.ObjectiveCriteria$RenderType", "net.minecraft.world.scores.criteria.IScoreboardCriteria$EnumScoreboardHealthDisplay");
    DATA_RESULT_CLASS = ReflectUtil.getClassOrThrow("com.mojang.serialization.DataResult");
    DYNAMIC_OPS_CLASS = ReflectUtil.getClassOrThrow("com.mojang.serialization.DynamicOps");
    JSON_OPS_CLASS = ReflectUtil.getClassOrThrow("com.mojang.serialization.JsonOps");
    CODEC_CLASS = ReflectUtil.getClassOrThrow("com.mojang.serialization.Codec");
    SERVER_PLAYER_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.server.level.ServerPlayer", "net.minecraft.server.level.EntityPlayer");
    PLAYER_CONNECTION_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.server.network.ServerGamePacketListenerImpl", "net.minecraft.server.network.PlayerConnection");
    ADVENTURE_COMPONENT_CLASS = ReflectUtil.getOptionalClass("io.papermc.paper.adventure.AdventureComponent");
  }

  public static final boolean IS_1_20_2_OR_ABOVE, IS_1_20_3_OR_ABOVE, IS_1_20_5_OR_ABOVE, IS_1_21_5_OR_ABOVE, IS_1_21_6_OR_ABOVE;

  static {
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
      Class.forName("net.minecraft.network.protocol.common.ClientboundTransferPacket"); // Random 1.20.5 class
      is1_20_5OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_20_5_OR_ABOVE = is1_20_5OrAbove;

    boolean is1_21_5OrAbove = false;
    try {
      Class.forName("net.minecraft.world.item.component.BlocksAttacks"); // Random 1.21.5 class
      is1_21_5OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_21_5_OR_ABOVE = is1_21_5OrAbove;

    boolean is1_21_6OrAbove = false;
    try {
      Class.forName("net.minecraft.server.dialog.Dialog"); // Random 1.21.6 class
      is1_21_6OrAbove = true;
    } catch (ClassNotFoundException ignored) {
    }
    IS_1_21_6_OR_ABOVE = is1_21_6OrAbove;

    if (is1_21_5OrAbove) {
      NAME_TAG_VISIBILITY_FIELD_1_21_5 = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, TEAM_VISIBILITY_CLASS);
      COLLISION_RULE_FIELD_1_21_5 = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, TEAM_COLLISION_RULE_CLASS);
      NAME_TAG_VISIBILITY_FIELD_1_21_4 = null;
      COLLISION_RULE_FIELD_1_21_4 = null;
    } else {
      NAME_TAG_VISIBILITY_FIELD_1_21_4 = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, String.class);
      COLLISION_RULE_FIELD_1_21_4 = ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 1, String.class);
      NAME_TAG_VISIBILITY_FIELD_1_21_5 = null;
      COLLISION_RULE_FIELD_1_21_5 = null;
    }
  }

  static {
    if (IS_1_20_2_OR_ABOVE) {
      RESET_SCORE_CONSTRUCTOR = ReflectUtil.findConstructor(RESET_SCORE_PKT_CLASS, String.class, String.class);
    } else {
      RESET_SCORE_CONSTRUCTOR = null;
    }

    if (IS_1_20_5_OR_ABOVE) {
      OBJECTIVE_NUMBER_FORMAT_FIELD = ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, Optional.class);
      SCORE_1_20_5_CONSTRUCTOR = ReflectUtil.findConstructor(SET_SCORE_PKT_CLASS, String.class, String.class, int.class, Optional.class, Optional.class);
      SCORE_1_20_3_CONSTRUCTOR = null;
      SCORE_1_20_2_CONSTRUCTOR = null;
      SCORE_1_20_2_METHOD_CHANGE = null;
      SCORE_1_20_2_METHOD_REMOVE = null;
    } else if (IS_1_20_3_OR_ABOVE) {
      OBJECTIVE_NUMBER_FORMAT_FIELD = ReflectUtil.findFieldUnchecked(SET_OBJECTIVE_PKT_CLASS, 0, NUMBER_FORMAT_CLASS);
      SCORE_1_20_5_CONSTRUCTOR = null;
      SCORE_1_20_3_CONSTRUCTOR = ReflectUtil.findConstructor(SET_SCORE_PKT_CLASS, String.class, String.class, int.class, COMPONENT_CLASS, NUMBER_FORMAT_CLASS);
      SCORE_1_20_2_METHOD_CHANGE = null;
      SCORE_1_20_2_METHOD_REMOVE = null;
      SCORE_1_20_2_CONSTRUCTOR = null;
    } else {
      OBJECTIVE_NUMBER_FORMAT_FIELD = null;
      SCORE_1_20_5_CONSTRUCTOR = null;
      SCORE_1_20_3_CONSTRUCTOR = null;

      Class<?> methodClass = ReflectUtil.getClassOrThrow("net.minecraft.server.ServerScoreboard$Method", "net.minecraft.server.ScoreboardServer$Action");
      SCORE_1_20_2_METHOD_CHANGE = ReflectUtil.getEnumInstance(methodClass, "CHANGE", "a");
      SCORE_1_20_2_METHOD_REMOVE = ReflectUtil.getEnumInstance(methodClass, "REMOVE", "b");
      SCORE_1_20_2_CONSTRUCTOR = ReflectUtil.findConstructor(SET_SCORE_PKT_CLASS, methodClass, String.class, String.class, int.class);
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

  public static final MethodAccessor CHAT_FORMATTING_GET_BY_CODE =
    ReflectUtil.findMethod(CHAT_FORMATTING_CLASS,true, MethodType.methodType(CHAT_FORMATTING_CLASS, char.class), "getByCode", "a");

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

  public static final ConstructorAccessor<?> DISPLAY_1_20_2_CONSTRUCTOR =
    ReflectUtil.findOptionalConstructor(SET_DISPLAY_OBJECTIVE_PKT_CLASS, DISPLAY_SLOT_CLASS, OBJECTIVE_CLASS);
  public static final ConstructorAccessor<?> DISPLAY_1_20_1_CONSTRUCTOR =
    ReflectUtil.findOptionalConstructor(SET_DISPLAY_OBJECTIVE_PKT_CLASS, int.class, OBJECTIVE_CLASS);
  public static final FieldAccessor<Object, String> DISPLAY_OBJECTIVE_NAME =
    ReflectUtil.findFieldUnchecked(SET_DISPLAY_OBJECTIVE_PKT_CLASS, 0, String.class);

  public static final ConstructorAccessor<?> RESET_SCORE_CONSTRUCTOR; // 1.20.3+

  public static final ConstructorAccessor<?> SCORE_1_20_5_CONSTRUCTOR;
  public static final ConstructorAccessor<?> SCORE_1_20_3_CONSTRUCTOR;

  public static final Object SCORE_1_20_2_METHOD_CHANGE, SCORE_1_20_2_METHOD_REMOVE;
  public static final ConstructorAccessor<?> SCORE_1_20_2_CONSTRUCTOR;

  public static final ConstructorAccessor<?> TEAM_PACKET_CONSTRUCTOR =
    ReflectUtil.findConstructor(SET_PLAYER_TEAM_PKT_CLASS, String.class, int.class, Optional.class, Collection.class);
  public static final PacketConstructor<?> PARAMETERS_CONSTRUCTOR =
    ReflectUtil.getEmptyConstructor(TEAM_PARAMETERS_PKT_CLASS);
  public static final FieldAccessor<Object, Object> DISPLAY_NAME_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, COMPONENT_CLASS);
  public static final FieldAccessor<Object, Object> PREFIX_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 1, COMPONENT_CLASS);
  public static final FieldAccessor<Object, Object> SUFFIX_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 2, COMPONENT_CLASS);

  public static final FieldAccessor<Object, String> NAME_TAG_VISIBILITY_FIELD_1_21_4;
  public static final FieldAccessor<Object, String> COLLISION_RULE_FIELD_1_21_4;
  public static final FieldAccessor<Object, Object> NAME_TAG_VISIBILITY_FIELD_1_21_5;
  public static final FieldAccessor<Object, Object> COLLISION_RULE_FIELD_1_21_5;

  public static final FieldAccessor<Object, Object> COLOR_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, CHAT_FORMATTING_CLASS);
  public static final FieldAccessor<Object, Integer> OPTIONS_FIELD =
    ReflectUtil.findFieldUnchecked(TEAM_PARAMETERS_PKT_CLASS, 0, int.class);

  private PacketAccessors() {
  }

  public static @NotNull Object fromAdventureComponent(@NotNull Component component) {
    return Objects.requireNonNull(PacketAccessors.ADVENTURE_COMPONENT_CONSTRUCTOR).invoke(component);
  }
}
