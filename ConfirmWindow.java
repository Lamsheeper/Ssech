import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

public class ConfirmWindow extends JPanel {
  //Window that pops up to confirm if you want to do an action

  JLabel bg = new JLabel(new ImageIcon("Misc/confirmBG.png"));
  JButton confButton = new JButton();

  public ConfirmWindow(String type) {
    
    this.add(bg);
    bg.setLayout(null);

    confButton.setBounds(50,60,100,30);
    confButton.setIcon(new ImageIcon("Misc/" + type + ".png"));
    bg.add(confButton);

  }

  public Component getConfirmButton() {
    return confButton;
  }
}