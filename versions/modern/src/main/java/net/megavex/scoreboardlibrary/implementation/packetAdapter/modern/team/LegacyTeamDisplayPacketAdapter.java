package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.packet.provider.PacketTypeMap;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Types;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.megavex.scoreboardlibrary.api.team.enums.NameTagVisibility;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class LegacyTeamDisplayPacketAdapter implements TeamDisplayPacketAdapter {
  private final ViaAPI<Player> via;
  private final String teamName;
  private final ImmutableTeamProperties<String> properties;

  public LegacyTeamDisplayPacketAdapter(final ViaAPI<Player> via, final String teamName, final ImmutableTeamProperties<String> properties) {
    this.via = via;
    this.teamName = teamName;
    this.properties = properties;
  }

  @Override
  public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
    ModernTeamPackets.sendEntries(this.teamName, packetType, players, entries);
  }

  @Override
  public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
    for (final Player player : players) {
      final UserConnection conn = via.getConnection(player.getUniqueId());
      if (conn == null) {
        throw new IllegalStateException("player conn doesnt exist");
      }
      int teamsPacketId = -1;
      for (final Protocol<?, ?, ?, ?> proto : conn.getProtocolInfo().getPipeline().pipes()) {
        final PacketTypeMap<?> map = proto.getPacketTypesProvider().mappedClientboundPacketTypes().get(State.PLAY);
        if (map == null) continue;
        final PacketType type = map.typeByName("SET_PLAYER_TEAM");
        if (type == null) continue;
        teamsPacketId = type.getId();
        break;
      }
      if (teamsPacketId == -1) {
        throw new IllegalStateException("team packet id for " + conn.getProtocolInfo().protocolVersion() + " not found");
      }

      ByteBuf buf = Unpooled.buffer(128);
      Types.VAR_INT.writePrimitive(buf, teamsPacketId);

      Types.STRING.write(buf, teamName);

      Types.BYTE.writePrimitive(buf, (byte) TeamConstants.mode(packetType));

      Types.STRING.write(buf, properties.displayName());
      Types.STRING.write(buf, properties.prefix());
      Types.STRING.write(buf, properties.suffix());

      Types.BYTE.writePrimitive(buf, (byte) properties.packOptions());

      boolean is1_7 = conn.getProtocolInfo().protocolVersion().olderThan(ProtocolVersion.v1_8);
      if (is1_7) {
        System.out.println("1.7 TODO");
        Types.STRING.write(buf, NameTagVisibility.ALWAYS.key());
        Types.BYTE.writePrimitive(buf, (byte) 15);
      }

      Types.STRING.write(buf, properties.nameTagVisibility().key());
      if (conn.getProtocolInfo().protocolVersion().newerThan(ProtocolVersion.v1_9)) {
        System.out.println("1.9 shit");
        Types.STRING.write(buf, properties.collisionRule().key());
      }

      Types.BYTE.writePrimitive(buf, (byte) LegacyFormatUtil.getIndex(properties.playerColor()));

      if (packetType == PropertiesPacketType.CREATE) {
        if (is1_7) {
          Types.SHORT.writePrimitive(buf, (short) properties.syncedEntries().size());
        } else {
          Types.VAR_INT.writePrimitive(buf, properties.syncedEntries().size());
        }

        for (final String entry : properties.syncedEntries()) {
          Types.STRING.write(buf, entry);
        }
      }

      conn.scheduleSendRawPacket(buf);
      if (packetType == PropertiesPacketType.CREATE) {
        System.out.println("sent create packet");
      }
    }
  }
}
