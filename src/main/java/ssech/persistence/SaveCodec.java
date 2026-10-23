package ssech.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ssech.model.BoardState;
import ssech.model.Layouts;
import ssech.model.Position;
import ssech.model.Rules;
import ssech.model.Team;
import ssech.model.Terrain;
import ssech.model.Tile;
import ssech.model.TileType;
import ssech.model.Troop;
import ssech.model.TroopType;

/**
 * Plain-text save format.
 *
 * <pre>
 * Classic            terrain name, or "Empty" for an unused slot
 * 1                  team to move (0 = blue, 1 = red)
 * 0                  blue score
 * 0                  red score
 * Ground Knight 0    120 tile lines, column-major (x outer, y inner):
 * River              tile token, then optional troop type and team
 * Flags 0,0,0 4,0,0  optional: remaining flags as x,y,team
 * </pre>
 *
 * Files written before the {@code Flags} line existed are still readable; their flags are
 * assumed to be on every unoccupied starting flag square.
 */
public final class SaveCodec {
  public static final String EMPTY = "Empty";
  private static final String FLAGS = "Flags";

  private SaveCodec() {}

  public static String encodeEmpty() {
    return EMPTY + "\n";
  }

  public static String encode(BoardState state) {
    StringBuilder out = new StringBuilder();
    out.append(state.terrain().displayName()).append('\n');
    out.append(state.turn().index()).append('\n');
    out.append(state.score(Team.BLUE)).append('\n');
    out.append(state.score(Team.RED)).append('\n');

    List<String> flags = new ArrayList<>();
    for (int x = 0; x < BoardState.WIDTH; x++) {
      for (int y = 0; y < BoardState.HEIGHT; y++) {
        Tile tile = state.tile(x, y);
        out.append(tileToken(tile));
        if (tile.isOccupied()) {
          out.append(' ').append(tile.troop().type().displayName())
              .append(' ').append(tile.troop().team().index());
        }
        out.append('\n');
        if (tile.flag() != null) {
          flags.add(x + "," + y + "," + tile.flag().index());
        }
      }
    }
    out.append(FLAGS);
    for (String f : flags) {
      out.append(' ').append(f);
    }
    out.append('\n');
    return out.toString();
  }

  /**
   * @return the saved game, or empty for an unused slot
   * @throws IllegalArgumentException if the text is not a valid save
   */
  public static Optional<BoardState> decode(String text) {
    List<String> lines = text.lines().map(String::strip).toList();
    if (lines.isEmpty() || lines.get(0).equals(EMPTY)) {
      return Optional.empty();
    }
    int headerLines = 4;
    int tileLines = BoardState.WIDTH * BoardState.HEIGHT;
    if (lines.size() < headerLines + tileLines) {
      throw new IllegalArgumentException("Save file is truncated");
    }

    BoardState state = new BoardState(Terrain.fromDisplayName(lines.get(0)));
    state.setTurn(Team.fromIndex(parseInt(lines.get(1))));
    state.setScore(Team.BLUE, parseScore(lines.get(2)));
    state.setScore(Team.RED, parseScore(lines.get(3)));

    int line = headerLines;
    for (int x = 0; x < BoardState.WIDTH; x++) {
      for (int y = 0; y < BoardState.HEIGHT; y++) {
        decodeTile(state.tile(x, y), lines.get(line++));
      }
    }

    String flagLine = line < lines.size() ? lines.get(line) : "";
    if (flagLine.startsWith(FLAGS)) {
      decodeFlags(state, flagLine);
    } else {
      inferLegacyFlags(state);
    }
    return Optional.of(state);
  }

  private static String tileToken(Tile tile) {
    return switch (tile.type()) {
      case GROUND -> "Ground";
      case RIVER -> "River";
      case BRIDGE -> "Bridge";
      case CRACKED_BRIDGE -> "Cracked";
      case ICE -> "Ice" + tile.iceDurability();
    };
  }

  private static void decodeTile(Tile tile, String line) {
    String[] parts = line.split("\\s+");
    String token = parts[0];
    switch (token) {
      case "Ground" -> tile.setType(TileType.GROUND);
      case "River" -> tile.setType(TileType.RIVER);
      case "Bridge" -> tile.setType(TileType.BRIDGE);
      case "Cracked" -> tile.setType(TileType.CRACKED_BRIDGE);
      default -> {
        if (!token.startsWith("Ice")) {
          throw new IllegalArgumentException("Unknown tile: " + token);
        }
        tile.setType(TileType.ICE);
        int durability = parseInt(token.substring(3));
        if (durability < 1) {
          throw new IllegalArgumentException("Invalid ice durability: " + token);
        }
        // Early versions of the game started ice at 7.
        tile.setIceDurability(Math.min(durability, Tile.FULL_ICE_DURABILITY));
      }
    }
    if (parts.length >= 3) {
      tile.setTroop(new Troop(TroopType.fromDisplayName(parts[1]), Team.fromIndex(parseInt(parts[2]))));
    }
  }

  private static void decodeFlags(BoardState state, String line) {
    String[] entries = line.split("\\s+");
    for (int i = 1; i < entries.length; i++) {
      String[] xyt = entries[i].split(",");
      if (xyt.length != 3) {
        throw new IllegalArgumentException("Invalid flag entry: " + entries[i]);
      }
      Position p = new Position(parseInt(xyt[0]), parseInt(xyt[1]));
      if (!BoardState.isOnBoard(p)) {
        throw new IllegalArgumentException("Flag off the board: " + entries[i]);
      }
      state.tile(p).setFlag(Team.fromIndex(parseInt(xyt[2])));
    }
  }

  private static void inferLegacyFlags(BoardState state) {
    for (Team team : Team.values()) {
      for (Position p : Layouts.flagPositions(team)) {
        if (!state.tile(p).isOccupied()) {
          state.tile(p).setFlag(team);
        }
      }
    }
  }

  private static int parseScore(String s) {
    int score = parseInt(s);
    if (score < 0 || score >= Rules.WINNING_SCORE) {
      throw new IllegalArgumentException("Invalid score: " + s);
    }
    return score;
  }

  private static int parseInt(String s) {
    try {
      return Integer.parseInt(s.strip());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Expected a number but found: " + s);
    }
  }
}
