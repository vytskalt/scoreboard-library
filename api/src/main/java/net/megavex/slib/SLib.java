package net.megavex.slib;

import net.megavex.slib.sidebar.Sidebar;
import net.megavex.slib.sidebar.SidebarRenderer;

public interface SLib<P> {
  Sidebar<P> createSidebar(final SidebarRenderer<P> renderer);

  void close();
}
