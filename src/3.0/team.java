import java.util.*;
import java.awt.*;

class Teammate
{
	String name = "lil-red-dragon";
	
	int index; // index of teammate 0/1/2/3/4/5
	
	char attr = 'R';
	char type = 'D';
	int hp = 1, atk = 1, rcv = 1;
	
	Skill skill = new Skill();
	LeaderSkill leaderskill = new LeaderSkill();
	
	double get_base_damage(Board board)
	{
		return atk * board.damage[attr];
	}
	double get_leader_multiplier(Team team, Board board)
	{
		double best_multiplier = 1.0;
		
		for(int t = 0; t < team.members.size(); t++)
		{
			LeaderSkill ls = team.members.get(t).leaderskill;
			for(int o = 0; o < ls.options.size(); o++)
			{
				if(ls.options.get(o).meets_requirements(this, team, board))
					if(best_multiplier < ls.options.get(o).multiplier)
						best_multiplier = ls.options.get(o).multiplier;
			}
		}
		
		return best_multiplier;
	}
	int get_final_damage(Team team, Board board, Monster target)
	{
		int final_damage = (int)(get_base_damage(board) * board.get_combo_multiplier() * get_leader_multiplier(team, board));
		
		if(final_damage == 0) return 0; // no orbs matched
		if(target == null) return final_damage; // no specified target, so return now
		
		AttrTable t = new AttrTable();
		if(t.effect(attr, target.attr) == 1) final_damage *= 2;
		else if(t.effect(attr, target.attr) == -1) final_damage /= 2;
		
		final_damage -= target.def;
		if(final_damage < 1) final_damage = 1;
		
		return final_damage;
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
			if(token.equals("type"))
			{
				System.out.print(path + "/" + token + " > ");
				type = sc.next().charAt(0);
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
			if(token.equals("leaderskill"))
			{
				leaderskill.input(path + "/leaderskill", sc);
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