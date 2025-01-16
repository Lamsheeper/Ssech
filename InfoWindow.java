import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

public class InfoWindow extends JPanel {
  //Window that explains the rules of the game
  
  private JLabel s = new JLabel(new ImageIcon("gameInfo/screen0.png"));

  //Buttons to navigate the slides
  private JButton l = new JButton(new ImageIcon("gameInfo/left.png"));
  private JButton r = new JButton(new ImageIcon("gameInfo/right.png"));

  private int sNum = 0;

  public InfoWindow() {

    this.add(s);

    s.setLayout(null);

    s.add(l);
    l.setBounds(10,135,16,32);
    l.setBorderPainted(false);
    s.add(r);
    r.setBounds(375,135,16,32);
    r.setBorderPainted(false);

    l.addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
        if (sNum > 0) {
          sNum --;
        }
        else {
          sNum = 3;
        }
        s.setIcon(new ImageIcon("gameInfo/screen" + sNum + ".png"));
      }
    });

    r.addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
        if (sNum < 3) {
          sNum ++;
        }
        else {
          sNum = 0;
        }
        s.setIcon(new ImageIcon("gameInfo/screen" + sNum + ".png"));
      }
    });
  }

  public InfoWindow getThis() {
    return this;
  }
}