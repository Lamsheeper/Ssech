package ssech.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import ssech.model.Terrain;

/** Lets the player pick which map to play a new game on. */
final class TerrainSelectScreen extends JPanel {

  TerrainSelectScreen(Consumer<Terrain> onSelect) {
    super(new GridLayout(0, 1));
    setOpaque(true);
    setBackground(Color.BLACK);

    for (Terrain terrain : Terrain.values()) {
      JLabel background = new JLabel(Assets.icon("Misc/" + terrain.displayName() + ".png"));
      background.setLayout(null);

      JButton play = new JButton(Assets.icon("Misc/playButton.png"));
      play.setBounds(405, 100, 70, 25);
      play.addActionListener(e -> onSelect.accept(terrain));
      background.add(play);

      JPanel panel = new JPanel();
      panel.add(background);
      add(panel);
    }
  }
}
