import java.util.ArrayList;
import java.util.Scanner;

class Skill
{
	String name = "heat-ball";
	
	int atk_damage = 0; // normal attack damage
	int fixed_damage = 0; // ignore-element-and-defense damage (i.e. Ra)
	char atk_attr = 'R';
	int gravity_damage = 0; // damage equal to X% of opponent's hp
	boolean attack_all = false; // attack everyone? TODO some attacks only damage certain attributes
	
	String orb_change_from = ""; // "array" of orb colors to change from
	String orb_change_to = ""; // "array" of orb colors to change to (same order as from)
	// TODO add orb-enhance, attribute burst (i.e. I&I), type burst (i.e. King Baddie)
	
	int cur_cd = 1, max_cd = 1; // cooldown
	
	Skill()
	{
		
	}
	
	int get_skill_damage(Monster target)
	{
		int total_damage = 0;
		AttrTable t = new AttrTable();
		
		if(atk_damage > 0)
		{
			int final_damage = atk_damage;
			if(t.effect(atk_attr, target.attr) == 1) final_damage *= 2;
			else if(t.effect(atk_attr, target.attr) == -1) final_damage /= 2;
			
			final_damage -= target.def;
			if(final_damage < 1) final_damage = 1;
			
			total_damage += final_damage;
		}
		if(fixed_damage > 0)
		{
			total_damage += fixed_damage;
		}
		if(gravity_damage > 0)
		{
			total_damage += (long)(target.cur_hp) * gravity_damage / 100; 
		}
		
		return total_damage;
	}
	
	void activate(State state)
	{
		int target_index = state.floor.choose_target(atk_attr);
		if(cur_cd > 0) return; // not charged up yet; can't activate
		cur_cd = max_cd;
		for(int i = 0; i < state.floor.monsters.size(); i++)
		{
			Monster m = state.floor.monsters.get(i);
			if(attack_all || i == target_index)
			{
				m.lose_hp(get_skill_damage(m));
			}
		}
		
		for(int i = 0; i < orb_change_from.length(); i++)
		{
			char from = orb_change_from.charAt(i);
			char to = orb_change_to.charAt(i);
			for(int r = 0; r < 5; r++)
				for(int c = 0; c < 6; c++)
					state.board.board[r][c] = state.board.board[r][c].replace(from, to);
		}
	}
	
	void turn()
	{
		if(cur_cd > 0) cur_cd -= 1;
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
			if(token.equals("end")) break;
			
			else if(token.equals("name"))
			{
				System.out.print(path + "/" + token + " > ");
				name = sc.next();
				total_input.add(indents + '\t' + name);
			}
			else if(token.equals("atk_damage"))
			{
				System.out.print(path + "/" + token + " > ");
				atk_damage = sc.nextInt();
				total_input.add(indents + '\t' + atk_damage);
			}
			else if(token.equals("fixed_damage"))
			{
				System.out.print(path + "/" + token + " > ");
				fixed_damage = sc.nextInt();
				total_input.add(indents + '\t' + fixed_damage);
			}
			else if(token.equals("atk_attr"))
			{
				System.out.print(path + "/" + token + " > ");
				atk_attr = sc.next().charAt(0);
				total_input.add(indents + '\t' + atk_attr);
			}
			else if(token.equals("gravity_damage"))
			{
				System.out.print(path + "/" + token + " > ");
				gravity_damage = sc.nextInt();
				total_input.add(indents + '\t' + gravity_damage);
			}
			else if(token.equals("attack_all"))
			{
				System.out.print(path + "/" + token + " > ");
				attack_all = sc.next().equals("true");
				total_input.add(indents + '\t' + attack_all);
			}
			else if(token.equals("orb_change_from"))
			{
				System.out.print(path + "/" + token + " > ");
				orb_change_from = sc.next();
				total_input.add(indents + '\t' + orb_change_from);
			}
			else if(token.equals("orb_change_to"))
			{
				System.out.print(path + "/" + token + " > ");
				orb_change_to = sc.next();
				total_input.add(indents + '\t' + orb_change_to);
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

class LeaderSkillOption
{
	int num_combos = 0;
	String colors = "";
	
	String attrs = "RBGLD";
	String types = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // include them all for now... until a standard key for types is set
	
	double multiplier = 1.0;
	
	boolean meets_requirements(Teammate t, Team T, Board B) // need team to figure out which attack types are available
	{
		boolean meets_attr_or_type = false;
		for(int i = 0; i < attrs.length(); i++)
		{
			for(int a = 0; a < t.attr.length(); a++)
				if(t.attr.charAt(a) == attrs.charAt(i)) meets_attr_or_type = true;
		}
		for(int i = 0; i < types.length(); i++)
		{
			for(int j = 0; j < t.type.length(); j++)
			if(t.type.charAt(j) == types.charAt(i)) meets_attr_or_type = true;
		}
		
		boolean meets_combos = (B.num_combos >= num_combos);
		
		boolean meets_colors = true;
		for(int c = 0; c < colors.length(); c++)
		{
			boolean team_has_color = false;
			for(int i = 0; i < T.members.size(); i++)
			{
				for(int a = 0; a < t.attr.length(); a++)
					if(T.members.get(i).attr.charAt(a) == colors.charAt(c)) team_has_color = true;
			}
			
			boolean board_has_color = B.cleared[colors.charAt(c)];
			
			if(!(team_has_color && board_has_color)) meets_colors = false;
		}
		
		//System.out.println(meets_attr_or_type+"/"+meets_combos+"/"+meets_colors);
		return meets_attr_or_type && meets_combos && meets_colors;
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
			if(token.equals("end")) break;
			
			else if(token.equals("num_combos"))
			{
				System.out.print(path + "/" + token + " > ");
				num_combos = sc.nextInt();
				total_input.add(indents + '\t' + num_combos);
			}
			else if(token.equals("colors"))
			{
				System.out.print(path + "/" + token + " > ");
				colors = sc.next();
				total_input.add(indents + '\t' + colors);
			}
			else if(token.equals("attrs"))
			{
				System.out.print(path + "/" + token + " > ");
				attrs = sc.next();
				total_input.add(indents + '\t' + attrs);
			}
			else if(token.equals("types"))
			{
				System.out.print(path + "/" + token + " > ");
				types = sc.next();
				total_input.add(indents + '\t' + types);
			}
			else if(token.equals("multiplier"))
			{
				System.out.print(path + "/" + token + " > ");
				multiplier = sc.nextDouble();
				total_input.add(indents + '\t' + multiplier);
			}
			else total_input.remove(total_input.size() - 1); // not a valid token, so remove it from total_input
		}
	}
}

class LeaderSkill
{
	ArrayList<LeaderSkillOption> options = new ArrayList<LeaderSkillOption>();

	void input(String path, Scanner sc, String indents, ArrayList<String> total_input)
	{
		String token;
		//Scanner sc = new Scanner(System.in);
		while(true)
		{
			System.out.print(path + " > ");
			token = sc.next();
			total_input.add(indents + token);
			if(token.equals("end")) break;

			else if(token.equals("options"))
			{
				LeaderSkillOption o = new LeaderSkillOption();
				o.input(path + "/options[" + options.size() + "]", sc, indents + '\t', total_input);
				options.add(o);
			}
			else total_input.remove(total_input.size() - 1); // not a valid token, so remove it from total_input
		}
	}
}
