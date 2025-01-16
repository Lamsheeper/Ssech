import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

class Main {
  
  public static void main(String[] args) {

    //JPanel screen is shown on main JFrame and uses CardLayout to switch between different screens

    String MENU = "menutext";
    String GAME = "gametext";
    String TM = "terrainmanagertext";
    
    CardLayout c = new CardLayout();

    JFrame frame = new JFrame("Ssech Mega");

    JPanel screen = new JPanel(c);

    frame.add(screen);

    //game is the screen which the user plays on, contains board and sidebar

    JPanel game = new JPanel(new BorderLayout());

    Sidebar sidebar = new Sidebar();

    //Declaring the 2 players
    Player p0 = new Player(0, sidebar); //blue
    Player p1 = new Player(1, sidebar); //red

    //menu is the screen with the main menu
    JPanel menu = new JPanel();
    ImageIcon menuBG = new ImageIcon("Misc/titlescreen.png");
    JLabel menuBackground = new JLabel(menuBG);
    menuBackground.setLayout(null);
    menu.add(menuBackground);

    //newGame button starts a new game
    JButton newGame = new JButton();
    newGame.setIcon(new ImageIcon("Misc/newGame.png"));
    newGame.setBounds(100,220,120,30);
    menuBackground.add(newGame);

    //loadGame button loads a previously saved game
    JButton loadGame = new JButton();
    loadGame.setIcon(new ImageIcon("Misc/loadSaved.png"));
    loadGame.setBounds(100,270,120,30);
    menuBackground.add(loadGame);

    //gameInfo button prompts a dialog with the rules of the game
    JButton gameInfo = new JButton();
    gameInfo.setIcon(new ImageIcon("Misc/gameInfo.png"));
    gameInfo.setBounds(100,320,120,30);
    menuBackground.add(gameInfo);

    //panel with game rules opened by gameInfo button
    InfoWindow infoDialog = new InfoWindow();

    //terrainManager is the screen where the user selects which terrain to play on
    JPanel terrainManager = new JPanel();
    terrainManager.setOpaque(true);
    terrainManager.setBackground(Color.BLACK);

    //classic, volcanic, and tundra are panels with information about their respective terrains and a button to select that terrain
    JPanel classic = new JPanel();
    JLabel classicBG = new JLabel(new ImageIcon("Misc/Classic.png"));
    classicBG.setLayout(null);
    classic.add(classicBG);
    JButton selectClassic = new JButton();
    selectClassic.setIcon(new ImageIcon("Misc/playButton.png"));
    selectClassic.setBounds(405,100,70,25);
    classicBG.add(selectClassic);

    JPanel volcanic = new JPanel();
    JLabel volcanicBG = new JLabel(new ImageIcon("Misc/Volcanic.png"));
    volcanicBG.setLayout(null);
    volcanic.add(volcanicBG);
    JButton selectVolcanic = new JButton();
    selectVolcanic.setIcon(new ImageIcon("Misc/playButton.png"));
    selectVolcanic.setBounds(405,100,70,25);
    volcanicBG.add(selectVolcanic);

    JPanel tundra = new JPanel();
    JLabel tundraBG = new JLabel(new ImageIcon("Misc/Tundra.png"));
    tundraBG.setLayout(null);
    tundra.add(tundraBG);
    JButton selectTundra = new JButton();
    selectTundra.setIcon(new ImageIcon("Misc/playButton.png"));
    selectTundra.setBounds(405,100,70,25);
    tundraBG.add(selectTundra);

    terrainManager.setLayout(new GridLayout(0,1));
    terrainManager.add(classic);
    terrainManager.add(volcanic);
    terrainManager.add(tundra);

    //popups prompting the user to choose a save slot
    SaveMenu mainSM = new SaveMenu();
    mainSM.setType("load");

    SaveMenu newSM = new SaveMenu();
    newSM.setType("new");

    //game board
    Board board = new Board(sidebar, p0, p1);

    selectClassic.addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
        board.initiateGUI("Classic");
        c.show(screen,GAME);
      }
    });

    selectVolcanic.addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
        board.initiateGUI("Volcanic");
        c.show(screen,GAME);
      }
    });

    selectTundra.addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
        board.initiateGUI("Tundra");
        c.show(screen,GAME);
      }
    });

    game.add(board.getGui(), BorderLayout.CENTER);    
    game.add(sidebar.getGUI(), BorderLayout.EAST);
    sidebar.setBoard(board);

    screen.add(menu, MENU);
    screen.add(game, GAME);
    screen.add(terrainManager, TM);

    newGame.addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
        JOptionPane.showOptionDialog(menuBackground, newSM, "", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
      }
    });

    gameInfo.addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
        JOptionPane.showOptionDialog(menuBackground,infoDialog, "Game Info", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
      }
    });

    loadGame.addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) { 
                JOptionPane.showOptionDialog(menuBackground, mainSM, "", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
      }
    });


    for (int i=1; i<7; i++) {
      int j = i;
      mainSM.getSlotButton(i).addMouseListener(new java.awt.event.MouseAdapter(){
  
        public void mousePressed(java.awt.event.MouseEvent evt) { 

          board.setSave(j);
          board.loadGame();
          c.show(screen,GAME);
  
          Window w = SwingUtilities.getWindowAncestor(mainSM.getSlotButton(1));
  
          if (w != null) {
            w.setVisible(false);
          }
        }
      });
    }

    for (int i=1; i<7; i++) {
      int j = i;
      newSM.getSlotButton(i).addMouseListener(new java.awt.event.MouseAdapter(){
  
        public void mousePressed(java.awt.event.MouseEvent evt) { 

          board.setSave(j);
          board.clearSave();
          c.show(screen,TM);
  
          Window w = SwingUtilities.getWindowAncestor(newSM.getSlotButton(1));
  
          if (w != null) {
            w.setVisible(false);
          }
        }
      });
    }

    //Main menu button on sidebar clears the game and returns to main menu
    sidebar.getMain().addMouseListener(new java.awt.event.MouseAdapter(){
      public void mousePressed(java.awt.event.MouseEvent evt) { 
        board.getBoard().removeAll();
        p0.getTroops().clear();
        p1.getTroops().clear();
        p0.getFlagCoord().clear();
        p1.getFlagCoord().clear();
        p0.setFlagLosses(0);
        p1.setFlagLosses(0);
        c.show(screen, MENU);
      }
    });

    frame.setDefaultCloseOperation(
    JFrame.DISPOSE_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  
}