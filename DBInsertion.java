// package sql_project;

import java.sql.*;
import java.util.ArrayList;
import java.text.*;

public class DBInsertion {

	// public static void main(String[] args) throws Exception{
	public static void doInsertion(Connection new_conn, String location) throws Exception {
		// connection new_c = new connection();
		// Connection new_conn = new_c.connect();
		CSVparser newParser = new CSVparser(location);
		ArrayList<CSVparser.Node> newResults = newParser.getContent();


		for (CSVparser.Node result : newResults){
			if (result.flag.equals("Org")) {
				for (String s: result.content){
					String[] newContent = s.split(",");
					CallableStatement stmt = new_conn.prepareCall("{call InsertOrg(?,?,?)}");
					stmt.setString(1, newContent[0]);
					stmt.setString(2, newContent[1]);
					if (newContent[2].equals("")) {
						stmt.setNull(3, java.sql.Types.BOOLEAN);
					}
					else {stmt.setBoolean(3, Boolean.parseBoolean(newContent[2]));}
					stmt.execute();
					stmt.close();
				}
			}
		}


		for (CSVparser.Node result : newResults){
			if (result.flag.equals("Leg")){
				for (String s: result.content){
					String[] newContent = s.split(",");
					CallableStatement stmt = new_conn.prepareCall("{call InsertLeg(?)}");
					stmt.setInt(1, Integer.parseInt(newContent[0]));
					stmt.execute();
					stmt.close();
				}
			}
		}

		for (CSVparser.Node result : newResults){
			if (result.flag.equals("Stroke")){
				for (String s: result.content){
					String[] newContent = s.split(",");
					CallableStatement stmt = new_conn.prepareCall("{call InsertStroke(?)}");
					stmt.setString(1, newContent[0]);
					stmt.execute();
					stmt.close();
				}
			}
		}

		for (CSVparser.Node result : newResults){
			if (result.flag.equals("Distance")){
				for (String s: result.content){
					String[] newContent = s.split(",");
					CallableStatement stmt = new_conn.prepareCall("{call InsertDistance(?)}");
					stmt.setInt(1, Integer.parseInt(newContent[0]));
					stmt.execute();
					stmt.close();
				}
			}
		}

		for (CSVparser.Node result : newResults){
			if (result.flag.equals("Meet")){
				for (String s: result.content){
					String[] newContent = s.split(",");
					CallableStatement stmt = new_conn.prepareCall("{call InsertMeet(?,?,?,?)}");
					stmt.setString(1, newContent[0]);
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					if (newContent[1].equals("")) {
						stmt.setNull(2, java.sql.Types.DATE);
					}
					else{
					java.sql.Date sqlDate = new java.sql.Date(format.parse(newContent[1]).getTime());
					stmt.setDate(2,sqlDate);}
					if (newContent[2].equals("")) {
						stmt.setNull(3, java.sql.Types.INTEGER);
					}
					else{
					stmt.setInt(3, Integer.parseInt(newContent[2]));}
					stmt.setString(4, newContent[3]);
					stmt.execute();
					stmt.close();
				}
			}
		}

		for (CSVparser.Node result : newResults){
			if (result.flag.equals("Participant")){
				for (String s: result.content){
					String[] newContent = s.split(",");
					CallableStatement stmt = new_conn.prepareCall("{call InsertParticipant(?,?,?,?)}");
					stmt.setString(1, newContent[0]);
					stmt.setString(2, newContent[3]);
					stmt.setString(3, newContent[1]);
					stmt.setString(4, newContent[2]);
					stmt.execute();
					stmt.close();
				}
			}
		}

		for (CSVparser.Node result : newResults){
			if (result.flag.equals("Event")){
				for (String s: result.content){
					String[] newContent = s.split(",");
					CallableStatement stmt = new_conn.prepareCall("{call InsertEvent(?,?,?)}");
					stmt.setString(1, newContent[0]);
					stmt.setString(2, newContent[1]);
					if (newContent[2].equals("")) {
						stmt.setNull(3, java.sql.Types.INTEGER);
					}
					else{
					stmt.setInt(3, Integer.parseInt(newContent[2]));}
					stmt.execute();
					stmt.close();
				}
			}
		}

		for (CSVparser.Node result : newResults){
			if (result.flag.equals("StrokeOf")){
				for (String s: result.content){
					String[] newContent = s.split(",");
					CallableStatement stmt = new_conn.prepareCall("{call InsertStrokeOf(?,?,?)}");
					stmt.setString(1, newContent[0]);
					stmt.setInt(2, Integer.parseInt(newContent[1]));
					stmt.setString(3, newContent[2]);
					stmt.execute();
					stmt.close();
				}
			}
		}

		for (CSVparser.Node result : newResults){
			if (result.flag.equals("Heat")){
				for (String s: result.content){
					String[] newContent = s.split(",");
					CallableStatement stmt = new_conn.prepareCall("{call InsertHeat(?,?,?)}");
					stmt.setInt(1, Integer.parseInt(newContent[0]));
					stmt.setString(2, newContent[1]);
					stmt.setString(3, newContent[2]);
					stmt.execute();
					stmt.close();
				}
			}
		}

		for (CSVparser.Node result : newResults){
			if (result.flag.equals("Swim")){
				for (String s: result.content){
					String[] newContent = s.split(",");
					CallableStatement stmt = new_conn.prepareCall("{call InsertSwim(?,?,?,?,?,?)}");
					stmt.setInt(1, Integer.parseInt(newContent[0]));
					stmt.setString(2, newContent[1]);
					stmt.setString(3, newContent[2]);
					stmt.setString(4, newContent[3]);
					if (newContent[4].equals("")) {
						stmt.setNull(5, java.sql.Types.INTEGER);
					}
					else{
					stmt.setInt(5, Integer.parseInt(newContent[4]));}
					if (newContent[5].equals("")) {
						stmt.setNull(6, java.sql.Types.DOUBLE);
					}
					else{
					stmt.setDouble(6, Double.parseDouble(newContent[5]));}
					stmt.execute();
					stmt.close();
				}
			}
		}

		new_conn.close();
	}

}

