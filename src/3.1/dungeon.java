import java.util.*;

class Floor
{
	String name = "untitled";
	ArrayList<Monster> monsters = new ArrayList<Monster>();
	int target = -1;
	
	boolean is_clear()
	{
		for(int i = 0; i < monsters.size(); i++)
		{
			Monster m = monsters.get(i);
			if(m.cur_hp > 0) return false;
		}
		return true;
	}
	
	int choose_target(char atk_attr) // (auto)chooses target monster to attack, or -1 if all enemies are dead
	{
		// if manual target, then select that monster
		if(target != -1 && monsters.get(target).cur_hp > 0)
			return target;
		
		// algorithm: prioritizes effectiveness, then on low cur_hp
		AttrTable t = new AttrTable();
		
		int target_id = -1, target_effect = -2, target_hp = 0;
		for(int i = 0; i < monsters.size(); i++)
		{
			Monster m = monsters.get(i);
			if(m.cur_hp <= 0) continue;
			if((t.effect(atk_attr, m.attr) > target_effect)
					|| (t.effect(atk_attr, m.attr) == target_effect && m.cur_hp < target_hp))
			{
				target_id = i;
				target_effect = t.effect(atk_attr, m.attr);
				target_hp = m.cur_hp;
			}
		}
		
		return target_id;
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
			else if(token.equals("monsters"))
			{
				Monster m = new Monster();
				m.input(path + "/monsters[" + monsters.size() + "]", sc, indents + '\t', total_input);
				monsters.add(m);
			}
			else total_input.remove(total_input.size() - 1); // not a valid token, so remove it from total_input
		}
	}
}

class Dungeon
{
	String name = "untitled";
	ArrayList<Floor> floors = new ArrayList<Floor>();
	int cur_floor_id = -1;
	
	Floor next_floor() // returns null if dungeon is finished
	{
		cur_floor_id++;
		if(cur_floor_id == floors.size()) return null;
		return floors.get(cur_floor_id);
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
			else if(token.equals("floors"))
			{
				Floor f = new Floor();
				f.input(path + "/floors[" + floors.size() + "]", sc, indents + '\t', total_input);
				floors.add(f);
			}
			else total_input.remove(total_input.size() - 1); // not a valid token, so remove it from total_input
		}
	}
}