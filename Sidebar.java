import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

public class Sidebar {
  //Panel at the side of the game board that displays various information

  private JPanel sb = new JPanel(new BorderLayout());

  private JLabel selectedT = new JLabel();
  private JLabel blueScore = new JLabel();
  private JLabel redScore = new JLabel();
  private JPanel scoreboard = new JPanel(new BorderLayout());
  private JPanel gameCont = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
  private JButton end = new JButton();
  private JButton restart = new JButton();
  private JButton main = new JButton();
  private JLabel save = new JLabel();
  private Board b;

  ConfirmWindow endConfirm = new ConfirmWindow("endGame");
  ConfirmWindow restartConfirm = new ConfirmWindow("restartGame");

  public Sidebar() {

    sb.setBorder(new LineBorder(Color.BLUE));
    sb.setBackground(new Color(50,50,50));
    
    sb.add(scoreboard, BorderLayout.CENTER);
    scoreboard.add(blueScore, BorderLayout.WEST);
    scoreboard.add(redScore, BorderLayout.EAST);
    scoreboard.setBackground(Color.LIGHT_GRAY);

    JLabel scoreHeader = new JLabel();
    scoreHeader.setIcon(new ImageIcon("Score/header.png"));
    scoreHeader.setBorder(new LineBorder(Color.BLUE));

    scoreboard.add(scoreHeader, BorderLayout.NORTH);
 
    blueScore.setIcon(new ImageIcon("Score/blue0.png"));
    blueScore.setBorder(new LineBorder(Color.BLUE));

    redScore.setIcon(new ImageIcon("Score/red0.png"));
    redScore.setBorder(new LineBorder(Color.BLUE));

    sb.add(selectedT, BorderLayout.SOUTH);
    
    selectedT.setPreferredSize(new Dimension(scoreboard.getWidth(),225));

    sb.add(gameCont, BorderLayout.NORTH);
    gameCont.setBorder(new LineBorder(Color.BLUE));
    gameCont.setBackground(new Color(50,50,50));
    gameCont.setPreferredSize(new Dimension(
      scoreboard.getWidth(), 175));
    
    JLabel gcTitle = new JLabel();    
    gameCont.add(gcTitle);
    gcTitle.setIcon(new ImageIcon("Misc/ctrlTitle.png"));
    gcTitle.setPreferredSize(new Dimension(118,16));

    end.setIcon(new ImageIcon("Misc/endGame.png"));
    end.setPreferredSize(new Dimension(100,30));
    restart.setIcon(new ImageIcon("Misc/restartGame.png"));
    restart.setPreferredSize(new Dimension(100,30));
    main.setIcon(new ImageIcon("Misc/menuButton.png"));
    main.setPreferredSize(new Dimension(100,30));

    save.setPreferredSize(new Dimension(100,30));

    gameCont.add(end);
    gameCont.add(restart);
    gameCont.add(main);
    gameCont.add(save);

    restart.addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
        JOptionPane.showOptionDialog(b.getGui(), restartConfirm, "", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
      }
    });

    end.addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
        JOptionPane.showOptionDialog(b.getGui(), endConfirm, "", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
      }
    });

    endConfirm.getConfirmButton().addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
        b.winGame();

        Window w = SwingUtilities.getWindowAncestor(endConfirm.getConfirmButton());
  
        if (w != null) {
          w.setVisible(false);
        }
      }
    });

    restartConfirm.getConfirmButton().addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
        b.restart();

        Window w = SwingUtilities.getWindowAncestor(restartConfirm.getConfirmButton());
  
        if (w != null) {
          w.setVisible(false);
        }
      }
    });

  }

  public JComponent getGUI() {
    return sb;
  }
  
  public void updateSelectedTroop(Troop t) {
    selectedT.setIcon(t.getDescription());
  }

  public void noSelectedTroop() {
      selectedT.setIcon(new ImageIcon("Descriptions/" + b.getTerrain() + "Desc.png"));
  }

  public void updateScore(){
    blueScore.setIcon(new ImageIcon("Score/blue" + b.getP0Score() + ".png"));
    redScore.setIcon(new ImageIcon("Score/red" + b.getP1Score() + ".png"));
  }

  public void setBoard(Board board) {
    b = board;
  }

  public Board getBoard() {
    return b;
  }

  public void setSaveInfo() {
    save.setIcon(new ImageIcon("SavePNGs/saveInfo" + b.getSave() + ".png"));
  }

  public Component getMain() {
    return main;
  }
  
}