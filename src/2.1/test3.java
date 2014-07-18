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


public class test3 extends JPanel
{
	//TODO add a busy flag so no moving orb while processing...
	
	static Board cur_board = new Board();
	static boolean started = false;
	static boolean busy = false;
	
	public test3()
	{
		setFocusable(true);
		addKeyListener(new KeyAdapter()
		{
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

	    game.add(new test3());

	    game.setLocationRelativeTo(null);
	    game.setVisible(true);
	    
		//JFrame board = new JFrame();
		//board.add(new board());
		//board.setVisible(true);
	}
}