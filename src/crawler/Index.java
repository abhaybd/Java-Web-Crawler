package crawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Objects;
public class Index {
	File folder;
	String path;
	
	public boolean Equals(Index i){
		return folder.equals(i.folder) && path.equals(i.path);
	}
	public int hashCode(){
		return Objects.hash(folder,path);
	}
	public Index(String path) throws IOException{
		folder = new File(path);
		//blow up if the folder isn't a directory, or if it doesn't exist
		if(!folder.exists() || !folder.isDirectory()){
			throw new IOException();
		}
		this.path = folder.getCanonicalPath() + "\\";
	}
	public String[] GetResults(String query){
		HashSet<String> results = new HashSet<String>();
		String[] parts = query.split(" ");
		//split query into words (cant match everything at once, since words are indexed individually)
		for(String s:parts){
			try {
				File toCheck = new File(path + Hash.MD5(s.toLowerCase()) + ".ser"); //hash the word (to get rid of invalid characters)
				FileInputStream fileIn = new FileInputStream(toCheck); //grab the file with the hashed word
				ObjectInputStream in = new ObjectInputStream(fileIn); //wrap it in a objectinputstream
				@SuppressWarnings("unchecked")
				HashSet<String> urls = (HashSet<String>) in.readObject(); //grab the object and save it. suppress the warnings (no idea bout a better way)
				in.close();
				fileIn.close();
				//close all inputstreams
				results.addAll(urls); //add the urls from the object to the total thing.
			}catch(Exception e) {
				continue;
			}
		}
		return results.toArray(new String[0]);
		
	}
	public void Save(String word, String URL) throws IOException{
		File toCheck = new File(path + Hash.MD5(word.toLowerCase()) + ".ser"); //hash the word to get rid of invalid chars
		HashSet<String> urls = new HashSet<String>();
		if(toCheck.exists()){ //check to see if it exists, so we can ADD to the list, not replace it.
			urls = getObject(toCheck.getCanonicalPath());
		}
		try {
			urls.add(URL); //add the URL to it
			FileOutputStream fileOut = new FileOutputStream(toCheck); //get the output stream to the file
	        ObjectOutputStream out = new ObjectOutputStream(fileOut); //wrap in objectoutput stream
		    out.writeObject(urls); //write it
		    out.close();
		    fileOut.close();
		    //close streams
		}
		catch(IOException i) {
	        i.printStackTrace();
		}		
	}
	HashSet<String> getObject(String path){
		try(FileInputStream fileIn = new FileInputStream(path)){
	         ObjectInputStream in = new ObjectInputStream(fileIn); //try the input streams and wrap it
	         @SuppressWarnings("unchecked")
			 HashSet<String> urls = (HashSet<String>)in.readObject(); //cache the hashset, suppress warnings.
	         in.close(); //close streams.
	         return urls;
	      }catch(Exception e) {
	    	  e.printStackTrace();
	          return new HashSet<String>();
	      }
	}
}
