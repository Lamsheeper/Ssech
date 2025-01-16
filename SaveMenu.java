import java.awt.*;
import javax.swing.*;

public class SaveMenu extends JPanel {
  //Popup window that prompts the user to select a save slot

  private JLabel bg = new JLabel(new ImageIcon("SavePNGs/choiceBG.png"));
  private JLabel desc = new JLabel();

  private JButton s1 = new JButton(new ImageIcon("SavePNGs/slot1.png"));
  private JButton s2 = new JButton(new ImageIcon("SavePNGs/slot2.png"));
  private JButton s3 = new JButton(new ImageIcon("SavePNGs/slot3.png"));
  private JButton s4 = new JButton(new ImageIcon("SavePNGs/slot4.png"));
  private JButton s5 = new JButton(new ImageIcon("SavePNGs/slot5.png"));
  private JButton s6 = new JButton(new ImageIcon("SavePNGs/slot6.png"));

  public SaveMenu() {
    this.add(bg);

    bg.setLayout(null);

    s1.setBounds(30,140,100,30);
    bg.add(s1);

    s2.setBounds(30,190,100,30);
    bg.add(s2);

    s3.setBounds(30,240,100,30);
    bg.add(s3);

    s4.setBounds(170,140,100,30);
    bg.add(s4);

    s5.setBounds(170,190,100,30);
    bg.add(s5);

    s6.setBounds(170,240,100,30);
    bg.add(s6);

    desc.setBounds(25,85,250,50);
    bg.add(desc);
  }

  public Component getSlotButton(int i) {
    if (i==1) {
      return s1;
    }
    if (i==2) {
      return s2;
    }
    if (i==3) {
      return s3;
    }
    if (i==4) {
      return s4;
    }
    if (i==5) {
      return s5;
    }
    else {
      return s6;
    }
  }

  public void setType(String type) {
    desc.setIcon(new ImageIcon("SavePNGs/" + type + "Desc.png"));
  }
  
}