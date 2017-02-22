package crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
	public static void main(String[] args){
		Crawler bot = new Crawler(); //init the crawler bot
		Scanner input = new Scanner(System.in);
		System.out.println("Where would you like me to start?");
		String site = input.nextLine(); //get starting location
		bot.Start(site); //start crawling at that location
		input.close(); //close the scanner
	}
	
	@Override
	public boolean equals(Object other){
		if(!(other instanceof Crawler)) return false;
		Crawler c = (Crawler) other;
		return running == c.running;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(running);
	}
	private List<Website> sites = new ArrayList<Website>();
	private int saveCounter = 0;
	private boolean running = false;
	public void Start(String site){
		running = true;
		sites = Collections.synchronizedList(new ArrayList<Website>());
		Thread thread = new Thread(new Runnable() {
		    @Override
		    public void run() {
		        try{
		            ArrayList<String> contents = getHTML(site); //grab the html of the website in an arraylist
		            //use Collections.synchronizedList() instead of directly referencing the list, this makes it thread safe
		        	Collections.synchronizedList(sites).add(new Website(site,assembleHTML(contents)));
		            ArrayList<String> links = getLinks(contents); //grab the urls linked in the html
		       	    crawl(links); //start crawling the links
		        }
		        catch(IOException i){
		        	System.out.println("Fatal error!"); //blow up if its bad
		        	i.printStackTrace();
		        }     
		    }
		});
		        
		thread.start(); //start the thread. crawling is done in a thread, so the monitoring loop can be run in the main
		
		//this is the while loop for monitoring the progress of the crawler
		Scanner input = new Scanner(System.in);
		String response = "";
		while(!response.equals("quit")){
			System.out.println("Press enter to see the size! Type 'list' to see the full list. (It's veery long) Type quit to quit.");
			response = input.nextLine();
			if(!response.equals("quit")){
				System.out.println("Size: " + Collections.synchronizedList(sites).size());
			}
			if(response.equals("list")){
				printSites(Collections.synchronizedList(sites));
			}
		}
		input.close();
		System.out.println("Exited!");
		System.exit(0);
	}
	void printSites(List<Website> print){ //prints out the list of websites
		for(Website web:print){
			System.out.println(web.toString());
		}
	}
	void Save(){
		File dir = new File("sites"); //fix this. make the folder a variable. in the meantime, sites will be saved to the sites folder
		File[] files = dir.listFiles(); //get the files in the folder
		int name = files.length;//number the saved files.
		System.out.println("saving");
		for(Website w:Collections.synchronizedList(sites)){
			File toWrite = new File("sites/" + name + ".txt"); //write the file to this place.
			try(FileWriter write = new FileWriter(toWrite)){
				PrintWriter print = new PrintWriter(write); //try the filewriter, and printwriter.
				print.printf("%s" + "%n", w.getURL());//write the shit to the printer, which goes to the file. the first argument is the formatting.
				print.printf("%s" + "%n", w.getContent());
				name++; //increment the name
				print.flush(); //flush and close the printwriter. the filewriter will close on its own through the try.
				print.close();
			}
			catch(IOException e){
				continue;
			}
		}
		sites = Collections.synchronizedList(new ArrayList<Website>()); //reset the sites once it has been saved. This prevents too much memory being used.
	}
	
	void counter(){ //purely for incrementing the counter for saving.
		saveCounter++;
		if(saveCounter >= 3){
			saveCounter = 0;
			Save();
		}
	}
	void crawl(ArrayList<String> toCrawl){
		//perform a breadth-first search. Therefore compile links BEFORE going through them.
		for(String s:toCrawl){
			try{
				ArrayList<String> contents = getHTML(s); //get the HTML
				Collections.synchronizedList(sites).add(new Website(s,assembleHTML(contents)));//add the website to the list
				counter(); //increment save counter
			}
			catch(Exception e){
				continue;
			}
		}
		for(String s:toCrawl){//go through the crawl list
			try{
				ArrayList<String> contents = getHTML(s); //grab the contents of the site
				crawl(getLinks(contents)); //crawl the links of the site
			}
			catch(Exception e){
				continue;
			}
		}
	}
	String assembleHTML(ArrayList<String> contents){
		String toReturn = "";
		//go through the contents and assemble it all. add brackets to the lines.
		for(String s:contents){
			if(s.equals("\n")){
				continue;
			}
			toReturn += "<" + s + ">";
		}
		return toReturn;
	}
	ArrayList<String> getLinks(ArrayList<String> contents){
		ArrayList<String> links = new ArrayList<String>();
		String content = assembleHTML(contents); //get the assembled html into one string.
		//compile a pattern and a matcher for identifying urls inside the html, and pull them out. add them to lnks.
		Pattern p = Pattern.compile(("(((https?:\\/\\/)|(www\\.))[\\w\\*\\~\\.\\,\\(\\)\\@\\?\\#\\+\\/\\:\\=\\[\\]\\&\\%\\!\\;\\-]+)"));
		Matcher match = p.matcher(content);
		while(match.find()){
			links.add(match.group(1));
		}
		return links;
	}
	ArrayList<String> getHTML(String site) throws IOException{
		URL url = new URL(site); //grab a new URL object (form java.net)
		Scanner in = new Scanner(url.openStream()); //start a new scanner form the url.
		in.useDelimiter("<|>"); //use the brackets as delimiters, so everything in between is taken. (html is seperate from content)
		ArrayList<String> content = new ArrayList<String>();
		while(in.hasNext()){
			String next = in.next(); //grab the next thing.
			String[] parts = next.split(" "); //split it up into words
			Index i = new Index("indexes"); //create a new Index object pointing to the indexes folder
			for(String s:parts){
				i.Save(s,site); //save and index the word which points to the url
			}
			content.add(next); //add the word to the contents
		}
		in.close();//remember to close the stream
		return content;
	}
}
