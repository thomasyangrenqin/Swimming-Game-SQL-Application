
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class CSVparser {
	private static String mode;
	private String csvFile;
	private ArrayList<Node> results;

	public static class Node{
		public ArrayList<String> content;
		public String flag;
	}

	public CSVparser(String file){
		csvFile = file;
		mode = null;
		results = new ArrayList<Node>();
	}

	public ArrayList<Node> getContent() throws Exception{
		try{
			Node newNode = new Node();
			ArrayList<String> newList = new ArrayList<String>();
			Scanner scanner = new Scanner(new File(csvFile));
	        while (scanner.hasNext()) {
	        	String nextline = scanner.nextLine();
	        	if (nextline.contains("*")) {
	        		if (mode != null){
	        			newNode.content = newList;
	        			results.add(newNode);
	        		}
	        		newNode = new Node();
	        		newList = new ArrayList<String>();
	        		mode = nextline.split("\\*")[1].split(",")[0];
	        		newNode.flag = mode;
	        	}else{
	        		newList.add(nextline);
	        	}
	        }
	        newNode.content = newList;
	        results.add(newNode);
	        return results;
		}catch (Exception e){
			throw new Exception(e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception{
		CSVparser newParser = new CSVparser("/Users/yangrenqin/Desktop/533.csv");
		ArrayList<Node> newResults = newParser.getContent();
		for (Node result : newResults){
			System.out.println(result.flag);
			for (String s: result.content){
				System.out.println(s);
			}
		}
	}
}
