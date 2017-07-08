import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.*;

public class SQLExecutor {

	static public void query(Connection conn) {
		Statement stmt = null;
		String query = "SELECT * FROM Org";
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			stmt = conn.createStatement();
			// stmt = conn.prepareStatement(query);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				// Map map = new HashMap();
				// ResultSetMetaData rsmd = rs.getMetaData();
				// for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				//     map.put(rsmd.getColumnName(i), rs.getObject(i));
				// }
				// list.add(map);
				int id = rs.getInt("id");
				String name = rs.getString("name");

				System.out.println(id + "\t" + name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static public int loadFromFile(String location) {
		int flag = 1;
		File inFile = new File(location);
		if (!inFile.exists()) {
			return 30;
		}
		Connection conn = null;
		try {
			conn = DBConnection.getConnection();
			DBInsertion.doInsertion(conn, location);
		} catch (Exception e) {
			e.printStackTrace();
			flag = 32;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	// static public int

	static public int saveToFile(String location) {
		File outFile = new File(location);
		try {
			outFile.createNewFile();
			saveFromDB(outFile);
		} catch (Exception e) {
			e.printStackTrace();
			return 40;
		}
		return 1;
	}

	static public boolean saveFromDB(File outFile) {
		String[] tableNames = {"Org", "Leg", "Stroke", "Distance", "Meet", "Participant", "Event", "StrokeOf", "Heat", "Swim"};
		Connection conn = null;
		Statement stmt = null;
		// String query = "";
		ResultSet rs = null;
		boolean b = true;
		FileWriter fw;
		try {
			conn = DBConnection.getConnection();
			fw = new FileWriter(outFile, true);
			for (String tn : tableNames) {
				String query = "SELECT * FROM save" + tn + "();";
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				boolean need_col_name = false;
				fw.write("*" + tn + "\n");
				fw.flush();
				writeToFile(fw, rs, need_col_name);
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
			b = false;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return b;
	}

	static private void writeToFile(FileWriter fw, ResultSet rs, boolean need_col_name) throws Exception {
        try {
            ResultSetMetaData rd = rs.getMetaData();
            int fields = rd.getColumnCount();
            if (rd.getColumnName(fields).equals("RN")) {
                fields--;
            }
            if (need_col_name) {
                for (int i = 1; i <= fields; i++) {
                    fw.write(rd.getColumnName(i));
                    if (i == fields)
                        fw.write("\n");
                    else
                        fw.write(",");
                }
                fw.flush();
            }
            writeToFile(fields, fw, rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

	static private void writeToFile(int fields, FileWriter fw, ResultSet rs) throws Exception {
        try {
            while (rs.next()) {
                for (int i = 1; i <= fields; i++) {
                    String temp = rs.getString(i);
                    if (!rs.wasNull()) {
                        // replace special symbol
                        temp = temp.replaceAll(",", "&%&");
                        temp = temp.replaceAll("\n\r|\r|\n|\r\n", "&#&");
                        fw.write(temp);
                    }
                    if (i == fields)
                        fw.write("\r\n");
                    else
                        fw.write(",");
                }

                fw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

	static public List<String> query(int query_type, String keys) throws Exception {
		String[] keyArr = keys.split(",");
		if (keyArr.length > 2) {
			return null;
		}
		if (query_type == 1 || query_type == 6) {
			if (keyArr.length > 1) {
				return null;
			}
		}
		List<String> res = new ArrayList<String>();
		switch(query_type) {
			case 1:
				// meet
				res = heatOfMeet(keyArr[0]);
				break;
			case 2:
				// swimmer, meet
				res = heatForSwimmer(keyArr[0], keyArr[1]);
				break;
			case 3:
				// school, meet
				res = heatForSchool(keyArr[0], keyArr[1]);
				break;
			case 4:
				// school, meet
				res = competingSwimmer(keyArr[0], keyArr[1]);
				break;
			case 5:
				// event, meet
				res = eventResult(keyArr[0], keyArr[1]);
				break;
			case 6:
				// meet
				res = scoreboardOfMeet(keyArr[0]);
				break;
		}
		return res;
	}

	static public List<String> heatOfMeet(String meet) {
		List<String> result = new ArrayList<String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConnection();
			String query = "SELECT *FROM heat_sheet1(\'" + meet
							+ "\');";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			// String r = "";
			// while (rs.next()) {
			// 	r = "";
			// 	r += rs.getString("event") + "\t";
			// 	r += rs.getInt("leg") + "\t";
			// 	r += rs.getInt("heat") + "\t";
			// 	r += rs.getString("swimmer_id") + "\t";
			// 	r += rs.getString("swimmer") + "\t";
			// 	r += rs.getString("school") + "\t";
			// 	r += rs.getDouble("person_time") + "\t";
			// 	r += rs.getDouble("group_time") + "\t";
			// 	r += rs.getInt("rank");

			// 	System.out.println(r);
			// 	result.add(r);
			// }

			result = DBTablePrinter.printResultSet(rs);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	static public List<String> heatForSwimmer(String meet, String swimmer) {
		List<String> result = new ArrayList<String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConnection();
			String query = "SELECT * FROM heat_sheet2(\'" + meet + "\',\'" + swimmer
							+ "\');";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			result = DBTablePrinter.printResultSet(rs);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	static public List<String> heatForSchool(String meet, String school) {
		List<String> result = new ArrayList<String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConnection();
			String query = "SELECT * FROM heat_sheet3(\'" + meet + "\',\'" + school
							+ "\');";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			result = DBTablePrinter.printResultSet(rs);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	static public List<String> competingSwimmer(String meet, String school) {
		List<String> result = new ArrayList<String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConnection();
			String query = "SELECT * FROM heat_sheet4(\'" + meet + "\',\'" + school
							+ "\');";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			result = DBTablePrinter.printResultSet(rs);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	static public List<String> eventResult(String meet, String event) {
		List<String> result = new ArrayList<String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConnection();
			String query = "SELECT * FROM heat_sheet5(\'" + meet + "\',\'" + event
							+ "\');";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			result = DBTablePrinter.printResultSet(rs);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	static public List<String> scoreboardOfMeet(String meet) {
		List<String> result = new ArrayList<String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnection.getConnection();
			String query = "SELECT * FROM heat_sheet6(\'" + meet
							+ "\');";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			result = DBTablePrinter.printResultSet(rs);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}


	static public String insert(String rowdata) {
		String[] keyArr = rowdata.split(",");
		String error = "20";
		switch(keyArr[0]) {
			case "Org":
				// id,     neme,   is_univ
				// String, String, boolean
				if (keyArr.length != 4) {
					return error;
				}
				return insertIntoOrg(keyArr);
			case "Meet":
				// name,   start_date, num_days, org_id
				// String, Date,       int,      String
				if (keyArr.length != 5) {
					return error;
				}
				return insertIntoMeet(keyArr);
			case "Participant":
				// id,     gender, org_id
				// String, String, String
				if (keyArr.length != 4) {
					return error;
				}
				return insertIntoParticipant(keyArr);
			case "Event":
				// id,     gender, distance
				// String, String, int
				if (keyArr.length != 4) {
					return error;
				}
				return insertIntoEvent(keyArr);
			case "StrokeOf":
				// event_id, leg, stroke
				// String,   int, String
				if (keyArr.length != 4) {
					return error;
				}
				return insertIntoStrokeOf(keyArr);
			case "Heat":
				// id,  event_id, meet_name
				// int, String,   String
				if (keyArr.length != 4) {
					return error;
				}
				return insertIntoHeat(keyArr);
			case "Swim":
				// heat_id, event_id, meet_name, p_id,   leg, time
				// int,     String,   String,    String, int, double
				if (keyArr.length != 7) {
					return error;
				}
				return insertIntoSwim(keyArr);
			default:
				return "22";
		}
	}

	// All the insert function will return the insertion result
	// or return error type
	static public String insertIntoOrg(String[] newContent) {
		String msg = "1";
		Connection conn = null;
		CallableStatement stmt = null;
		try {
			conn = DBConnection.getConnection();
			String procedure = "{ call InsertOrg(?, ?, ?)}";
			stmt = conn.prepareCall(procedure);

			// id,     neme,   is_univ
			// String, String, boolean

			stmt.setString(1, newContent[1]);
			stmt.setString(2, newContent[2]);
			if (newContent[3].equals(";")) {
				stmt.setNull(3, java.sql.Types.BOOLEAN);
			}
			else {
				stmt.setBoolean(3, Boolean.parseBoolean(newContent[3].split(";")[0]));
			}
			// statement.registerOutParameter(1, Types.INTEGER);
			stmt.execute();
		} catch (Exception e) {
			// e.printStackTrace();
			String msgs = e.toString();
			msg = msgs.split("\n")[0];
			System.out.println(msg);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	static public String insertIntoMeet(String[] newContent) {
		String msg = "1";
		Connection conn = null;
		CallableStatement stmt = null;
		try {
			conn = DBConnection.getConnection();
			String procedure = "{ call InsertMeet(?, ?, ?, ?)}";
			stmt = conn.prepareCall(procedure);

			// name,   start_date, num_days, org_id
			// String, Date,       int,      String

			stmt.setString(1, newContent[1]);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if (newContent[2].equals("")) {
				stmt.setNull(2, java.sql.Types.DATE);
			} else {
				java.sql.Date sqlDate = new java.sql.Date(format.parse(newContent[2]).getTime());
				stmt.setDate(2,sqlDate);
			}
			if (newContent[3].equals("")) {
				stmt.setNull(3, java.sql.Types.INTEGER);
			}
			else{
				stmt.setInt(3, Integer.parseInt(newContent[3]));
			}
			stmt.setString(4, newContent[4].split(";")[0]);
			stmt.execute();
		} catch (Exception e) {
			// e.printStackTrace();
			String msgs = e.toString();
			msg = msgs.split("\n")[0];
			System.out.println(msg);
			String fk = fkConstraint(msg);
			if (fk != "") {
				msg = fk + "Org ID";
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	static public String insertIntoParticipant(String[] newContent) {
		String msg = "1";
		Connection conn = null;
		CallableStatement stmt = null;
		try {
			conn = DBConnection.getConnection();
			String procedure = "{ call InsertParticipant(?, ?, ?, ?)}";
			stmt = conn.prepareCall(procedure);

			// id,     gender, org_id
			// String, String, String

			stmt.setString(1, newContent[1]);
			stmt.setString(2, newContent[2]);
			stmt.setString(3, newContent[3]);
			stmt.setString(4, newContent[4].split(";")[0]);
			stmt.execute();
		} catch (Exception e) {
			// e.printStackTrace();
			String msgs = e.toString();
			msg = msgs.split("\n")[0];
			System.out.println(msg);
			String fk = fkConstraint(msg);
			if (fk != "") {
				msg = fk + "Org ID";
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	static public String insertIntoEvent(String[] newContent) {
		String msg = "1";
		Connection conn = null;
		CallableStatement stmt = null;
		try {
			conn = DBConnection.getConnection();
			String procedure = "{ call InsertEvent(?, ?, ?)}";
			stmt = conn.prepareCall(procedure);

			// id,     gender, distance
			// String, String, int

			stmt.setString(1, newContent[1]);
			stmt.setString(2, newContent[2]);
			if (newContent[3].equals("")) {
				stmt.setNull(3, java.sql.Types.INTEGER);
			} else {
				stmt.setInt(3, Integer.parseInt(newContent[3].split(";")[0]));
			}
			stmt.execute();
		} catch (Exception e) {
			// e.printStackTrace();
			String msgs = e.toString();
			msg = msgs.split("\n")[0];
			System.out.println(msg);
			String fk = fkConstraint(msg);
			if (fk != "") {
				msg = fk + "Meet Name";
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	static public String insertIntoStrokeOf(String[] newContent) {
		String msg = "1";
		Connection conn = null;
		CallableStatement stmt = null;
		try {
			conn = DBConnection.getConnection();
			String procedure = "{ call InsertStrokeOf(?, ?, ?)}";
			stmt = conn.prepareCall(procedure);

			// event_id, leg, stroke
			// String,   int, String

			stmt.setString(1, newContent[1]);
			stmt.setInt(2, Integer.parseInt(newContent[2]));
			stmt.setString(3, newContent[3].split(";")[0]);
			stmt.execute();
		} catch (Exception e) {
			// e.printStackTrace();
			String msgs = e.toString();
			msg = msgs.split("\n")[0];
			System.out.println(msg);
			String fk = fkConstraint(msg);
			if (fk != "") {
				msg = fk + "Event ID, Leg, Stroke";
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	static public String insertIntoHeat(String[] newContent) {
		String msg = "1";
		Connection conn = null;
		CallableStatement stmt = null;
		try {
			conn = DBConnection.getConnection();
			String procedure = "{ call InsertHeat(?, ?, ?)}";
			stmt = conn.prepareCall(procedure);

			// id,  event_id, meet_name
			// int, String,   String

			stmt.setInt(1, Integer.parseInt(newContent[1]));
			stmt.setString(2, newContent[2]);
			stmt.setString(3, newContent[3].split(";")[0]);
			stmt.execute();
		} catch (Exception e) {
			// e.printStackTrace();
			String msgs = e.toString();
			msg = msgs.split("\n")[0];
			System.out.println(msg);
			String fk = fkConstraint(msg);
			if (fk != "") {
				msg = fk + "Event ID, Meet Name";
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	static public String insertIntoSwim(String[] newContent) {
		String msg = "1";
		Connection conn = null;
		CallableStatement stmt = null;
		try {
			conn = DBConnection.getConnection();
			String procedure = "{ call InsertSwim(?, ?, ?, ?, ?, ?)}";
			stmt = conn.prepareCall(procedure);

			// heat_id, event_id, meet_name, p_id,   leg, time
			// int,     String,   String,    String, int, double

			stmt.setInt(1, Integer.parseInt(newContent[1]));
			stmt.setString(2, newContent[2]);
			stmt.setString(3, newContent[3]);
			stmt.setString(4, newContent[4]);
			if (newContent[5].equals("")) {
				stmt.setNull(5, java.sql.Types.INTEGER);
			} else {
				stmt.setInt(5, Integer.parseInt(newContent[5]));
			}
			if (newContent[6].equals(";")) {
				stmt.setNull(6, java.sql.Types.DOUBLE);
			} else {
				stmt.setDouble(6, Double.parseDouble(newContent[6].split(";")[0]));
			}
			stmt.execute();
		} catch (Exception e) {
			// e.printStackTrace();
			String msgs = e.toString();
			msg = msgs.split("\n")[0];
			System.out.println(msg);
			String fk = fkConstraint(msg);
			if (fk != "") {
				msg = fk + "Heat ID, Event ID, Meet Name, Participant ID";
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	static private String fkConstraint(String msg) {
		if (msg.contains("foreign")) {
			return "Insert or Update Violates Foreign Key Constraint.\nPlease check: ";
		}
		return "";
	}


	// public static void main(String[] args) throws Exception {
	// 	SqlFileExecutor exe = new SqlFileExecutor();
	// 	// exe.execute(args[0]);
	// 	Connection conn = DBConnection.getConnection();
	// 	System.out.println("Try Query Now ++++++");
	// 	exe.query(conn);
	// 	// Scanner sc = new Scanner(System.in);

	// 	// List<String> sqlList = new SqlFileExecutor().loadSql(args[0]);
	// 	// System.out.println("size:" + sqlList.size());
	// 	// for (String sql : sqlList) {
	// 	//     System.out.println(sql);
	// 	//     System.out.println("------------------------------------");
	// 	// }
	// }
}