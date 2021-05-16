/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author thebrownboy
 */
public class DocumentInfo {
    
    String URL;
    int TF ; 
    Map <String ,Integer>occurences; 

    public DocumentInfo(String URL) {
        setURL(URL);
        occurences= new HashMap(); 
    }
    
    
    public void setURL(String URL){
        this.URL=URL ; 
    }
    
    public String getURL(){
        return this.URL ; 
    }
    public void setTF(int TF){
        this.TF=TF ; 
    }
    public int getTF(){
        return this.TF ; 
    }
    
    public void addOccurence(String tagName){
        if(occurences.containsKey(tagName)){
            Integer i = occurences.get(tagName); 
            i++; 
            occurences.replace(tagName, i); 
        }
        else
            occurences.put(tagName,1); 
    }
    public Integer getTagOccurences(String tagName){
        if(occurences.containsKey(tagName))
            return occurences.get(tagName); 
        else
            return 0 ; 
    }
    public void setOccurence(String tagName, Integer numberOfOcc){
        if(occurences.containsKey(tagName))
            occurences.replace(tagName, numberOfOcc); 
        else
            occurences.put(tagName, numberOfOcc); 
    }  
}
