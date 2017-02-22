package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Search {
	public static void main(String[] args){
		try(Scanner input = new Scanner(System.in)){
			Search s = new Search("sites"); //initialize the Search object to the sites folder
			
			//while loop responsible for grabbing input
			String response = "";
			while(!response.equals("quit")){
				System.out.println("What would you like to search? Type quit to quit.");
				response = input.nextLine();
				if(response.equals("quit")){ //stop loop if person types quit
					break;
				}
				System.out.println("Searching...");
				String[] results = s.LookUp(response); //get results
				for(String site:results){ //print out results
					System.out.println(site);
				}
				System.out.println("=====================================\n");
			}
		}
		catch(IOException i){
			System.out.println("Fatal Error!"); //some generic exception statement
			main(new String[0]);
		}
	}
	private File folder;
	private File[] files;
	public Search(String directory) throws IOException{
		folder = new File(directory);
		
		//blow up if the file doesn't exist, or if it is not a file
		if(!folder.exists() || !folder.isDirectory()){
			throw new IOException();
		}
		files = folder.listFiles();
	}
	public String[] LookUp(String query) throws IOException{
		Index i = new Index("indexes"); //init Index object to indexes folder
		String[] results = i.GetResults(query); //Grab the of the results
		return results;
	}
	public Website[] LookUpOld(String query){
		ArrayList<Website> results = new ArrayList<Website>();
		int counter = 0;
		for(File file:files){
			try(BufferedReader buff = new BufferedReader(new FileReader(file))){
				String url = buff.readLine();
				String content = "";
				String line = null;
				while((line = buff.readLine()) != null){
					content += line.toLowerCase();
				}
				if(content.contains(query.toLowerCase())||url.toLowerCase().contains(query.toLowerCase())){
					Website site = new Website(url, content);
					results.add(site);
					counter++;
				}
				if(counter >= 10){
					return results.toArray(new Website[0]);
				}
			}
			catch(IOException e){
				continue;
			}
		}
		return results.toArray(new Website[0]);
	}
	public boolean equals(Search s){
		return this.folder.equals(s.folder);
	}
	public int hashCode(){
		return Objects.hash(folder);
	}
}
