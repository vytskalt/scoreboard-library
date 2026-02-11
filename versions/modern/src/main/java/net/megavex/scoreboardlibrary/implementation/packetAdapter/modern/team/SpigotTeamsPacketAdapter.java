package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

public class SpigotTeamsPacketAdapter extends AbstractTeamsPacketAdapterImpl {
  public SpigotTeamsPacketAdapter(@NotNull String teamName) {
    super(teamName);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new TeamDisplayPacketAdapterImpl(properties);
  }

  private class TeamDisplayPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl.TeamDisplayPacketAdapterImpl {
    public TeamDisplayPacketAdapterImpl(ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
      Collection<String> entries = ImmutableList.copyOf(properties.syncedEntries());
      LocalePacketUtil.sendLocalePackets(
        ModernPacketSender.INSTANCE,
        players,
        locale -> {

          if (PacketAccessors.IS_1_17_OR_ABOVE) {
            assert PacketAccessors.PARAMETERS_CONSTRUCTOR != null;
            Object parameters = PacketAccessors.PARAMETERS_CONSTRUCTOR.invoke();
            fillParameters(parameters, locale);

            return PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
              teamName,
              TeamConstants.mode(packetType),
              Optional.of(parameters),
              entries
            );
          } else {
            assert PacketAccessors.TEAM_NAME_FIELD != null;
            assert PacketAccessors.TEAM_MODE_FIELD != null;
            assert PacketAccessors.TEAM_ENTRIES_FIELD != null;

            Object packet = PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke();
            PacketAccessors.TEAM_NAME_FIELD.set(packet, teamName);
            PacketAccessors.TEAM_MODE_FIELD.set(packet, TeamConstants.mode(packetType));
            PacketAccessors.TEAM_ENTRIES_FIELD.set(packet, entries);
            fillParameters(packet, locale);
            return packet;
          }
        }
      );
    }

    @Override
    protected void fillParameters(@NotNull Object parameters, @UnknownNullability Locale locale) {
      super.fillParameters(parameters, locale);

      PacketAccessors.DISPLAY_NAME_FIELD.set(parameters, ModernComponentProvider.fromAdventure(properties.displayName(), locale));
      PacketAccessors.PREFIX_FIELD.set(parameters, ModernComponentProvider.fromAdventure(properties.prefix(), locale));
      PacketAccessors.SUFFIX_FIELD.set(parameters, ModernComponentProvider.fromAdventure(properties.suffix(), locale));
    }
  }
}
