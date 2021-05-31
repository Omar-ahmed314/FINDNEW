/**
 *
 * @author thebrownboy
 */
package indexer;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author thebrownboy
 */
public class IndexerManager implements Runnable{
    IndexerDatabase mainDatabase ; 
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
                   System.out.println("Error While Reading the source of the Page"+allURls[i]);
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
    public static void main(String args[]) throws InterruptedException, IOException{
        // it is supposed that we will read the previous clawer size and the  URLS
        
         
       
       
        IndexerManager manager = new IndexerManager();
        int previousSize;
        File databasefile=new File("Database.ser");
        if(databasefile.exists()){
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("Database.ser"));
            

            try {
                manager.mainDatabase=(IndexerDatabase)in.readObject();
            } catch (ClassNotFoundException ex) {

            }
            in = new ObjectInputStream(new FileInputStream("PrevoiousSize.ser"));
            try {
                previousSize = (int) in.readObject();
            } catch (ClassNotFoundException ex) {
                previousSize=0 ; 
            }
          
        }
        else    
            previousSize=0; 
            
       
            
            
        
        Set<String> URLs = manager.readURL();
        manager.allURls=new String[URLs.size()]; 
        URLs.toArray(manager.allURls); 
        
        if(manager.allURls.length==previousSize){
            //in.close();
            return;
        }
        //in.close();
        String []temp=new String[manager.allURls.length-previousSize];
        int j =0 ; 
        for(int i = previousSize; i<manager.allURls.length;i++){
            temp[j]=manager.allURls[i]; 
            j++; 
        }
        manager.allURls=temp ; 
            

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
        
        
        
        ObjectOutputStream out = new ObjectOutputStream(
        new FileOutputStream("Database.ser")
        );
        out.writeObject(manager.mainDatabase);
        out.flush();
        out.close();
        
        out = new ObjectOutputStream(
        new FileOutputStream("PrevoiousSize.ser")
        );
        out.writeObject(manager.allURls.length);
        
         
        System.out.println(manager.wordOccurences.keySet());
        System.out.println("Hello");
        Gson gson = new Gson(); 
        String json = gson.toJson(manager.mainDatabase.indexerMap); 
        File file = new File("DB.txt");
        file.createNewFile(); 
        
        FileWriter fw = new FileWriter("DB.json"); 
        fw.write(json);
        fw.close();
        
        
        for (DocumentInfo a : manager.mainDatabase.indexerMap.get("python")){
            a.printInfo();
        }
        
    }
    
}




