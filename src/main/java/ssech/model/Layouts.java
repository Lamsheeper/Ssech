package ssech.model;

import java.util.List;

/** Starting maps and armies for each terrain. */
public final class Layouts {
  private static final int[] FLAG_COLUMNS = {0, 4, 5, 9};

  private Layouts() {}

  public static BoardState newGame(Terrain terrain) {
    BoardState state = new BoardState(terrain);
    switch (terrain) {
      case CLASSIC -> buildClassic(state);
      case VOLCANIC -> buildVolcanic(state);
      case TUNDRA -> buildTundra(state);
    }
    placeArmy(state, Team.BLUE);
    placeArmy(state, Team.RED);
    return state;
  }

  /** Where a team's four flags start. */
  public static List<Position> flagPositions(Team team) {
    int row = backRow(team);
    return List.of(
        new Position(FLAG_COLUMNS[0], row),
        new Position(FLAG_COLUMNS[1], row),
        new Position(FLAG_COLUMNS[2], row),
        new Position(FLAG_COLUMNS[3], row));
  }

  private static int backRow(Team team) {
    return team == Team.BLUE ? 0 : BoardState.HEIGHT - 1;
  }

  private static int frontRow(Team team) {
    return team == Team.BLUE ? 1 : BoardState.HEIGHT - 2;
  }

  private static void buildClassic(BoardState state) {
    for (int x = 0; x < BoardState.WIDTH; x++) {
      for (int y = 5; y <= 6; y++) {
        boolean bridge = x == 1 || x == 4 || x == 5 || x == 8;
        state.tile(x, y).setType(bridge ? TileType.BRIDGE : TileType.RIVER);
      }
    }
  }

  private static void buildVolcanic(BoardState state) {
    for (int x = 0; x < BoardState.WIDTH; x++) {
      for (int y = 4; y <= 7; y++) {
        boolean bridge = (x >= 1 && x <= 3) || (x >= 6 && x <= 8);
        state.tile(x, y).setType(bridge ? TileType.BRIDGE : TileType.RIVER);
      }
    }
    int[][] cracked = {{1, 5}, {1, 6}, {2, 4}, {3, 6}, {6, 5}, {7, 7}, {8, 5}, {8, 6}};
    for (int[] c : cracked) {
      state.tile(c[0], c[1]).setType(TileType.CRACKED_BRIDGE);
    }
  }

  private static void buildTundra(BoardState state) {
    for (int x = 0; x < BoardState.WIDTH; x++) {
      for (int y = 4; y <= 7; y++) {
        state.tile(x, y).setType(TileType.ICE);
      }
    }
    int[][] bridges = {{1, 5}, {3, 6}, {6, 5}, {8, 6}};
    for (int[] b : bridges) {
      state.tile(b[0], b[1]).setType(TileType.BRIDGE);
    }
  }

  private static void placeArmy(BoardState state, Team team) {
    int back = backRow(team);
    int front = frontRow(team);
    boolean blue = team == Team.BLUE;

    for (int x = 1; x <= 8; x++) {
      place(state, x, front, TroopType.KNIGHT, team);
    }
    place(state, 0, front, TroopType.FAIRY, team);
    place(state, 9, front, TroopType.FAIRY, team);
    for (int x : new int[] {1, 3, 6, 8}) {
      place(state, x, back, TroopType.PRINCE, team);
    }
    place(state, blue ? 2 : 7, back, TroopType.BANDIT, team);
    place(state, blue ? 7 : 2, back, TroopType.WIZARD, team);

    for (Position p : flagPositions(team)) {
      state.tile(p).setFlag(team);
    }
  }

  private static void place(BoardState state, int x, int y, TroopType type, Team team) {
    state.tile(x, y).setTroop(new Troop(type, team));
  }
}
