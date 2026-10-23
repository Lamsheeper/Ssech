package ssech.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import ssech.model.BoardState;
import ssech.model.Game;
import ssech.model.Position;

/** The 10x12 grid plus the turn/result banner above it. */
final class BoardPanel extends JPanel {
  private final TileButton[][] buttons = new TileButton[BoardState.WIDTH][BoardState.HEIGHT];
  private final JLabel banner = new JLabel();

  BoardPanel(Consumer<Position> onClick) {
    super(new BorderLayout());

    banner.setPreferredSize(new Dimension(378, 40));
    banner.setBorder(new LineBorder(Color.BLUE));
    add(banner, BorderLayout.NORTH);

    JPanel grid = new JPanel(new GridLayout(BoardState.HEIGHT, BoardState.WIDTH));
    grid.setBorder(new LineBorder(Color.BLUE));
    for (int y = 0; y < BoardState.HEIGHT; y++) {
      for (int x = 0; x < BoardState.WIDTH; x++) {
        TileButton button = new TileButton(new Position(x, y));
        button.addActionListener(e -> onClick.accept(button.position()));
        buttons[x][y] = button;
        grid.add(button);
      }
    }
    add(grid, BorderLayout.CENTER);
  }

  void render(Game game) {
    for (TileButton[] column : buttons) {
      for (TileButton button : column) {
        button.render(game);
      }
    }
    banner.setIcon(Assets.icon(bannerAsset(game)));
  }

  private static String bannerAsset(Game game) {
    if (game.isOver()) {
      return game.winner().map(t -> "TurnPNGs/win" + t.index() + ".png").orElse("TurnPNGs/tie.png");
    }
    return "TurnPNGs/move" + game.state().turn().index() + ".png";
  }
}
