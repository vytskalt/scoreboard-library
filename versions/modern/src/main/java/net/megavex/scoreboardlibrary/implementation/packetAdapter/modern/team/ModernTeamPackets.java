package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public final class ModernTeamPackets {
  private ModernTeamPackets() {
  }

  public static void sendEntries(String teamName, @NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
    if (PacketAccessors.IS_1_17_OR_ABOVE) {
      Object packet = PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
        teamName,
        TeamConstants.mode(packetType),
        Optional.empty(),
        entries
      );
      ModernPacketSender.INSTANCE.sendPacket(players, packet);
    } else {
      assert PacketAccessors.TEAM_NAME_FIELD != null;
      assert PacketAccessors.TEAM_MODE_FIELD != null;
      assert PacketAccessors.TEAM_ENTRIES_FIELD != null;

      Object packet = PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke();
      PacketAccessors.TEAM_NAME_FIELD.set(packet, teamName);
      PacketAccessors.TEAM_MODE_FIELD.set(packet, TeamConstants.mode(packetType));
      PacketAccessors.TEAM_ENTRIES_FIELD.set(packet, entries);
      ModernPacketSender.INSTANCE.sendPacket(players, packet);
    }
  }
}
