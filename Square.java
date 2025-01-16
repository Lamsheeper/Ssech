//package Game;

//import Troops.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.ArrayList;

public class Square extends JButton{

  private boolean occupied;
  private boolean river;
  private boolean bridge;
  private boolean cracked;
  private boolean ice;
  private String flag;
  
  private boolean selected;
  private ArrayList<int[]> pos;
  private ArrayList<int[]> wizPos;
  private int banMove;
  private int x;
  private int y;
  private boolean inverted;
  private String terr;

  private Troop occTroop;

  private Board board;

  private boolean ended;

  private int iceCount;
  

  public Square(Board b) {
    Insets buttonMargin = new Insets(0,0,0,0);
    this.setMargin(buttonMargin);
    board = b;
    selected = false;
    flag = "";
    Square[][] boardArr = b.getSquares();
    pos = new ArrayList<int[]>();

    inverted = false;
    ended = false;

    // Mouse Listener
    this.addMouseListener(new java.awt.event.MouseAdapter(){

      public void mousePressed(java.awt.event.MouseEvent evt) {

        // if clicked square is occupied by any troop
        if(!ended){
          if(occupied){
            /* scrapped method for fairy 
            if(occTroop instanceof Fairy){
              pos = fairyPositions(boardArr);
            }
            else pos = positions(boardArr);
            */
            pos = positions(boardArr, 0);
            // if no square with troop is currently selected, set this square as selected
            if (selected == false && b.getSelected() == null && occTroop.getPlayer().getPNum() == b.getTurn()) {
              b.setSelected(getThis());
              getThis().highlight();
              selected = true;
              // highlight squares the troop on this square can move to
              for(int[] a: pos){
                Square sq = boardArr[a[0]][a[1]];
                sq.highlight();
              }
              //checks for wizard, do special wizard operations if so
              if(occTroop instanceof Wizard /*&& occTroop.getFlying()==false*/){
                wizPos = wizPositions(boardArr);
                for(int[] a: wizPos){
                  boardArr[a[0]][a[1]].wizHighlight();
                }
              }else if(occTroop instanceof Bandit){
                banMove = 1;
              }
              
            }
            // check for troop eating/teleporting condition
            else if(b.getSelected() != null && selected == false){
              
              // checks for if this troop is in the positions that the selected troop can move to. if so, that means this troop is an enemy troop and may be slain. the isInPos function is often used for detecting if this square is in range of selected
              if(isInPos(b.getSelected().getPos(), x, y)){
                occTroop.getPlayer().deleteTroop(occTroop);
                Square temp = b.getSelected();
  
                // cleanup for ending a turn: dehighlight everything, deselect everything, perform movement, switch turn, UNLESS it is a bandit
                reset(temp, boardArr);
                move(temp, getThis());
                // if it is a bandit, dont reset everything and allow for 1 more move
                if(temp.getBanMove() == 1){
                  temp.setBanMove(0);
                  b.setSelected(getThis());
                  getThis().highlight();
                  selected = true;
                  pos = getThis().positions(boardArr, 1);
                  for(int[] a: pos){
                    Square sq = boardArr[a[0]][a[1]];
                    sq.highlight();
                  }
                }else{
                  //this stuff is repeated throughout the method to reset
                  b.setSelected(null);
                  selected = false;
                  b.switchTurn();
                }
              // if selected is a wizard, do teleportation instead
              }else if(b.getSelected().getTroop() instanceof Wizard){
                if(isInPos(b.getSelected().getWizPos(), x, y)){               
                  reset(b.getSelected(), boardArr);
                  
                  //if teleported onto river tiles, set it to a flying state (scrapped)
                  if(river) b.getSelected().getTroop().setFlying(true);

                  
                  wizSwap(b.getSelected(), getThis());
                  b.setSelected(null);
                  selected = false;
                  b.switchTurn();
                  
                }
              }
              
              
            }
            //deselecting currently selected troop (doesn't end turn unless it's a bandit that's already moved)
            else if(b.getSelected() != null && selected == true){
              b.setSelected(null);
              selected = false;
              getThis().deHighlight();
              if(banMove == 0 && occTroop instanceof Bandit){
                b.switchTurn();
              }
              banMove = 0;
              reset(getThis(), boardArr);
              
            }
          }
          // checking for if this square is an enemy flag to the selected troop. if so, it can be captured
          else if(b.getSelected() != null && !(flag.equals(""))){
            Square temp = b.getSelected();
            if(isInPos(b.getSelected().getPos(), x, y)){
              if(temp.getTroop().getPlayer().getPNum()==0 && flag.equals("red") || temp.getTroop().getPlayer().getPNum()==1 && flag.equals("blue")){
                reset(temp, boardArr);
              
                temp.getTroop().getPlayer().removeFlag();
                temp.getTroop().getPlayer().removeFlagCoord(new int[] {x,y});
                flag="";
                
                move(temp, getThis());
                b.setSelected(null);
                b.switchTurn();

              }
            }
          }
          // if this square is unoccupied and in range of selected troop, move selected onto this
          else if(b.getSelected() != null && selected == false){
            
            Square temp = b.getSelected();
            if(isInPos(temp.getPos(), x, y)){
              reset(temp, boardArr);
              
              move(temp, getThis());
              selected = false;
              b.switchTurn();
              b.setSelected(null);
            }
          }
        }
      }
    });

    ImageIcon icon = new ImageIcon(new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB));

