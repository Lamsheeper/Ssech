import javax.swing.*;

public class Fairy extends Troop{
  //Fairy troop type
  
  public Fairy(int x, int y, Player p) {
    
    super(x,y,p);
    this.setTravelRange(1);
    this.setType("Fairy");
    
    if (this.getPlayer().getPNum() == 0) {
      this.setPathname("blueFairy.png");
    }
    else {
      this.setPathname("redFairy.png");
    }

    this.setDescription(new ImageIcon("Descriptions/FDesc.png"));
  }
}