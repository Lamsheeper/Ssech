package ssech.ui;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Slideshow explaining the rules. */
final class InfoDialog {
  private static final int SLIDE_COUNT = 4;

  private InfoDialog() {}

  static void show(Component parent) {
    int[] slide = {0};
    JLabel screen = new JLabel(slideIcon(0));
    screen.setLayout(null);

    JButton left = new JButton(Assets.icon("gameInfo/left.png"));
    left.setBounds(10, 135, 16, 32);
    left.setBorderPainted(false);
    left.addActionListener(e -> {
      slide[0] = (slide[0] + SLIDE_COUNT - 1) % SLIDE_COUNT;
      screen.setIcon(slideIcon(slide[0]));
    });
    screen.add(left);

    JButton right = new JButton(Assets.icon("gameInfo/right.png"));
    right.setBounds(375, 135, 16, 32);
    right.setBorderPainted(false);
    right.addActionListener(e -> {
      slide[0] = (slide[0] + 1) % SLIDE_COUNT;
      screen.setIcon(slideIcon(slide[0]));
    });
    screen.add(right);

    JPanel content = new JPanel();
    content.add(screen);
    Dialogs.showModal(parent, "Game Info", content);
  }

  private static Icon slideIcon(int i) {
    return Assets.icon("gameInfo/screen" + i + ".png");
  }
}
