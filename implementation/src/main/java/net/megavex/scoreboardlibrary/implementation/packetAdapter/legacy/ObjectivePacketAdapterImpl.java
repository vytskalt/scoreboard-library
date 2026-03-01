package net.megavex.scoreboardlibrary.implementation.packetAdapter.legacy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectiveConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;

public class ObjectivePacketAdapterImpl implements ObjectivePacketAdapter {
  private final String objectiveName;
  private Object removePacket;

  public ObjectivePacketAdapterImpl(@NotNull String objectiveName) {
    this.objectiveName = objectiveName;
  }

  @Override
  public @NotNull String objectiveName() {
    return objectiveName;
  }

  @Override
  public void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot) {
    Object packet = PacketAccessors.DISPLAY_OBJECTIVE_CONSTRUCTOR.invoke();
    PacketAccessors.DISPLAY_OBJECTIVE_POSITION.set(packet, ObjectiveConstants.displaySlotIndex(slot, false));
    PacketAccessors.DISPLAY_OBJECTIVE_NAME.set(packet, objectiveName);
    LegacyPacketSender.INSTANCE.sendPacket(players, packet);
  }

  @Override
  public void sendProperties(
    @NotNull Collection<Player> players,
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType,
    @Nullable ScoreFormat scoreFormat
  ) {
    LocalePacketUtil.sendLocalePackets(
      LegacyPacketSender.INSTANCE,
      players,
      locale -> createPropertiesPacket(packetType, GlobalTranslator.render(value, locale), renderType)
    );
  }

  @Override
  public void remove(@NotNull Collection<Player> players) {
    if (removePacket == null) {
      removePacket = PacketAccessors.OBJECTIVE_CONSTRUCTOR.invoke();
      PacketAccessors.OBJECTIVE_NAME_FIELD.set(removePacket, objectiveName);
      PacketAccessors.OBJECTIVE_DISPLAY_NAME_FIELD.set(removePacket, "");
      PacketAccessors.OBJECTIVE_MODE_FIELD.set(removePacket, ObjectiveConstants.MODE_REMOVE);
    }
    LegacyPacketSender.INSTANCE.sendPacket(players, removePacket);
  }

  @Override
  public void sendScore(
    @NotNull Collection<Player> players,
    @NotNull String entry,
    int value,
    @Nullable Component display,
    @Nullable ScoreFormat scoreFormat
  ) {
    Object packet = PacketAccessors.SCORE_CONSTRUCTOR.invoke(entry);
    PacketAccessors.SCORE_OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    PacketAccessors.SCORE_VALUE_FIELD.set(packet, value);
    if (PacketAccessors.SCORE_ACTION_FIELD_1_8 != null) {
      PacketAccessors.SCORE_ACTION_FIELD_1_8.set(packet, PacketAccessors.SCORE_ACTION_CHANGE_1_8);
    } else {
      PacketAccessors.SCORE_ACTION_FIELD_1_7.set(packet, 0);
    }

    LegacyPacketSender.INSTANCE.sendPacket(players, packet);
  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    Object packet = PacketAccessors.SCORE_CONSTRUCTOR.invoke(entry);
    PacketAccessors.SCORE_OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    LegacyPacketSender.INSTANCE.sendPacket(players, packet);
  }

  private @NotNull Object createPropertiesPacket(
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType
  ) {
    Object packet = PacketAccessors.OBJECTIVE_CONSTRUCTOR.invoke();
    PacketAccessors.OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    PacketAccessors.OBJECTIVE_MODE_FIELD.set(packet, ObjectiveConstants.mode(packetType));

    String legacyValue = LegacyFormatUtil.limitLegacyText(legacySection().serialize(value), ObjectiveConstants.LEGACY_VALUE_CHAR_LIMIT);
    PacketAccessors.OBJECTIVE_DISPLAY_NAME_FIELD.set(packet, legacyValue);

    if (PacketAccessors.OBJECTIVE_HEALTH_DISPLAY_FIELD != null) {
      Object nmsRenderType;
      switch (renderType) {
        case INTEGER:
          nmsRenderType = PacketAccessors.HEALTH_DISPLAY_INTEGER;
          break;
        case HEARTS:
          nmsRenderType = PacketAccessors.HEALTH_DISPLAY_HEARTS;
          break;
        default:
          throw new IllegalStateException();
      }
      PacketAccessors.OBJECTIVE_HEALTH_DISPLAY_FIELD.set(packet, nmsRenderType);
    }

    return packet;
  }
}
