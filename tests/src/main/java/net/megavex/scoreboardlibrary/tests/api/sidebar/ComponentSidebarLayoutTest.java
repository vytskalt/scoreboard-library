package net.megavex.scoreboardlibrary.tests.api.sidebar;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.CollectionSidebarAnimation;
import net.megavex.scoreboardlibrary.api.sidebar.component.animation.SidebarAnimation;
import net.megavex.scoreboardlibrary.tests.Assert;

import java.util.Arrays;

import static net.kyori.adventure.text.Component.text;

public final class ComponentSidebarLayoutTest {
  private static final Sidebar sidebar = new NoopScoreboardLibrary().createSidebar();

  private ComponentSidebarLayoutTest() {
  }

  public static void maxLines() {
    SidebarComponent.Builder builder = SidebarComponent.builder();
    for (int i = 0; i < Sidebar.MAX_LINES + 1; i++) {
      builder.addComponent(SidebarComponent.staticLine(text(i)));
    }

    ComponentSidebarLayout componentSidebar = new ComponentSidebarLayout(drawable -> {
    }, builder.build());

    componentSidebar.apply(sidebar);

    for (int i = 0; i < Sidebar.MAX_LINES; i++) {
      Assert.isTrue(sidebar.line(i) != null, "line not null");
    }
  }

  public static void titleComponent() {
    Component title = text("title");
    ComponentSidebarLayout componentSidebar = new ComponentSidebarLayout(SidebarComponent.staticLine(title), drawable -> {
    });
    componentSidebar.apply(sidebar);
    Assert.equals(title, sidebar.title());
  }

  public static void animatedLines() {
    SidebarAnimation<Component> animation = new CollectionSidebarAnimation<>(Arrays.asList(text("frame 1"), text("frame 2")));
    SidebarComponent lines = SidebarComponent.builder().addAnimatedLine(animation).build();
    ComponentSidebarLayout componentSidebar = new ComponentSidebarLayout(drawable -> {
    }, lines);

    componentSidebar.apply(sidebar);
    Assert.equals(animation.currentFrame(), sidebar.line(0));

    animation.nextFrame();
    componentSidebar.apply(sidebar);
    Assert.equals(animation.currentFrame(), sidebar.line(0));
  }

  public static void animatedComponents() {
    Component frame1Line = text("frame with one line");
    Component frame2Line1 = text("frame with");
    Component frame2Line2 = text("two lines");

    SidebarComponent frame1 = SidebarComponent.staticLine(frame1Line);
    SidebarComponent frame2 = drawable -> {
      drawable.drawLine(frame2Line1);
      drawable.drawLine(frame2Line2);
    };

    SidebarAnimation<SidebarComponent> animation = new CollectionSidebarAnimation<>(Arrays.asList(frame1, frame2));
    SidebarComponent lines = SidebarComponent.builder().addAnimatedComponent(animation).build();
    ComponentSidebarLayout componentSidebar = new ComponentSidebarLayout(drawable -> {
    }, lines);

    componentSidebar.apply(sidebar);
    Assert.equals(frame1Line, sidebar.line(0));
    Assert.isNull(sidebar.line(1));

    animation.nextFrame();
    componentSidebar.apply(sidebar);
    Assert.equals(frame2Line1, sidebar.line(0));
    Assert.equals(frame2Line2, sidebar.line(1));

    animation.nextFrame();
    componentSidebar.apply(sidebar);
    Assert.equals(frame1Line, sidebar.line(0));
    Assert.isNull(sidebar.line(1));
  }
}
