import java.util.*;
import java.awt.*;

class State
{
	Dungeon dungeon = new Dungeon();
	Floor floor; // must call dungeon.next_floor()
	Team team = new Team();
	Board board = new Board();
	
	void team_attack()
	{
		for(int i = 0; i < team.members.size() && floor.monsters.size() > 0; i++)
		{
			Teammate t = team.members.get(i);
			for(int attr_index = 0; attr_index < t.attr.length(); attr_index++)
			{
				char attr = t.attr.charAt(attr_index);
				int monster_id = floor.choose_target(attr);
				if(monster_id == -1) break; // floor is finished
				
				for(int j = 0; j < floor.monsters.size(); j++)
				{
					Monster m = floor.monsters.get(j);
					if(m.cur_hp <= 0) continue;
					if(board.mass_attack[attr] || j == monster_id)
					{
						double base_damage = t.get_base_damage(board, attr_index);
						if(base_damage == 0) continue; // no attack
						int final_damage = t.get_final_damage(team, board, m, attr_index);
						
						System.out.println(t.name + " attacks enemy " + m.name + " for " + (int)(base_damage) + " -> " + final_damage + " damage!");
						
						m.lose_hp(final_damage);
						if(m.cur_hp <= 0)
							System.out.println("Monster " + m.name + " was defeated!");
					}
				}
			}
		}
	}
	
	void team_recover()
	{
		double base_recover = team.rcv * board.damage['H'];
		int final_recover = (int)(base_recover * board.get_combo_multiplier());
		System.out.println("Team recovers " + (int)(base_recover) + " -> " + final_recover + " hp!");
		team.recover_hp(final_recover);
	}
	
	void enemy_attack()
	{
		for(int i = 0; i < floor.monsters.size(); i++)
		{
			Monster m = floor.monsters.get(i);
			if(m.cur_hp <= 0) continue; // already dead
			
			if(m.poison > 0) // check poison
			{
				System.out.println("Monster " + m.name + " takes " + m.poison + " poison damage!");
				m.lose_hp(m.poison);
			}
			if(m.cur_hp <= 0)
			{
				System.out.println("Monster " + m.name + " was defeated!"); // died from poison
				continue;
			}
			
			if(m.cur_cd == 1) // attack team
			{
				System.out.println("Enemy " + m.name + " attacks for " + m.atk + " damage!");
				team.lose_hp(m.atk);
				m.cur_cd = m.max_cd;
			}
			else m.cur_cd--;
		}
		//System.out.println("Defend:\n" + defend_table.display());
	}

	void activate_skill(Teammate teammate)
	{
		Skill skill = teammate.skill;
		
		if(skill.cur_cd > 0) return; // not charged up yet; can't activate
		
		System.out.println("Activated " + teammate.name + "'s skill " + skill.name + "!");
		
		// attack monsters
		int target_index = floor.choose_target(skill.atk_attr);
		skill.cur_cd = skill.max_cd;
		for(int i = 0; i < floor.monsters.size(); i++)
		{
			Monster m = floor.monsters.get(i);
			if(skill.attack_all || i == target_index)
			{
				int skill_damage = skill.get_skill_damage(m);
				if(skill_damage > 0)
				{
					System.out.println(teammate.name + "'s skill " + skill.name + " attacks enemy " + m.name + " for " + skill_damage + " damage!");
					m.lose_hp(skill.get_skill_damage(m));
				}
			}
		}
		
		// enemy status-effects (delay/poison)
		for(int i = 0; i < floor.monsters.size(); i++)
		{
			Monster m = floor.monsters.get(i);
			if(skill.delay > 0 && m.delay == 0) // monster must not already be delayed
			{
				System.out.println(teammate.name + "'s skill " + skill.name + " delays enemy " + m.name + " for " + skill.delay + " turns!");
				m.delay = skill.delay;
				m.cur_cd += skill.delay;
			}
			if(skill.poison > 0)
			{
				System.out.println(teammate.name + "'s skill " + skill.name + " poisons enemy " + m.name + " for " + skill.poison + " damage every turn!");
				m.poison = skill.poison;
			}
		}
		
		// change orbs
		for(int i = 0; i < skill.orb_change_from.length(); i++)
		{
			char from = skill.orb_change_from.charAt(i);
			char to = skill.orb_change_to.charAt(i);
			System.out.println(teammate.name + "'s skill " + skill.name + " changes " + from + " orbs to " + to + " orbs!");
			for(int r = 0; r < 5; r++)
				for(int c = 0; c < 6; c++)
					board.board[r][c] = board.board[r][c].replace(from, to);
		}
	}
	
