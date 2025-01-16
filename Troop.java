import javax.swing.*;

//troop superclass
public class Troop {

  private int xPos;
  private int yPos;
  private int travelRange;
  private String pathname;
  private Player player;
  private ImageIcon description;
  private String type;
  private boolean flying; // scrapped flying attribute to give to wizard

  public Troop(int x, int y, Player p) {
    //Creates a troop at the squares (x,y) belonging to Player p
    xPos = x;
    yPos = y;
    travelRange=1;
    player = p;
    flying = false;
  }

  public void setTravelRange(int range){
    travelRange = range;
  }

  public void setPathname(String pname) {
    pathname = pname;
  }

  public void setCoords(int a, int b){
    xPos = a;
    yPos = b;
  }

  public String getPathname(){
    return pathname;
  }

  public int getTravelRange(){
    return travelRange;
  }

  public ImageIcon getDescription() {
    return description;
  }

  public void setDescription(ImageIcon d) {
    description = d;
  }

  public int getX(){
    return xPos;
  }

  public int getY(){
    return yPos;
  }

  public Player getPlayer(){
    return player;
  }

  public String getType() {
    return type;
  }

  public void setType(String t) {
    type = t;
  }

  public boolean getFlying(){
    return flying;
  }

  public void setFlying(boolean f){
    flying = f;
  }
}