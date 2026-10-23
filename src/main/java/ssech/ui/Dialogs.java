package ssech.ui;

import java.awt.Component;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

final class Dialogs {
  private Dialogs() {}

  /** Shows {@code content} in a modal dialog and blocks until it is closed. */
  static void showModal(Component parent, String title, JComponent content) {
    Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
    JDialog dialog = new JDialog(owner, title, JDialog.DEFAULT_MODALITY_TYPE);
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    dialog.setContentPane(content);
    dialog.setResizable(false);
    dialog.pack();
    dialog.setLocationRelativeTo(owner);
    dialog.setVisible(true);
  }

  /** Closes the dialog containing {@code c}. */
  static void close(Component c) {
    Window w = SwingUtilities.getWindowAncestor(c);
    if (w != null) {
      w.dispose();
    }
  }
}
