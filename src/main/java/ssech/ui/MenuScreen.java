package ssech.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Title screen with New Game, Load Game and Game Info buttons. */
final class MenuScreen extends JPanel {

  MenuScreen(Runnable onNewGame, Runnable onLoadGame, Runnable onGameInfo) {
    JLabel background = new JLabel(Assets.icon("Misc/titlescreen.png"));
    background.setLayout(null);
    add(background);

    background.add(button("Misc/newGame.png", 220, onNewGame));
    background.add(button("Misc/loadSaved.png", 270, onLoadGame));
    background.add(button("Misc/gameInfo.png", 320, onGameInfo));
  }

  private static JButton button(String iconPath, int y, Runnable action) {
    JButton button = new JButton(Assets.icon(iconPath));
    button.setBounds(100, y, 120, 30);
    button.addActionListener(e -> action.run());
    return button;
  }
}
