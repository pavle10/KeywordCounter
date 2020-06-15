package com.keywords.job;

import com.keywords.job.scanning_job.IScanningJob;

public interface IBlockingQueue {
    void enqueue(IScanningJob job);
    IScanningJob dequeue();
}
