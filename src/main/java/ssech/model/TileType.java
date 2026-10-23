package ssech.model;

public enum TileType {
  GROUND,
  /** Impassable for everything except fairies. */
  RIVER,
  BRIDGE,
  /** Volcanic only: collapses into river once a non-fairy troop steps off it. */
  CRACKED_BRIDGE,
  /** Tundra only: weakens each turn a non-fairy troop stands on it, then drowns it. */
  ICE
}
