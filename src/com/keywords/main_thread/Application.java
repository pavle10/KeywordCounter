package com.keywords.main_thread;

import com.keywords.directory_crawler.DirectoryCrawler;
import com.keywords.file_scanner.FileScanner;
import com.keywords.job.JobDispatcher;
import com.keywords.job.JobQueue;
import com.keywords.job.scanning_job.StopJob;
import com.keywords.result_retriever.ResultRetiever;
import com.keywords.utils.Constants;
import com.keywords.utils.SystemConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class Application implements Runnable, Commands {
    private Scanner inputScanner = new Scanner(System.in);

    private JobQueue jobQueue;
    private CopyOnWriteArrayList<String> directories;
    private AtomicBoolean stopDirectoryCrawler;
    private ResultRetiever resultRetiever;

    public Application() {
        // Setup system config
        SystemConfig config = new SystemConfig();
        try {
            config.setConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Setup Directory crawler
        this.jobQueue = new JobQueue(Constants.JOBQUEUE_LIMIT);
        this.stopDirectoryCrawler = new AtomicBoolean(false);
        this.directories = new CopyOnWriteArrayList<>();
        Thread directoryCrawlerThread = new Thread(new DirectoryCrawler(stopDirectoryCrawler,
                                                                        directories,
                                                                        config.getFileCorpusPrefix(),
                                                                        config.getDirCrawlerSleepTime(),
                                                                        jobQueue));
        // Setup ResultRetriever
        this.resultRetiever = new ResultRetiever();

        // Setup FileScanner
        FileScanner fileScanner = new FileScanner(config.getKeywords(),
                                                  config.getFileScanningSizeLimit(),
                                                  this.resultRetiever);

        // Setup JobDispatcherThread
        Thread jobDispatcherThread = new Thread(new JobDispatcher(jobQueue, fileScanner));

        directoryCrawlerThread.start();
        jobDispatcherThread.start();

        boolean toStop = false;

        while (!toStop) {
            System.out.println("Enter command:");
            String[] input = inputScanner.nextLine().split(" ");
            String prefix = input[0];

            switch (prefix) {
                case Constants.ADD_DIR :
                    if (input.length > 1) ad(input[1]);
                    else System.out.println("No directory is specified!");
                    break;
                case Constants.GET_RESULT :
                    if (input.length > 1) get(input[1]);
                    else System.out.println("Corpus is not specified!");
                    break;
                case Constants.QUERY_RESULT :
                    if (input.length > 1) query(input[1]);
                    else System.out.println("Corpus is not specified");
                    break;
                case Constants.CFS :
                    cfs();
                    break;
                case Constants.STOP:
                    stop();
                    toStop = true;
                    break;
                default :
                    System.out.println("Invalid command. Please enter valid one!");

            }
        }

        try {
            directoryCrawlerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("System is stopped.");
        System.out.println("Bye");
    }

    @Override
    public void run() {
    }

    @Override
    public void ad(String dir) {
        File file = new File("src/" + Constants.RESOURCES + dir);
        if (!file.exists()) {
            System.out.println("File "+ dir +" doesn't exist!");
            return;
        }
        System.out.println("Adding new directory " + dir + " for scanning...");
        if (!directories.contains(dir)) directories.add(dir);
    }

    @Override
    public void aw(String url) {

    }

    @Override
    public void get(String param) {
        if (param.contains("summary")) {
            try {
                System.out.println(this.resultRetiever.getSummary(false));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Future<HashMap<String, Integer>> result = this.resultRetiever.getCorpusResult(param);
            if (result != null) {
                try {
                    System.out.println(result.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Corpus doesn't exist!");
            }
        }
    }

    @Override
    public void query(String param) {
        try {
            if (param.contains("summary")) {
                System.out.println(this.resultRetiever.querySummary());
            } else {
                System.out.println(this.resultRetiever.queryCorpusResult(param));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cws() {

    }

    @Override
    public void cfs() {
        this.resultRetiever.eraseSummary();
    }

    @Override
    public void stop() {
        System.out.println("Stopping the system...");

        this.stopDirectoryCrawler.set(true);
        this.jobQueue.enqueue(new StopJob());
        this.resultRetiever.finishWork();
    }
}
