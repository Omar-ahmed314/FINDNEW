package main.java.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Robot
{
    String[] subLinks;
    int size;
    int count=0;
    BufferedReader in;

    public Robot(String[] subLinks,int size,String link)
    {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://google.com/robots.txt").openStream()))) {
            String line = null;
            while((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.subLinks=subLinks;
        this.size=size;
    }


}


