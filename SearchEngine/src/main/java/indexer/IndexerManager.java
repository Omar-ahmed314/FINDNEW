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
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


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
    
     
    public Set<String> readURL(){
        Set <String>URLs= new HashSet<>(); 
        File myObj = new File("ClawerURLs.txt");
        try (Scanner myReader = new Scanner(myObj)) {
            while(myReader.hasNextLine()) {
                 URLs.add(myReader.nextLine());
            }
        }   catch (FileNotFoundException ex) {
                System.out.println("Error while Reading the file");
        }  
        
        return URLs ; 
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
                if(mainDatabase.containsWord(entry.getKey())){//write your comments 
                    // if your database stored this word before 
                    // so you have two choices whether to update the doc with the position
                    // of the occurance or to add a new doc  
                    Set<DocumentInfo> wordDocs=mainDatabase.getWordSet(entry.getKey()); 
                    boolean exist= false; 
                    for(DocumentInfo a : wordDocs){
                        if(a.getURL().equals(URL)){
                            // that means that I put this doc by another tag name
                            a.setOccurence(tagName, entry.getValue());
                            exist=true; 
                             
                        }
                    }
                    if(exist)
                        continue;
                }
                // write your comments 
                // this code will be executed in two senarios 
                // if the word was not in the database 
                // ror this doc is newly  related with this word 
                // the previous code will be executed if and only if 
                // the doc was related with the word but we have to update the occurence of it 
                DocumentInfo doc=new DocumentInfo(URL);
                doc.setTF(wordOccurences.get(entry.getKey()));
                doc.setOccurence(tagName,entry.getValue());
                mainDatabase.addDocument(entry.getKey(), doc);
                
                // suppose that there is a word in h1 and p how can your data base act 
                
            }
        }
        
    }
    public void buildDatabase(String URL){
       for(int i = 1 ; i<=6; i++){
             UpdatingDatabase(URL, "h"+i);
         }
         UpdatingDatabase(URL, "a");
         UpdatingDatabase(URL, "p");
         UpdatingDatabase(URL, "li");
         UpdatingDatabase(URL, "title");
         //UpdatingDatabase(URL, "meta");
         //UpdatingDatabase(URL, "div");
         UpdatingDatabase(URL, "ol");
         UpdatingDatabase(URL, "dt");
         UpdatingDatabase(URL, "dl");
         UpdatingDatabase(URL, "label");
         UpdatingDatabase(URL, "button");
        
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
        Set<String> URLs = manager.readURL();
        for(String a : URLs){
            String source ="";
            try {
                source=manager.getPageSource(a); 
                manager.wordOccurences= manager.getPageTextContent(source);

            }catch (IOException ex) {
                   System.out.println("Error While Reading the source of the Page");
            }
            manager.tagsContent=manager.getTagsContent(source); 
            System.out.println(manager.tagsContent);
            manager.buildDatabase(a);
        }
        
        for (DocumentInfo a : manager.mainDatabase.indexerMap.get("python")){
            a.printInfo();
        }
        
    }
    
}




