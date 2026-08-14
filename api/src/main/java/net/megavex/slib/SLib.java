package net.megavex.slib;

import net.megavex.slib.sidebar.Sidebar;
import net.megavex.slib.sidebar.SidebarRenderer;
import net.megavex.slib.sidebar.SidebarSettings;
import net.megavex.slib.team.TeamGroup;

public interface SLib<T> {
  TeamGroup<T> createTeamGroup();

  Sidebar<T> createSidebar(final SidebarRenderer<T> renderer, final SidebarSettings settings);

  default Sidebar<T> createSidebar(final SidebarRenderer<T> renderer) {
    return createSidebar(renderer, new SidebarSettings());
  }

  void close();
}
