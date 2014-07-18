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
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;

public class game extends JPanel
{
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
			board_timer = new Timer(500, process_board_al);
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
				if(busy) return;
				if(!started) return; // if time up before you released mouse
				//System.out.println("you released");
				cur_board.reset();
				busy = true;
				board_timer.stop();
				board_timer = new Timer(500, process_board_al);
				board_timer.start();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent e)
			{
				if(busy) return;
				if(!started)
				{
					//System.out.println("started!");
					started = true;
					board_timer = new Timer(4000, board_time_limit);
					board_timer.start();
					cur_board.cur_r = e.getY() / 100;
					cur_board.cur_c = e.getX() / 100;
					repaint();
				}
				int target_r = e.getY() / 100, target_c = e.getX() / 100;
				cur_board.move_to(target_r, target_c);
				repaint();
			}
		});
	}
	
	static Color[] color_table = new Color[256]; // needs initializing in paintComponent
	boolean color_table_initialized = false;
	@Override
	public void paintComponent(Graphics G)
	{
		//super.paintComponent(G);
		if(!color_table_initialized)
		{
			// initialize colors
			color_table['R'] = new Color(160, 0, 0);
			color_table['G'] = Color.blue;
			color_table['B'] = new Color(0, 160, 0);
			color_table['L'] = new Color(160, 160, 0);
			color_table['D'] = new Color(128, 0, 128);
			color_table['H'] = Color.magenta;
			color_table['.'] = Color.white;
			color_table_initialized = true;
		}
		
		//System.out.println("painting...");
		for(int r = 0; r < 5; r++)
			for(int c = 0; c < 6; c++)
			{
				G.setColor(color_table[cur_board.board[r][c].charAt(0)]);
				G.fillOval(c * 100, r * 100, 100, 100);
				if(r == cur_board.cur_r && c == cur_board.cur_c)
				{
					Graphics2D G2 = (Graphics2D)(G);
					G2.setColor(Color.black);
					G2.setStroke(new BasicStroke(10));
					G2.drawOval(c * 100 + 6, r * 100 + 6, 88, 88);
				}
			}
	}
	
	void turn()
	{
		if(!cur_state.turn(sc_std)) System.exit(0);
		cur_state.display();
	}
	
	public static void main(String[] args)
	{
		cur_state.input("~", sc_std);
		
		// display
		
		JFrame game = new JFrame();
	    game.setTitle("Orb board");
	    game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    game.setSize(600, 500);
	    game.setResizable(false);

	    game.add(new game());

	    game.setLocationRelativeTo(null);
	    game.setVisible(true);
	    
		//JFrame board = new JFrame();
		//board.add(new board());
		//board.setVisible(true);
	}
}