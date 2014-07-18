import java.util.*;
import java.awt.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Timer;


class Board
{
	Random rand = new Random();
	//int[] score_table = {0, 3, 8, 15, 24, 35};
	/*int index(int r, int c)
	{
		return r * 6 + c;
	}*/
	
	String colors = "RBGLDH";
	
	String[][] board = new String[5][6];
	int cur_r = 0, cur_c = 0;
	
	//int num_colors;
	boolean[] cleared = new boolean[256];
	
	int num_combos;
	int num_moves;
	//int score;
	
	double[] damage = new double[256];
	
	void reset()
	{
		for(int i = 0; i < 256; i++)
		{
			cleared[i] = false;
			damage[i] = 0.0;
		}
		num_combos = 0;
		//num_colors = 0;
		num_moves = 0;
	}
	
	Board()
	{
		do
		{
			for(int i = 0; i < 5; i++)
				for(int j = 0; j < 6; j++)
					board[i][j] = Character.toString(colors.charAt(rand.nextInt(colors.length())));
			//num_colors = 0;
		}
		while(find_matches());
		
		reset();
	}
	
	Board(Board copy)
	{
		board = copy.board.clone();
		cur_r = copy.cur_r;
		cur_c = copy.cur_c;
		//num_colors = copy.num_colors;
		cleared = copy.cleared.clone();
		num_moves = copy.num_moves;
	}
	
	/*void exclude(String S)
	{
		for(int i = 0; i < S.length(); i++)
			cleared[Character.getNumericValue(S.charAt(i))] = true;
	}*/
	
	/*int encode()
	{
		return String.copyValueOf(board).hashCode() + cur_r * 100000007 + cur_c * 10007;
	}*/
	
	void move(int dr, int dc)
	{
		if(!(0 <= cur_r + dr && cur_r + dr < 5 && 0 <= cur_c + dc && cur_c + dc < 6)) return;
		
		String temp = board[cur_r][cur_c];
		board[cur_r][cur_c] = board[cur_r + dr][cur_c + dc];
		board[cur_r + dr][cur_c + dc] = temp;
		cur_r += dr;
		cur_c += dc;
		num_moves++;
	}
	
	void move_to(int target_r, int target_c)
	{
		if(!(0 <= target_r && target_r < 5 && 0 <= target_c && target_c < 6)) return;

		String temp = board[cur_r][cur_c];
		board[cur_r][cur_c] = board[target_r][target_c];
		board[target_r][target_c] = temp;
		cur_r = target_r;
		cur_c = target_c;
		num_moves++;
	}
	
	/*void unmove(int dr, int dc)
	{
		char temp = board[cur_r][cur_c];
		board[cur_r][cur_c] = board[cur_r - dr][cur_c - dc];
		board[cur_r - dr][cur_c - dc] = temp;
		cur_r -= dr;
		cur_c -= dc;
		num_moves--;
	}*/
	
	boolean[][] match;
	boolean[][] visited;
	int flood_fill(int r, int c)
	{
		visited[r][c] = true;
		int found = 0;
		if(match[r][c]) found++;
		
		if(r > 0 && board[r - 1][c].equals(board[r][c]) && !visited[r - 1][c])
			found += flood_fill(r - 1, c);
		if(r < 4 && board[r + 1][c].equals(board[r][c]) && !visited[r + 1][c])
			found += flood_fill(r + 1, c);
		if(c > 0 && board[r][c - 1].equals(board[r][c]) && !visited[r][c - 1])
			found += flood_fill(r, c - 1);
		if(c < 5 && board[r][c + 1].equals(board[r][c]) && !visited[r][c + 1])
			found += flood_fill(r, c + 1);
		
		return found;
	}
	
	boolean find_matches()
	{
		match = new boolean[5][6];
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 6; j++)
				match[i][j] = false;
		
		// horizontal
		for(int i = 0; i < 5; i++)
		{
			for(int j = 1; j < 5; j++)
			{
				if(!board[i][j].equals(".") && board[i][j - 1].equals(board[i][j]) && board[i][j].equals(board[i][j + 1]))
					match[i][j - 1] = match[i][j] = match[i][j + 1] = true;
			}
		}
		// vertical
		for(int i = 1; i < 4; i++)
		{
			for(int j = 0; j < 6; j++)
			{
				if(!board[i][j].equals(".") && board[i - 1][j].equals(board[i][j]) && board[i][j].equals(board[i + 1][j]))
					match[i - 1][j] = match[i][j] = match[i + 1][j] = true;
			}
		}
		
