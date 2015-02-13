package RPG1;
public class Dragon {
	public int HP = 75;
	public int claws = 25;
	public int fire = 6;
	public int dex = 15;
	
	public void take(int dmg) {
		if(dmg>dex) {
			HP -= dmg-dex;
		}
		if(HP < 0)
			HP = 0;
	}
}
