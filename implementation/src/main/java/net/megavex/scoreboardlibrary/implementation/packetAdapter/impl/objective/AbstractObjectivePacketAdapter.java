package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.objective;

import net.megavex.scoreboardlibrary.api.objective.ObjectiveDisplaySlot;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveRenderType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.NmsAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.PacketAdapterProviderImpl;
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
      this.removePacket = NmsAccessors.OBJECTIVE_PACKET_CONSTRUCTOR.invoke();
      NmsAccessors.OBJECTIVE_NAME_FIELD.set(removePacket, objectiveName);
      NmsAccessors.OBJECTIVE_MODE_FIELD.set(removePacket, ObjectiveConstants.MODE_REMOVE);
    }
    provider.packetSender().sendPacket(players, removePacket);
  }

  @Override
  public void removeScore(@NotNull Collection<Player> players, @NotNull String entry) {
    Object packet;
    if (NmsAccessors.IS_1_20_3_OR_ABOVE) {
      packet = Objects.requireNonNull(NmsAccessors.RESET_SCORE_CONSTRUCTOR)
        .invoke(entry, objectiveName);
    } else {
      packet = Objects.requireNonNull(NmsAccessors.SCORE_CONSTRUCTOR)
        .invoke(NmsAccessors.SCORE_1_20_2_METHOD_REMOVE, objectiveName, entry, 0);
    }
    provider.packetSender().sendPacket(players, packet);
  }

  protected @NotNull Object createDisplayPacket(@NotNull ObjectiveDisplaySlot displaySlot) {
    Object packet;
    if (NmsAccessors.IS_1_20_2_OR_ABOVE) {
      packet = Objects.requireNonNull(NmsAccessors.DISPLAY_CONSTRUCTOR)
        .invoke(NmsAccessors.DISPLAY_SLOT_VALUES.get(ObjectiveConstants.displaySlotIndex(displaySlot)), null);
    } else if (NmsAccessors.IS_1_17_OR_ABOVE){
      packet = Objects.requireNonNull(NmsAccessors.DISPLAY_CONSTRUCTOR)
        .invoke(ObjectiveConstants.displaySlotIndex(displaySlot), null);
    } else {
      assert NmsAccessors.DISPLAY_SLOT != null;
      packet = Objects.requireNonNull(NmsAccessors.DISPLAY_CONSTRUCTOR).invoke();
      NmsAccessors.DISPLAY_SLOT.set(packet, ObjectiveConstants.displaySlotIndex(displaySlot));
    }
    NmsAccessors.DISPLAY_OBJECTIVE_NAME.set(packet, objectiveName);
    return packet;
  }

  protected @NotNull Object createScorePacket(
    @NotNull String entry,
    int value,
    @Nullable Object nmsDisplay,
    @Nullable Object numberFormat
  ) {
    if (NmsAccessors.IS_1_20_5_OR_ABOVE) {
      return Objects.requireNonNull(NmsAccessors.SCORE_CONSTRUCTOR)
        .invoke(entry, objectiveName, value, Optional.ofNullable(nmsDisplay), Optional.ofNullable(numberFormat));
    } else if (NmsAccessors.IS_1_20_3_OR_ABOVE) {
      return Objects.requireNonNull(NmsAccessors.SCORE_CONSTRUCTOR)
        .invoke(entry, objectiveName, value, nmsDisplay, numberFormat);
    } else {
      return Objects.requireNonNull(NmsAccessors.SCORE_CONSTRUCTOR)
        .invoke(NmsAccessors.SCORE_1_20_2_METHOD_CHANGE, objectiveName, entry, value);
    }
  }

  protected @NotNull Object createObjectivePacket(
    @NotNull PropertiesPacketType packetType,
    @NotNull Object nmsValue,
    @NotNull ObjectiveRenderType renderType,
    @Nullable Object numberFormat
  ) {
    Object packet = NmsAccessors.OBJECTIVE_PACKET_CONSTRUCTOR.invoke();
    NmsAccessors.OBJECTIVE_MODE_FIELD.set(packet, ObjectiveConstants.mode(packetType));
    NmsAccessors.OBJECTIVE_NAME_FIELD.set(packet, objectiveName);
    NmsAccessors.OBJECTIVE_VALUE_FIELD.set(packet, nmsValue);

    if (NmsAccessors.IS_1_20_3_OR_ABOVE) {
      assert NmsAccessors.OBJECTIVE_NUMBER_FORMAT_FIELD != null;
      Object value;
      if (NmsAccessors.IS_1_20_5_OR_ABOVE) {
        value = Optional.ofNullable(numberFormat);
      } else {
        value = numberFormat;
      }
      NmsAccessors.OBJECTIVE_NUMBER_FORMAT_FIELD.set(packet, value);
    }

    NmsAccessors.OBJECTIVE_RENDER_TYPE_FIELD.set(packet, NmsAccessors.renderType(renderType));
    return packet;
  }
}
