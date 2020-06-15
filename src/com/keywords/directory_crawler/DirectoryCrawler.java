package com.keywords.directory_crawler;

import com.keywords.job.JobQueue;
import com.keywords.job.scanning_job.FileJob;
import com.keywords.utils.Constants;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class DirectoryCrawler implements Runnable {
    private CopyOnWriteArrayList<String> directories;
    private AtomicBoolean toStop;
    private final String corpusPrefix;
    private final long sleepTime;
    private HashMap<String, Long> files;
    private JobQueue jobQueue;

    public DirectoryCrawler(AtomicBoolean toStop,
                            CopyOnWriteArrayList<String> directories,
                            String corpusPrefix,
                            long sleepTime,
                            JobQueue jobQueue) {
        this.toStop = toStop;
        this.directories = directories;
        this.corpusPrefix =  corpusPrefix;
        this.sleepTime = sleepTime;
        this.jobQueue = jobQueue;
        files = new HashMap<>();
    }

    @Override
    public void run() {
        while (!toStop.get()) {
            for (String dirName : directories) {
                Queue<Directory> queue = new LinkedList<>();
                addToQueue("src/" + Constants.RESOURCES + dirName, queue);

                while (!queue.isEmpty()) {
                    Directory directory = queue.remove();
                    addToQueue(directory.getAbsolutPath(), queue);
                }
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addToQueue(String absolutPath, Queue<Directory> queue) {
        File dir = new File(absolutPath);
        File[] dirList = dir.listFiles();

        if (dirList != null) {
            boolean added = false;
            for (File d : dirList) {
                if (d.isDirectory()) {
                    Directory tempDir = new Directory(d.getAbsolutePath());
                    queue.add(new Directory(d.getAbsolutePath()));
                } else {
                    // Check file for job
                    Directory temp = new Directory(absolutPath);
                    if (temp.getName().startsWith(corpusPrefix)) {
                        if (!files.containsKey(d.getAbsolutePath()) ||
                                files.get(d.getAbsolutePath()) != d.lastModified()) {
                            files.put(d.getAbsolutePath(), d.lastModified());
                            if (!added) {
                                jobQueue.enqueue(new FileJob(temp));
                                added = true;
                            }
                        }
                    }
                }
            }
        } else System.out.println("Directory: " + dir + " doesn't exist!");

    }
}
