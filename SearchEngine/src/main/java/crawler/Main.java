package main.java.crawler;

import java.util.ArrayList;

public class Main
{
    public static final int NUM_THREADS=5;
    public static void main(String[] args)
    {
        String start_url="https://www.bbc.com/";
        Thread[] threads=new Thread[NUM_THREADS];
        Spider spider=new Spider();

        Spider.setNotVisited("https://www.bbc.com/");
        Spider.setNotVisited("https://www.cnn.com");
        Spider.setNotVisited("https://www.coursera.org/");
        Spider.setNotVisited("https://www.reddit.com");
        Spider.setNotVisited("https://www.washingtonpost.com/");
        //spider.crawl();
        for(int i=0;i<NUM_THREADS;i++)
        {
            threads[i]=new Thread(spider);
            threads[i].start();
        }
       /* for (Thread t : threads) {
            try {
                t.join();

            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }*/


    }
}
