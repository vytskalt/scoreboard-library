package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.team;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.PacketAdapterProviderImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsEnums;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsTeams;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.util.LocalePacketUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

import static net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil.limitLegacyText;

public final class LegacyTeamsPacketAdapterImpl implements TeamsPacketAdapter {
  private final PacketAdapterProviderImpl provider;
  private final String teamName;
  private Object removePacket;

  public LegacyTeamsPacketAdapterImpl(PacketAdapterProviderImpl provider, @NotNull String teamName) {
    this.provider = provider;
    this.teamName = teamName;
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new AdventureTeamDisplayPacketAdapter(properties);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createLegacyTeamDisplayAdapter(@NotNull ImmutableTeamProperties<String> properties) {
    return new LegacyTeamDisplayPacketAdapter(properties);
  }

  private abstract class AbstractTeamDisplayPacketAdapter<C> implements TeamDisplayPacketAdapter {
    protected final ImmutableTeamProperties<C> properties;

    public AbstractTeamDisplayPacketAdapter(@NotNull ImmutableTeamProperties<C> properties) {
      this.properties = properties;
    }

    @Override
    public void removeTeam(@NotNull Iterable<Player> players) {
      if (removePacket == null) {
        removePacket = NmsTeams.TEAM_PACKET_CONSTRUCTOR.invoke();
        Objects.requireNonNull(NmsTeams.TEAM_NAME_FIELD).set(removePacket, teamName);
        Objects.requireNonNull(NmsTeams.TEAM_MODE_FIELD).set(removePacket, TeamConstants.MODE_REMOVE);
      }

      provider.packetSender().sendPacket(players, removePacket);
    }

    @Override
    public void sendEntries(@NotNull EntriesPacketType packetType, @NotNull Collection<Player> players, @NotNull Collection<String> entries) {
      Object packet = NmsTeams.TEAM_PACKET_CONSTRUCTOR.invoke();
      Objects.requireNonNull(NmsTeams.TEAM_NAME_FIELD).set(packet, teamName);
      Objects.requireNonNull(NmsTeams.TEAM_MODE_FIELD).set(packet, TeamConstants.mode(packetType));
      Objects.requireNonNull(NmsTeams.TEAM_ENTRIES_FIELD).set(packet, entries);
      provider.packetSender().sendPacket(players, packet);
    }

    @Override
    public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
      LocalePacketUtil.sendLocalePackets(
        provider.packetSender(),
        players,
        locale -> {
          String displayName = limitLegacyText(toLegacy(properties.displayName(), locale), TeamConstants.DISPLAY_NAME_LEGACY_LIMIT);
          String prefix = limitLegacyText(toLegacy(properties.prefix(), locale), TeamConstants.PREFIX_SUFFIX_LEGACY_LIMIT);
          String suffix = limitLegacyText(toLegacy(properties.suffix(), locale), TeamConstants.PREFIX_SUFFIX_LEGACY_LIMIT);

          Object packet = NmsTeams.TEAM_PACKET_CONSTRUCTOR.invoke();
          Objects.requireNonNull(NmsTeams.TEAM_NAME_FIELD).set(packet, teamName);
          Objects.requireNonNull(NmsTeams.TEAM_MODE_FIELD).set(packet, TeamConstants.mode(packetType));
          Objects.requireNonNull(NmsTeams.DISPLAY_NAME_FIELD).set(packet, displayName);
          Objects.requireNonNull(NmsTeams.PREFIX_FIELD).set(packet, prefix);
          Objects.requireNonNull(NmsTeams.SUFFIX_FIELD).set(packet, suffix);
          Objects.requireNonNull(NmsTeams.OPTIONS_FIELD).set(packet, properties.packOptions());
          if (NmsTeams.NAME_TAG_VISIBILITY_FIELD != null) {
            NmsTeams.NAME_TAG_VISIBILITY_FIELD.set(packet, properties.nameTagVisibility().key());
          }

          NamedTextColor color = properties.playerColor();
          if (NmsTeams.COLOR_FIELD != null && color != null) {
            int teamColorField = NmsEnums.ADVENTURE_TO_NMS_CHAT_FORMATTING.get(color).ordinal();
            NmsTeams.COLOR_FIELD.set(packet, teamColorField);
          }

          if (NmsTeams.COLLISION_RULE_FIELD != null) {
            NmsTeams.COLLISION_RULE_FIELD.set(packet, properties.collisionRule().key());
          }

          if (packetType == PropertiesPacketType.CREATE) {
            Objects.requireNonNull(NmsTeams.TEAM_ENTRIES_FIELD).set(packet, ImmutableList.copyOf(properties.syncedEntries()));
          }

          return packet;
        }
      );
    }

    protected abstract @NotNull String toLegacy(@NotNull C component, @NotNull Locale locale);
  }

  private class AdventureTeamDisplayPacketAdapter extends AbstractTeamDisplayPacketAdapter<Component> {
    public AdventureTeamDisplayPacketAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
      Collection<Player> remainingPlayers = players;
      if (provider.via() != null) {
        for (final Player player : players) {
          if (provider.lineRenderingStrategy(player) == LineRenderingStrategy.MODERN) {
            if (remainingPlayers == players) {
              remainingPlayers = new ArrayList<>(players);
            }
            remainingPlayers.remove(player);
            ViaModernTeamPackets.sendProperties(provider.via(), player, packetType, properties);
          }
        }
      }

      super.sendProperties(packetType, remainingPlayers);
    }

    @Override
    protected @NotNull String toLegacy(@NotNull Component component, @NotNull Locale locale) {
      return LegacyFormatUtil.serialize(component, locale);
    }
  }

  private class LegacyTeamDisplayPacketAdapter extends AbstractTeamDisplayPacketAdapter<String> {
    public LegacyTeamDisplayPacketAdapter(@NotNull ImmutableTeamProperties<String> properties) {
      super(properties);
    }

    @Override
    protected @NotNull String toLegacy(@NotNull String component, @NotNull Locale locale) {
      return component;
    }
  }
}
