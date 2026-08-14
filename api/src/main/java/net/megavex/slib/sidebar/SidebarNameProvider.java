package net.megavex.slib.sidebar;

import org.apache.commons.lang.RandomStringUtils;

public interface SidebarNameProvider {
  SidebarNameProvider DEFAULT = new SidebarNameProvider() {
  };

  default String getObjectiveName() {
    return RandomStringUtils.randomAscii(16);
  }

  default String getTeamName(final int line) {
    return "$slib_line_" + line;
  }
}
