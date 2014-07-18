import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;

public class game extends JPanel
{
	// constants
	static int WINDOW_WIDTH = 600, WINDOW_HEIGHT = 720;
	static int MONSTERS_STARTX = 0, MONSTERS_STARTY = 0, MONSTER_SIZE = 100;
	static int ORB_STARTX = 0, ORB_STARTY = 100, ORB_SIZE = 100;
	static int TEAM_STARTX = 0, TEAM_STARTY = 600, TEAMMATE_SIZE = 100;
	static int NEWLINE_SIZE = 20;
	static int TEAM_HP_STARTX = 0, TEAM_HP_STARTY = 700;
	
	static int MOVEMENT_TIME = 4000; // in milliseconds
	static int PAUSE_TIME = 500; // pauses during orb board processing
	
	static Scanner sc_std = new Scanner(System.in);
	
	static State cur_state = new State();
	
	static Board cur_board = cur_state.board;
	static boolean started = false;
	static boolean busy = false;
	static Timer board_timer;

	ActionListener process_board_al = new ActionListener()
	{
		public void actionPerformed(ActionEvent ae)
		{
			if(!cur_board.select_one_match())
			{
				cur_board.collapse();
				if(!cur_board.refresh())
				{
					busy = false;
					started = false;
					board_timer.stop();
					turn();
				}
			}
			else
				cur_board.clear_matches();
			repaint();
		}
	};
	ActionListener board_time_limit = new ActionListener()
	{
		public void actionPerformed(ActionEvent ae)
		{
			//System.out.println("time up!");
			cur_board.reset();
			busy = true;
			board_timer.stop();
			board_timer = new Timer(PAUSE_TIME, process_board_al);
			board_timer.start();
		}
	};
	
