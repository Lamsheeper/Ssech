package ssech.ui;

import java.awt.CardLayout;
import java.io.IOException;
import java.util.Optional;
import java.util.OptionalInt;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ssech.model.BoardState;
import ssech.model.Game;
import ssech.model.Layouts;
import ssech.model.Terrain;
import ssech.persistence.SaveStore;

/** Main window; switches between the menu, terrain picker and game screens. */
public final class AppFrame extends JFrame {
  private static final String MENU = "menu";
  private static final String TERRAIN = "terrain";
  private static final String GAME = "game";

  private final SaveStore store;
  private final CardLayout cards = new CardLayout();
  private final JPanel screens = new JPanel(cards);
  private final GameScreen gameScreen;
  private int pendingSlot;

  public AppFrame(SaveStore store) {
    super("Ssech Mega");
    this.store = store;
    this.gameScreen = new GameScreen(store, () -> show(MENU));

    screens.add(new MenuScreen(this::newGame, this::loadGame, () -> InfoDialog.show(this)), MENU);
    screens.add(new TerrainSelectScreen(this::startNewGame), TERRAIN);
    screens.add(gameScreen, GAME);
    setContentPane(screens);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    pack();
    setLocationRelativeTo(null);
  }

  private void newGame() {
    OptionalInt slot = SaveSlotDialog.choose(this, SaveSlotDialog.Mode.NEW);
    if (slot.isPresent()) {
      pendingSlot = slot.getAsInt();
      show(TERRAIN);
    }
  }

  private void startNewGame(Terrain terrain) {
    play(Layouts.newGame(terrain), pendingSlot);
  }

  private void loadGame() {
    OptionalInt slot = SaveSlotDialog.choose(this, SaveSlotDialog.Mode.LOAD);
    if (slot.isEmpty()) {
      return;
    }
    try {
      Optional<BoardState> saved = store.load(slot.getAsInt());
      play(saved.orElseGet(() -> Layouts.newGame(Terrain.CLASSIC)), slot.getAsInt());
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, e.getMessage(), "Could not load game", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void play(BoardState state, int slot) {
    gameScreen.start(new Game(state), slot);
    show(GAME);
  }

  private void show(String card) {
    cards.show(screens, card);
  }
}
