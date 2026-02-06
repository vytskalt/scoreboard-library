package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.commons.LegacyFormatUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.EntriesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

public abstract class AbstractTeamsPacketAdapterImpl implements TeamsPacketAdapter {
  protected final String teamName;
  private Object removePacket;

  public AbstractTeamsPacketAdapterImpl(@NotNull String teamName) {
    this.teamName = teamName;
  }

  public static Object createTeamsPacket(
    int method,
    @NotNull String name,
    @Nullable Object parameters,
    @Nullable Collection<String> entries
  ) {
    return PacketAccessors.TEAM_PACKET_CONSTRUCTOR.invoke(
      name,
      method,
      Optional.ofNullable(parameters),
      entries == null ? Collections.emptyList() : entries
    );
  }

  @Override
  public void removeTeam(@NotNull Iterable<Player> players) {
    if (removePacket == null) {
      removePacket = createTeamsPacket(TeamConstants.MODE_REMOVE, teamName, null, null);
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
      ModernPacketSender.INSTANCE.sendPacket(players, createTeamsPacket(TeamConstants.mode(packetType), teamName, null, entries));
    }

    protected void fillParameters(@NotNull Object parameters, @UnknownNullability Locale locale) {
      if (PacketAccessors.IS_1_21_5_OR_ABOVE) {
        Object nameTagVisibility;
        switch (properties.nameTagVisibility()){
          case NEVER:
            nameTagVisibility = PacketAccessors.NAME_TAG_VISIBILITY_NEVER;
            break;
          case ALWAYS:
            nameTagVisibility = PacketAccessors.NAME_TAG_VISIBILITY_ALWAYS;
            break;
          case HIDE_FOR_OTHER_TEAMS:
            nameTagVisibility = PacketAccessors.NAME_TAG_VISIBILITY_HIDE_FOR_OTHER_TEAMS;
            break;
          case HIDE_FOR_OWN_TEAM:
            nameTagVisibility = PacketAccessors.NAME_TAG_VISIBILITY_HIDE_FOR_OWN_TEAM;
            break;
          default:
            throw new IllegalStateException("unknown name tag visibility " + properties.nameTagVisibility().name());
        }

        Objects.requireNonNull(PacketAccessors.NAME_TAG_VISIBILITY_FIELD_1_21_5)
          .set(parameters, nameTagVisibility);

        Object collisionRule;
        switch (properties.collisionRule()){
          case NEVER:
            collisionRule = PacketAccessors.COLLISION_RULE_NEVER;
            break;
          case ALWAYS:
            collisionRule = PacketAccessors.COLLISION_RULE_ALWAYS;
            break;
          case PUSH_OTHER_TEAMS:
            collisionRule = PacketAccessors.COLLISION_RULE_PUSH_OTHER_TEAMS;
            break;
          case PUSH_OWN_TEAM:
            collisionRule = PacketAccessors.COLLISION_RULE_PUSH_OWN_TEAM;
            break;
          default:
            throw new IllegalStateException("unknown collision rule " + properties.collisionRule().name());
        }

        Objects.requireNonNull(PacketAccessors.COLLISION_RULE_FIELD_1_21_5)
          .set(parameters, collisionRule);
      } else {
        Objects.requireNonNull(PacketAccessors.NAME_TAG_VISIBILITY_FIELD_1_21_4)
          .set(parameters, properties.nameTagVisibility().key());

        Objects.requireNonNull(PacketAccessors.COLLISION_RULE_FIELD_1_21_4)
          .set(parameters, properties.collisionRule().key());
      }

      char legacyChar = LegacyFormatUtil.getChar(properties.playerColor());
      PacketAccessors.COLOR_FIELD.set(parameters, PacketAccessors.CHAT_FORMATTING_GET_BY_CODE.invoke(legacyChar));

      int options = properties.packOptions();
      PacketAccessors.OPTIONS_FIELD.set(parameters, options);
    }
  }
}
