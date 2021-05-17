/**
 *
 * @author thebrownboy
 */
package indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;


/**
 *
 * @author thebrownboy
 */
public class IndexerManager {
    private IndexerDatabase mainDatabase ; 
    Map<String ,Map<String, Integer>> tagsContent;
     
    Map<String,Integer> wordOccurences;

    public IndexerManager() {
        mainDatabase= new IndexerDatabase(); 
    }
    
     
    public String readURL(){
        String URL="" ; 
        File myObj = new File("ClawerURLs.txt");
        try (Scanner myReader = new Scanner(myObj)) {
            if (myReader.hasNextLine()) {
                 URL= myReader.nextLine();
            }
        }   catch (FileNotFoundException ex) {
                System.out.println("Error while Reading the file");
        }  
        
        return URL ; 
    }  
    public Map<String, Integer> getPageTextContent(String  source){
        ContentExtractor extractor=new ContentExtractor(); 
        return extractor.getPageWords(source);
    }
    public Map<String ,Map<String, Integer>>  getTagsContent(String source ){
        TagsTextExtractor tagsTextExtractor= new TagsTextExtractor(); 
        return tagsTextExtractor.getAllTagsText(source); 
    }
    void UpdatingDatabase(String URL,String tagName){
        Map<String,Integer> pTagMap=tagsContent.get(tagName); 
        for (Map.Entry<String,Integer> entry : pTagMap.entrySet()){
            if(wordOccurences.containsKey(entry.getKey())){// Iam sure that it will be true forever  bc any word that is in p it will definetly be in wordOccurences
                DocumentInfo doc=new DocumentInfo(URL);
                doc.setTF(wordOccurences.get(entry.getKey()));
                doc.setOccurence("p",entry.getValue());
                mainDatabase.addDocument(entry.getKey(), doc);
                
            }
        }
        
    }
    
    
    public String getPageSource(String URL) throws MalformedURLException, IOException{
        URL page = new URL(URL);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(page.openStream()));

        String inputLine, source=""; 
        while ((inputLine = in.readLine()) != null)
        {   source+=inputLine;
            source+="\n"; 
        }
        return source ; 
    }
    public static void main(String args[]){
        
        IndexerManager manager = new IndexerManager();
        String URL = manager.readURL();
       
        String source ="";
        try {
            source=manager.getPageSource(URL); 
            manager.wordOccurences= manager.getPageTextContent(source);

        }catch (IOException ex) {
               System.out.println("Error While Reading the source of the Page");
        }
        manager.tagsContent=manager.getTagsContent(source); 
        
        
        
        
    
    
    }
    
}

