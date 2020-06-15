package com.keywords.file_scanner.workers;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class CorpusScannerWorker extends AScannerWorker {
    private String query;

    public CorpusScannerWorker(String query,
                               HashMap<String, Integer> keywordsCount,
                               AtomicBoolean isFinished) {
        this.query = query;
        this.keywordsCount = keywordsCount;
        this.isFinished = isFinished;

    }

    @Override
    public HashMap<String, Integer> call() throws Exception {
        HashMap<String, HashMap<String, Integer>> toReturn = new HashMap<>();

        while (!this.isFinished.get()) {
            Thread.sleep(100);
        }
        Thread.sleep(15000);

        return this.keywordsCount;
    }
}
