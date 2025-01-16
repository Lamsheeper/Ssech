import javax.swing.*;

public class Knight extends Troop {
  //The Knight troop type
  
  public Knight(int x, int y, Player p) {
    super(x, y, p);
    this.setTravelRange(1);

    this.setType("Knight");

    if (this.getPlayer().getPNum() == 0) {
      this.setPathname("blueKnight.png");
    }
    else {
      this.setPathname("redKnight.png");
    }

    this.setDescription(new ImageIcon("Descriptions/KDesc.png"));
  }
}