import java.util.*;
import java.awt.*;

class Monster
{
	String name = "lil-red-dragon";
	
	char attr = 'R'; // attribute R/B/G/L/D
	int cur_hp = 1, max_hp = 1;
	int atk = 1, def = 0;
	
	int cur_cd = 1, max_cd = 1; // cooldown
	
	String status = "none"; // "none"/"poison"/"delay"
	int status_value = 0; // poison/delay value
	
	Monster()
	{
		
	}
	
	void attack(Team team) // attacks team
	{
		team.lose_hp(atk);
	}
	
	void lose_hp(int damage)
	{
		cur_hp -= damage;
		if(cur_hp < 0) cur_hp = 0;
	}
	
	void defend_atk(char atk_attr, int atk_damage) // attacked by team member
	{
		switch(atk_attr)
		{
		case 'R':
			if(attr == 'G') atk_damage *= 2;
			if(attr == 'B') atk_damage /= 2;
			break;
		case 'G':
			if(attr == 'B') atk_damage *= 2;
			if(attr == 'R') atk_damage /= 2;
			break;
		case 'B':
			if(attr == 'R') atk_damage *= 2;
			if(attr == 'G') atk_damage /= 2;
			break;
		case 'L':
			if(attr == 'D') atk_damage *= 2;
			break;
		case 'D':
			if(attr == 'L') atk_damage *= 2;
			break;
		}
		
		atk_damage -= def;
		if(atk_damage < 1) atk_damage = 1;
		lose_hp(atk_damage);
	}
	
	void defend_gravity(double gravity_value)
	{
		// NOTE: rounds down
		// NOTE: overflow when hp > 20 million
		cur_hp = (int)(cur_hp * (1.0 - gravity_value));
	}
	
	void defend_fixed(int atk_damage) // defend against fixed damage
	{
		lose_hp(atk_damage);
	}
	
	void turn(Team team) // pass a turn
	{
		if(cur_hp <= 0) return; // dead
		
		if(cur_cd == 1)
		{
			attack(team);
			cur_cd = max_cd;
			if(status == "delay") status = "none";
		}
		else cur_cd--;
		
		if(status == "poison")
		{
			if(status_value - def > 0)
				lose_hp(status_value - def);
		}
	}
	
	void input(String path, Scanner sc)
	{
		String token;
		//Scanner sc = new Scanner(System.in);
		while(true)
		{
			System.out.print(path + " > ");
			token = sc.next();
			//System.out.println("you entered " + token );
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
			if(token.equals("max_hp") || token.equals("hp"))
			{
				System.out.print(path + "/" + token + " > ");
				cur_hp = max_hp = sc.nextInt();
			}
			if(token.equals("atk"))
			{
				System.out.print(path + "/" + token + " > ");
				atk = sc.nextInt();
			}
			if(token.equals("def"))
			{
				System.out.print(path + "/" + token + " > ");
				def = sc.nextInt();
			}
			if(token.equals("max_cd") || token.equals("cd"))
			{
				System.out.print(path + "/" + token + " > ");
				cur_cd = max_cd = sc.nextInt();
			}
		}
	}
}
