import javax.swing.*;

public class Wizard extends Troop {
  //Wizard troop type
  
  public Wizard(int x, int y, Player p) {
    super(x, y, p);
    this.setTravelRange(1);

    this.setType("Wizard");

    if (this.getPlayer().getPNum() == 0) {
      this.setPathname("blueWizard.png");
    }
    if (this.getPlayer().getPNum() == 1) {
      this.setPathname("redWizard.png");
    }

    this.setDescription(new ImageIcon("Descriptions/WDesc.png"));
  }

}