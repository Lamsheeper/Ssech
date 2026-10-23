package ssech.ui;

import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ssech.model.Game;
import ssech.model.Position;
import ssech.persistence.SaveStore;

/** The board and sidebar for an active game, autosaving to its slot after every turn. */
final class GameScreen extends JPanel {
  private final SaveStore store;
  private final BoardPanel board;
  private final Sidebar sidebar;
  private Game game;
  private int slot;

  GameScreen(SaveStore store, Runnable onMainMenu) {
    super(new BorderLayout());
    this.store = store;
    this.board = new BoardPanel(this::onTileClicked);
    this.sidebar = new Sidebar(this::onEndGame, this::onRestart, onMainMenu);
    add(board, BorderLayout.CENTER);
    add(sidebar, BorderLayout.EAST);
  }

  void start(Game game, int slot) {
    this.game = game;
    this.slot = slot;
    game.setOnTurnEnd(this::persist);
    persist();
    render();
  }

  private void onTileClicked(Position p) {
    game.click(p);
    render();
  }

  private void onEndGame() {
    if (!game.isOver() && ConfirmDialog.confirm(this, "Misc/endGame.png")) {
      game.forfeit();
      persist();
      render();
    }
  }

  private void onRestart() {
    if (ConfirmDialog.confirm(this, "Misc/restartGame.png")) {
      game.restart();
      persist();
      render();
    }
  }

  /** Finished games free up their slot; games in progress are saved. */
  private void persist() {
    try {
      if (game.isOver()) {
        store.clear(slot);
      } else {
        store.save(slot, game.state());
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this,
          "Could not save to " + store.directory() + ":\n" + e.getMessage(),
          "Save failed", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void render() {
    board.render(game);
    sidebar.render(game, slot);
  }
}
