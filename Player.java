import java.util.Random;

public class Player {
	public int HP, att, dex, wAtt=0,aDex,bHP;
	public int tHP,tAtt,tDex;
	public int level=0,points=0,money=0,exp=0;
	public String weapon = "Dull Sword",armor="None";
	public int[] pots = new int[4];

	public Random g = new Random();
	
	public boolean training = false;
	public int[] abilities = new int[3]; //damage, crit chance, HP
	
	public Player(int hp, int a, int d) {
		tHP = hp;
		bHP = hp;
		att = a;
		dex = d;
		reheal();
	}
	
	public void reheal() {
		HP = tHP;
	}
	
	public void take(int dmg) {
		if(dmg>tDex) {
			HP -= dmg-tDex;
		}
		if(HP < 0)
			HP = 0;
	}
	
	public void takeFire(int dmg) {
		if(!armor.equals("Fireproof Armor"))
			HP -= dmg-aDex;
	}
	
	public boolean hasPots() {
		if(pots[0] == 0 && pots[1] == 0 && pots[2] == 0 && pots[3] == 0)
			return false;
		else
			return true;
	}

	public boolean crit() {
		for(int x = 0; x <= abilities[1]; x++) {
			if(g.nextInt(20+abilities[1]) == 19+abilities[1]) {
				tAtt *= 2;
				return true;
			}
		}
		return false;
	}
}
