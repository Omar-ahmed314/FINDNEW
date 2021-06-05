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


/*Documentation of the DocumentInfoClass 
    
    this object will be created for every word for example 
    
    word1->{URL="some URL" , TF=someTF, Map(tag1->"someOccurens",tag2->"someoccurens")}
    URL="some URL" , TF=someTF, Map(tag1->"someOccurens",tag2->"someoccurens")->this the Document info instance . 



    every obejct of this class will have 
    1-URL of this Document 
    2-TF-> number of occurencs of the word in the document . 
    3-map that  capture where the word exists and number of occurences in this specific tag "place".
    

    this class has  nine functions some of them are set and get and one of them are print Information  of the Document ..
    the most important functions here are 
    1- addOccurences ->
    2- setOccurences ->it takes tag name and number of occurnce of the word in that tag name  if the doc had this tag before it will update the number of Occurneces  and if not it will set the tags and 
       the number of the occurences . 


*/



public class DocumentInfo implements java.io.Serializable {
    
    String URL;
    int TF ; 
    Map <String ,Integer>occurences; 
/*******************************************************************************************************************************************************/
    public DocumentInfo(String URL) {
        setURL(URL);
        occurences= new HashMap(); 
    }
    
/*******************************************************************************************************************************************************/  
    public void setURL(String URL){
        this.URL=URL ; 
    }
/*******************************************************************************************************************************************************/
    public String getURL(){
        return this.URL ; 
    }
/*******************************************************************************************************************************************************/
    public void setTF(int TF){
        this.TF=TF ; 
    }
/*******************************************************************************************************************************************************/
    public int getTF(){
        return this.TF ; 
    }
/*******************************************************************************************************************************************************/
    public void addOccurence(String tagName){
        if(occurences.containsKey(tagName)){
            Integer i = occurences.get(tagName); 
            i++; 
            occurences.replace(tagName, i); 
        }
        else
            occurences.put(tagName,1); 
    }
/*******************************************************************************************************************************************************/    
    public Integer getTagOccurences(String tagName){
        if(occurences.containsKey(tagName))
            return occurences.get(tagName); 
        else
            return 0 ; 
    }
/*******************************************************************************************************************************************************/
    public void setOccurence(String tagName, Integer numberOfOcc){
        if(occurences.containsKey(tagName))
            occurences.replace(tagName, numberOfOcc); 
        else
            occurences.put(tagName, numberOfOcc); 
    }
/*******************************************************************************************************************************************************/
    public void printInfo(){
        System.out.println(URL);
        System.out.println(TF);
        System.out.println(occurences);
    }
    
    
    
  
    
}