	boolean check_cleared() // if floor is cleared, then go to next floor and return true
	{
		if(floor.is_clear())
		{
			System.out.println("Floor " + floor.name + " cleared!");
			floor = dungeon.next_floor();
			if(floor == null)
			{
				System.out.println("Dungeon cleared!");
				System.exit(0);
			}
			System.out.println("Entering floor " + floor.name + "!");
			return true;
		}
		return false;
	}
	boolean turn(Scanner sc) // return false if floor is finished or team died
	{
		// first, attack enemies
		team_attack();
		team_recover();
		
		// if floor is not cleared, then enemies attack
		if(!check_cleared())
		{
			enemy_attack();
			if(team.cur_hp <= 0)
			{
				System.out.println("Game over!");
				return false;
			}
		}
		
		for(int i = 0; i < team.members.size(); i++)
		{
			team.members.get(i).turn();
		}
		
		check_cleared(); // if last enemy died from poison, then we must check here
		
		System.out.println(); // break-line in-between turns
		
		return true;
	}
	void input(String path, Scanner sc, String indents, ArrayList<String> total_input)
	{
		String token;
		//Scanner sc = new Scanner(System.in);
		while(true)
		{
			System.out.print(path + " > ");
			token = sc.next();
			//System.out.println("you entered " + token );
			if(token.equals("end"))
			{
				total_input.add(indents + token);
				break;
			}
			
			else if(token.equals("dungeon"))
			{
				total_input.add(indents + token);
				dungeon.input(path + "/dungeon", sc, indents + '\t', total_input);
			}
			else if(token.equals("team"))
			{
				total_input.add(indents + token);
				team.input(path + "/team", sc, indents + '\t', total_input);
			}
		}
	}
	/*void display()
	{
		for(int i = 0; i < monsters.size(); i++)
		{
			Monster m = monsters.get(i);
			System.out.println("monster name=" + m.name + " cd=" + m.cur_cd + " hp%=" + m.cur_hp * 100.0 / m.max_hp);
		}
		System.out.println();
		
		for(int i = 0; i < team.members.size(); i++)
		{
			Teammate t = team.members.get(i);
			System.out.println("teammate name=" + t.name + " skill-cd=" + t.skill.cur_cd);
		}
		System.out.println("team hp: " + team.cur_hp + "/" + team.max_hp);
		System.out.println();
		
		board.output();
		System.out.println();
	}*/
	void display()
	{
		/*Table monster_table = new Table();
		Collections.addAll(monster_table.columns, "monster", "attr", "cd", "hp%");
		for(int i = 0; i < floor.monsters.size(); i++)
		{
			Monster m = floor.monsters.get(i);
			ArrayList<String> row = new ArrayList<String>();
			Collections.addAll(row, m.name, Character.toString(m.attr), Integer.toString(m.cur_cd), Double.toString(m.cur_hp * 100.0 / m.max_hp));
			monster_table.add_row(row);
		}
		//System.out.println("Monsters:\n" + monster_table.display());
		
		Table team_table = new Table();
		Collections.addAll(team_table.columns, "name", "attr", "skill cd");
		for(int i = 0; i < team.members.size(); i++)
		{
			Teammate t = team.members.get(i);
			ArrayList<String> row = new ArrayList<String>();
			Collections.addAll(row, t.name, Character.toString(t.attr), Integer.toString(t.skill.cur_cd));
			team_table.add_row(row);
		}
		//System.out.println("Team:\n" + team_table.display() + "Team hp: " + team.cur_hp + "/" + team.max_hp + "\n");
		System.out.println();*/
	}
}

public class state
{
	public static void main(String[] args)
	{
		State S = new State();
		Scanner sc = new Scanner(System.in);
		ArrayList<String> total_input = new ArrayList<String>();
		
		S.input("state", sc, "", total_input);
		do
		{
			S.display();
			System.out.println();
		}
		while(S.turn(sc));
	}
}