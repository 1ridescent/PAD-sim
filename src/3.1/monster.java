import java.util.*;
import java.awt.*;

class AttrTable
{
	int effect(char atk_attr, char def_attr)
	{
		switch(atk_attr)
		{
		case 'R':
			if(def_attr == 'G') return 1;
			if(def_attr == 'B') return -1;
			break;
		case 'G':
			if(def_attr == 'B') return 1;
			if(def_attr == 'R') return -1;
			break;
		case 'B':
			if(def_attr == 'R') return 1;
			if(def_attr == 'G') return -1;
			break;
		case 'L':
			if(def_attr == 'D') return 1;
			break;
		case 'D':
			if(def_attr == 'L') return 1;
			break;
		}
		return 0;
	}
}

class Monster
{
	String name = "flamie";
	
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

	void input(String path, Scanner sc, String indents, ArrayList<String> total_input)
	{
		String token;
		//Scanner sc = new Scanner(System.in);
		while(true)
		{
			System.out.print(path + " > ");
			token = sc.next();
			total_input.add(indents + token);
			//System.out.println("you entered " + token );
			if(token.equals("end")) break;
			
			else if(token.equals("name"))
			{
				System.out.print(path + "/" + token + " > ");
				name = sc.next();
				total_input.add(indents + '\t' + name);
			}
			else if(token.equals("attr"))
			{
				System.out.print(path + "/" + token + " > ");
				attr = sc.next().charAt(0);
				total_input.add(indents + '\t' + attr);
			}
			else if(token.equals("max_hp") || token.equals("hp"))
			{
				System.out.print(path + "/" + token + " > ");
				cur_hp = max_hp = sc.nextInt();
				total_input.add(indents + '\t' + max_hp);
			}
			else if(token.equals("atk"))
			{
				System.out.print(path + "/" + token + " > ");
				atk = sc.nextInt();
				total_input.add(indents + '\t' + atk);
			}
			else if(token.equals("def"))
			{
				System.out.print(path + "/" + token + " > ");
				def = sc.nextInt();
				total_input.add(indents + '\t' + def);
			}
			else if(token.equals("max_cd") || token.equals("cd"))
			{
				System.out.print(path + "/" + token + " > ");
				cur_cd = max_cd = sc.nextInt();
				total_input.add(indents + '\t' + max_cd);
			}
			else total_input.remove(total_input.size() - 1); // not a valid token, so remove it from total_input
		}
	}
}
