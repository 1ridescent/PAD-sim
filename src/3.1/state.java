import java.util.*;
import java.awt.*;

class State
{
	Dungeon dungeon = new Dungeon();
	Floor floor; // must call dungeon.next_floor()
	Team team = new Team();
	Board board = new Board();
	
	void attack()
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
	
	void recover()
	{
		double base_recover = team.rcv * board.damage['H'];
		int final_recover = (int)(base_recover * board.get_combo_multiplier());
		System.out.println("Team recovers " + (int)(base_recover) + " -> " + final_recover + " hp!");
		team.recover_hp(final_recover);
	}
	
	void defend()
	{
		for(int i = 0; i < floor.monsters.size(); i++)
		{
			Monster m = floor.monsters.get(i);
			if(m.cur_hp <= 0) continue; // dead
			
			if(m.cur_cd == 1)
				System.out.println("Enemy " + m.name + " attacks for " + m.atk + " damage!");
			m.turn(team);
		}
		//System.out.println("Defend:\n" + defend_table.display());
	}
	
	boolean check_cleared()
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
		//board.process_path(sc.nextInt(), sc.nextInt(), sc.next()); // orb movement is by mouse now
		
		attack();
		recover();
		if(!check_cleared()) // if not cleared, then monsters attack
		{
			defend();
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
			total_input.add(indents + token);
			//System.out.println("you entered " + token );
			if(token.equals("end")) break;
			
			else if(token.equals("dungeon"))
				dungeon.input(path + "/dungeon", sc, indents + '\t', total_input);
			else if(token.equals("team"))
				team.input(path + "/team", sc, indents + '\t', total_input);
			else total_input.remove(total_input.size() - 1); // not a valid token, so remove it from total_input
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