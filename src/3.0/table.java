import java.util.*;

class Table
{
	ArrayList<String> columns = new ArrayList<String>();
	ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
	void add_column(String col)
	{
		columns.add(col);
		for(int i = 0; i < data.size(); i++)
		{
			data.get(i).add(""); // new column data initialized to ""
		}
	}
	void add_row(ArrayList<String> row)
	{
		while(row.size() < columns.size()) row.add("");
		data.add(row);
	}
	String same_char(int n, char c)
	{
		char[] C = new char[n];
		Arrays.fill(C, c);
		return new String(C);
	}
	String display()
	{
		if(columns.size() == 0 || data.size() == 0) return "";
		
		int[] maxwidth = new int[columns.size()];
		for(int i = 0; i < columns.size(); i++) maxwidth[i] = columns.get(i).length();
		
		for(int r = 0; r < data.size(); r++)
			for(int c = 0; c < columns.size(); c++)
			{
				if(data.get(r).get(c).length() > maxwidth[c])
					maxwidth[c] = data.get(r).get(c).length();
			}
		
		String h_bar = "+";
		for(int c = 0; c < columns.size(); c++)
			h_bar += same_char(maxwidth[c], '-') + "+";
		h_bar += "\n";
		
		String output = h_bar;
		for(int r = -1; r < data.size(); r++) // -1 is column names
		{
			String row_output = "|";
			for(int c = 0; c < columns.size(); c++)
			{
				String s = "";
				if(r == -1) // column name
					s = columns.get(c);
				else s = data.get(r).get(c);
				row_output += same_char(maxwidth[c] - s.length(), ' ') + s + "|";
			}
			row_output += "\n";
			output += row_output;
			output += h_bar;
		}
		
		return output;
	}
}

public class table {
	public static void main(String[] args)
	{
		Table t = new Table();
		Scanner sc = new Scanner(System.in);
		
		int N = sc.nextInt(), M = sc.nextInt();
		for(int c = 0; c < M; c++) t.add_column(sc.next());
		for(int r = 0; r < N; r++)
		{
			ArrayList<String> row = new ArrayList<String>();
			for(int c = 0; c < M; c++) row.add(sc.next());
			t.add_row(row);
		}
		
		System.out.println(t.display());
	}
}
