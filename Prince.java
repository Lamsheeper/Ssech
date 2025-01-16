import javax.swing.*;

public class Prince extends Troop{
  //Prince troop type
  
  public Prince(int x, int y, Player p) {
    super(x, y, p);
    this.setTravelRange(2);

    this.setType("Prince");

    if (this.getPlayer().getPNum() == 0) {
      this.setPathname("bluePrince.png");
    }
    else {
      this.setPathname("redPrince.png");
    }

    this.setDescription(new ImageIcon("Descriptions/PDesc.png"));
  }
}