package main.java.crawler;

import java.util.ArrayList;

public class Main
{
    private static final int NUM_THREADS=3;

    public static void main(String[] args)
    {
            Thread[] threads = new Thread[NUM_THREADS];
            String[] links = new String[NUM_THREADS];
            links[0] = "https://abcnews.go.com";
            links[1] = "https://www.npr.org";
            links[2] = "https://www.nytimes.com";



        /*
        for(int i=0;i<NUM_THREADS;i++)
        {

            threads[i]=new Thread(new Spider(links[i],i));
        }
        for(int i=0;i<NUM_THREADS;i++)
        {
            try
            {
                threads[i].join();
            }
            catch(InterruptedException ie)
            {
                ie.printStackTrace();
            }

        }
        */
            ArrayList<Spider> spiders = new ArrayList<>();
            spiders.add(new Spider("https://abcnews.go.com", 1));
            spiders.add(new Spider("https://www.npr.org", 2));
            spiders.add(new Spider("https://www.nytimes.com", 3));
            for (Spider s : spiders) {
                try {
                    s.getThread().join();

                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }


    }
}