		visited = new boolean[5][6];
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 6; j++)
				visited[i][j] = false;
		
		for(int r = 0; r < 5; r++)
			for(int c = 0; c < 6; c++)
			{
				if(!board[r][c].equals(".") && !visited[r][c])
				{
					int num_cleared = flood_fill(r, c);
					if(num_cleared > 0)
					{
						//System.out.println(board[r][c] + " ++");
						damage[board[r][c].charAt(0)] += 0.25 * (num_cleared + 1);
						num_combos++;
					}
				}
			}
		
		return clear_matches();
	}
	
	/*boolean exists_one_match()
	{
		// horizontal
		for(int i = 0; i < 5; i++)
		{
			for(int j = 1; j < 5; j++)
			{
				if(!board[i][j].equals(".") && board[i][j - 1].equals(board[i][j]) && board[i][j].equals(board[i][j + 1]))
					return true;
			}
		}
		// vertical
		for(int i = 1; i < 4; i++)
		{
			for(int j = 0; j < 6; j++)
			{
				if(!board[i][j].equals(".") && board[i - 1][j].equals(board[i][j]) && board[i][j].equals(board[i + 1][j]))
					return true;
			}
		}
		return false;
	}*/
	
	boolean select_one_match() // marks match[][], but does not process the match
	{
		boolean found_match = false;
		match = new boolean[5][6];
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 6; j++)
				match[i][j] = false;
		
		// horizontal
		for(int i = 0; i < 5; i++)
		{
			for(int j = 1; j < 5; j++)
			{
				if(!board[i][j].equals(".") && board[i][j - 1].equals(board[i][j]) && board[i][j].equals(board[i][j + 1]))
					match[i][j - 1] = match[i][j] = match[i][j + 1] = true;
			}
		}
		// vertical
		for(int i = 1; i < 4; i++)
		{
			for(int j = 0; j < 6; j++)
			{
				if(!board[i][j].equals(".") && board[i - 1][j].equals(board[i][j]) && board[i][j].equals(board[i + 1][j]))
					match[i - 1][j] = match[i][j] = match[i + 1][j] = true;
			}
		}
		
		visited = new boolean[5][6];
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 6; j++)
				visited[i][j] = false;
		
		for(int r = 0; r < 5; r++)
			for(int c = 0; c < 6; c++)
			{
				if(!board[r][c].equals(".") && !visited[r][c])
				{
					int num_cleared = flood_fill(r, c);
					if(num_cleared > 0)
					{
						//System.out.println(board[r][c] + " ++");
						damage[board[r][c].charAt(0)] += 0.25 * (num_cleared + 1);
						num_combos++;
						
						found_match = true;
						for(int i = 0; i < 5; i++)
							for(int j = 0; j < 6; j++)
								if(!visited[i][j])
									match[i][j] = false; // ignore other matches
					}
				}
			}
		
		return found_match;
	}
	
	boolean clear_matches()
	{
		boolean found_match = false;
		
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 6; j++)
				if(match[i][j])
				{
					if(!cleared[board[i][j].charAt(0)])
					{
						cleared[board[i][j].charAt(0)] = true;
						//num_colors++;
					}
					board[i][j] = ".";
					found_match = true;
				}
		
		return found_match;
	}
	
	void collapse()
	{
		for(int j = 0; j < 6; j++)
		{
			int next = 4;
			for(int i = 4; i >= 0; i--)
			{
				if(!board[i][j].equals("."))
				{
					board[next][j] = board[i][j];
					if(next != i) board[i][j] = ".";
					next--;
				}
			}
		}
	}
	
	boolean refresh()
	{
		boolean filled_empty = false;
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 6; j++)
				if(board[i][j].equals("."))
				{
					board[i][j] = Character.toString(colors.charAt(rand.nextInt(colors.length())));
					filled_empty = true;
				}
		return filled_empty;
	}
	
	void process()
	{
		reset();
		while(find_matches())
		{
			collapse();
			refresh(); // comment out if debugging
		}
	}
	
	/*void get_score()
	{
		num_combos = 0;
		num_colors = 0;
		Board test_board = new Board(this);
		test_board.process();
		num_combos = test_board.num_combos;
		num_colors = test_board.num_colors;
		int x = num_combos;
		score = score_table[num_colors] + x * (x + 7) / 2 - 2 * num_moves;
	}*/
	
	double get_combo_multiplier()
	{
		return 0.25 * (num_combos + 3);
	}
	
	void process_path(int start_r, int start_c, String path)
	{
		cur_r = start_r;
		cur_c = start_c;
		for(int i = 0; i < path.length(); i++)
		{
			switch(path.charAt(i))
			{
			case 'w':
				move(-1, 0);
				break;
			case 'a':
				move(0, -1);
				break;
			case 's':
				move(1, 0);
				break;
			case 'd':
				move(0, 1);
				break;
			}
		}
		//output();
		process();
	}
	void input()
	{
		Scanner sc = new Scanner(System.in);
		for(int i = 0; i < 5; i++)
		{
			String S = sc.next();
			for(int j = 0; j < 6; j++)
			{
				board[i][j] = Character.toString(S.charAt(j));
			}
		}
	}
	/*void output()
	{
		for(int i = 0; i < 5; i++)
		{
			for(int j = 0; j < 6; j++)
				System.out.print(board[i][j]);
			System.out.println();
		}
	}*/
	void pretty_output()
	{
		System.out.println(" 012345");
		for(int i = 0; i < 5; i++)
		{
			System.out.print(i);
			for(int j = 0; j < 6; j++)
			{
				switch(board[i][j].charAt(0))
				{
				case 'R':
					System.out.print("\033[1;31m0\033[0m");
					break;
				case 'B':
					System.out.print("\033[1;34m0\033[0m");
					break;
				case 'G':
					System.out.print("\033[1;32m0\033[0m");
					break;
				case 'L':
					System.out.print("\033[1;33m0\033[0m");
					break;
				case 'D':
					System.out.print("\033[1;35m0\033[0m");
					break;
				case 'H':
					System.out.print("\033[1;37m0\033[0m");
					break;
				}
				//System.out.print(board[i][j]);
			}
			System.out.println();
		}
	}
}

