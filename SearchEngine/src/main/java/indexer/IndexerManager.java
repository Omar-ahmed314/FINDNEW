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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


/**
 *
 * @author thebrownboy
 */
public class IndexerManager implements Runnable{
    private IndexerDatabase mainDatabase ; 
    static final Object LOCK1 = new Object();
    String []allURls ; 
    int numberOfThreads ; 
   // Map<String ,Map<String, Integer>> tagsContent; // 
    
    Map<String,Map<String ,Map<String, Integer>>> tagsContent ;  // url->map(tag name->map(word->occurnece)); 
     
  //  Map<String,Integer> wordOccurences;
    
    Map<String,Map<String,Integer>> wordOccurences;  // url->map(word->occurences); 

    public IndexerManager() {
        mainDatabase= new IndexerDatabase(); 
        wordOccurences=new HashMap<>(); 
        tagsContent=new HashMap<>(); 
    }
    
     
    public Set<String> readURL(){
        Set <String>URLs= new HashSet<>(); 
        File myObj = new File("ClawerURLs.txt");
        try (Scanner myReader = new Scanner(myObj)) {
            while(myReader.hasNextLine()) {
                 URLs.add(myReader.nextLine());
            }
        }   catch (FileNotFoundException ex) {
                System.out.println("Error while Reading the file"+Thread.currentThread().getName());
        }  
        
        return URLs ; 
    }  
    public void run(){
        
        int start , end;
        System.out.println("I am thread "+Thread.currentThread().getName());
//        if(Thread.currentThread().getName().equals("1")){
//            start=0 ; 
//            end=allURls.length/2 ; 
//        }
//        else {
//            start = allURls.length/2; 
//            end= allURls.length ; 
//        }
            
        start=Integer.parseInt(Thread.currentThread().getName())*(allURls.length/numberOfThreads);
        if(Integer.parseInt(Thread.currentThread().getName())==numberOfThreads-1)
            end= allURls.length; 
        else 
            end=(Integer.parseInt(Thread.currentThread().getName())+1)*(allURls.length/numberOfThreads);
        
        
        
        for(int i = start ; i<end ; i++){
            String source ="";
            try {
                source=this.getPageSource(allURls[i]);  
                // getting the html code of the page 
                synchronized(LOCK1){
                this.wordOccurences.put(allURls[i],  this.getPageTextContent(source));// url of the doc -> map(words->Occurences);  
                }
            }catch (IOException ex) {
                   System.out.println("Error While Reading the source of the Page");
            }
             synchronized(LOCK1){
            this.tagsContent.put(allURls[i], this.getTagsContent(source)); 
             }
            
            synchronized(LOCK1){
                this.buildDatabase(allURls[i]);
            }
        }
        
    
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
        Map<String,Integer> pTagMap=tagsContent.get(URL).get(tagName);// every url has its specific tags and every tag has its specific words and occurences .  
        for (Map.Entry<String,Integer> entry : pTagMap.entrySet()){
            if(wordOccurences.get(URL).containsKey(entry.getKey())){// Iam sure that it will be true forever  bc any word that is in p it will definetly be in wordOccurences
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
                doc.setTF(wordOccurences.get(URL).get(entry.getKey()));
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
    public static void main(String args[]) throws InterruptedException{
        
        IndexerManager manager = new IndexerManager();
        Set<String> URLs = manager.readURL();
        manager.allURls=new String[URLs.size()]; 
        URLs.toArray(manager.allURls); 
        
        //manager.run();
        int numberOfCores= Runtime.getRuntime().availableProcessors(); 
        manager.numberOfThreads=numberOfCores ; 
        Thread []threads= new Thread[numberOfCores]; 
        for(int i = 0 ; i<numberOfCores; i++){
            threads[i]=new Thread(manager); 
            threads[i].setName(Integer.toString(i));
            threads[i].start();
        }
      
        for(int i = 0 ; i<numberOfCores; i++){
            threads[i].join();
        }
        
        System.out.println(manager.wordOccurences.keySet());
        System.out.println("Hello");
        for (DocumentInfo a : manager.mainDatabase.indexerMap.get("seo")){
            a.printInfo();
        }
        
    }
    
}




