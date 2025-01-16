import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Board {
  private JPanel ssechBoard = new JPanel();
  private final JPanel gui = new JPanel(); 
  private Square[][] squares = new Square[10][12];
  private Square selected;
  private JLabel turnIndicator = new JLabel();
  private Sidebar sb;
  private int turn;
  private int p0Score = 0;
  private int p1Score = 0;
  private Player player0;
  private Player player1;
  private String terrain;
  private int saveNum = 0;

  public Board(Sidebar s, Player p0, Player p1) {

    selected = null;

    sb = s;

    player0 = p0;
    player1 = p1;

    gui.setLayout(new BorderLayout());
    gui.add(ssechBoard, BorderLayout.CENTER);
    gui.add(turnIndicator, BorderLayout.NORTH);
    ssechBoard.setLayout(new GridLayout(0,10));
    ssechBoard.setBorder(new LineBorder(Color.BLUE));
    turnIndicator.setPreferredSize(new Dimension(378,40));
    turnIndicator.setBorder(new LineBorder(Color.BLUE));
  }


  public void initiateGUI(String t) {

    turn = 1;
    
    terrain = t;

    for (int i=0; i<10; i++) {
      for (int j=0; j<12; j++) {
        Square sq = new Square(this);

        sq.setTerrain(terrain);
        
        sq.setCoords(i, j);
        sq.setToGrass();
        squares[i][j] = sq;
      }
    }

    
    for (int j=0; j<12; j++) {
      for (int i=0; i<10; i++) {
        ssechBoard.add(squares[i][j]);
      }
    }

    
    fillBoard();

    turnIndicator.setIcon(new ImageIcon("TurnPNGs/move1.png"));

    sb.noSelectedTroop();

    p0Score = 0;
    p1Score = 0;

    sb.updateScore();
    sb.setSaveInfo();
    
  }

  public JComponent getGui() {
    return gui;
  }

  public JComponent getBoard(){
    return ssechBoard;
  }

  public Square[][] getSquares(){
    return squares;
  }

  public void setSelected(Square s) {
    selected = s;
    if (s != null) {
      sb.updateSelectedTroop(s.getTroop());
    }
    else {
      sb.noSelectedTroop();
    }
  }

  public void switchTurn() {
    turn = Math.abs(turn-1);
    turnIndicator.setIcon(new ImageIcon("TurnPNGs/move" + turn + ".png"));

    if(terrain.equals("Tundra")){
      for(int i = 0; i < 10; i++){
        for(int j = 0; j < 11; j++){
          if(squares[i][j].isOccupied() && squares[i][j].isIce() && !(squares[i][j].getTroop() instanceof Fairy)){
            
            int c = squares[i][j].crack();
            if(c==0){
              squares[i][j].removeTroop();
              squares[i][j].setToRiver();
            }
          }
        }
      }
    }
    if (p0Score == 2 || p1Score == 2) {
      winGame();
    }
    else {
      saveGame();
    }
  }

  public void restart() {
    fillBoard();

    if (selected != null) {
      selected.reset(selected,squares);
  
      setSelected(null);
    }


    p0Score = 0;
    p1Score = 0;

    turnIndicator.setIcon(new ImageIcon("TurnPNGs/move1.png"));
    turn = 1;

    sb.updateScore();
  }

  //main method of filling up the board with squares corresponding to what terrain map was chosen. is used at the start of each game
  public void fillBoard(){
    for (int i=0; i<10; i++) {
      for (int j=0; j<12; j++) {
        if (squares[i][j].isOccupied()) {
          squares[i][j].removeTroop();
          squares[i][j].restart();
        }
        if(terrain.equals("Classic")){
          if (j==5 || j==6) {
            if (i==1 || i==8 || i==4 || i==5) {
              squares[i][j].setToBridge();
            }
            else {
              squares[i][j].setToRiver();
            }
          }else{
            squares[i][j].setToGrass();
          }
        }else if(terrain.equals("Tundra")){
          if(j >=4 && j <=7){
            squares[i][j].setIce();
          }else{
            squares[i][j].setToGrass();
          }
          if(i==1 && j==5 || i==3 && j==6 || i==6 && j==5 || i==8 && j==6){
            squares[i][j].setToBridge();
          }
        }
        else{
          squares[i][j].setToGrass();
          if(j >= 4 && j <=7){
            if(i>= 1 && i <=3 || i>=6 && i<=8){
              squares[i][j].setToBridge();
            }else{
              squares[i][j].setToRiver();
            }
          }
          if(j==6 && i==1 || j==6 && i==3 || j==5 && i==1 || j==4 && i==2 || j == 5 && i==6 || j==5 && i==8 || j==6 && i==8 || j==7 && i==7){
            squares[i][j].setBorderPainted(true);
            squares[i][j].setCrackedBridge();
          }
        }

        squares[i][j].setEnded(false);
        squares[i][j].setEnabled(true);
        squares[i][j].setBorder(UIManager.getBorder("Button.border"));
      }
    }

    player0.reset();
    player1.reset();

    player0.initiateTroops(squares);
    player1.initiateTroops(squares);

    saveGame();
  }

  public String getTerrain() {
    return terrain;
  }

  public int getTurn() {
    return turn;
  }

  public Square getSelected() {
    return selected;
  }

  public int getP0Score() {
    return p0Score;
  }

  public int getP1Score() {
    return p1Score;
  }

  public void setP0Score(int s) {
    p0Score = s;
  }

  public void setP1Score(int s) {
    p1Score = s;
  }

  public void winGame() {
    if (selected != null) {
      selected.deHighlight();
      selected.reset(selected,squares);
  
      setSelected(null);
    }
    
    if (p1Score > p0Score) {
      turnIndicator.setIcon(new ImageIcon("TurnPNGs/win1.png"));
      for (int i=0; i<10; i++) {
        for (int j=0; j<12; j++) {
          squares[i][j].setBorder(new LineBorder(Color.RED));
        }
      }
    }
    else if (p0Score > p1Score) {
      turnIndicator.setIcon(new ImageIcon("TurnPNGs/win0.png"));
      for (int i=0; i<10; i++) {
        for (int j=0; j<12; j++) {
          squares[i][j].setBorder(new LineBorder(Color.BLUE));
        }
      }
    }
    else {
      turnIndicator.setIcon(new ImageIcon("TurnPNGs/tie.png"));
      for (int i=0; i<10; i++) {
        for (int j=0; j<12; j++) {
          squares[i][j].setBorder(new LineBorder(new Color(138,43,226)));
        }
      }
    }
    for (int i=0; i<10; i++) {
      for (int j=0; j<12; j++) {
        if (squares[i][j].isOccupied() == false && squares[i][j].getFlag().equals("")) {
          squares[i][j].setIcon(null);
        }
        squares[i][j].setEnded(true);
        squares[i][j].setEnabled(false);
      }
    }
    clearSave();
  }

  public void setSave(int s) {
    saveNum = s;
  }

  public void saveGame() {
    File saveFile = new File("Saves/save" + saveNum + ".txt");
    PrintWriter out = null;

    try {
      out = new PrintWriter(saveFile);
    }
    catch (FileNotFoundException ex) {
		  System.exit(1);  // quit the program
    }

    out.flush();
    
    out.println(terrain);
    out.println(turn+"");
    out.println(p0Score);
    out.println(p1Score);

    for(int i = 0; i < 10; i++){
      for(int j = 0; j < 12; j++){
        Square sq = squares[i][j];
        
        if (sq.isRiver()) {
          out.print("River ");
        }
        else if (sq.isBridge()) {
          out.print("Bridge ");
        }
        else if (sq.getCracked()) {
          out.print("Cracked ");
        }
        else if (sq.isIce()) {
          out.print("Ice" + sq.getIceCount() + " ");
        }
        else {
          out.print("Ground ");
        }

        if (sq.isOccupied()) {
          out.print(sq.getTroop().getType() + " ");
          out.print(sq.getTroop().getPlayer().getPNum() + " ");
        }
        
        out.println("");
      }
    }

    out.close();
  }

  public void loadGame() {
    File saveFile = new File("Saves/save" + saveNum + ".txt");
    Scanner reader = null;

    try {
      reader = new Scanner(saveFile);
    }
    catch (FileNotFoundException ex) {
	    System.exit(1);  // quit the program
	  }

    terrain = reader.nextLine();

    if (terrain.equals("Empty")) {
      initiateGUI("Classic");
    }
    else {
      turn = Integer.parseInt(reader.nextLine());
      p0Score = Integer.parseInt(reader.nextLine());
      p1Score = Integer.parseInt(reader.nextLine());
  
      player0.getTroops().clear();
      player1.getTroops().clear();
      player0.getFlagCoord().clear();
      player1.getFlagCoord().clear();
  
      player0.setFlagLosses(p0Score);
      player1.setFlagLosses(p1Score);
  
      for(int i = 0; i < 10; i++){
        for(int j = 0; j < 12; j++){
          Square sq = new Square(this);
          sq.setTerrain(terrain);
          sq.setCoords(i, j);
  
          String line = reader.nextLine();
          String[] inpList = line.split(" ");
  
          String tileType = inpList[0];
          if (tileType.equals("Ground")) {
            sq.setToGrass();
          }
          else if (tileType.equals("River")) {
            sq.setToRiver();
          }
          else if (tileType.equals("Cracked")) {
            sq.setCrackedBridge();
          }
          else if (tileType.equals("Bridge")) {
            sq.setToBridge();
          }
          else {
            sq.setIce();
            int ic = Integer.parseInt(tileType.substring(3));
            for (int x=0; x<7-ic; x++) {
              sq.crack();
            }
          }
  
          if (inpList.length > 1) {
            String type = inpList[1];
            int p = Integer.parseInt(inpList[2]);
  
            if (p==0) {
              if (type.equals("Knight")) {
                player0.getTroops().add(new Knight(i,j,player0));
              }
              if (type.equals("Prince")) {
                player0.getTroops().add(new Prince(i,j,player0));
              }
              if (type.equals("Wizard")) {
                player0.getTroops().add(new Wizard(i,j,player0));
              }
              if (type.equals("Bandit")) {
                player0.getTroops().add(new Bandit(i,j,player0));
              }
              if (type.equals("Fairy")) {
                player0.getTroops().add(new Fairy(i,j,player0));
              }
            }
            else {
              if (type.equals("Knight")) {
                player1.getTroops().add(new Knight(i,j,player1));
              }
              if (type.equals("Prince")) {
                player1.getTroops().add(new Prince(i,j,player1));
              }
              if (type.equals("Wizard")) {
                player1.getTroops().add(new Wizard(i,j,player1));
              }
              if (type.equals("Bandit")) {
                player1.getTroops().add(new Bandit(i,j,player1));
              }
              if (type.equals("Fairy")) {
                player1.getTroops().add(new Fairy(i,j,player1));
              }
            }
          }
  
          squares[i][j] = sq;
        }
      }
  
      for (Troop t : player0.getTroops()) {
        squares[t.getX()][t.getY()].setTroop(t);
      }
  
      for (Troop t : player1.getTroops()) {
        squares[t.getX()][t.getY()].setTroop(t);
      }
  
      player0.getFlagCoord().add(new int[] {0,0});
      player0.getFlagCoord().add(new int[] {4,0});
      player0.getFlagCoord().add(new int[] {5,0});
      player0.getFlagCoord().add(new int[] {9,0});
      //ArrayList<int[]> dupList0 = player0.getFlagCoord();
  
      player1.getFlagCoord().add(new int[] {0,11});
      player1.getFlagCoord().add(new int[] {4,11});
      player1.getFlagCoord().add(new int[] {5,11});
      player1.getFlagCoord().add(new int[] {9,11});
      //ArrayList<int[]> dupList1 = player0.getFlagCoord();

      int i1 = 0;
      while (i1 < player1.getFlagCoord().size()) {
        int[] coord = player1.getFlagCoord().get(i1);
        if (squares[coord[0]][coord[1]].isOccupied() == false) {
          squares[coord[0]][coord[1]].setFlag("red");
          i1++;
        }
        else {
          player1.removeFlagCoord(coord);
        }
      }
  
      int i0 = 0;
      while (i0 < player0.getFlagCoord().size()) {
        int[] coord = player0.getFlagCoord().get(i0);
        if (squares[coord[0]][coord[1]].isOccupied() == false) {
          squares[coord[0]][coord[1]].setFlag("blue");
          i0++;
        }
        else {
          player0.removeFlagCoord(coord);
        }
      }
      
      
      for (int j=0; j<12; j++) {
        for (int i=0; i<10; i++) {
          ssechBoard.add(squares[i][j]);
        }
      }
  
      turnIndicator.setIcon(new ImageIcon("TurnPNGs/move" + turn + ".png"));
      sb.updateScore();
      sb.noSelectedTroop();
      sb.setSaveInfo();
    }
  }

  public void clearSave() {
    File saveFile = new File("Saves/save" + saveNum + ".txt");
    PrintWriter out = null;

    try {
      out = new PrintWriter(saveFile);
    }
    catch (FileNotFoundException ex) {
		  System.exit(1);  // quit the program
    }

    out.flush();
    out.println("Empty");

    out.close();
  }

  public int getSave() {
    return saveNum;
  }

}