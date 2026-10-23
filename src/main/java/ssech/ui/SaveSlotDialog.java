package ssech.ui;

import java.awt.Component;
import java.util.OptionalInt;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import ssech.persistence.SaveStore;

/** Asks the player to pick one of the six save slots. */
final class SaveSlotDialog {
  enum Mode {
    NEW("SavePNGs/newDesc.png"),
    LOAD("SavePNGs/loadDesc.png");

    private final String descriptionAsset;

    Mode(String descriptionAsset) {
      this.descriptionAsset = descriptionAsset;
    }
  }

  private SaveSlotDialog() {}

  /** @return the chosen slot (1-6), or empty if the dialog was closed */
  static OptionalInt choose(Component parent, Mode mode) {
    int[] chosen = {0};

    JLabel background = new JLabel(Assets.icon("SavePNGs/choiceBG.png"));
    background.setLayout(null);

    JLabel description = new JLabel(Assets.icon(mode.descriptionAsset));
    description.setBounds(25, 85, 250, 50);
    background.add(description);

    for (int slot = 1; slot <= SaveStore.SLOT_COUNT; slot++) {
      int s = slot;
      JButton button = new JButton(Assets.icon("SavePNGs/slot" + slot + ".png"));
      int column = (slot - 1) / 3;
      int row = (slot - 1) % 3;
      button.setBounds(30 + 140 * column, 140 + 50 * row, 100, 30);
      button.addActionListener(e -> {
        chosen[0] = s;
        Dialogs.close(button);
      });
      background.add(button);
    }

    JPanel content = new JPanel();
    content.add(background);
    Dialogs.showModal(parent, "", content);
    return chosen[0] == 0 ? OptionalInt.empty() : OptionalInt.of(chosen[0]);
  }
}
