package com.keywords.result_retriever;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class SummaryWorker implements Callable<Integer> {
    private HashMap<String, Integer> hashMap;

    public SummaryWorker(HashMap<String, Integer> hashMap) {
        this.hashMap = hashMap;
    }

    @Override
    public Integer call() throws Exception {
        int result = 0;

        for (String key : this.hashMap.keySet()) {
            result += this.hashMap.get(key);
        }

        return result;
    }
}
