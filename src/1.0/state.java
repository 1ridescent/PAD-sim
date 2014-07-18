import java.util.*;
import java.awt.*;

class State
{
	ArrayList<Monster> monsters = new ArrayList<Monster>();
	Team team = new Team();
	Board board = new Board();
	boolean turn(Scanner sc) // return false if floor is finished or team died
	{
		board.process_path(sc.nextInt(), sc.nextInt(), sc.next());

		double combo_multiplier = 0.25 * (board.num_combos + 3);
		for(int i = 0; i < team.members.size() && monsters.size() > 0; i++)
		{
			Teammate t = team.members.get(i);
			Monster m = monsters.get(0);
			
			double base_damage = t.atk * board.damage[Character.getNumericValue(t.attr)];
			int damage = (int)(base_damage * combo_multiplier);
			
			System.out.println("teammate=" + t.name + " attacks monster=" + m.name + " for " + (int)(base_damage) + " -> " + damage + " damage!");
			m.lose_hp(damage);
			if(m.cur_hp <= 0)
			{
				System.out.println("monster=" + m.name + " was defeated!");
				monsters.remove(0);
			}
		}
		
		double base_recover = team.rcv * board.damage[Character.getNumericValue('H')];
		int recover = (int)(base_recover * combo_multiplier);
		System.out.println("team recovers " + (int)(base_recover) + " -> " + recover + " hp!");
		team.recover_hp(recover);
		System.out.println();
		
		if(monsters.size() == 0)
		{
			System.out.println("Floor cleared!");
			return false;
		}
		
		for(int i = 0; i < monsters.size(); i++)
		{
			Monster m = monsters.get(0);
			if(m.cur_cd == 1)
			{
				System.out.println("monster=" + m.name + " attacks for " + m.atk + " damage!");
			}
			m.turn(team);
		}
		
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
	void display()
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