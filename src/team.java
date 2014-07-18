import java.util.*;
import java.awt.*;

class Teammate
{
	String name = "lil-red-dragon";
	
	//int index; // index of teammate 0/1/2/3/4/5
	
	String attr = "R";
	String type = "D";
	int hp = 1, atk = 1, rcv = 1;
	
	Skill skill = new Skill();
	LeaderSkill leaderskill = new LeaderSkill();
	
	double get_base_damage(Board board, int attr_index)
	{
		int base_atk;
		if(attr_index == 0) base_atk = atk;
		else
		{
			if(attr.charAt(attr_index) == attr.charAt(0)) base_atk = atk / 10; // same sub-attr
			else base_atk = atk * 3 / 10; // different sub-attr
		}
		return base_atk * board.damage[attr.charAt(attr_index)];
	}
	double get_leader_multiplier(Team team, Board board)
	{
		double total_multiplier = 1.0;
		
		for(int t = 0; t < team.members.size(); t++)
		{
			LeaderSkill ls = team.members.get(t).leaderskill;
			double best_multiplier = 1.0;
			for(int o = 0; o < ls.options.size(); o++)
			{
				if(ls.options.get(o).meets_requirements(this, team, board))
					if(best_multiplier < ls.options.get(o).multiplier)
						best_multiplier = ls.options.get(o).multiplier;
			}
			total_multiplier *= best_multiplier;
		}
		//System.out.println("multiplier = " + total_multiplier);
		
		return total_multiplier;
	}
	int get_final_damage(Team team, Board board, Monster target, int attr_index)
	{
		int final_damage = (int)(get_base_damage(board, attr_index) * board.get_combo_multiplier() * get_leader_multiplier(team, board));
		
		if(final_damage == 0) return 0; // no orbs matched
		if(target == null) return final_damage; // no specified target, so return now
		
		AttrTable t = new AttrTable();
		if(t.effect(attr.charAt(attr_index), target.attr) == 1) final_damage *= 2;
		else if(t.effect(attr.charAt(attr_index), target.attr) == -1) final_damage /= 2;
		
		final_damage -= target.def;
		if(final_damage < 1) final_damage = 1;
		
		return final_damage;
	}
	
	void turn()
	{
		skill.turn();
	}

	void input(String path, Scanner sc, String indents, ArrayList<String> total_input)
	{
		String token;
		//Scanner sc = new Scanner(System.in);
		while(true)
		{
			System.out.print(path + " > ");
			token = sc.next();
			if(token.equals("end"))
			{
				total_input.add(indents + token);
				break;
			}
			
			else if(token.equals("name"))
			{
				System.out.print(path + "/" + token + " > ");
				name = sc.next();
				total_input.add(indents + token);
				total_input.add(indents + '\t' + name);
			}
			else if(token.equals("attr"))
			{
				System.out.print(path + "/" + token + " > ");
				attr = sc.next();
				total_input.add(indents + token);
				total_input.add(indents + '\t' + attr);
				attr = attr.replace('F', 'R'); // in case F/W entered by mistake
				attr = attr.replace('W', 'B');
			}
			else if(token.equals("type"))
			{
				System.out.print(path + "/" + token + " > ");
				type = sc.next();
				total_input.add(indents + token);
				total_input.add(indents + '\t' + type);
			}
			else if(token.equals("hp"))
			{
				System.out.print(path + "/" + token + " > ");
				hp = sc.nextInt();
				total_input.add(indents + token);
				total_input.add(indents + '\t' + hp);
			}
			else if(token.equals("atk"))
			{
				System.out.print(path + "/" + token + " > ");
				atk = sc.nextInt();
				total_input.add(indents + token);
				total_input.add(indents + '\t' + atk);
			}
			else if(token.equals("rcv"))
			{
				System.out.print(path + "/" + token + " > ");
				rcv = sc.nextInt();
				total_input.add(indents + token);
				total_input.add(indents + '\t' + rcv);
			}
			else if(token.equals("skill"))
			{
				total_input.add(indents + token);
				skill.input(path + "/skill", sc, indents + '\t', total_input);
			}
			else if(token.equals("leaderskill"))
			{
				total_input.add(indents + token);
				leaderskill.input(path + "/leaderskill", sc, indents + '\t', total_input);
			}
		}
	}
}

class Team
{
	ArrayList<Teammate> members = new ArrayList<Teammate>();
	
	int cur_hp = 1, max_hp = 1, rcv = 0;
	
	void lose_hp(int damage)
	{
		cur_hp -= damage;
		if(cur_hp < 0) cur_hp = 0;
	}
	
	double get_base_recover(Board board)
	{
		return rcv * board.damage['H'];
	}
	int get_final_recover(Board board)
	{
		return (int)(get_base_recover(board) * board.get_combo_multiplier());
	}
	
	void recover_hp(int recovered)
	{
		cur_hp += recovered;
		if(cur_hp > max_hp) cur_hp = max_hp;
	}
	
	Team()
	{

	}

	void input(String path, Scanner sc, String indents, ArrayList<String> total_input)
	{
		String token;
		//Scanner sc = new Scanner(System.in);
		while(true)
		{
			System.out.print(path + " > ");
			token = sc.next();
			if(token.equals("end"))
			{
				total_input.add(indents + token);
				break;
			}
			
			else if(token.equals("members"))
			{
				total_input.add(indents + token);
				Teammate t = new Teammate();
				t.input(path + "/members[" + members.size() + "]", sc, indents + '\t', total_input);
				members.add(t);
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
		ArrayList<String> total_input = new ArrayList<String>();
		T.input("team", sc, "", total_input);
	}
}