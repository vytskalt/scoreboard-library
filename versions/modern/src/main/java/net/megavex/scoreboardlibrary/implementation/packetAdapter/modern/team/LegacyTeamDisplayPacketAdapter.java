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
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAdapterProviderImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil.limitLegacyText;

public final class LegacyTeamDisplayPacketAdapter implements TeamDisplayPacketAdapter {
  private final PacketAdapterProviderImpl provider;
  private final String teamName;
  private final ImmutableTeamProperties<String> properties;

  public LegacyTeamDisplayPacketAdapter(final PacketAdapterProviderImpl provider, final String teamName, final ImmutableTeamProperties<String> properties) {
    this.provider = provider;
    this.teamName = teamName;
    this.properties = properties;
  }

  @Override
  public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
    ModernTeamPackets.sendEntries(provider.packetSender(), this.teamName, packetType, players, entries);
  }

  @Override
  public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
    ViaAPI<Player> via = provider.via();
    assert via != null;
    for (final Player player : players) {
      final UserConnection conn = via.getConnection(player.getUniqueId());
      if (conn == null) return;

      Integer teamsPacketId = this.provider.viaTeamPacketIds().get(player);
      if (teamsPacketId == null) {
        for (final Protocol<?, ?, ?, ?> proto : conn.getProtocolInfo().getPipeline().pipes()) {
          final PacketTypeMap<?> map = proto.getPacketTypesProvider().mappedClientboundPacketTypes().get(State.PLAY);
          if (map == null) continue;
          final PacketType type = map.typeByName("SET_PLAYER_TEAM");
          if (type == null) continue;
          teamsPacketId = type.getId();
          break;
        }
        if (teamsPacketId == null) {
          throw new IllegalStateException("team packet id for " + conn.getProtocolInfo().protocolVersion() + " not found");
        }
        this.provider.viaTeamPacketIds().put(player, teamsPacketId);
      }

      final ByteBuf buf = Unpooled.buffer(128);
      Types.VAR_INT.writePrimitive(buf, teamsPacketId);

      Types.STRING.write(buf, teamName);

      Types.BYTE.writePrimitive(buf, (byte) TeamConstants.mode(packetType));

      final String displayName = limitLegacyText(properties.displayName(), TeamConstants.DISPLAY_NAME_LEGACY_LIMIT);
      final String prefix = limitLegacyText(properties.prefix(), TeamConstants.PREFIX_SUFFIX_LEGACY_LIMIT);
      final String suffix = limitLegacyText(properties.suffix(), TeamConstants.PREFIX_SUFFIX_LEGACY_LIMIT);
      Types.STRING.write(buf, displayName);
      Types.STRING.write(buf, prefix);
      Types.STRING.write(buf, suffix);

      Types.BYTE.writePrimitive(buf, (byte) properties.packOptions());

      final boolean is1_7 = conn.getProtocolInfo().protocolVersion().olderThanOrEqualTo(ProtocolVersion.v1_7_6);
      if (!is1_7) {
        Types.STRING.write(buf, properties.nameTagVisibility().key());
      }

      if (conn.getProtocolInfo().protocolVersion().newerThanOrEqualTo(ProtocolVersion.v1_9)) {
        Types.STRING.write(buf, properties.collisionRule().key());
      }

      if (!is1_7) {
        Types.BYTE.writePrimitive(buf, (byte) LegacyFormatUtil.getIndex(properties.playerColor()));
      }

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

      via.sendRawPacket(player.getUniqueId(), buf);
    }
  }
}
