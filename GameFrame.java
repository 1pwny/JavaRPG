import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

public class GameFrame {
	private Player p = new Player(10,1,0);
	private Random gen = new Random();
	
	private boolean intown = false;
	private boolean critting = false;
	
	private boolean es = true;
	
	private int rounds;
	private String in = "";
	private boolean resp;
	
	private JPanel jp;
	private JFrame f;
	private JTextField infield;
	private JTextArea jta;
	private JLabel[] lstats = new JLabel[4], various;
	
	
	//version of the game
	private final double version = 4.1;
	//version of the game
	
	public GameFrame() {
		f = makeFrame("RPG Frame");
		f.setBounds(0,0,600,600);
		JPanel textPanel = new JPanel(new FlowLayout());
		textPanel.setBounds(0, 0, 300, 300);
		
		jta = new JTextArea("");
		jta.setBounds(15,30,150,150);
		jta.setWrapStyleWord(true);
		
		infield = new JTextField("",15);
		infield.setEditable(true);
		infield.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                	in = infield.getText();
                	resp = true;
                }
            }
        });
		
		textPanel.add(jta);
		textPanel.add(infield);
		f.add(textPanel);
		jta.setEditable(false);
	}
	
	
	//main method\\
	public void Play() throws IOException {
		say("Welcome to the experimental RPG, v" + version);
		ask("Do you wish to take the tutorial?" + "\n" +
											"If affirmative, enter 'Yes'");
		if(in.equalsIgnoreCase("Yes")) {
			tutorial();
			levelup();
		} else if(in.equalsIgnoreCase("'Yes'")) {
			say("Don't actually put apostraphes around your answer.");
			tutorial();
			levelup();
		} else if(in.equalsIgnoreCase("load")) {
			Load();
		} else {
			say("Remember, you can pull up the tutorial at any time with 'T'");
			levelup();
		}
		while(p.HP > 0) {
			/*input = ask("What do you want to do?" +
									"\nIf you need a list of locations, type 'L'");
			option(input);*/
			if(intown) {
				say("You arrive in a town." +
											"\nHP: " + p.HP + "/" + p.tHP);
				intown = false;
				p.training = true;
			}
			town();
		}
		say("You died. Whoops! If you saved, then you can re-load your save.");
	}
	
	
	//various fights\\
	public void forestFight(int time) {
		int slevel = p.level + gen.nextInt(3) - 1;
		Mob s = new Mob(round(slevel),round(0.9*slevel),round(1.1*slevel));
		Mob bear = new Mob(round(1.1*slevel),round(2.4*slevel),round(0.5*slevel));
		
		int dmg;
		
		if(time == 0) {
			say("You get into a fight with a spider!");
			while(s.HP > 0 && p.HP > 0) {
				if(p.crit())
					critting = true;
				say("You attack and deal " + d(p.tAtt,s.dex) + " dmg");
				s.take(p.tAtt);
				if(s.HP < 1)
					break;
				say("It attacks and deals " + d(s.att,p.tDex) + " dmg");
				p.take(s.att);
				if(gen.nextInt(4)==1 && p.level>5 && !d(s.att,p.tDex).equals("no")) {
					say("You were poisoned!");
					rounds = 4;
				}
			
				if(p.level < 6 && p.tDex >= s.att && p.tAtt <= s.dex) {
					say("You fought for hours, collapsed, and were eaten.");
					System.exit(0);
				}
				
				if(rounds > 0) {
					dmg = round(p.level/5);
					if(dmg < 1)
						dmg = 1;
					p.HP -= dmg;
					rounds--;
				}
				if(p.HP<1) {
					say("You died. Whoops! If you saved, then you can re-load your save.");
					System.exit(0);
				}
				if(p.hasPots()) {
					ask("Spider: " + s.HP + "\nPlayer: " + p.HP +
							"\n\nDo you want to use a potion?" +
							"\nRegular x" + p.pots[0] +
							"\nSuper x" + p.pots[1] +
							"\nHyper x" + p.pots[2] +
							"\nFull Heal x" + p.pots[3]);
					usePots();
				} else
					say("Spider: " + s.HP + "\nPlayer: " + p.HP);
				if(critting) {
					p.tAtt /= 2;
					critting = false;
				}
			}
			if(rounds > 0) {
				dmg = round(p.level/5);
				if(dmg < 1)
					dmg = 1;
				p.HP -= dmg;
			}
			say("You sell the corpse for some money.");
			p.money += 2*(gen.nextInt(slevel)+1);
			exp(round(slevel/2));
		} else {
			say("A bear charges at you!");
			while(bear.HP > 0 && p.HP > 0) {
				if(p.crit())
					critting = true;
				say("It attacks and deals " + d(s.att,p.tDex) + " dmg");
				p.take(bear.att);
				if(gen.nextInt(4)==1 && p.level>5 && !d(bear.att,p.tDex).equals("no")) {
					say("You began to bleed!");
					rounds = 4;
				}
				if(p.HP < 1)
					break;
				
				say("You attack and deal " + d(p.tAtt,bear.dex) + " dmg");
				bear.take(p.tAtt);
			
				if(rounds > 0) {
					dmg = round(p.level/5);
					if(dmg < 1)
						dmg = 1;
					p.HP -= dmg;
					rounds--;
				}
				
				if(p.HP<1) {
					say("You died. Whoops! If you saved, then you can re-load your save.");
					System.exit(0);
				}
				
				if(p.hasPots()) {
					ask("Bear: " + bear.HP + "\nPlayer: " + p.HP +
							"\n\nDo you want to use a potion?" +
							"\nRegular x" + p.pots[0] +
							"\nSuper x" + p.pots[1] +
							"\nHyper x" + p.pots[2] +
							"\nFull Heal x" + p.pots[3]);
					usePots();
				} else
					say("Bear: " + bear.HP + "\nPlayer: " + p.HP);
				bear.att = round(1.4*slevel);
				if(critting) {
					p.tAtt /= 2;
					critting = false;
				}
			}
			if(rounds > 0) {
				dmg = round(p.level/5);
				if(dmg < 1)
					dmg = 1;
				p.HP -= dmg;
			}
			say("You sell the corpse for some money.");
			p.money += 2*(gen.nextInt(slevel)+3);
			exp(round(slevel/2));
		}
	}
	public void fieldFight(int time) {
		int td = p.dex;
		int hlevel = p.level + gen.nextInt(3) - 1;
		if(time==1) {
			Mob zombie = new Mob(round(1.5*hlevel),round(hlevel),round(hlevel*0.5));
			say("It is night time in the fields...");
			say("A zombie comes to eat your brains!");
			while(zombie.HP > 0 && p.HP > 0) {
				if(p.crit())
					critting = true;
				say("You attack and deal " + d(p.tAtt,zombie.dex) + " dmg");
				zombie.take(p.tAtt);
				if(zombie.HP < 1) {
					break;
				}
				say("It attacks and deals " + d(zombie.att,p.tDex) + " dmg");
				p.take(zombie.att);
				
				if(p.HP<1) {
					say("You died. Whoops! If you saved, then you can re-load your save.");
					System.exit(0);
				}
				
				if(gen.nextInt(4)==1 && p.level>5) {
					say("You were sickened by the stench, and started to throw up."
							+ "\nDex down!");
					rounds = 3;
				}
				
				
				if(rounds > 0) {
					p.dex = round(td/2);
					rounds--;
				} else
					p.dex = td;
				
				if(p.hasPots()) {
					ask("Zombie: " + zombie.HP + "\nPlayer: " + p.HP +
							"\n\nDo you want to use a potion?" +
							"\nRegular x" + p.pots[0] +
							"\nSuper x" + p.pots[1] +
							"\nHyper x" + p.pots[2] +
							"\nFull Heal x" + p.pots[3]);
					usePots();
				} else
					say("Zombie: " + zombie.HP + "\nPlayer: " + p.HP);
				if(critting) {
					p.tAtt /= 2;
					critting = false;
				}
			}
			say("You sell the zombie's clothes.");
			p.money += gen.nextInt(hlevel) + 2;
		} else {
			
			say("It is day time in the fields...");
			Mob bandit = new Mob(round(0.5*hlevel),round(1.5*hlevel),round(hlevel));
			say("A bandit tries to take your money!");
			while(bandit.HP > 0 && p.HP > 0) {
				if(p.crit())
					critting = true;
				say("You attack and deal " + d(p.tAtt,bandit.dex) + " dmg");
				bandit.take(p.tAtt);
				if(bandit.HP < 1)
					break;
				say("It attacks and deals " + d(bandit.att,p.tDex) + " dmg");
				p.take(bandit.att);
				
				if(p.HP<1) {
					say("You died. Whoops! If you saved, then you can re-load your save.");
					System.exit(0);
				}
				
				if(!d(bandit.att,p.tDex).equals("no")) {
					say("While hitting you, the bandit picked your pocket!");
					if(p.money >= 5)
						p.money -= 5;
					else
						p.money = 0;
				}
				if(p.hasPots()) {
					ask("Bandit: " + bandit.HP + "\nPlayer: " + p.HP +
							"\n\nDo you want to use a potion?" +
							"\nRegular x" + p.pots[0] +
							"\nSuper x" + p.pots[1] +
							"\nHyper x" + p.pots[2]);
					usePots();
				} else
					say("Bandit: " + bandit.HP + "\nPlayer: " + p.HP);
				if(critting) {
					p.tAtt /= 2;
					critting = false;
				}
			}
			say("You take the bandit's money");
			p.money += 3*(gen.nextInt(hlevel+3));
		}
		p.dex = td;
		exp(round(hlevel/2));
	}
	public void waterFight(int s) {
		Mob fish;
		
		if(s==1) {
			say("You run into a wild Magikarp!");
			fish = new Mob(round(2.5*p.level),0,round(p.level*0.5));
			while(fish.HP > 0 && p.HP > 0) {
				if(p.crit())
					critting = true;
				say("You attack and deal " + d(p.tAtt,fish.dex) + " dmg");
				fish.take(p.tAtt);
				if(fish.HP < 1)
					break;
				say("It used 'Splash'!\n\nBut nothing happened!");
				say("Magikarp: " + fish.HP + "/" + fish.tHP + "\nPlayer: " + p.HP + "/" + p.tHP);
				if(critting) {
					p.tAtt /= 2;
					critting = false;
				}
			}
			say("Magikarps are utterly useless, and have nothing of value.");
			exp(round(p.level/2));
		} else if(s==2) {
			say("You run into a wild Gyarados!");
			fish = new Mob(round(p.level*0.9),round(p.level*1.25),round(p.level*0.85));
			while(fish.HP > 0 && p.HP > 0) {
				if(p.crit())
					critting = true;
				say("You attack and deal " + d(p.tAtt,fish.dex) + " dmg");
				fish.take(p.tAtt);
				if(fish.HP < 1)
					break;
				say("Gyarados used bite! You take " + d(fish.att,p.tDex) + " dmg");
				p.take(fish.att);
				
				if(p.HP<1) {
					say("You died. Whoops! If you saved, then you can re-load your save.");
					System.exit(0);
				}
				
				if(p.hasPots()) {
					ask("Gyarados: " + fish.HP + "\nPlayer: " + p.HP + "/" + p.tHP +
							"\n\nDo you want to use a potion?" +
							"\nRegular x" + p.pots[0] +
							"\nSuper x" + p.pots[1] +
							"\nHyper x" + p.pots[2] +
							"\nFull Heal x" + p.pots[3]);
					usePots();
				} else
					say("Gyarados: " + fish.HP + "\nPlayer: " + p.HP);
				if(critting) {
					p.tAtt /= 2;
					critting = false;
				}
			}
			say("You caught it in a pokeball, and sold it for $$$");
			p.money += 3*(gen.nextInt(p.level+3));
			exp(round(p.level/2));
		} else if(s==3) {
			say("You get attacked by a giant squid!");
			fish = new Mob(round(p.level),round(p.level),round(p.level));
			while(fish.HP > 0 && p.HP > 0) {
				if(p.crit())
					critting = true;
				say("You attack and deal " + d(p.tAtt,fish.dex) + " dmg");
				fish.take(p.tAtt);
				if(fish.HP < 1)
					break;
				say("The squid suckered you with suckers! "
						+ "\nYou take " + d(fish.att,p.tDex) + " dmg! Dex down!");
				p.take(fish.att);
				p.tDex--;
				
				if(p.HP<1) {
					say("You died. Whoops! If you saved, then you can re-load your save.");
					System.exit(0);
				}
				
				if(p.hasPots()) {
					ask("Giant Squid: " + fish.HP + "\nPlayer: " + p.HP + "/" + p.tHP +
							"\n\nDo you want to use a potion?" +
							"\nRegular x" + p.pots[0] +
							"\nSuper x" + p.pots[1] +
							"\nHyper x" + p.pots[2] +
							"\nFull Heal x" + p.pots[3]);
					usePots();
				} else
					say("Giant Squid: " + fish.HP + "\nPlayer: " + p.HP);
				if(critting) {
					p.tAtt /= 2;
					critting = false;
				}
			}
			say("You saved some of the ink in a bottle, and sold it.");
			p.money += 3*(gen.nextInt(p.level+3));
			exp(round(p.level/2));
			calcTotals();
		} else if(s==4) {
			say("You get attacked by a large barracuda!");
			fish = new Mob(round(p.level*0.5),round(p.level*1.75),round(p.level*1.25));
			while(fish.HP > 0 && p.HP > 0) {
				if(p.crit())
					critting = true;
				say("You attack and deal " + d(p.tAtt,fish.dex) + " dmg");
				fish.take(p.tAtt);
				if(fish.HP < 1)
					break;
				say("The barracuda bit you with sharp teeth! "
						+ "\nYou take " + d(fish.att,p.tDex) + " dmg!");
				p.take(fish.att);
				
				if(p.HP<1) {
					say("You died. Whoops! If you saved, then you can re-load your save.");
					System.exit(0);
				}
				
				if(p.hasPots()) {
					ask("Barracuda: " + fish.HP + "\nPlayer: " + p.HP + "/" + p.tHP +
							"\n\nDo you want to use a potion?" +
							"\nRegular x" + p.pots[0] +
							"\nSuper x" + p.pots[1] +
							"\nHyper x" + p.pots[2] +
							"\nFull Heal x" + p.pots[3]);
					usePots();
				} else
					say("Barracuda: " + fish.HP + "\nPlayer: " + p.HP);
				if(critting) {
					p.tAtt /= 2;
					critting = false;
				}
			}
			say("You stored the largest tooth, and sold it.");
			p.money += 3*(gen.nextInt(p.level+3));
			exp(round(p.level/2));
		}
	}
	public void fightChange() {
		int a,b2,c;
		a = gen.nextInt(2*p.level);
		b2 = gen.nextInt(2*p.level);
		if(a+b2 > p.level*3)
			b2 = p.level*3 - a;
		c = p.level*3 - a - b2;
		
		Mob changeling = new Mob(a,b2,c);
		
		say("A changeling appears and attacks you!");
		rounds = 0;
		
		while(changeling.HP > 0 && p.HP > 0) {
			if(p.crit())
				critting = true;
			say("It attacks and deals " + d(changeling.att,p.tDex) + " dmg");
			p.take(changeling.att);
			if(p.HP < 1)
				break;
			
			say("You attack and deal " + d(p.tAtt,changeling.dex) + " dmg");
			changeling.take(p.tAtt);
		
			if(p.hasPots()) {
				ask("Changeling: " + changeling.HP + "\nPlayer: " + p.HP +
						"\n\nDo you want to use a potion?" +
						"\nRegular x" + p.pots[0] +
						"\nSuper x" + p.pots[1] +
						"\nHyper x" + p.pots[2]);
				usePots();
			} else
				say("Changeling: " + changeling.HP + "\nPlayer: " + p.HP);
			if(critting) {
				p.tAtt /= 2;
				critting = false;
			}
			rounds++;
			
			if(p.HP<1) {
				say("You died. Whoops! If you saved, then you can re-load your save.");
				System.exit(0);
			}
			
			if(rounds > 1 && changeling.HP>0) {
				say("The changeling begins to adapt to your fighting style!");
				changeling.att += p.level/10+1;
			}
		}
		say("You sell the corpse for some money.");
		p.money += 2*(gen.nextInt(p.level)+3);
		exp(round(p.level/2));
	}
	public void fightPrisoner() {
		Mob prisoner = new Mob(4,1,1);
		say("The prisoner pulls a knife on you!");
		while(prisoner.HP > 0 && p.HP > 0) {
			say("He attacks and deals " + d(prisoner.att,p.tDex) + " damage");
			p.take(prisoner.att);
			if(p.crit())
				critting = true;
			say("You hit him and deal " + d(p.tAtt,prisoner.dex) + " damage");
			prisoner.take(p.tAtt);
			say("Prisoner: " + prisoner.HP +
											 "\nPlayer: " + p.HP);
			if(critting) {
				p.tAtt /= 2;
				critting = false;
			}
		}
		exp(1);
	}
	public void fightDragon() {
		Mob guard;
		int type = gen.nextInt(2);
		if(type==0)
			guard = new Mob(35,20,20);
		else
			guard = new Mob(90,4,0);
		
		say("A lizardlike guardian attacks you!");
		while(guard.HP > 0 && p.HP > 0) {
			if(type==0) {
				say("The lizard slashed with it's claws and deals " + d(guard.att,p.tDex) + " dmg");
				p.take(guard.att);
				if(p.HP <= 0) {
					say("You died to the slashhing claws.");
					System.exit(0);
				} else {
					if(p.crit())
						critting = true;
					say("You attacked and dealt the guard " + d(p.att,guard.dex));
					guard.take(p.att);
				}
			} else {
				if(p.crit())
					critting = true;
				say("You attacked and dealt the guard " + d(p.att,guard.dex) + " dmg");
				guard.take(p.att);
				if(guard.HP <= 0) {
					break;
				} else {
					p.takeFire(guard.att);
					say("The lizard let out a weak fireball: " + (guard.att-p.aDex) + " dmg");
				}
			}
			
			if(p.HP<1) {
				say("You died. Whoops! If you saved, then you can re-load your save.");
				System.exit(0);
			}
			
			if(p.hasPots()) {
				ask("Guard: " + guard.HP + "\nPlayer: " + p.HP +
						"\n\nDo you want to use a potion?" +
						"\nRegular x" + p.pots[0] +
						"\nSuper x" + p.pots[1] +
						"\nHyper x" + p.pots[2]);
				usePots();
			} else
				say("Guard: " + guard.HP + "\nPlayer: " + p.HP);
			if(critting) {
				p.tAtt /= 2;
				critting = false;
			}
		}
		say( "You defeated the Lizard Guard!");
		//----------------------------------------------------------------------\\
		Dragon boss = new Dragon();
		say("A dragon rears out of the depths" +
				"\nand attacks you!!");
		while(boss.HP > 0 && p.HP > 0) {
			if(p.crit())
				critting = true;
			say("You attack and deal " + (p.tAtt-boss.dex) + " dmg");
			boss.take(p.tAtt);
			if(gen.nextInt(2)==0) {
				say("The dragon slashed with it's claws and deals " + (boss.claws-p.tDex) + " dmg");
				p.take(boss.claws);
			} else {
				say("The dragon sweeps the room with fire: " + (boss.fire-p.aDex) + " dmg");
				p.takeFire(boss.fire);
			}
			
			if(p.HP<1) {
				say("You died. Whoops! If you saved, then you can re-load your save.");
				System.exit(0);
			}
			
			if(p.hasPots()) {
				ask("Dragon: " + boss.HP + "\nPlayer: " + p.HP +
						"\n\nDo you want to use a potion?" +
						"\nRegular x" + p.pots[0] +
						"\nSuper x" + p.pots[1] +
						"\nHyper x" + p.pots[2]);
				usePots();
			} else
				say("Dragon: " + boss.HP + "\nPlayer: " + p.HP);
		}
		say("You sell the scales for a bunch of money.");
		p.money += 5*(gen.nextInt(10)+1);
		levelup();
	}
	public void fightGoblins() {
		Mob tGob = new Mob(65,15,15);
		Mob dGob = new Mob(20,34,10);
		Mob mGob = new Mob(15,2,0);
		
		int[] gobs = new int[3];
		gobs[0] = gen.nextInt(3);
		gobs[1] = gen.nextInt(3);
		gobs[2] = gen.nextInt(3);
		
		say("You run into a grop of goblins!");
		
		int t,d=p.dex;
		
		while((gobs[0]>0 || gobs[1]>0 || gobs[2]>0) && p.HP>0) {
			ask("Player: " + p.HP + "/" + p.tHP +
				  "\n\nTanks: " + gobs[0] +
					"\nDPS: " + gobs[1] +
					"\nMages: " + gobs[2] +
				  "\n\nWhich do you target?");
				  
			if(in.equals("1") || in.equalsIgnoreCase("tanks"))
				t = 1;
			else if(in.equals("2") || in.equalsIgnoreCase("dps"))
				t = 2;
			else if(in.equals("3") || in.equalsIgnoreCase("mages"))
				t = 3;
			else
				t = 1;
				  
			t--;
			if(t==0 && gobs[t]>0) {
				if(p.crit())
					critting = true;
				say("You have targeted the tanks! You attack and deal " + d(p.tAtt,tGob.dex) + " dmg");
				tGob.take(p.tAtt);
			}
			if(tGob.HP < 1) {
				tGob.HP = tGob.tHP;
				gobs[0]--;
				say("You killed a tank!");
			}
			if(t==1 && gobs[t]>0) {
				if(p.crit())
					critting = true;
				say("You have targeted the DPS! You attack and deal " + d(p.tAtt,dGob.dex) + " dmg");
				dGob.take(p.tAtt);
			}
			if(dGob.HP < 1) {
				dGob.HP = tGob.tHP;
				gobs[1]--;
				say("You killed a DPS!");
			}
			if(t==2 && gobs[t]>0) {
				if(gobs[0]>0 || gobs[1]>0)
					say("You try to run to the mages, but the melee goblins block your path.");
				else {
					if(p.crit())
						critting = true;
					say("You have targeted the Mages! You attack and deal " + d(p.tAtt,mGob.dex) + " dmg");
					mGob.take(p.tAtt);
					if(mGob.HP < 1) {
						mGob.HP = mGob.tHP;
						gobs[2]--;
						say("You killed a mage!");
					}
				}
			}
			
			say("It's the goblins' turn!");
			for(int x = 0; x < gobs[0]; x++) {
				say("The Goblin Tank attacks and deals " + d(tGob.att,p.tDex) + " dmg");
				p.take(tGob.att);
				if(p.HP<1) {
					say("You died. Whoops! If you saved, then you can re-load your save.");
					System.exit(0);
				}
			}
			for(int x = 0; x < gobs[1]; x++) {
				say("The Goblin DPS attacks and deals " + d(dGob.att,p.tDex) + " dmg");
				p.take(dGob.att);
				if(p.HP<1) {
					say("You died. Whoops! If you saved, then you can re-load your save.");
					System.exit(0);
				}
			}
			for(int x = 0; x < gobs[2]; x++) {
				if(gen.nextInt(2)==0) {
					say("The mage shot a magic blast and did " + mGob.att + " dmg.");
					p.HP-= mGob.att;
				} else {
					say("The mage shot an ice blast. You're slowed! Dodge down!");
					p.dex--;
				}
				if(p.HP<1) {
					say("You died. Whoops! If you saved, then you can re-load your save.");
					System.exit(0);
				}
			}
			if(critting) {
				p.tAtt /= 2;
				critting = false;
			}
		}
		p.dex = d;
		levelup();
		levelup();
	}
	
	
	//getPlayer method\\
	public Player getP() {
		return p;
	}
		

	//things in town\\
	public void town() throws IOException {
		int t;
		ask("Option / Hotkey" +
									  "\n===============" +
									  "\nVisit Dungeon / d" +
									  "\nVisit Shop / sh" +
									  "\nVisit Inn (heal) / i" +
									  "\nTrain / tr" +
									  "\nStart next adventure / a" +
									  "\nGo on a Quest / q" +
									  "\nTravel Operations / ops" +
									"\n\nWhat do you want to do?");
		if(in.equalsIgnoreCase("d")) {
			d1();
			t=pInt(in);
			d2(t);
		} else if(in.equalsIgnoreCase("sh")) {
			shop();
		} else if(in.equalsIgnoreCase("tr")) {
			train();
		} else if(in.equalsIgnoreCase("dl")) {
			dl();
		} else if(in.equalsIgnoreCase("a")) {
			ask("Which adventure do you want to go on?" +
					"\n1) Forest Findings" +
					"\n2) Frantic Fields" +
					"\n3) Severe Sea");
			adventure(pInt(in)-1);
		} else if(in.equalsIgnoreCase("t")) {
			tutorial();
		} else if(in.equalsIgnoreCase("q")) {
			if(es) {
				ask("Are you sure? Quests are much harder than adventures."
					+ "\n'L' to leave now.");
			} else {
				in = "n";
			}
			if(!in.equalsIgnoreCase("l"))
				ask("Which quest do you want to go on?" +
						"\n1) Dragon's Cave - lv.20" +
						"\n2) Goblin Gang - lv.25" +
						"\n3) The Changeling - Any");
				quest(pInt(in)-1);
		} else if(in.equalsIgnoreCase("i")) {
			say( "You are at the inn!");
			ask("Getting a room reheals you to full HP" +
					"\nbut costs 5gp. Do you want a room?");
			if(p.money >= 5) {
				if(in.equalsIgnoreCase("yes"))
					p.money -= 5; p.reheal();
					say("You have rested.");
			} else
				say("You don't have enough money.");
		} else {
			execAlwaysOps();
		}
	}
	public void shop() {
		int tempCost = 0,tempNum,tN=0,t;
		String n = "";
		boolean exists = true;
		
		ask("Which shop do you want to go to?" +
				"\nChoices: Weapons, Armor, Potions");
		if(in.equalsIgnoreCase("weapons") || in.equalsIgnoreCase("w")) {
			ask("Weapons Shop:" +
					"\nCurrect Weapon: " + p.weapon + "\nYou have " + p.money + " gold\n" +
					"\n1) Average Sword (+1 dmg)/ 25 gold" +
					"\n2) Sharp Sword (+2 dmg)/ 50 gold" +
					"\n3) Mega Sword (+3 dmg)/ 75 gold");
			t = pInt(in);
			switch(t) {
			case 1:
				if(p.wAtt<1 && p.money>24) {
					p.weapon = "Average Sword";
					p.wAtt = 1;
					p.money -= 25;
					calcTotals();
					say("Transaction successful.");
				} else {
					say("You either already have that weapon, or don't have enough money.");
				}
				break;
			case 2:
				if(p.wAtt<2 && p.money>49) {
					p.weapon = "Sharp Sword";
					p.wAtt = 2;
					p.money -= 50;
					calcTotals();
					say("Transaction successful.");
				} else {
					say("You either already have that weapon, or don't have enough money.");
				}
				break;
			case 3:
				if(p.wAtt<3 && p.money>75) {
					p.weapon = "Master Sword";
					p.wAtt = 3;
					p.money -= 75;
					calcTotals();
					say("Transaction successful.");
				} else {
					say("You either already have that weapon, or don't have enough money.");
				}
				break;
			default:
				say("There is no such weapon.");
			}
		} else if(in.equalsIgnoreCase("armor") || in.equalsIgnoreCase("a")) {
			ask("Armor Shop:" +
					"\nCurrect Armor: " + p.armor + "\nYou have " + p.money + " gold\n" +
					"\n1) Leather Armor (+1 armor)/ 25 gold" +
					"\n2) Iron Armor (+2 armor)/ 50 gold" +
					"\n3) Diamond Armor (+3 armor)/ 100 gold" +
					"\n4) Fireproof Armor (+0 armor)/ 200 gold");
			t = pInt(in);
			switch(t) {
			case 1:
				if(p.aDex<1 && p.money>24) {
					p.armor = "Leather Armor";
					p.aDex = 1;
					p.money -= 25;
					calcTotals();
					say("Transaction successful.");
				} else {
					say("You either already have that armor, or don't have enough money.");
				}
				break;
			case 2:
				if(p.aDex<2 && p.money>49) {
					p.armor = "Iron Armor";
					p.aDex = 2;
					p.money -= 50;
					calcTotals();
					say("Transaction successful.");
				} else {
					say("You either already have that armor, or don't have enough money.");
				}
				break;
			case 3:
				if(p.aDex<3 && p.money>99) {
					p.armor = "Diamond Armor";
					p.aDex = 3;
					p.money -= 100;
					calcTotals();
					say("Transaction successful.");
				} else {
					say("You either already have that armor, or don't have enough money.");
				}
				break;
			case 4:
				if(!p.armor.equals("Fireproof Armor") && p.money>199) {
					p.armor = "Fireproof Armor";
					p.aDex = 0;
					p.money -= 200;
					calcTotals();
					say("Transaction successful.");
				} else {
					say("You either already have that armor, or don't have enough money.");
				}
				break;
			default:
				say("There is no such armor.");
			}
		} else if(in.equalsIgnoreCase("potions") || in.equalsIgnoreCase("p")) {
			ask("Potions Shop:" +
					"\n1) Potion (heals 10)" +
					"\n2) Super Potion (heals 25)" +
					"\n3) Hyper Potion (heals 75)" +
					"\n4) Full Heal (heals status)");
			t = pInt(in);
			switch(t) {
			case 1:
				tN = 0;
				n = "regular potion";
				tempCost = 10;
				break;
			case 2:
				tN = 1;
				n = "super potion";
				tempCost = 20;
				break;
			case 3:
				tN = 2;
				n = "hyper potion";
				tempCost = 55;
				break;
			case 4:
				tN = 3;
				n = "full heal";
				tempCost = 20;
				break;
			default:
				say("There is no such potion.");
				exists = false;
				break;
			}
			if(exists) {
				ask("How many " + n + "s do you want?"
						+ "\nMoney: " + p.money
						+ "\nCost per: " + tempCost);
				tempNum = pInt(in);
				if(tempNum*tempCost > p.money)
					say("You have not enough money for " + tempNum + " " + n + "s.");
				else {
					p.money -= tempNum*tempCost;
					p.pots[tN] += tempNum;
				}
			}
		} else {
			say("You searched for a "+in+" shop, but couldn't find one.");
		}
	}
	public void statEditor() {
		various = new JLabel[1];
		JPanel jp2 = new JPanel();
		JButton[] pluses = new JButton[6];
		JButton[] minuses = new JButton[6];
		jp2.setLayout(new FlowLayout());
		jp2.setBounds(300,300,600,600);
		
		various[0] = new JLabel("Edit your stats:                       ");
		lstats[3] = new JLabel("Points remaining: " + p.points);
		
		jp2.add(various[0]);
		various[0].setVisible(true);
		addButtons(minuses,pluses,jp2);
		jp2.add(lstats[3]);
		lstats[3].setVisible(true);
		f.add(jp2);
		
		updateLabels();
		
		f.add(jp2);
	}
	public void train() {
		if(p.training) {
			if(es) {
				say("This is the training hall. Here, you can specialize by" +
						"\ntraining under different instructors. You can train once per town," +
						"\nat which point you have learned all you can.");
				say("There are two kinds of training you can take:" +
						"\nBarbarian: You hit harder and have more HP." +
						"\nWarrior: You hit harder and crit more often.");
				ask("What training do you want to take? Warrior/Barbarian");
			} else {
				ask("Which training?");
				if(in.equals("b"))
					in = "barbarian";
				else if(in.equals("w"))
					in = "warrior";
			}
			if(in.equalsIgnoreCase("warrior")) {
				p.abilities[0]++;
				p.abilities[1]++;
				p.training = false;
			} else if(in.equalsIgnoreCase("barbarian")) {
				p.abilities[0]++;
				p.abilities[2]++;
				p.training = false;
			} else {
				say( "You searched and searched for someone who could teach" +
						"\nyou to be a "+in+" but couldn't find one.");
			}
			if(!p.training && es) {
				say( "Training...");
				say( "Done!");
			}
		} else {
			say("You have already learned all you could from"
					+ "\nthe instructors in this town. Move on to learn more.");
		}
		calcTotals();
	}
	public void d1() {
		ask("You walk to the dungeons. Do you" + 
				"\n1) Talk to a prisoner?" +
				"\n2) Explore?" +
				"\n3) Gamble with the guards?");
	}
	public void d2(int i) {
		int roll,choice;
		if(i==1) {
			fightPrisoner();
		} else if(i==2) {
			ask("The passage splits off ahead of you. Do you want to" +
					"\n1) Go right" +
					"\n2) Stay strait" +
					"\n3) Go left");
			choice = pInt(in);
			switch(choice) {
			case 1:
				say( "You are killed in an arrow trap meant for escaping prisoners.");
				p.HP=0;
				break;
			case 2:
				say( "You are mistaken for a convict and are captured by guards.");
				p.HP=0;
				break;
			case 3:
				ask("You walk into a torture chamber. Do you"+
					"\n1) Examine the equipment?" +
					"\n2) Keep walking" +
					"\n3) Turn around");
				switch(pInt(in)) {
				case 1:
					if(gen.nextInt(2)==0) {
						say( "You find some money!");
						p.money += 10;
					} else {
						say( "You are caught by guards while examining the equipment.");
						System.exit(0);
					}
					break;
				case 2:
					say( "You are captured by the torturer and killed.");
					p.HP=0;
					break;
				case 3:
					say( "You slipped on a crack and broke your back.");
					p.HP=0;
					break;
				default:
					say( "You do something stupid and take damage.");
					p.HP-= 1;
					break;
				}
				break;
			default:
				say( "You do something stupid and take damage.");
				p.HP-= 1;
				break;
			}
		} else if(i==3) {
			while(in.equals("Yes")) {
				ask("How much do you want to bet?");
				choice = pInt(in);
				if(choice<=p.money) {
					roll = gen.nextInt(6)+1;
					if(roll > gen.nextInt(6))
						p.money += choice;
					else
						p.money -= choice;
					say( "You now have " + p.money + "gp");
				} else 
					say( "You do not have that much money.");
				ask("Want to play again?");
			}
			
			
		} else {
			say("You slippped in a puddle of gunk, fell, hit your head and died.");
			p.HP=0;
		}
	}
	public void adventure(int c) throws IOException {
		if(c == 0) {
			say("You start your adventure in a forest!");
			forestFight(0);
		} else if (c==1) {
			say("You start your adventure in the fields!");
			fieldFight(0);
		} else if (c==2) {
			say("You start your adventure swimming in the ocean!");
			waterFight(gen.nextInt(4)+1);
		}
		do {
			alwaysOps();
			ask("Enter 'c' to continue with your adventure.");
		} while(!in.equals("c"));
		//--------------------------------------------------------------------\\
		if(c == 0) {
			forestFight(0);
		} else if(c==1){
			fieldFight(0);
		} else if(c==2){
			waterFight(gen.nextInt(4)+1);
		}
		do {
			alwaysOps();
			ask("Enter 'c' to continue with your adventure.");
		} while(!in.equals("c"));
		//--------------------------------------------------------------------\\
		if(c == 0) {
			forestFight(0);
		} else if(c==1){
			fieldFight(1);
		} else if(c==2){
			waterFight(gen.nextInt(4)+1);
		}
		do {
			alwaysOps();
			ask("Enter 'c' to continue with your adventure.");
		} while(!in.equals("c"));
		//--------------------------------------------------------------------\\
		if(c == 1) {
			fieldFight(1);
		} else if(c==0) {
			say("You find a clearing in the forest!");
			forestFight(1);
		} else if(c==2){
			waterFight(gen.nextInt(4)+1);
		}
		intown = true;
	}
	public void quest(int c) {
		if(c==0)
			fightDragon();
		else if(c==1)
			fightGoblins();
		else if(c==2)
			fightChange();
	}

	
	//always ops\\
	public void execAlwaysOps() throws IOException {
		if(in.equalsIgnoreCase("t")) {
			tutorial();
		} else if(in.equalsIgnoreCase("q")) {
			if(es)
				say("Closing...");
			System.exit(0);
		} else if(in.equalsIgnoreCase("s")) {
			stats();
		} else if(in.equalsIgnoreCase("te")) {
			toggleES();
		} else if(in.equalsIgnoreCase("save")) {
			Save();
		} else if(in.equalsIgnoreCase("load")) {
			Load();
		} else if(in.equalsIgnoreCase("dl")) {
			dl();
		} else if(in.equalsIgnoreCase("v")) {
			view();
		} else if(in.equalsIgnoreCase("d")) {
			delete();
		} else {
			say("The chosen operation is not enabled.");
		}
	}
	public void alwaysOps() throws IOException {
		ask("Operations / Hotkeys " +
				  "\n====================" +
				  "\nTutorial / t" +
				  "\nView Stats / v" +
				  "\nImprove Stats / s" +
				  "\nToggle Explanations / te" +
				  "\nSave / save" +
				  "\nLoad / load" +
				  "\nQuit Game / q" +
				"\n\nChoose one:");
		execAlwaysOps();
	}
	public void toggleES() {
		if(es) {
			String tempStr;
			if(es)
				tempStr = "ON";
			else
				tempStr = "OFF";
			
			ask("Explanations:"
				   + "\n============="
				   + "\nExplanations, in this game, are those boxes that are kind"
				   + "\nof unecessary for pure gameplay. Like the introduction to"
				   + "\nTraining that pops up every time. Here, you can toggle"
				   + "\nthem off or on."
				   + "\n\nCurrently, Explanations are " + tempStr
				   + "\nInput 'Yes' to toggle.");
		} else {
			ask("Toggle Explanations:"
			   + "\n===================="
			   + "\nCurrent setting: " + es);
		}
		if(in.equalsIgnoreCase("yes") || in.equalsIgnoreCase("y"))
			es = !es;
	}
	public void usePots() {
		if(in.equalsIgnoreCase("regular")) {
			if(p.HP < p.tHP-10)
				p.reheal();
			else
				p.HP += 10;
			say("You have been healed to " + p.HP + "/" + p.tHP);
			p.pots[0]--;
		} else if(in.equalsIgnoreCase("super")) {
			if(p.HP < p.tHP-25)
				p.reheal();
			else
				p.HP += 25;
			say("You have been healed to " + p.HP + "/" + p.tHP);
			p.pots[1]--;
		} else if(in.equalsIgnoreCase("hyper")) {
			if(p.HP < p.tHP-75)
				p.reheal();
			else
				p.HP += 75;
			say("You have been healed to " + p.HP + "/" + p.tHP);
			p.pots[2]--;
		} else if(in.equalsIgnoreCase("full heal")) {
			rounds = 0;
		}
	}
	public void view() {
		calcTotals();
		DecimalFormat temp = new DecimalFormat("  (##0.00%)");
		say( "HP: " + p.HP + "/" + p.tHP +
										  "\nAttack: " + p.tAtt +
										  "\nDex: " + p.dex +
										"\n\nWeapon: " + p.weapon +
										  "\nArmor: " + p.armor +
										  "\nMoney: " + p.money +
										"\n\nLevel: " + p.level +
										  "\nExp: " + p.exp + "/" + p.level +
										  temp.format(1.0*p.exp/p.level));
	}
	public void stats() {
		calcTotals();
		
		statEditor();
		
		/*if(es) {
		say("This is where you increase your stats." +
				"\nYou get " + p.points + " points to distribute among your stats." +
				"\nAny  attempt to input more points than you have will result in an increase of 0.");
		}
		ask("You have " + p.points + " points:" +
			"\n====================\n" +
			"How many would you like to put in HP? (current total " + p.tHP + ")");
		if(pInt(in)<=p.points && pInt(in)>-1) {
			p.points -= pInt(in);
			p.bHP += 3*pInt(in);
			p.HP += pInt(in);
		}
		ask("You have " + p.points + " points:" +
			"\n====================\n" +
			"How many would you like to put in att? (currently " + p.att + ")");
		if(pInt(in)<=p.points && pInt(in)>-1) {
			p.points -= pInt(in);
			p.att += pInt(in);
		}
		ask("You have " + p.points + " points:" +
			"\n====================\n" +
			"How many would you like to put in dex? (currently " + p.dex + ")");
		if(pInt(in)<=p.points && pInt(in)>-1) {
			p.points -= pInt(in);
			p.dex += pInt(in);
		}
		
		say( "HP: " + p.HP + " / " + p.tHP +
										  "\nBase Att: " + p.att +
										  "\nBase Dex: " + p.dex);*/
		
		calcTotals();
	}
	public void tutorial() {
		say("You have three stats:\n" +
										   "HP: Your health\n" + 
										   "Att: Your damage and accuracy\n" +
										   "Dex: Your dodge (damage reduction).");
		say("Every monster has these three stats, too.");
		say("You get exp (which helps you level up) " +
									     "\nafter every battle, and at each level" +
										 "\nyou get more points to improve your stats.");
		say("You fight monsters by going on adventures." +
										 "\nAdventures consist of multiple monster fights." +
										 "\nQuests have far fewer fights, but they are" +
										 "\nmuch harder.");
		say("You can get money from various things, like" +
										 "\nkilling monsters. Go to the shop to buy better" +
										 "\nweapons and armor.");
		say("You can pull up this tutorial at any time with 'T'." +
									  	 "\n'Q' to quit.");
	}

	
	//developer's log\\
	public void dl() {
		/*say("Version 3.2. Changes implemented:"
				+ "\n- 'Cheat' file created."
				+ "\n- Dragon boss added. Recommended level: 35."
				+ "\n- New 'Magic' ability for player. It doesn't do anything yet."
				+ "\n=======Abusement Discourgement Changes======="
				+ "\n- Prisoner can only be fought until lv. 4. Prevents level spamming."
				+ "\n- Spiders can poison you after lv. 5. Adds danger to low-HP builds."
				+ "\n- Zombies can make you lose dex. Adds danger to dodging builds."
				+ "\n- Bandits occasionally will blow you up. Adds danger to low-HP builds.");*/
		/*say("Version 3.5 - Adventure Update" +
				"\nChanges Implemented:" +
				"\n====================" +
				"\n- Eliminated single encounters." +
				"\n- Added 'Adventures', multi-encounter fights." +
				"\n- Added exp system." +
				"\n- Added slight variability to monster levels." +
				"\n- Fixed glitches with HP and total HP." +
				"\n- Eliminated obsolete/unneeded commands." +
				"\n=======Abusement Discourgement Changes=======" +
				"\n- Prisoner can be fought whenever, but gives such low exp that " +
				"\nit is not a valid way to grind after the early levels.");*/
		/*say("Version 3.5.5"
				+ "\n- Bug Fixes"
				+ "\n- More bug fixes"
				+ "\n- More bug fixes"
				+ "\n- More bug fixes"
				+ "\n- Even more bug fixes"
				+ "\n- Bandits actually pick pockets"
				+ "\n- Added Quests"
				+ "\n- Added Changeling and Goblin Mob bosses"
				+ "\n- Fixed bug: Even if enemy misses, you can get status-ed.");*/
		/*say("Version 3.6:"
				+ "\n- Addition of miscellaneous methods to save space"
				+ " and improve readability."
				+ "\n- Added the water adventure"
				+ "\n- Overhaul of the entire Mob-spawning system in Adventures"
				+ " to improve scaling over levels."
				+ "\n- As a direct result of the above, mobs have more HP in general"
				+ "\n- Also, now if you die in the middle of an adventure, you actually die.");*/
		say("Version 4.1:"
				+ "\n - Things are now nonstatic!"
				+ "\n - As you can tell, in a frame!"
				+ "\n - Input get/transfer system simplified"
				+ "\n - Cut down on temporary variables"
				+ "\n - Fixed HP +/- glitch");
	}
	
	
	//methods for frame components\\
	public void updateLabels() {
		lstats[0].setText(p.HP+"");
		lstats[1].setText(p.att+"");
		lstats[2].setText(p.dex+"");
		for(JLabel jl: lstats)
			jl.setVisible(true);
	}
	public void updateLabel(int x) {
		if(x==0)
			lstats[x].setText(""+p.HP);
		if(x==1)
			lstats[x].setText(""+p.att);
		if(x==2)
			lstats[x].setText(""+p.dex);
		lstats[3].setText("Points remaining: " + p.points);
	}
	public void addButtons(JButton[] minuses, JButton[] pluses,JPanel jp) {
		JLabel[] labs = new JLabel[6];
		labs[0] = new JLabel("HP:    ");
		labs[1] = new JLabel("Str:   ");
		labs[2] = new JLabel("Dex:   ");
		lstats[0] = new JLabel();
		lstats[1] = new JLabel();
		lstats[2] = new JLabel();
		minuses[0] = new JButton("-");
		//---------------------------------------------------------------------------------------//
		minuses[0] = new JButton("-");
		minuses[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(p.HP > 3) {
					p.HP-=3;
					p.tHP -= 3;
					p.points++;
				}
				updateLabel(0);
			}
		});
		pluses[0] = new JButton("+");
		pluses[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(p.points > 0) {
					p.HP+=3;
					p.tHP += 3;
					p.points--;
				}
				updateLabel(0);
			}
		});

		minuses[1] = new JButton("-");
		minuses[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(p.att  > 0) {
					p.att--;
					p.points++;
				}
				updateLabel(1);
			}
		});
		pluses[1] = new JButton("+");
		pluses[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(p.points > 0) {
					p.att++;
					p.points--;
				}
				updateLabel(1);
			}
		});


		minuses[2] = new JButton("-");
		minuses[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(p.dex  > 0) {
					p.dex--;
					p.points++;
				}
				updateLabel(2);
			}
		});
		pluses[2] = new JButton("+");
		pluses[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(p.points > 0) {
					p.dex++;
					p.points--;
				}
				updateLabel(2);
			}
		});

		for(int x = 0; x < 3; x++) {
			jp.add(labs[x]);
			jp.add(minuses[x]);
			jp.add(lstats[x]);
			jp.add(pluses[x]);
			labs[x].setVisible(true);
			minuses[x].setVisible(true);
			lstats[x].setVisible(true);
			pluses[x].setVisible(true);
		}
		
		//---------------------------------------------------------------------------------------//
	}
	public JFrame makeFrame(String name) {
 		JFrame f = new JFrame(name);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return f;
 	}
	
	
	//random small methods\\
	public void calcTotals() {
		p.tAtt = p.wAtt+p.att+(p.abilities[0]+p.abilities[0]%2)/2;
		p.tDex = p.aDex+p.dex;
		p.tHP = p.bHP+p.abilities[2];
	}
	public void say(String i) {
		if(!f.isVisible())
			f.setVisible(true);
		if(!jta.isVisible())
			jta.setVisible(true);
		
		infield.setText("");
		resp = false;
		jta.setText(i);
		while(!resp) {sleep(1000);}
	}
	public void ask(String i) {
		if(!f.isVisible())
			f.setVisible(true);
		if(!jta.isVisible())
			jta.setVisible(true);
		
		infield.setText("[Reply Here]");
		resp = false;
		jta.setText(i);
		while(!resp) {sleep(1000);}
	}
	public void exp(int a) {
		if(a<=0)
			a = 1;
		p.exp += a;
		if(p.exp >= p.level) {
			p.exp -= p.level;
			levelup();
		}
	}
	public void levelup() {
		p.points += 3;
		p.level++;
		p.training = true;
		say( "You leveled up! Now level " + p.level +
				"\nUse 'Improve Stats' in the Adventure Options menu" +
				"\nto use your points.");
	}

	
	//=========================================\\
	//            Shortened Methods            \\
	//=========================================\\
	public static int round(double d) {
		return (int)((d - d%1 > 5) ? d + 1 - d%1 : d - d%1);
	}
	public static int pInt(String i) {
		try {
			return Integer.parseInt(i);
		} catch(NumberFormatException ex) {
			return 0;
		}
	}
	public static String d(int a, int d) {
		if(a>d)
			return (a-d)+"";
		else
			return "no";
	}
	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch(InterruptedException ie) {
			;
		} finally {}
	}
	
	
	//Save-Delete-Load Files\\
	public void delete() throws IOException {
		File bank = new File("saves.txt");
	    Scanner ba = new Scanner(bank);
		
		ask("Which file do you want to delete?");
		
		String names = "",temp;
		
		while(ba.hasNext()) {
			temp = ba.nextLine();
			if(!temp.equals(in)) {
				names += temp;
				names += "\n";
			}
		}
		say("Remaining Files:\n===============\n"+names);
		ba.close();
		
		PrintWriter o = new PrintWriter("saves.txt");
		o.print(names);
		o.close();
		
		File willBeDeleted = new File(in+".txt");
		willBeDeleted.delete();
	}
	public void Save() throws IOException {
		FileWriter savebank = new FileWriter("saves.txt",true);
        PrintWriter o = new PrintWriter(savebank);
        File bank = new File("saves.txt");
        Scanner ba = new Scanner(bank);
        boolean exists = false;
		
		ask("What do you want to name your save?");
		while(ba.hasNext()) {
			if(ba.nextLine().equals(in)) {
				say("That save already exists. Overwriting...");
				exists = true;
			}
		}
		PrintWriter saver = new PrintWriter(in+".txt");
		saver.println(p.bHP);
		saver.println(p.att);
		saver.println(p.dex);
		saver.println(p.wAtt);
		saver.println(p.aDex);
		saver.println(p.level);
		saver.println(p.exp);
		saver.println(p.points);
		saver.println(p.money);
		saver.println(p.weapon);
		saver.println(p.armor);
		saver.println(p.abilities[0]);
		saver.println(p.abilities[1]);
		saver.println(p.abilities[2]);
		saver.println(p.pots[0]);
		saver.println(p.pots[1]);
		saver.println(p.pots[2]);
		saver.println(p.training);
		if(!exists)
			o.println(in);
		saver.close();
		ba.close();
		o.close();
		o.flush();
		saver.flush();
		say( "Done!");
	}
	public void Load() throws IOException {
	     String files="";
	     File bank = new File("saves.txt");
	     Scanner ba = new Scanner(bank);
	     while(ba.hasNext()) {
	    	 files += ba.nextLine() + "\n";
	     }
		
		 ask("What is the file you want to load?" +
		 		"\n=============\n"+files);
		 File file = new File(in+".txt");
		 Scanner scan = new Scanner(file);
		 p.bHP = pInt(scan.nextLine());
		 p.att = pInt(scan.nextLine());
		 p.dex = pInt(scan.nextLine());
		 p.wAtt = pInt(scan.nextLine());
		 p.aDex = pInt(scan.nextLine());
		 p.level = pInt(scan.nextLine());
		 p.exp = pInt(scan.nextLine());
		 p.points = pInt(scan.nextLine());
		 p.money = pInt(scan.nextLine());
		 p.weapon = scan.nextLine();
		 p.armor = scan.nextLine();
		 p.abilities[0] = pInt(scan.nextLine());
		 p.abilities[1] = pInt(scan.nextLine());
		 p.abilities[2] = pInt(scan.nextLine());
		 p.pots[0] = pInt(scan.nextLine());
		 p.pots[1] = pInt(scan.nextLine());
		 p.pots[2] = pInt(scan.nextLine());
		 p.training = scan.nextBoolean();
		 calcTotals();
		 p.reheal();
		 scan.close();
		 ba.close();
		 say("Loaded!");
	 }

	public static void main(String[] args) throws IOException {
		GameFrame rpg = new GameFrame();
		rpg.Play();
	}
}