/*public class board
{
	public static void main(String[] args)
	{
		Board B = new Board();
		B.output();
		Scanner sc = new Scanner(System.in);
		B.process_path(sc.nextInt(), sc.nextInt(), sc.next());
		B.output();
		System.out.println("num combos = " + B.num_combos);
		for(int i = 1; i <= 6; i++)
		{
			System.out.println("damage of " + i + " is " + B.damage[i]);
		}
	}
}*/

public class board extends JPanel
{
	//TODO add a busy flag so no moving orb while processing...
	
	static Board cur_board = new Board();
	
	public board()
	{
		setFocusable(true);
		addKeyListener(new KeyAdapter()
		{
			boolean started = false;
			boolean busy = false;
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
							((Timer)ae.getSource()).stop();
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
					System.out.println("time up!");
					cur_board.reset();
					busy = true;
					Timer t = new Timer(500, process_board_al);
					t.start();
					((Timer)ae.getSource()).stop();
					
				}
			};
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(busy) return;
				if(!started)
				{
					System.out.println("started!");
					started = true;
					Timer t = new Timer(4000, board_time_limit);
					t.start();
				}
				switch(e.getKeyCode())
				{
				case KeyEvent.VK_LEFT:
					cur_board.move(0, -1);
					break;
				case KeyEvent.VK_RIGHT:
					cur_board.move(0, 1);
					break;
				case KeyEvent.VK_UP:
					cur_board.move(-1, 0);
					break;
				case KeyEvent.VK_DOWN:
					cur_board.move(1, 0);
					break;
				}
				
				repaint();
			}
		});
	}
	
	static Color[] color_table = new Color[256]; // needs initializing in main()
	@Override
	public void paintComponent(Graphics G)
	{
		//super.paintComponent(G);
		System.out.println("painting...");
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
	
	public static void main(String[] args)
	{
		// initialize colors
		color_table['R'] = new Color(160, 0, 0);
		color_table['G'] = Color.blue;
		color_table['B'] = new Color(0, 160, 0);
		color_table['L'] = new Color(160, 160, 0);
		color_table['D'] = new Color(128, 0, 128);
		color_table['H'] = Color.magenta;
		color_table['.'] = Color.white;
		
		JFrame game = new JFrame();
	    game.setTitle("Orb board");
	    game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    game.setSize(600, 500);
	    game.setResizable(false);

	    game.add(new board());

	    game.setLocationRelativeTo(null);
	    game.setVisible(true);
	    
		//JFrame board = new JFrame();
		//board.add(new board());
		//board.setVisible(true);
	}
}