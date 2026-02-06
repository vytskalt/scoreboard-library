package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface ComponentProvider {
  @NotNull net.minecraft.network.chat.Component fromAdventure(@NotNull Component adventure, @Nullable Locale locale);
}
