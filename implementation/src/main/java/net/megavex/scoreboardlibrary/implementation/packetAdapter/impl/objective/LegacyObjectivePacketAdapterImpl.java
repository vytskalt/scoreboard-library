package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.objective;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsEnums;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsObjectives;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.PacketAdapterProviderImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectiveConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;

public final class LegacyObjectivePacketAdapterImpl implements ObjectivePacketAdapter {
  private final PacketAdapterProviderImpl provider;
  private final String objectiveName;
  private Object removePacket;

  public LegacyObjectivePacketAdapterImpl(PacketAdapterProviderImpl provider, @NotNull String objectiveName) {
    this.provider = provider;
    this.objectiveName = objectiveName;
  }

  @Override
  public @NotNull String objectiveName() {
    return objectiveName;
  }

  @Override
  public void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot) {
    Object packet = NmsObjectives.DISPLAY_CONSTRUCTOR.invoke();
    Objects.requireNonNull(NmsObjectives.DISPLAY_SLOT).set(packet, ObjectiveConstants.displaySlotIndex(slot, false));
    NmsObjectives.DISPLAY_OBJECTIVE_NAME.set(packet, objectiveName);
    provider.packetSender().sendPacket(players, packet);
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
      provider.packetSender(),
      players,
      locale -> createPropertiesPacket(packetType, GlobalTranslator.render(value, locale), renderType)
    );
  }

  @Override
  public void remove(@NotNull Collection<Player> players) {
    if (removePacket == null) {
      removePacket = NmsObjectives.OBJECTIVE_PACKET_CONSTRUCTOR.invoke();
      NmsObjectives.OBJECTIVE_NAME_FIELD.set(removePacket, objectiveName);
      NmsObjectives.OBJECTIVE_VALUE_FIELD.set(removePacket, "");
      NmsObjectives.OBJECTIVE_MODE_FIELD.set(removePacket, ObjectiveConstants.MODE_REMOVE);
    }
    provider.packetSender().sendPacket(players, removePacket);
  }

  @Override
  public void sendScore(
    @NotNull Collection<Player> players,
    @NotNull String entry,
    int value,
    @Nullable Component display,
    @Nullable ScoreFormat scoreFormat
  ) {
    Object packet = NmsObjectives.SCORE_CONSTRUCTOR.invoke(entry);
    Objects.requireNonNull(NmsObjectives.SCORE_OBJECTIVE_NAME_FIELD).set(packet, objectiveName);
    Objects.requireNonNull(NmsObjectives.SCORE_VALUE_FIELD).set(packet, value);
    if (NmsClasses.IS_1_8_OR_ABOVE) {
      Objects.requireNonNull(NmsObjectives.SCORE_ACTION_FIELD).set(packet, NmsEnums.SCORE_ACTION_CHANGE);
    } else {
      Objects.requireNonNull(NmsObjectives.SCORE_ACTION_FIELD).set(packet, 0);
    }

    provider.packetSender().sendPacket(players, packet);
  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    Object packet = NmsObjectives.SCORE_CONSTRUCTOR.invoke(entry);
    Objects.requireNonNull(NmsObjectives.SCORE_OBJECTIVE_NAME_FIELD).set(packet, objectiveName);
    provider.packetSender().sendPacket(players, packet);
  }

  private @NotNull Object createPropertiesPacket(
    @NotNull PropertiesPacketType packetType,
    @NotNull Component value,
    @NotNull ObjectiveRenderType renderType
  ) {
    Object packet = NmsObjectives.OBJECTIVE_PACKET_CONSTRUCTOR.invoke();
    NmsObjectives.OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    NmsObjectives.OBJECTIVE_MODE_FIELD.set(packet, ObjectiveConstants.mode(packetType));

    String legacyValue = LegacyFormatUtil.limitLegacyText(legacySection().serialize(value), ObjectiveConstants.LEGACY_VALUE_CHAR_LIMIT);
    NmsObjectives.OBJECTIVE_VALUE_FIELD.set(packet, legacyValue);

    if (NmsObjectives.OBJECTIVE_RENDER_TYPE_FIELD != null) {
      NmsObjectives.OBJECTIVE_RENDER_TYPE_FIELD.set(packet, NmsEnums.renderType(renderType));
    }

    return packet;
  }
}
