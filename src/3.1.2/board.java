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
	
	int num_combos;
	int num_moves;
	//int score;

	//int num_colors;
	boolean[] cleared = new boolean[256];
	boolean[] mass_attack = new boolean[256];
	double[] damage = new double[256];
	
	void reset()
	{
		for(int i = 0; i < 256; i++)
		{
			cleared[i] = false;
			mass_attack[i] = false;
			damage[i] = 0.0;
		}
		num_combos = 0;
		//num_colors = 0;
		num_moves = 0;
		
		cur_r = cur_c = -1;
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
		while(select_one_match());
		
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
	
	//void exclude(String S)
	
	//int encode()
	
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
	
	//void unmove(int dr, int dc)
	
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
	
	//boolean exists_one_match()
	
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
						cleared[board[r][c].charAt(0)] = true;
						num_combos++;
						if(num_cleared >= 5)
							mass_attack[board[r][c].charAt(0)] = true;
						
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
					/*if(!cleared[board[i][j].charAt(0)])
					{
						cleared[board[i][j].charAt(0)] = true;
						num_colors++;
					}*/
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
	
	//void process()
	
	//void get_score()
	
	double get_combo_multiplier()
	{
		return 0.25 * (num_combos + 3);
	}
	
	//void process_path(int start_r, int start_c, String path)
	
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
