package com.example.movies.threads;

import com.example.movies.utilities.SearchInterface;

public class ThreadWait extends Thread {

    private String text;
    private long millis;
    private volatile boolean alive;
    private SearchInterface searchInterface;

    public ThreadWait(SearchInterface searchInterface, String text, long millis) {
        this.searchInterface = searchInterface;
        this.alive = true;
        this.millis = millis;
        this.text = text;
    }

    @Override
    public void run() {
        super.run();
        try {
            Thread.sleep(millis);
            if (alive) {
                searchInterface.search(text);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        this.alive = false;
    }

}