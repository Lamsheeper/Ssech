import javax.swing.*;

public class Bandit extends Troop {
  //The Bandit troop type
  
  public Bandit(int x, int y, Player p) {
    super(x, y, p);
    this.setTravelRange(2);

    this.setType("Bandit");
    
    if (this.getPlayer().getPNum() == 0) {
      this.setPathname("blueBandit.png");
    }
    else {
      this.setPathname("redBandit.png");
    }

    this.setDescription(new ImageIcon("Descriptions/BDesc.png"));

  }
}