import java.util.ArrayList;
// 🤓

public class Player {
  private ArrayList<Troop> troops;
  private int pNum;
  private ArrayList<Troop> deadTroops;
  private ArrayList<int[]> flagCoords;
  
  private int flagLosses;
  private Sidebar sb;
  
  public Player(int num, Sidebar sb1) {
    flagCoords = new ArrayList<int[]>();
    deadTroops = new ArrayList<Troop>();
    troops = new ArrayList<Troop>();
    flagLosses = 0;
    pNum = num;
    sb = sb1;
  }

  public void initiateTroops(Square[][] board){
    if (pNum==0) {
      
      for (int i=1; i<9; i++) {
        troops.add(new Knight(i,1,this));
      }

      troops.add(new Fairy(0,1,this));
      troops.add(new Fairy(9,1,this));

      troops.add(new Prince(1,0,this));
      troops.add(new Prince(3,0,this));
      troops.add(new Prince(6,0,this));
      troops.add(new Prince(8,0,this));

      troops.add(new Bandit(2,0,this));

      troops.add(new Wizard(7,0,this));

      board[0][0].setFlag("blue");
      board[4][0].setFlag("blue");
      board[5][0].setFlag("blue");
      board[9][0].setFlag("blue");
      
      flagCoords.add(new int[] {0,0});
      flagCoords.add(new int[] {4,0});
      flagCoords.add(new int[] {5,0});
      flagCoords.add(new int[] {9,0});
      
    }

    else {

      for (int i=1; i<9; i++) {
        troops.add(new Knight(i,10,this));
      }

      troops.add(new Fairy(0,10,this));
      troops.add(new Fairy(9,10,this));

      troops.add(new Prince(1,11,this));
      troops.add(new Prince(3,11,this));
      troops.add(new Prince(6,11,this));
      troops.add(new Prince(8,11,this));

      troops.add(new Bandit(7,11,this));

      troops.add(new Wizard(2,11,this));

      board[0][11].setFlag("red");
      board[4][11].setFlag("red");
      board[5][11].setFlag("red");
      board[9][11].setFlag("red");

      flagCoords.add(new int[] {0,11});
      flagCoords.add(new int[] {4,11});
      flagCoords.add(new int[] {5,11});
      flagCoords.add(new int[] {9,11});
      
    }

    for (Troop t : troops) {
      board[t.getX()][t.getY()].setTroop(t);
    }

    

    
  }

  
  
  public int getPNum() {
    return pNum;
  }

  public void deleteTroop(Troop t){
    troops.remove(troops.indexOf(t));
    deadTroops.add(t);
  }

  public void removeFlag(){
    flagLosses ++;
    if(pNum == 0){
      sb.getBoard().setP0Score(flagLosses);
    }else{
      sb.getBoard().setP1Score(flagLosses);
    }
    sb.updateScore();
  }

  public void setFlagLosses(int f) {
    flagLosses = f;
  }

  public void reset() {
    troops = new ArrayList<Troop>();
    deadTroops = new ArrayList<Troop>();

    flagLosses = 0;
  }

  public ArrayList<Troop> getTroops() {
    return troops;
  }

  public void removeFlagCoord(int[] coord) {
    if (flagCoords.indexOf(coord) >= 0)
      flagCoords.remove(flagCoords.indexOf(coord));
  }

  public ArrayList<int[]> getFlagCoord() {
    return flagCoords;
  }
  
}