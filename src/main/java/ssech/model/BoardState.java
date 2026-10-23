package ssech.model;

import java.util.EnumMap;
import java.util.Map;

/** Everything needed to resume a game: the map, troops, flags, scores and whose turn it is. */
public final class BoardState {
  public static final int WIDTH = 10;
  public static final int HEIGHT = 12;

  private final Terrain terrain;
  private final Tile[][] tiles = new Tile[WIDTH][HEIGHT];
  private final Map<Team, Integer> scores = new EnumMap<>(Team.class);
  private Team turn = Team.RED;

  /** Creates a board of plain ground tiles. */
  public BoardState(Terrain terrain) {
    this.terrain = terrain;
    for (int x = 0; x < WIDTH; x++) {
      for (int y = 0; y < HEIGHT; y++) {
        tiles[x][y] = new Tile(TileType.GROUND);
      }
    }
    for (Team t : Team.values()) {
      scores.put(t, 0);
    }
  }

  public Terrain terrain() {
    return terrain;
  }

  public Tile tile(Position p) {
    return tiles[p.x()][p.y()];
  }

  public Tile tile(int x, int y) {
    return tiles[x][y];
  }

  public static boolean isOnBoard(Position p) {
    return p.x() >= 0 && p.x() < WIDTH && p.y() >= 0 && p.y() < HEIGHT;
  }

  public Team turn() {
    return turn;
  }

  public void setTurn(Team turn) {
    this.turn = turn;
  }

  /** Number of enemy flags this team has captured. */
  public int score(Team team) {
    return scores.get(team);
  }

  public void setScore(Team team, int score) {
    scores.put(team, score);
  }
}
