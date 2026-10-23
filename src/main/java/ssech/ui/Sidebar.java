package ssech.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import ssech.model.Game;
import ssech.model.Team;

/** Game controls, scoreboard and the description of the selected troop or terrain. */
final class Sidebar extends JPanel {
  private final JLabel description = new JLabel();
  private final JLabel blueScore = new JLabel();
  private final JLabel redScore = new JLabel();
  private final JLabel saveInfo = new JLabel();

  Sidebar(Runnable onEndGame, Runnable onRestart, Runnable onMainMenu) {
    super(new BorderLayout());
    setBorder(new LineBorder(Color.BLUE));
    setBackground(Palette.SIDEBAR);

    JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
    controls.setBorder(new LineBorder(Color.BLUE));
    controls.setBackground(Palette.SIDEBAR);
    controls.setPreferredSize(new Dimension(0, 175));

    JLabel title = new JLabel(Assets.icon("Misc/ctrlTitle.png"));
    title.setPreferredSize(new Dimension(118, 16));
    controls.add(title);
    controls.add(button("Misc/endGame.png", onEndGame));
    controls.add(button("Misc/restartGame.png", onRestart));
    controls.add(button("Misc/menuButton.png", onMainMenu));
    saveInfo.setPreferredSize(new Dimension(100, 30));
    controls.add(saveInfo);
    add(controls, BorderLayout.NORTH);

    JPanel scoreboard = new JPanel(new BorderLayout());
    scoreboard.setBackground(Color.LIGHT_GRAY);
    JLabel header = new JLabel(Assets.icon("Score/header.png"));
    header.setBorder(new LineBorder(Color.BLUE));
    scoreboard.add(header, BorderLayout.NORTH);
    blueScore.setBorder(new LineBorder(Color.BLUE));
    redScore.setBorder(new LineBorder(Color.BLUE));
    scoreboard.add(blueScore, BorderLayout.WEST);
    scoreboard.add(redScore, BorderLayout.EAST);
    add(scoreboard, BorderLayout.CENTER);

    description.setPreferredSize(new Dimension(0, 225));
    add(description, BorderLayout.SOUTH);
  }

  void render(Game game, int slot) {
    String desc = game.selectedTroop()
        .map(t -> t.type().letter() + "Desc.png")
        .orElse(game.state().terrain().displayName() + "Desc.png");
    description.setIcon(Assets.icon("Descriptions/" + desc));
    blueScore.setIcon(Assets.icon("Score/blue" + game.state().score(Team.BLUE) + ".png"));
    redScore.setIcon(Assets.icon("Score/red" + game.state().score(Team.RED) + ".png"));
    saveInfo.setIcon(Assets.icon("SavePNGs/saveInfo" + slot + ".png"));
  }

  private static JButton button(String iconPath, Runnable action) {
    JButton button = new JButton(Assets.icon(iconPath));
    button.setPreferredSize(new Dimension(100, 30));
    button.addActionListener(e -> action.run());
    return button;
  }
}
