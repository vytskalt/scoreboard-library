package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.reflect.ReflectUtil;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NmsClasses {
  private NmsClasses() {
  }

  private static final String OLD_NMS_VERSION_STRING;
  private static final String CB_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();

  private static @Nullable String oldSpigotClassName(String clazz) {
    if (OLD_NMS_VERSION_STRING != null) {
      return "net.minecraft.server." + OLD_NMS_VERSION_STRING + "." + clazz;
    }
    return null;
  }

  private static @NotNull String craftBukkitClassName(String className) {
    return CB_PACKAGE + '.' + className;
  }

  // --- VERSIONS ---

  public static final boolean
    IS_1_8_OR_ABOVE,
    IS_1_9_OR_ABOVE,
    IS_1_13_OR_ABOVE,
    IS_1_17_OR_ABOVE,
    IS_1_20_2_OR_ABOVE,
    IS_1_20_3_OR_ABOVE,
    IS_1_20_5_OR_ABOVE,
    IS_1_21_5_OR_ABOVE,
    IS_1_21_6_OR_ABOVE,
    IS_26_2_OR_ABOVE;

  static {
    IS_1_17_OR_ABOVE = ReflectUtil.hasClass("net.minecraft.world.item.BundleItem");

    if (!IS_1_17_OR_ABOVE) {
      OLD_NMS_VERSION_STRING = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    } else {
      OLD_NMS_VERSION_STRING = null;
    }

    IS_1_8_OR_ABOVE = IS_1_17_OR_ABOVE || ReflectUtil.hasClass(oldSpigotClassName("IScoreboardCriteria$EnumScoreboardHealthDisplay"));
    IS_1_9_OR_ABOVE = IS_1_17_OR_ABOVE || ReflectUtil.hasClass(oldSpigotClassName("ScoreboardTeamBase$EnumTeamPush"));
    IS_1_13_OR_ABOVE = IS_1_17_OR_ABOVE || ReflectUtil.hasClass(oldSpigotClassName("EntityDolphin"));

    IS_1_20_2_OR_ABOVE = ReflectUtil.hasClass("net.minecraft.world.scores.DisplaySlot");
    IS_1_20_3_OR_ABOVE = ReflectUtil.hasClass("net.minecraft.network.chat.numbers.NumberFormat");
    IS_1_20_5_OR_ABOVE = ReflectUtil.hasClass("net.minecraft.network.protocol.common.ClientboundTransferPacket");
    IS_1_21_5_OR_ABOVE = ReflectUtil.hasClass("net.minecraft.world.item.component.BlocksAttacks");
    IS_1_21_6_OR_ABOVE = ReflectUtil.hasClass("net.minecraft.server.dialog.Dialog");
    IS_26_2_OR_ABOVE = ReflectUtil.hasClass("net.minecraft.world.scores.TeamColor");
  }

  /// --- CLASSES ---

  public static final Class<?> PKT_CLASS,
    SET_OBJECTIVE_PKT_CLASS,
    SET_DISPLAY_OBJECTIVE_PKT_CLASS,
    SET_SCORE_PKT_CLASS,
    RESET_SCORE_PKT_CLASS,
    SET_PLAYER_TEAM_PKT_CLASS,
    TEAM_PARAMETERS_PKT_CLASS, // 1.17
    COMPONENT_CLASS,
    CHAT_SERIALIZER_CLASS,
    COMPONENT_SERIALIZATION_CLASS,
    STYLE_CLASS,
    STYLE_SERIALIZER_CLASS,
    NUMBER_FORMAT_CLASS,
    DISPLAY_SLOT_CLASS, // 1.20.2
    OBJECTIVE_CLASS,
    TEAM_VISIBILITY_CLASS,
    TEAM_COLLISION_RULE_CLASS,
    TEAM_COLOR_OR_CHAT_FORMATTING_CLASS,
    OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS,
    DATA_RESULT_CLASS,
    DYNAMIC_OPS_CLASS,
    JSON_OPS_CLASS,
    CODEC_CLASS,
    CRAFT_REGISTRY_CLASS,
    CRAFT_PLAYER_CLASS,
    SERVER_PLAYER_CLASS,
    PLAYER_CONNECTION_CLASS,
    ADVENTURE_COMPONENT_CLASS,
    SCORE_ACTION_CLASS;

  static {
    PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.Packet", oldSpigotClassName("Packet"));
    SET_OBJECTIVE_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetObjectivePacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective", oldSpigotClassName("PacketPlayOutScoreboardObjective"));
    SET_DISPLAY_OBJECTIVE_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective", oldSpigotClassName("PacketPlayOutScoreboardDisplayObjective"));
    SET_SCORE_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetScorePacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore", oldSpigotClassName("PacketPlayOutScoreboardScore"));
    RESET_SCORE_PKT_CLASS = ReflectUtil.getOptionalClass("net.minecraft.network.protocol.game.ClientboundResetScorePacket", "net.minecraft.network.protocol.game.ClientboundResetScorePacket");
    SET_PLAYER_TEAM_PKT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam", oldSpigotClassName("PacketPlayOutScoreboardTeam"));
    TEAM_PARAMETERS_PKT_CLASS = ReflectUtil.getOptionalClass("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket$Parameters", "net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$b");
    COMPONENT_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.Component", "net.minecraft.network.chat.IChatBaseComponent", oldSpigotClassName("IChatBaseComponent"));
    CHAT_SERIALIZER_CLASS = ReflectUtil.getOptionalClass("net.minecraft.network.chat.Component$Serializer", "net.minecraft.network.chat.IChatBaseComponent$ChatSerializer", oldSpigotClassName("IChatBaseComponent$ChatSerializer"));
    COMPONENT_SERIALIZATION_CLASS = ReflectUtil.getOptionalClass("net.minecraft.network.chat.ComponentSerialization");
    STYLE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.network.chat.Style", "net.minecraft.network.chat.ChatModifier", oldSpigotClassName("ChatModifier"));
    STYLE_SERIALIZER_CLASS = ReflectUtil.getOptionalClass("net.minecraft.network.chat.Style$Serializer", "net.minecraft.network.chat.ChatModifier$ChatModifierSerializer", oldSpigotClassName("ChatModifier$ChatModifierSerializer"));
    NUMBER_FORMAT_CLASS = ReflectUtil.getOptionalClass("net.minecraft.network.chat.numbers.NumberFormat");
    DISPLAY_SLOT_CLASS = ReflectUtil.getOptionalClass("net.minecraft.world.scores.DisplaySlot");
    OBJECTIVE_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.world.scores.Objective", "net.minecraft.world.scores.ScoreboardObjective", oldSpigotClassName("ScoreboardObjective"));
    TEAM_VISIBILITY_CLASS = ReflectUtil.getOptionalClass("net.minecraft.world.scores.Team$Visibility", "net.minecraft.world.scores.ScoreboardTeamBase$EnumNameTagVisibility", oldSpigotClassName("ScoreboardTeamBase$EnumNameTagVisibility"));
    TEAM_COLLISION_RULE_CLASS = ReflectUtil.getOptionalClass("net.minecraft.world.scores.Team$CollisionRule", "net.minecraft.world.scores.ScoreboardTeamBase$EnumTeamPush", oldSpigotClassName("ScoreboardTeamBase$EnumTeamPush"));
    TEAM_COLOR_OR_CHAT_FORMATTING_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.world.scores.TeamColor", "net.minecraft.ChatFormatting", "net.minecraft.EnumChatFormat", oldSpigotClassName("EnumChatFormat"));
    OBJECTIVE_CRITERIA_RENDER_TYPE_CLASS = ReflectUtil.getOptionalClass("net.minecraft.world.scores.criteria.ObjectiveCriteria$RenderType", "net.minecraft.world.scores.criteria.IScoreboardCriteria$EnumScoreboardHealthDisplay", oldSpigotClassName("IScoreboardCriteria$EnumScoreboardHealthDisplay"));
    DATA_RESULT_CLASS = ReflectUtil.getOptionalClass("com.mojang.serialization.DataResult");
    DYNAMIC_OPS_CLASS = ReflectUtil.getOptionalClass("com.mojang.serialization.DynamicOps");
    JSON_OPS_CLASS = ReflectUtil.getOptionalClass("com.mojang.serialization.JsonOps");
    CODEC_CLASS = ReflectUtil.getOptionalClass("com.mojang.serialization.Codec");
    CRAFT_REGISTRY_CLASS = ReflectUtil.getOptionalClass(craftBukkitClassName("CraftRegistry"));
    CRAFT_PLAYER_CLASS = ReflectUtil.getClassOrThrow(craftBukkitClassName("entity.CraftPlayer"));
    SERVER_PLAYER_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.server.level.ServerPlayer", "net.minecraft.server.level.EntityPlayer", oldSpigotClassName("EntityPlayer"));
    PLAYER_CONNECTION_CLASS = ReflectUtil.getClassOrThrow("net.minecraft.server.network.ServerGamePacketListenerImpl", "net.minecraft.server.network.PlayerConnection", oldSpigotClassName("PlayerConnection"));
    ADVENTURE_COMPONENT_CLASS = ReflectUtil.getOptionalClass("io.papermc.paper.adventure.AdventureComponent");
    SCORE_ACTION_CLASS = ReflectUtil.getOptionalClass("net.minecraft.server.ServerScoreboard$Method", "net.minecraft.server.ScoreboardServer$Action", oldSpigotClassName("ScoreboardServer$Action"), oldSpigotClassName("PacketPlayOutScoreboardScore$EnumScoreboardAction"));
  }
}
