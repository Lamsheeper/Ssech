package ssech;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import ssech.persistence.SaveStore;
import ssech.ui.AppFrame;

public final class Main {
  private Main() {}

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      // The tile colors are painted as button backgrounds, which native look-and-feels (e.g. macOS Aqua) ignore.
      try {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
        throw new IllegalStateException(e);
      }
      new AppFrame(SaveStore.defaultStore()).setVisible(true);
    });
  }
}
