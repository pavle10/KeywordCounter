package com.keywords.file_scanner;

import com.keywords.file_scanner.workers.CorpusScannerWorker;
import com.keywords.file_scanner.workers.FileScannerWorker;
import com.keywords.job.scanning_job.FileJob;
import com.keywords.result_retriever.ResultRetiever;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FileScanner {
    private ArrayList<String> keywords;
    private long fileScanningSizeLimit;
    private ExecutorService threadPool;
    private CompletionService<HashMap<String, Integer>> service;
    private ResultRetiever resultRetiever;

    public FileScanner(ArrayList<String> keywords, long fileScanningSizeLimit, ResultRetiever resultRetiever) {
        this.keywords = keywords;
        this.fileScanningSizeLimit = fileScanningSizeLimit;
        threadPool = Executors.newCachedThreadPool();
        service = new ExecutorCompletionService<>(threadPool);
        this.resultRetiever = resultRetiever;
    }

    public void startScanner(FileJob job) {
        HashMap<String, Integer> hashMap = new HashMap<>();
        AtomicBoolean isFinished = new AtomicBoolean(false);
        AtomicInteger counter = new AtomicInteger(0);

        Future<HashMap<String, Integer>> future = service.submit(new CorpusScannerWorker(job.getQuery(),
                                                                                    hashMap,
                                                                                    isFinished));
        // Put future into result retriver
        this.resultRetiever.putCorpusResult(job.getQuery(), future);

        ArrayList<File> files = new ArrayList<>();
        long currentSize = 0;

        for (File file : job.getFiles()) {
            files.add(file);
            currentSize += file.length();

            if (currentSize > this.fileScanningSizeLimit) {
                counter.getAndIncrement();
                service.submit(new FileScannerWorker(hashMap, isFinished, new ArrayList<>(files), this.keywords, counter));
                files.clear();
                currentSize = 0;
            }
        }
        if (!files.isEmpty()) {
            counter.getAndIncrement();
            service.submit(new FileScannerWorker(hashMap, isFinished, files, this.keywords, counter));
        }


    }

    public void finishWork() {
        threadPool.shutdown();
    }
}
