package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.megavex.scoreboardlibrary.implementation.commons.LineRenderingStrategy;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketAdapterProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective.PaperObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.objective.SpigotObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.PaperTeamsPacketAdapterImpl;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team.SpigotTeamsPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.ModernPacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.objective.ObjectivePacketAdapter;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.WeakHashMap;

@SuppressWarnings("unused")
public class PacketAdapterProviderImpl implements PacketAdapterProvider {
  private final ViaAPI<Player> via;
  private final ModernPacketSender packetSender;
  private final WeakHashMap<Player, Integer> viaTeamPacketIds = new WeakHashMap<>();

  public PacketAdapterProviderImpl(Plugin plugin) {
    final String viaPlugin = "ViaVersion";
    boolean isViaEnabled = plugin.getServer().getPluginManager().isPluginEnabled(viaPlugin);
    boolean isViaAllowed = plugin.getDescription().getSoftDepend().contains(viaPlugin) || plugin.getDescription().getDepend().contains(viaPlugin);
    if (isViaEnabled && isViaAllowed) {
      //noinspection unchecked
      this.via = (ViaAPI<Player>) Via.getAPI();
    } else {
      this.via = null;
    }
    this.packetSender = new ModernPacketSender(this.via);
  }

  @Override
  public @NotNull ObjectivePacketAdapter createObjectiveAdapter(@NotNull String objectiveName) {
    return ModernComponentProvider.IS_NATIVE_ADVENTURE
      ? new PaperObjectivePacketAdapter(this, objectiveName)
      : new SpigotObjectivePacketAdapter(this, objectiveName);
  }

  @Override
  public @NotNull TeamsPacketAdapter createTeamPacketAdapter(@NotNull String teamName) {
    return ModernComponentProvider.IS_NATIVE_ADVENTURE
      ? new PaperTeamsPacketAdapterImpl(this, teamName)
      : new SpigotTeamsPacketAdapter(this, teamName);
  }

  @Override
  public @NotNull LineRenderingStrategy lineRenderingStrategy(@NotNull Player player) {
    if (this.via != null) {
      final ProtocolVersion ver = this.via.getPlayerProtocolVersion(player);
      if (ver.olderThan(ProtocolVersion.v1_13)) {
        //System.out.println("[DEBUG] " + player.getName() + " is legacy player");
        return LineRenderingStrategy.LEGACY;
      }
    }
    //System.out.println("[DEBUG] is modern player");
    return LineRenderingStrategy.MODERN;
  }

  public @Nullable ViaAPI<Player> via() {
    return via;
  }

  public @NotNull ModernPacketSender packetSender() {
    return packetSender;
  }

  public @NotNull WeakHashMap<Player, Integer> viaTeamPacketIds() {
    return viaTeamPacketIds;
  }
}
