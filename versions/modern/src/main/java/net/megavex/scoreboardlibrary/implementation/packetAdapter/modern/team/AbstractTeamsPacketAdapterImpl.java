package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

public abstract class AbstractTeamsPacketAdapterImpl implements TeamsPacketAdapter {
  protected final String teamName;
  private Object removePacket;

  public AbstractTeamsPacketAdapterImpl(@NotNull String teamName) {
    this.teamName = teamName;
  }

  @Override
  public void removeTeam(@NotNull Iterable<Player> players) {
    if (removePacket == null) {
      if (PacketAccessors.IS_1_17_OR_ABOVE) {
        removePacket = PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
          teamName,
          TeamConstants.MODE_REMOVE,
          null,
          Collections.emptyList()
        );
      } else {
        assert PacketAccessors.TEAM_NAME_FIELD != null;
        assert PacketAccessors.TEAM_MODE_FIELD != null;

        removePacket = PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke();
        PacketAccessors.TEAM_NAME_FIELD.set(removePacket, teamName);
        PacketAccessors.TEAM_MODE_FIELD.set(removePacket, TeamConstants.MODE_REMOVE);
      }
    }
    ModernPacketSender.INSTANCE.sendPacket(players, removePacket);
  }

  public abstract class TeamDisplayPacketAdapterImpl implements TeamDisplayPacketAdapter {
    protected final ImmutableTeamProperties<Component> properties;

    public TeamDisplayPacketAdapterImpl(ImmutableTeamProperties<Component> properties) {
      this.properties = properties;
    }

    @Override
    public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
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

    protected void fillParameters(@NotNull Object parameters, @UnknownNullability Locale locale) {
      if (PacketAccessors.IS_1_21_5_OR_ABOVE) {
        PacketAccessors.NAME_TAG_VISIBILITY_FIELD.set(parameters, PacketAccessors.nameTagVisibility(properties.nameTagVisibility()));
        PacketAccessors.COLLISION_RULE_FIELD.set(parameters, PacketAccessors.collisionRule(properties.collisionRule()));
      } else {
        PacketAccessors.NAME_TAG_VISIBILITY_FIELD.set(parameters, properties.nameTagVisibility().key());
        PacketAccessors.COLLISION_RULE_FIELD.set(parameters, properties.collisionRule().key());
      }

      Object color = PacketAccessors.NMS_CHAT_FORMATTING_MAP.get(properties.playerColor() != null ? properties.playerColor() : NamedTextColor.WHITE);
      PacketAccessors.COLOR_FIELD.set(parameters, color);
      PacketAccessors.OPTIONS_FIELD.set(parameters, properties.packOptions());
    }
  }
}
