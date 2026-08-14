package net.megavex.slib.sidebar;

public interface SidebarRenderer<T> {
  SidebarDisplay render(final T viewer);
}
