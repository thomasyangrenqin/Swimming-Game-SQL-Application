import javax.swing.*;

import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Menu extends JFrame implements ActionListener {
	// frame
	private JFrame frame = new JFrame("Swimming Meets");
	// panels
	private JPanel main_panel = new JPanel();
	private JTabbedPane content = new JTabbedPane(JTabbedPane.TOP);
	private JPanel file_page = new JPanel();
	private JPanel update_page = new JPanel();
	private JPanel query_page = new JPanel();

	// label
	private JLabel title = new JLabel("Welcome to Swimming Meets System");
	private JLabel select = new JLabel("Click to Select:");

	// file page
	private JButton load = new JButton("Load");
	private JButton save = new JButton("Save");
	private JTextField path1 = new JTextField(42);
	private JTextField path2 = new JTextField(42);

	// update page
	private JTextField rowdata = new JTextField(42);
	private JButton submit = new JButton("Submit");

	// query page
	private JTextField query_type = new JTextField(42);
	private JTextField keyword = new JTextField(42);
	private JButton query = new JButton("Submit");
	private JButton clear = new JButton("Clear");
	private JTextArea res = new JTextArea();
	private JScrollPane result;
	// private JTextArea res = new JTextArea();


	// DB component
	private Connection conn = null;

	public void setupWindow() {

		// set window size etc.
		frame.setSize(600, 600);
		frame.setContentPane(main_panel);
		// set window to center of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		// frame.setLocation((dim.width-frame.getWidth())/2,(dim.height-frame.getHeight())/2);
		frame.setLocationRelativeTo(null);

		// set Panels
		setPanel();
		// add listener
		addListener();

		// override quit function
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				quit();
			}
		});
		frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == load) {
			load();
		} else if (e.getSource() == save) {
			save();
		} else if (e.getSource() == submit) {
			update();
		} else if (e.getSource() == query) {
			query();
		} else if (e.getSource() == clear) {
		    query_type.setText("");
		    keyword.setText("");
        }
	}

	public void addListener() {
		load.addActionListener(this);
		save.addActionListener(this);
		submit.addActionListener(this);
		query.addActionListener(this);
		clear.addActionListener(this);
	}

	public void setPanel() {
		// content.setSize(300, 300);
		// set main panel
		// title.setSize(main_panel.getWidth(), 20);

		main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));


		// set file panel
		// file_page.setLayout(new BoxLayout(file_page, BoxLayout.Y_AXIS));
		file_page.add(new JLabel("Enter the file path (Please use full path):          ",
						JLabel.CENTER));
		file_page.add(load);
		file_page.add(path1);

		file_page.add(new JLabel("Enter the file name (Save to current directory):          ",
						JLabel.CENTER));
		file_page.add(save);
		file_page.add(path2);

		// set update panel
		// update_page.setLayout(new BoxLayout(update_page, BoxLayout.Y_AXIS));
		update_page.add(new JTextArea("Enter the Table name and data\n" +
									  "Please end with \",\"\n" +
									  "Ex. Org,1,Rice,t;)\n" +
									  "Please put nothing for empty value\n" +
									  "Ex. Meet,Rice_bbb,,4,U430;"));
		update_page.add(rowdata);
		update_page.add(submit);

		// set query pabel
		// query_page.setLayout(new BoxLayout(query_page, BoxLayout.Y_AXIS));
		JTextArea info = new JTextArea("Query Type and keyword(s): \n" +
										"1. Heat Sheet for Meet (meet)\n" +
										"2. Heat Sheet for Swimmer (meet,swimmer)\n" +
										"3. Heat Sheet for School (meet,school)\n" +
										"4. Swimmers of School (meet,school)\n" +
										"5. Result for Event (meet,event)\n" +
										"6. Scoreboard for Meet (meet)");
		info.setColumns(40);
		info.setLineWrap(true);
		info.setRows(7);
		info.setEditable(false);
		query_page.add(info);

		query_page.add(new JLabel("Enter query type (Use the number)", JLabel.CENTER));
		query_page.add(query_type);
		query_page.add(new JLabel("Enter keyword(s), ex. SwimmerName,Meet", JLabel.CENTER));
		query_page.add(keyword);
		query_page.add(query);
		query_page.add(clear);

		res.setColumns(80);
		res.setLineWrap(false);
		res.setFont(new Font("Andale Mono", Font.PLAIN, 12));
		res.setRows(15);
		res.setEditable(false);
		result = new JScrollPane(res);
		query_page.add(result);

		content.add("Load/Save", file_page);
		content.add("Insert/Update", update_page);
		content.add("Meet Sheets", query_page);

		main_panel.add(title);
		main_panel.add(select);
        main_panel.add(content);

		/*
		set the height of the tab
		*/

		// JLabel lab = new JLabel("Load/Save");
		// lab.setPreferredSize(new Dimension(200, 200));
		// content.setTabComponentAt(0, lab);

		// content.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
		// 	@Override
		// 	protected int calculateTabHeight(int tabPlacement,
		// 									int tabIndex, int fontHeight) {
		// 	return 32;
		// 	}
		// });
	}

	public void quit() {
		int flag = 0;
		String mesg = "Do you want to exit the system?";
		flag = JOptionPane.showConfirmDialog(frame, mesg, "Notice", JOptionPane.YES_NO_OPTION);
		if(flag == JOptionPane.YES_OPTION){
			// System.out.println("Close Connection!\n\n");
			System.exit(0);
		}
	}

	public void load() {
		String location = path1.getText();
		// TODO: use parser class to open the file and insert
		this.alert(SQLExecutor.loadFromFile(location));
	}

	public void save() {
		String location = path2.getText();
		// TODO: get data from DB and write to file
		this.alert(SQLExecutor.saveToFile(location));
	}

	public void update() {
		String row = rowdata.getText();
		// this returns the insertion result or error type
		String error = SQLExecutor.insert(row);
		if (error == "1" || error == "20" || error == "22") {
			this.alert(Integer.parseInt(error));
		} else {
			JOptionPane.showMessageDialog(frame, error,
							"SQL Error", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void query() {
		int type = -1;
		// pre-check
		try {
			type = Integer.parseInt(query_type.getText());
		} catch (Exception e) {
			e.printStackTrace();
			res.setText("****** Query Failed ******");
			this.alert(10);
			return;
		}
		if (type < 1 || type > 6) {
			res.setText("****** Query Failed ******");
			this.alert(10);
			return;
		}
		String keys = keyword.getText();
		// TODO: do the query and get result
		res.setText("Query");

		List<String> rs = new ArrayList<String>();
		try {
			rs = new SQLExecutor().query(type, keys);
		} catch (Exception e) {
			res.setText("****** Query Failed ******");
			this.alert(11);
			return;
		}
		if (rs == null) {
			this.alert(12);
			return;
		}

		res.setText("****** Query Succeeded ******\n\n");
		for (String r : rs) {
			res.append(r);
		}
		this.alert(1);

	}

	public void alert(int type) {
		switch (type) {
			case 0:
				JOptionPane.showMessageDialog(frame, "Failed!",
							"Tip", JOptionPane.INFORMATION_MESSAGE);
				break;
			case 1:
				JOptionPane.showMessageDialog(frame, "Succeeded!",
							"Tip", JOptionPane.INFORMATION_MESSAGE);
				break;
			/****** Query Alert: Starts with 1 ******/
			case 10:
				JOptionPane.showMessageDialog(frame, "Wrong Query Type, Please Re-enter.",
							"Tip", JOptionPane.INFORMATION_MESSAGE);
				break;
			case 11:
				JOptionPane.showMessageDialog(frame, "Query Failed with SQLException, Please Check your input keyword(s).",
							"Error", JOptionPane.INFORMATION_MESSAGE);
				break;
			case 12:
				JOptionPane.showMessageDialog(frame, "No Result Returned.",
							"Error", JOptionPane.INFORMATION_MESSAGE);
				break;
			/****** Insert Alert: Starts with 2 ******/
			case 20:
				JOptionPane.showMessageDialog(frame, "Insertion Failed with Wrong Number of Keywords, Please Check your input keywords.",
							"Error", JOptionPane.INFORMATION_MESSAGE);
				break;
			case 21:
				JOptionPane.showMessageDialog(frame, "Insertion Failed with SQLException, Please Check your input keywords.",
							"Error", JOptionPane.INFORMATION_MESSAGE);
				break;
			case 22:
				JOptionPane.showMessageDialog(frame, "Wrong Table Name. Please re-enter.",
							"Error", JOptionPane.INFORMATION_MESSAGE);
				break;
			/****** Load Alert: Starts with 3 ******/
			case 30:
				JOptionPane.showMessageDialog(frame, "Wrong path, please re-enter. Notice that we only accept full path.",
							"Error", JOptionPane.INFORMATION_MESSAGE);
				break;
			case 32:
				JOptionPane.showMessageDialog(frame, "Load Failed with SQLException, Please Check your input file.",
							"Error", JOptionPane.INFORMATION_MESSAGE);
				break;
			/****** Save Alert: Starts with 4 ******/
			case 40:
				JOptionPane.showMessageDialog(frame, "Save Error. Please try again.",
							"Error", JOptionPane.INFORMATION_MESSAGE);
				break;
			default:
				break;
		}
	}

	public static void main(String[] args) throws Exception {
		Menu menu = new Menu();
		menu.setupWindow();
	}
}