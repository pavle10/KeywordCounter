package com.keywords.job;

import com.keywords.job.scanning_job.IScanningJob;

import java.util.ArrayList;

public class JobQueue implements IBlockingQueue {
    private ArrayList<IScanningJob> queue;
    private final int LIMIT;

    public JobQueue(int limit) {
        queue = new ArrayList<>();
        LIMIT = limit;
    }

    @Override
    public void enqueue(IScanningJob job) {
        synchronized (this) {
            while (queue.size() == LIMIT) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.add(job);
            notifyAll();
        }
    }

    @Override
    public IScanningJob dequeue() {
        IScanningJob job = null;

        synchronized (this) {
            while (queue.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            job = queue.get(0);
            queue.remove(0);
            notifyAll();
        }

        return job;
    }
}
