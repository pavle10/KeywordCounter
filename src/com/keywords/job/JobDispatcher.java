package com.keywords.job;

import com.keywords.file_scanner.FileScanner;
import com.keywords.job.scanning_job.FileJob;
import com.keywords.job.scanning_job.IScanningJob;

public class JobDispatcher implements Runnable {
    private JobQueue jobQueue;
    private FileScanner fileScanner;

    public JobDispatcher(JobQueue jobQueue, FileScanner fileScanner) {
        this.jobQueue = jobQueue;
        this.fileScanner = fileScanner;
    }

    @Override
    public void run() {
        boolean toStop = false;

        while (!toStop) {
            IScanningJob job = jobQueue.dequeue();

            switch (job.getType()) {
                case FILE:
                    // TODO Add logic for file jobs
                    this.fileScanner.startScanner((FileJob)job);
                    break;
                case WEB:
                    // TODO Add logic for web jobs
                    System.out.println("Web job is redirecting...");
                    break;
                case STOP_JOB:
                    fileScanner.finishWork();
                    toStop = true;
                    break;
                default:
                    System.out.println("Invalid job was placed in queue!");
            }
        }

    }
}
