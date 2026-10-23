package ssech.ui;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** "Are you sure?" popup with a single confirm button; closing the window cancels. */
final class ConfirmDialog {
  private ConfirmDialog() {}

  /** @param buttonAsset image for the confirm button, e.g. {@code "Misc/endGame.png"} */
  static boolean confirm(Component parent, String buttonAsset) {
    boolean[] confirmed = {false};

    JLabel background = new JLabel(Assets.icon("Misc/confirmBG.png"));
    background.setLayout(null);
    JButton button = new JButton(Assets.icon(buttonAsset));
    button.setBounds(50, 60, 100, 30);
    button.addActionListener(e -> {
      confirmed[0] = true;
      Dialogs.close(button);
    });
    background.add(button);

    JPanel content = new JPanel();
    content.add(background);
    Dialogs.showModal(parent, "", content);
    return confirmed[0];
  }
}
