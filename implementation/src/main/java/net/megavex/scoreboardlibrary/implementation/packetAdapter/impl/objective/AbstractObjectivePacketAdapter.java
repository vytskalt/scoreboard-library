package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.objective;

import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsEnums;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsObjectives;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.PacketAdapterProviderImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsObjectives;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectiveConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractObjectivePacketAdapter implements ObjectivePacketAdapter {
  protected final PacketAdapterProviderImpl provider;
  protected final String objectiveName;
  private Object removePacket;

  public AbstractObjectivePacketAdapter(@NotNull PacketAdapterProviderImpl provider, @NotNull String objectiveName) {
    this.provider = provider;
    this.objectiveName = objectiveName;
  }

  @Override
  public @NotNull String objectiveName() {
    return objectiveName;
  }

  @Override
  public void display(@NotNull Collection<Player> players, @NotNull ObjectiveDisplaySlot slot) {
    provider.packetSender().sendPacket(players, createDisplayPacket(slot));
  }

  @Override
  public void remove(@NotNull Collection<Player> players) {
    if (removePacket == null) {
      this.removePacket = NmsObjectives.OBJECTIVE_PACKET_CONSTRUCTOR.invoke();
      NmsObjectives.OBJECTIVE_NAME_FIELD.set(removePacket, objectiveName);
      NmsObjectives.OBJECTIVE_MODE_FIELD.set(removePacket, ObjectiveConstants.MODE_REMOVE);
    }
    provider.packetSender().sendPacket(players, removePacket);
  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    Object packet;
    if (NmsClasses.IS_1_20_3_OR_ABOVE) {
      packet = Objects.requireNonNull(NmsObjectives.RESET_SCORE_CONSTRUCTOR)
        .invoke(entry, objectiveName);
    } else {
      packet = Objects.requireNonNull(NmsObjectives.SCORE_CONSTRUCTOR)
        .invoke(NmsEnums.SCORE_ACTION_REMOVE, objectiveName, entry, 0);
    }
    provider.packetSender().sendPacket(players, packet);
  }

  protected @NotNull Object createDisplayPacket(@NotNull ObjectiveDisplaySlot displaySlot) {
    Object packet;
    if (NmsClasses.IS_1_20_2_OR_ABOVE) {
      packet = Objects.requireNonNull(NmsObjectives.DISPLAY_CONSTRUCTOR)
        .invoke(NmsEnums.DISPLAY_SLOT_VALUES[ObjectiveConstants.displaySlotIndex(displaySlot)], null);
    } else if (NmsClasses.IS_1_17_OR_ABOVE){
      packet = Objects.requireNonNull(NmsObjectives.DISPLAY_CONSTRUCTOR)
        .invoke(ObjectiveConstants.displaySlotIndex(displaySlot), null);
    } else {
      assert NmsObjectives.DISPLAY_SLOT != null;
      packet = Objects.requireNonNull(NmsObjectives.DISPLAY_CONSTRUCTOR).invoke();
      NmsObjectives.DISPLAY_SLOT.set(packet, ObjectiveConstants.displaySlotIndex(displaySlot));
    }
    NmsObjectives.DISPLAY_OBJECTIVE_NAME.set(packet, objectiveName);
    return packet;
  }

  protected @NotNull Object createScorePacket(
    @NotNull String entry,
    int value,
    @Nullable Object nmsDisplay,
    @Nullable Object numberFormat
  ) {
    if (NmsClasses.IS_1_20_5_OR_ABOVE) {
      return Objects.requireNonNull(NmsObjectives.SCORE_CONSTRUCTOR)
        .invoke(entry, objectiveName, value, Optional.ofNullable(nmsDisplay), Optional.ofNullable(numberFormat));
    } else if (NmsClasses.IS_1_20_3_OR_ABOVE) {
      return Objects.requireNonNull(NmsObjectives.SCORE_CONSTRUCTOR)
        .invoke(entry, objectiveName, value, nmsDisplay, numberFormat);
    } else {
      return Objects.requireNonNull(NmsObjectives.SCORE_CONSTRUCTOR)
        .invoke(NmsEnums.SCORE_ACTION_CHANGE, objectiveName, entry, value);
    }
  }

  protected @NotNull Object createObjectivePacket(
    @NotNull PropertiesPacketType packetType,
    @NotNull Object nmsValue,
    @NotNull ObjectiveRenderType renderType,
    @Nullable Object numberFormat
  ) {
    Object packet = NmsObjectives.OBJECTIVE_PACKET_CONSTRUCTOR.invoke();
    NmsObjectives.OBJECTIVE_MODE_FIELD.set(packet, ObjectiveConstants.mode(packetType));
    NmsObjectives.OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    NmsObjectives.OBJECTIVE_VALUE_FIELD.set(packet, nmsValue);

    if (NmsClasses.IS_1_20_3_OR_ABOVE) {
      assert NmsObjectives.OBJECTIVE_NUMBER_FORMAT_FIELD != null;
      Object value;
      if (NmsClasses.IS_1_20_5_OR_ABOVE) {
        value = Optional.ofNullable(numberFormat);
      } else {
        value = numberFormat;
      }
      NmsObjectives.OBJECTIVE_NUMBER_FORMAT_FIELD.set(packet, value);
    }

    assert NmsObjectives.OBJECTIVE_RENDER_TYPE_FIELD != null;
    NmsObjectives.OBJECTIVE_RENDER_TYPE_FIELD.set(packet, NmsEnums.renderType(renderType));
    return packet;
  }
}
