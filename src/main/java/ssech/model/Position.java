package ssech.model;

/** A board coordinate: {@code x} is the column (0-9), {@code y} the row (0-11, blue side first). */
public record Position(int x, int y) {

  public int distanceTo(Position other) {
    return Math.abs(x - other.x) + Math.abs(y - other.y);
  }
}
