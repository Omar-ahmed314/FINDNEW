package main.java.crawler;

import java.sql.*;
import java.util.ArrayList;

public class Main
{
    public static final int NUM_THREADS=5;
    public static void main(String[] args)
    {
        //String start_url="https://www.bbc.com/";
        Thread[] threads=new Thread[NUM_THREADS];
        dbSpider spider=new dbSpider();

        /*Spider.setNotVisited("https://www.bbc.com/");
        Spider.setNotVisited("https://www.cnn.com");
        Spider.setNotVisited("https://www.coursera.org/");
        Spider.setNotVisited("https://www.reddit.com");
        Spider.setNotVisited("https://www.washingtonpost.com/");*/

        /*Connection connection=null;
        Statement statement= null;
        ResultSet resultset=null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler_db", "root", "data6OO6suck5");
            statement = connection.createStatement();
            //resultset = statement.executeQuery("SELECT COUNT(*) FROM not_visited");
            //String link = "https://www.washingtonpost.com/";
            //String re;
            //if (resultset.next()) {
                //System.out.println(resultset.getString("count(*)"));
                //resultset = statement.executeQuery("SELECT EXISTS(SELECT * FROM not_visited WHERE link='" + link + "') AS link;");
                //resultset.next();
                //re = resultset.getString("link");
                //System.out.println(re);
                //System.out.println(num);
            //}
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }*/


        //spider.crawl();
       for(int i=0;i<NUM_THREADS;i++)
        {
            threads[i]=new Thread(spider);
            threads[i].start();
        }
        /*for (Thread t : threads) {
            try {
                t.join();

            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }*/


    }
}
