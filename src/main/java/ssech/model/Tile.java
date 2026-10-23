package ssech.model;

/** One board square: its terrain type plus whatever troop or flag currently sits on it. */
public final class Tile {
  public static final int FULL_ICE_DURABILITY = 5;

  private TileType type;
  private int iceDurability = FULL_ICE_DURABILITY;
  private Troop troop;
  private Team flag;

  public Tile(TileType type) {
    this.type = type;
  }

  public TileType type() {
    return type;
  }

  public void setType(TileType type) {
    this.type = type;
    this.iceDurability = FULL_ICE_DURABILITY;
  }

  public int iceDurability() {
    return iceDurability;
  }

  public void setIceDurability(int iceDurability) {
    this.iceDurability = iceDurability;
  }

  public Troop troop() {
    return troop;
  }

  public void setTroop(Troop troop) {
    this.troop = troop;
  }

  public boolean isOccupied() {
    return troop != null;
  }

  /** The team that owns the flag on this tile, or {@code null} if there is none. */
  public Team flag() {
    return flag;
  }

  public void setFlag(Team flag) {
    this.flag = flag;
  }
}
