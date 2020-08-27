package dataClass;

import funcScripts.TimeTestLogger;

public class TestTimer {
    long tsStartTime = 0;
    long tsEndTime = 0;
    long tsTime = 0;
    long tjStartTime = 0;
    long tjEndTime = 0;
    long tjTime = 0;

    public TestTimer(String contextPath){
        TimeTestLogger.initializeLogFile(contextPath);
    }


    public void startTSTimer(){
        this.tsStartTime = System.nanoTime();
    }


    public void endTSTimer(){
        this.tsEndTime = System.nanoTime();
        this.tsTime = this.tsEndTime - this.tsStartTime;
        TimeTestLogger.tsTimeTestWriteLog(this.tsTime);
    }


    public void startTJTimer(){
        this.tjStartTime = System.nanoTime();
    }


    public void pauseTJTimer(){
        this.tjEndTime = System.nanoTime();
        this.tjTime += this.tjEndTime - this.tjStartTime;
    }


    public void resumeTJTimer(){
        this.tjStartTime = System.nanoTime();
    }


    public void endTJTimer(){
        this.tjEndTime = System.nanoTime();
        this.tjTime += this.tjEndTime - this.tjStartTime;
        TimeTestLogger.tjTimeTestWriteLog(this.tjTime);
    }


    public void endLogger(){
        this.tsStartTime = 0;
        this.tsEndTime = 0;
        this.tsTime = 0;
        this.tjStartTime = 0;
        this.tjEndTime = 0;
        this.tjTime = 0;
        TimeTestLogger.closeTimeTestLogFile();
    }
}
