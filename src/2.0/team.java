import java.util.*;
import java.awt.*;

class Skill
{
	String name = "heat-ball";
	
	int atk_damage = 0; // normal attack damage
	int fixed_damage = 0; // ignore-defense damage (i.e. Ra)
	char atk_attr = 'R';
	double gravity_damage = 0; // damage equal to X * opponent's hp
	boolean attack_all = false; // attack everyone? TODO some attacks only damage certain attributes
	
	String orb_change_from = ""; // "array" of orb colors to change from
	String orb_change_to = ""; // "array" of orb colors to change to (same order as from)
	// TODO add orb-enhance, attribute burst (i.e. I&I), type burst (i.e. King Baddie)
	
	int cur_cd = 1, max_cd = 1; // cooldown
	
	Skill()
	{
		
	}
	
	void activate(Monster[] monsters, int target_index) // target_index is ignored if attack_all
	{
		if(cur_cd > 0) return; // not charged up yet; can't activate
		cur_cd = max_cd;
		for(int i = 0; i < monsters.length; i++)
		{
			if(attack_all || i == target_index)
			{
				monsters[i].defend_atk(atk_attr, atk_damage);
				monsters[i].defend_fixed(fixed_damage);
				monsters[i].defend_gravity(gravity_damage);
			}
		}
	}
	
	void turn()
	{
		if(cur_cd > 0) cur_cd -= 1;
	}
	
	void input(String path, Scanner sc)
	{
		String token;
		//Scanner sc = new Scanner(System.in);
		while(true)
		{
			System.out.print(path + " > ");
			token = sc.next();
			if(token.equals("end")) break;
			
			if(token.equals("name"))
			{
				System.out.print(path + "/" + token + " > ");
				name = sc.next();
			}
			if(token.equals("atk_damage"))
			{
				System.out.print(path + "/" + token + " > ");
				atk_damage = sc.nextInt();
			}
			if(token.equals("fixed_damage"))
			{
				System.out.print(path + "/" + token + " > ");
				fixed_damage = sc.nextInt();
			}
			if(token.equals("atk_attr"))
			{
				System.out.print(path + "/" + token + " > ");
				atk_attr = sc.next().charAt(0);
			}
			if(token.equals("gravity_damage"))
			{
				System.out.print(path + "/" + token + " > ");
				gravity_damage = sc.nextDouble();
			}
			if(token.equals("attack_all"))
			{
				System.out.print(path + "/" + token + " > ");
				attack_all = sc.next().equals("true");
			}
			if(token.equals("orb_change_from"))
			{
				System.out.print(path + "/" + token + " > ");
				orb_change_from = sc.next();
			}
			if(token.equals("orb_change_to"))
			{
				System.out.print(path + "/" + token + " > ");
				orb_change_to = sc.next();
			}
			if(token.equals("max_cd") || token.equals("cd"))
			{
				System.out.print(path + "/" + token + " > ");
				cur_cd = max_cd = sc.nextInt();
			}
		}
	}
}

class Teammate
{
	String name = "lil-red-dragon";
	
	int index; // index of teammate 0/1/2/3/4/5
	
	char attr = 'R';
	int hp = 1, atk = 1, rcv = 1;
	
	Skill skill = new Skill();
	
	void attack(double multiplier, Monster attacked)
	{
		attacked.defend_atk(attr, (int)(multiplier * atk));
	}
	
	void turn()
	{
		skill.turn();
	}
	
	void input(String path, Scanner sc)
	{
		String token;
		//Scanner sc = new Scanner(System.in);
		while(true)
		{
			System.out.print(path + " > ");
			token = sc.next();
			if(token.equals("end")) break;
			
			if(token.equals("name"))
			{
				System.out.print(path + "/" + token + " > ");
				name = sc.next();
			}
			if(token.equals("attr"))
			{
				System.out.print(path + "/" + token + " > ");
				attr = sc.next().charAt(0);
			}
			if(token.equals("hp"))
			{
				System.out.print(path + "/" + token + " > ");
				hp = sc.nextInt();
			}
			if(token.equals("atk"))
			{
				System.out.print(path + "/" + token + " > ");
				atk = sc.nextInt();
			}
			if(token.equals("rcv"))
			{
				System.out.print(path + "/" + token + " > ");
				rcv = sc.nextInt();
			}
			if(token.equals("skill"))
			{
				skill.input(path + "/skill", sc);
			}
		}
	}
}

class Team
{
	ArrayList<Teammate> members = new ArrayList<Teammate>();
	double[] multipliers = new double[10];
	// TODO add multipliers
	
	int cur_hp = 1, max_hp = 1, rcv = 0;
	
	void lose_hp(int damage)
	{
		cur_hp -= damage;
		if(cur_hp < 0) cur_hp = 0;
	}
	
	void recover_hp(int recovered)
	{
		cur_hp += recovered;
		if(cur_hp > max_hp) cur_hp = max_hp;
	}
	
	Team()
	{
		for(int i = 0; i < multipliers.length; i++)
			multipliers[i] = 1.0;
	}
	
	void input(String path, Scanner sc)
	{
		String token;
		//Scanner sc = new Scanner(System.in);
		while(true)
		{
			System.out.print(path + " > ");
			token = sc.next();
			if(token.equals("end")) break;
			
			if(token.equals("members"))
			{
				Teammate t = new Teammate();
				t.input(path + "/members[" + members.size() + "]", sc);
				members.add(t);
			}
			if(token.equals("multipliers"))
			{
				System.out.print(path + "/" + token + " > ");
				double multiplier = sc.nextDouble();
				String apply_to = sc.next(); // which teammates to apply to
				for(int i = 0; i < apply_to.length(); i++)
				{
					multipliers[apply_to.charAt(i)] *= multiplier;
				}
			}
		}
		
		max_hp = 0;
		for(int i = 0; i < members.size(); i++) max_hp += members.get(i).hp;
		cur_hp = max_hp;
		
		rcv = 0;
		for(int i = 0; i < members.size(); i++) rcv += members.get(i).rcv;
		if(rcv < 0) rcv = 0; // in case negative rcv
	}
}

public class team
{
	public static void main(String[] args)
	{
		Team T = new Team();
		Scanner sc = new Scanner(System.in);
		T.input("team", sc);
	}
}