    this.setIcon(icon);
  }

  
  public void restart(){
    selected = false;
    banMove = 0;
  }

  //deletes a captured flag
  public void deleteFlag(Player p){
    p.removeFlag();
    flag = "";
  }

    // process for wizard teleportation
  public void wizSwap(Square one, Square two){
    Troop temp = one.getTroop();
    one.setTroop(two.getTroop());
    two.setTroop(temp);
    one.setSelected(false);
    two.setSelected(false);
  }

  // dehighlights everything that was highlighted
  public void reset(Square s, Square[][] board){
    for(int[] a: s.getPos()){
      board[a[0]][a[1]].deHighlight();
    }

    if(s.getTroop() instanceof Wizard && s.getTroop().getFlying() == false
      ){
      for(int[] a: s.getWizPos()){
        board[a[0]][a[1]].deHighlight();
      }
      s.deHighlight();
    }
    
  }

  //checks for if the square with coords xP and yP is a square that the troop with positions arraylist p can move onto
  public boolean isInPos(ArrayList<int[]> p, int xP, int yP){
    for(int[] a: p){
      if(a[0] == xP && a[1] == yP) return true;
    }
    return false;
  }

  //performs troop movement onto an unoccupied square
  public void move(Square selected, Square moveTo){
    boolean s = selected.getTroop() instanceof Fairy;
    moveTo.setTroop(selected.getTroop());
    selected.setSelected(false);
    selected.removeTroop();
    if(selected.getCracked() && s == false){
      selected.setToRiver();
    }else if(selected.getCracked() && s) selected.setCrackedBridge();
    if(selected.isIce()){
      selected.setIce();
    }
    if(selected.isRiver()){
      selected.setToRiver();
    }
  }

  // highlights this square
  public void highlight(){
    this.setBorder(UIManager.getBorder("Button.border"));
    if(occupied == false && flag.equals("")){
      this.setIcon(null);
    }
    if(cracked|| ice) this.setBackground(new Color(255,100,100));
    else this.setBackground(new Color(215,185,0));
  }

  // highlights this square with a different color representing wiz teleportation
  public void wizHighlight(){
    if(board.getSelected().getTroop().getPlayer().getPNum() == 1){
      this.setBackground(new Color(255,200,200));
    }else{
      this.setBackground(new Color(200,200,255));
    }
  }

  // dehighlights this square according to what kind of square it is
  public void deHighlight() {
    //if square to dehighlight is empty
    if(occupied == false && flag.equals("")){
      if(river){
        this.setToRiver();
      }
      else if (bridge) {
        this.setToBridge();
      }
      else if(cracked) this.setCrackedBridge();
      else if(ice){
        this.setIcon(new ImageIcon("Tundra/ice.png"));
        this.setBackground(new Color(117,168,255));
      }
      else {
        this.setToGrass();
      }
    //if square to dehighlight is a flag
    }else if(!(flag.equals(""))){
      this.setIcon(new ImageIcon("Terrain/"+flag+"Flag.png"));
    }
    //if square to dehighlight is occupied
    if(river){
      if(terr.equals("Classic")){
        this.setBackground(new Color(64,80,210));
      }else if(terr.equals("Tundra")){
        this.setBackground(new Color(117,168,255));
      }else{
        this.setBackground(new Color(255, 81, 0));
      }
    }
    else if (bridge) {
      if(terr.equals("Classic")){
        this.setBackground(new Color(190,120,25));
      }else if(terr.equals("Tundra")){
        this.setBackground(new Color(66,66,73));
      }else{
        this.setBackground(new Color(18,0,36));
      }
    }
    else if(cracked){
      this.setBackground(new Color(18,0,36));
    }
    else if(ice){
      this.setBackground(new Color(117,168,255));
    }
    else {
      if(terr.equals("Classic")){
        this.setBackground(new Color(0,190,104));
      }
      else if(terr.equals("Tundra")){
        this.setBackground(new Color(227,234,255));
      }
      else{
        this.setBackground(new Color(59,26,26));
      }
    }
    
  }

  //sets a troop to square
  public void setTroop(Troop t){
    occTroop = t;
    occTroop.setCoords(x, y);
    this.setIcon(new ImageIcon("TroopPNGs/"+occTroop.getPathname()));
    occupied = true;
  }

  //removes a troop from a square
  public void removeTroop(){
    if(occTroop instanceof Wizard){
      wizPos = null;
    }
    pos = null;
    occTroop = null;
    occupied = false;
    if(bridge == true) this.setToBridge();
    else
    this.setToGrass();
  }

  //sets this square to normal ground type
  public void setToGrass(){
    occTroop = null;
    flag = "";
    this.setIcon(new ImageIcon(terr+"/ground.png"));
    if(terr.equals("Classic")){
      this.setBackground(new Color(0,190,104));
    } else if(terr.equals("Tundra")){
      this.setBackground(new Color(227,234,255));
    }
    else{
      
      this.setBackground(new Color(59, 26, 26));
    }
  }

  //sets this square to river that is uncrossable for all troops but fairy
  public void setToRiver() {
    river = true;
    cracked = false;
    this.setBorderPainted(false);
    this.setIcon(new ImageIcon(terr+"/liquid.png"));
    if(terr.equals("Classic")){
      this.setBackground(new Color(64,80,210));
    }else if(terr.equals("Tundra")){
      this.setBackground(new Color(117,168,255));
    }else{
      this.setBackground(new Color(255, 81, 0));
    }
  }

  //sets to bridge tile which is like ground, just different visuals
  public void setToBridge(){
    occTroop = null;
    ice = false;
    bridge = true;
    this.setIcon(new ImageIcon(terr+"/bridge.png"));
    if(terr.equals("Classic")){
      this.setBackground(new Color(190,120,25));
    }
    else if(terr.equals("Tundra")){
      this.setBackground(new Color(66,66,73));
    }
    else{
      this.setBackground(new Color(18,0,36));
    }
  }

  //sets to special cracked bridge tiles for Volcanic
  public void setCrackedBridge(){
    occTroop = null;
    bridge = false;
    cracked = true;
    this.setIcon(new ImageIcon("Volcanic/cracked.png"));
    this.setBackground(new Color(18,0,36));
  }

  //sets to special ice tiles for Tundra
  public void setIce(){
    ice = true;
    iceCount = 5;
    this.setBackground(new Color(117,168,255));
    this.setIcon(new ImageIcon("Tundra/ice.png"));
  }

  //method for Tundra ice tiles to reduce durability of ice tiles with troops standing on them
  public int crack(){
    iceCount--;
    this.setBackground(new Color(81+6*iceCount,120+8*iceCount,183+12*iceCount));
    return iceCount;
    
  }

  //gets every square's coord pair that the troop on this square can move onto based on travel range
  public ArrayList<int[]> positions(Square[][] board, int ban) {
    ArrayList<int[]> pos = new ArrayList<int[]>();
    int range = occTroop.getTravelRange();
    if(ban == 1) range = 1;

    for (int i=0; i<10; i++) {
      for (int j=0; j<12; j++) {
        if (Math.abs(Math.abs(occTroop.getX()-i) + Math.abs(occTroop.getY()-j)) <= range) {
          //checks for if current coord pair can be moved onto. if so, add it to arraylist to be returned
          if(this.check(board[i][j])){
            int[] coord = new int[2];
            coord[0] = i;
            coord[1] = j;
            pos.add(coord);
          }
        }
      }
    }
    return pos;
  }

  //scrapped method to get special fairy movement pattern
  public ArrayList<int[]> fairyPositions(Square[][] board) {
    ArrayList<int[]> pos = new ArrayList<int[]>();

    for (int i=x-1; i<=x+1; i++) {
      for (int j=y-1; j<=y+1; j++) {
        if(i>=0 && i < 10 && j>=0 && j<12 && this.check(board[i][j])){
          int[] coord = new int[2];
          coord[0] = i;
          coord[1] = j;
          pos.add(coord);
        }
      }
    }
    return pos;
  }

  //gets all coords of squares with troops within a range of 6 around the selected wizard
  public ArrayList<int[]> wizPositions(Square[][] tempBoard){
    ArrayList<int[]> wizPos = new ArrayList<int[]>();
    for (int i=0; i<10; i++) {
      for (int j=0; j<12; j++) {
        if(tempBoard[i][j].isOccupied()&& isInPos(board.getSelected().getPos(), i, j)==false && !tempBoard[i][j].isRiver()){
          if (Math.abs(Math.abs(occTroop.getX()-i) + Math.abs(occTroop.getY()-j)) <= 6) {
            if(!(i==x && j==y)){
              int[] coord = new int[2];
              coord[0] = i;
              coord[1] = j;
              wizPos.add(coord);
            }
          }
        }
      }
    }
    return wizPos;
  }

  //compares this square with the square to potentially move onto; runs through many checks like whether toCheck is an ally troop/flag or enemy troop/flag or unmoveable terrain
  public boolean check(Square toCheck){
    if(!(occTroop instanceof Fairy))
    if(toCheck.isRiver()) return false;

    if(!(toCheck.isOccupied()) && toCheck.getFlag().equals("")) return true;

    if(toCheck.isOccupied()){
      if(occTroop.getPlayer() == toCheck.getTroop().getPlayer()) return false;
      return true;
    }

    if(occTroop.getPlayer().getPNum()==0 && toCheck.getFlag().equals("red") || occTroop.getPlayer().getPNum()==1 && toCheck.getFlag().equals("blue")){
      return true;
    }
    
    return false;
  }

  //scrapped method to try and help with scrapped flip board function
  public void setThis(Square s){
    this.removeTroop();
    if(s.isRiver()) this.setToRiver();
    else if(s.isBridge()) this.setToBridge();
    else if(s.getCracked()) this.setCrackedBridge();
    else if(s.isIce()) this.setIce();
    else this.setToGrass();
    if(s.getTroop()!=null) this.setTroop(s.getTroop());
  }

  //various getters and setters
  public boolean isSelected() {
    return selected;
  }
  
  public boolean getCracked(){
    return cracked;
  }

  public int getIceCount(){
    return iceCount;
  }

  public boolean isIce(){
    return ice;
  }
  
  public void setTerrain(String t){
    terr = t;
  }

  public boolean getInverted() {
    return inverted;
  }

  public void setInverted(boolean i) {
    inverted = i;
  }

  public boolean isRiver() {
    return river;
  }

  public Square getThis(){
    return this;
  }

  public boolean isOccupied() {
    return occupied;
  }

  public boolean isBridge(){
    return bridge;
  }

  public String getFlag(){
    return flag;
  }

  public void setFlag(String color) {
    flag = color;
    this.setIcon(new ImageIcon("Terrain/"+color + "Flag.png"));
  }

  public void setEnded(boolean b){
    ended = b;
  }

  public int getBanMove(){
    return banMove;
  }

  public void setBanMove(int b){
    banMove = b;
  }

  public void setSelected(boolean b){
    selected = b;
  }

  public Troop getTroop() {
    return occTroop;
  }

  public ArrayList<int[]> getPos(){
    return pos;
  }

  public ArrayList<int[]> getWizPos(){
    return wizPos;
  }

  public void setCoords(int a, int b){
    x = a;
    y = b;
  }
}