	public game()
	{
		setFocusable(true);
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				if(busy) return; // if time up before you released mouse
				cur_board.reset();
				if(!started) return;
				//System.out.println("you released");
				busy = true;
				board_timer.stop();
				board_timer = new Timer(PAUSE_TIME, process_board_al);
				board_timer.start();
			}
			@Override
			public void mouseClicked(MouseEvent e)
			{
				// note: no check for busy, since you can target even while orbs are processing
				if(0 <= e.getX() - MONSTERS_STARTX && e.getX() - MONSTERS_STARTX < cur_state.floor.monsters.size() * MONSTER_SIZE
						&& 0 <= e.getY() - MONSTERS_STARTY && e.getY() - MONSTERS_STARTY < MONSTER_SIZE)
				{
					int i = (e.getX() - MONSTERS_STARTX) / MONSTER_SIZE;
					if(cur_state.floor.target == i)
						cur_state.floor.target = -1;
					else
						cur_state.floor.target = i;
					repaint();
				}
				
				if(!busy && !started // started can't really happen, but just to be safe...
						&& 0 <= e.getX() - TEAM_STARTX && e.getX() - TEAM_STARTX < cur_state.team.members.size() * TEAMMATE_SIZE
						&& 0 <= e.getY() - TEAM_STARTY && e.getY() - TEAM_STARTY < TEAMMATE_SIZE)
				{ // clicked a teammate skill
					int i = (e.getX() - TEAM_STARTX) / TEAMMATE_SIZE;
					//System.out.println("activating " + i);
					Teammate t = cur_state.team.members.get(i);
					t.skill.activate(cur_state);
					cur_state.check_cleared();
					repaint();
				}
				
			}
		});
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent e)
			{
				if(busy) return;
				if(!started && !(0 <= e.getY() - ORB_STARTY && e.getY() - ORB_STARTY < 5 * ORB_SIZE
						&& 0 <= e.getX() - ORB_STARTX && e.getX() - ORB_STARTX < 6 * ORB_SIZE))
					return; // don't start outside of board range

				int target_r = (e.getY() - ORB_STARTY) / ORB_SIZE, target_c = (e.getX() - ORB_STARTX) / ORB_SIZE;
				if(target_r < 0) target_r = 0;
				if(target_r > 4) target_r = 4;
				if(target_c < 0) target_c = 0;
				if(target_c > 5) target_c = 5;
				
				if(!started && cur_board.cur_r == -1 && cur_board.cur_c == -1)
				{
					cur_board.cur_r = target_r;
					cur_board.cur_c = target_c;
				}
				else if(!started && (cur_board.cur_r != target_r || cur_board.cur_c != target_c))
				{
					//System.out.println("started!");
					started = true;
					board_timer = new Timer(MOVEMENT_TIME, board_time_limit);
					board_timer.start();
				}
				
				cur_board.move_to(target_r, target_c);
				repaint();
			}
		});
	}
	
	static Color[] color_table = new Color[256]; // needs initializing in main
	@Override
	public void paintComponent(Graphics G)
	{
		//super.paintComponent(G);
		
		G.clearRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		G.setColor(Color.black);
		for(int i = 0; i < cur_state.floor.monsters.size(); i++)
		{
			Monster m = cur_state.floor.monsters.get(i);
			G.drawString(m.name, MONSTERS_STARTX + i * MONSTER_SIZE, MONSTERS_STARTY + NEWLINE_SIZE);
			G.drawString("hp%: " + Double.toString(m.cur_hp * 100.0 / m.max_hp), MONSTERS_STARTX + i * MONSTER_SIZE, MONSTERS_STARTY + 2 * NEWLINE_SIZE);
			G.drawString("cd: " + m.cur_cd, MONSTERS_STARTX + i * MONSTER_SIZE, MONSTERS_STARTY + 3 * NEWLINE_SIZE);
			if(i == cur_state.floor.target)
			{
				Graphics2D G2D = (Graphics2D)(G);
				G2D.setColor(Color.black);
				G2D.setStroke(new BasicStroke(10));
				G2D.drawRect(MONSTERS_STARTX + i * MONSTER_SIZE, MONSTERS_STARTY, MONSTER_SIZE, MONSTER_SIZE);
			}
		}
		
		//System.out.println("painting...");
		for(int r = 0; r < 5; r++)
			for(int c = 0; c < 6; c++)
			{
				G.setColor(color_table[cur_board.board[r][c].charAt(0)]);
				G.fillOval(ORB_STARTX + c * ORB_SIZE, ORB_STARTY + r * ORB_SIZE, ORB_SIZE, ORB_SIZE);
				if(r == cur_board.cur_r && c == cur_board.cur_c)
				{
					Graphics2D G2D = (Graphics2D)(G);
					G2D.setColor(Color.black);
					G2D.setStroke(new BasicStroke(10));
					G2D.drawOval(ORB_STARTX + c * ORB_SIZE, ORB_STARTY + r * ORB_SIZE, ORB_SIZE, ORB_SIZE);
					//G2D.setStroke(new BasicStroke(1)); // reset stroke
				}
			}
		
		G.setColor(Color.black);
		for(int i = 0; i < cur_state.team.members.size(); i++)
		{
			Teammate t = cur_state.team.members.get(i);
			//G.drawRect(i * 100, 500, 100, 100);
			G.drawString(t.name, TEAM_STARTX + i * TEAMMATE_SIZE, TEAM_STARTY + NEWLINE_SIZE);
			G.drawString(t.skill.name, TEAM_STARTX + i * TEAMMATE_SIZE, TEAM_STARTY + 2 * NEWLINE_SIZE);
			G.drawString("cd: " + Integer.toString(t.skill.cur_cd), TEAM_STARTX + i * TEAMMATE_SIZE, TEAM_STARTY + 3 * NEWLINE_SIZE);
			
			String damage_string = "atk: " + t.get_final_damage(cur_state.team, cur_state.board, null, 0);
			if(t.attr.length() > 1) // add subtype damage too
				damage_string += "/" + t.get_final_damage(cur_state.team, cur_state.board, null, 1);
			G.drawString(damage_string, TEAM_STARTX + i * TEAMMATE_SIZE, TEAM_STARTY + 4 * NEWLINE_SIZE);
		}
		
		G.drawString("Team hp: " + cur_state.team.cur_hp + "/" + cur_state.team.max_hp + " (recover " + cur_state.team.get_final_recover(cur_state.board) + ")", TEAM_HP_STARTX, TEAM_HP_STARTY);
	}
	
	void turn()
	{
		if(!cur_state.turn(sc_std)) System.exit(0);
		cur_state.display();
	}
	
	public static void main(String[] args)
	{
		// initialize color table
		color_table['R'] = new Color(160, 0, 0);
		color_table['B'] = Color.blue;
		color_table['G'] = new Color(0, 160, 0);
		color_table['L'] = new Color(160, 160, 0);
		color_table['D'] = new Color(128, 0, 128);
		color_table['H'] = Color.magenta;
		color_table['.'] = Color.white;

		ArrayList<String> total_input = new ArrayList<String>();
		cur_state.input("~", sc_std, "", total_input);
		System.out.println("\n\n\n***** Script of your input (feel free to copy and save) *****");
		for(int i = 0; i < total_input.size(); i++)
			System.out.println(total_input.get(i));
		System.out.print("\n\n\n");
		
		if(cur_state.team.members.size() == 0)
		{
			System.out.println("You do not have a team!");
			System.exit(0);
		}
		if(cur_state.dungeon.floors.size() == 0)
		{
			System.out.println("There are no floors in the dungeon!");
			System.exit(0);
		}
		
		cur_state.floor = cur_state.dungeon.next_floor(); // enter first floor
		
		// display
		
		JFrame game = new JFrame();
	    game.setTitle("Orb board");
	    game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    game.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	    game.setResizable(false);

	    game.add(new game());

	    game.setLocationRelativeTo(null);
	    game.setVisible(true);

	    /*while(true)
	    {
	    	System.out.println(sc_std.next());
	    }*/
		//JFrame board = new JFrame();
		//board.add(new board());
		//board.setVisible(true);
	}
}