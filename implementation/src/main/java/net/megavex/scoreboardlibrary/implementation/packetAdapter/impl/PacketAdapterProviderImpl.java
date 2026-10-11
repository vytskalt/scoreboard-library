package net.megavex.scoreboardlibrary.implementation.packetAdapter.impl;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsClasses;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.objective.LegacyObjectivePacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.team.LegacyTeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.objective.PaperObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.objective.SpigotObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.team.PaperTeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.team.SpigotTeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsComponent;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.impl.nms.NmsPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

@SuppressWarnings("unused")
public final class PacketAdapterProviderImpl implements PacketAdapterProvider {
  private final ViaAPI<Player> via;
  private final NmsPacketSender packetSender;
  private final WeakHashMap<Player, Integer> viaTeamPacketIds = new WeakHashMap<>();

  public PacketAdapterProviderImpl(Plugin plugin) {
    final String viaPlugin = "ViaVersion";
    boolean isViaEnabled = plugin.getServer().getPluginManager().isPluginEnabled(viaPlugin);
    boolean isViaAllowed = plugin.getDescription().getSoftDepend().contains(viaPlugin) || plugin.getDescription().getDepend().contains(viaPlugin);
    if (NmsClasses.IS_1_13_OR_ABOVE && isViaEnabled && isViaAllowed) {
      //noinspection unchecked
      this.via = (ViaAPI<Player>) Via.getAPI();
    } else {
      this.via = null;
    }
    this.packetSender = new NmsPacketSender(this.via);
  }

  @Override
  public @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName) {
    if (!NmsClasses.IS_1_13_OR_ABOVE) {
      return new LegacyObjectivePacketAdapterImpl(this, objectiveName);
    }

    return NmsComponent.IS_NATIVE_ADVENTURE
      ? new PaperObjectivePacketAdapter(this, objectiveName)
      : new SpigotObjectivePacketAdapter(this, objectiveName);
  }

  @Override
  public @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName) {
    if (!NmsClasses.IS_1_13_OR_ABOVE) {
      return new LegacyTeamsPacketAdapterImpl(this, teamName);
    }

    return NmsComponent.IS_NATIVE_ADVENTURE
      ? new PaperTeamsPacketAdapterImpl(this, teamName)
      : new SpigotTeamsPacketAdapter(this, teamName);
  }

  @Override
  public @NotNull LineRenderingStrategy lineRenderingStrategy(@NotNull Player player) {
    if (this.via != null) {
      final ProtocolVersion ver = this.via.getPlayerProtocolVersion(player);
      return ver.newerThanOrEqualTo(ProtocolVersion.v1_13) ? LineRenderingStrategy.MODERN : LineRenderingStrategy.LEGACY;
    }

    return NmsClasses.IS_1_13_OR_ABOVE ? LineRenderingStrategy.MODERN : LineRenderingStrategy.LEGACY;
  }

  public @Nullable ViaAPI<Player> via() {
    return via;
  }

  public @NotNull NmsPacketSender packetSender() {
    return packetSender;
  }

  public @NotNull WeakHashMap<Player, Integer> viaTeamPacketIds() {
    return viaTeamPacketIds;
  }
}
