package net.megavex.scoreboardlibrary.tests;

import net.megavex.scoreboardlibrary.tests.api.animation.CollectionAnimationTest;
import net.megavex.scoreboardlibrary.tests.api.sidebar.ComponentSidebarLayoutTest;
import net.megavex.scoreboardlibrary.tests.implementation.LegacyFormatUtilTest;

public final class Main {
  private Main() {
  }

  public static void main(final String[] args) {
    CollectionAnimationTest.loopTest();

    ComponentSidebarLayoutTest.maxLines();
    ComponentSidebarLayoutTest.titleComponent();
    ComponentSidebarLayoutTest.animatedLines();
    ComponentSidebarLayoutTest.animatedComponents();

    LegacyFormatUtilTest.serialize();

    System.out.println("ALL TESTS PASSED!");
  }
}
