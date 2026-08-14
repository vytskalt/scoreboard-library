package net.megavex.slib.sidebar;

import java.util.Objects;

public final class SidebarSettings {
  private final SidebarNameProvider nameProvider;
  private final int maxLines;

  public SidebarSettings(final SidebarNameProvider nameProvider, final int maxLines) {
    this.nameProvider = Objects.requireNonNull(nameProvider);
    if (maxLines < 1) {
      throw new IllegalArgumentException("Invalid max lines: " + maxLines);
    }
    this.maxLines = maxLines;
  }

  public SidebarSettings() {
    this(SidebarNameProvider.DEFAULT, Sidebar.VANILLA_MAX_LINES);
  }

  public SidebarNameProvider getNameProvider() {
    return nameProvider;
  }

  public int getMaxLines() {
    return maxLines;
  }
}
