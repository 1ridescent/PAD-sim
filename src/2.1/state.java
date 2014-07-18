import java.util.*;
import java.awt.*;

class State
{
	ArrayList<Monster> monsters = new ArrayList<Monster>();
	Team team = new Team();
	Board board = new Board();
	boolean turn(Scanner sc) // return false if floor is finished or team died
	{
		//board.process_path(sc.nextInt(), sc.nextInt(), sc.next()); // orb movement is by mouse now
		
		Table attack_table = new Table();
		Collections.addAll(attack_table.columns, "attacker", "defender", "base damage", "final damage");

		double combo_multiplier = 0.25 * (board.num_combos + 3);
		for(int i = 0; i < team.members.size() && monsters.size() > 0; i++)
		{
			Teammate t = team.members.get(i);
			Monster m = monsters.get(0);
			
			double base_damage = t.atk * board.damage[t.attr];
			int final_damage = (int)(base_damage * combo_multiplier);
			
			//System.out.println("teammate=" + t.name + " attacks monster=" + m.name + " for " + (int)(base_damage) + " -> " + final_damage + " damage!");
			ArrayList<String> row = new ArrayList<String>();
			Collections.addAll(row, t.name, m.name, Double.toString(base_damage), Integer.toString(final_damage));
			attack_table.add_row(row);
			
			m.lose_hp(final_damage);
			if(m.cur_hp <= 0)
			{
				System.out.println("monster " + m.name + " was defeated!");
				monsters.remove(0);
			}
		}
		System.out.println(attack_table.display());
		
		double base_recover = team.rcv * board.damage['H'];
		int final_recover = (int)(base_recover * combo_multiplier);
		//System.out.println("team recovers " + (int)(base_recover) + " -> " + final_recover + " hp!");
		Table recover_table = new Table();
		Collections.addAll(recover_table.columns, "base recovery", "final recovery");
		ArrayList<String> recover_row = new ArrayList<String>();
		Collections.addAll(recover_row, Double.toString(base_recover), Integer.toString(final_recover));
		recover_table.add_row(recover_row);
		team.recover_hp(final_recover);
		System.out.println(recover_table.display());
		//System.out.println();
		
		if(monsters.size() == 0)
		{
			System.out.println("Floor cleared!");
			return false;
		}
		
		Table defend_table = new Table();
		Collections.addAll(defend_table.columns, "monster", "damage");
		for(int i = 0; i < monsters.size(); i++)
		{
			Monster m = monsters.get(0);
			if(m.cur_cd == 1)
			{
				//System.out.println("monster=" + m.name + " attacks for " + m.atk + " damage!");
				ArrayList<String> row = new ArrayList<String>();
				Collections.addAll(row, m.name, Integer.toString(m.atk));
				defend_table.add_row(row);
			}
			m.turn(team);
		}
		System.out.println(defend_table.display());
		
		if(team.cur_hp <= 0)
		{
			System.out.println("Game over!");
			return false;
		}
		
		for(int i = 0; i < team.members.size(); i++)
		{
			team.members.get(i).turn();
		}
		
		return true;
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
			if(token.equals("monsters"))
			{
				Monster m = new Monster();
				m.input(path + "/monsters[" + monsters.size() + "]", sc);
				monsters.add(m);
			}
			if(token.equals("team")) team.input(path + "/team", sc);
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
		Table monster_table = new Table();
		Collections.addAll(monster_table.columns, "monster", "attr", "cd", "hp%");
		for(int i = 0; i < monsters.size(); i++)
		{
			Monster m = monsters.get(i);
			ArrayList<String> row = new ArrayList<String>();
			Collections.addAll(row, m.name, Character.toString(m.attr), Integer.toString(m.cur_cd), Double.toString(m.cur_hp * 100.0 / m.max_hp));
			monster_table.add_row(row);
		}
		System.out.println("Monsters:\n" + monster_table.display());
		
		Table team_table = new Table();
		Collections.addAll(team_table.columns, "name", "attr", "skill cd");
		for(int i = 0; i < team.members.size(); i++)
		{
			Teammate t = team.members.get(i);
			ArrayList<String> row = new ArrayList<String>();
			Collections.addAll(row, t.name, Character.toString(t.attr), Integer.toString(t.skill.cur_cd));
			team_table.add_row(row);
		}
		System.out.println("Team:\n" + team_table.display());
		
		System.out.println("Team hp: " + team.cur_hp + "/" + team.max_hp);
	}
}

public class state
{
	public static void main(String[] args)
	{
		State S = new State();
		Scanner sc = new Scanner(System.in);
		
		S.input("state", sc);
		do
		{
			S.display();
			System.out.println();
		}
		while(S.turn(sc));
	}
}