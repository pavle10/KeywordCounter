package com.keywords.result_retriever;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultRetiever {
    private ExecutorService threadPool;
    private CompletionService<Integer> service;
    private ConcurrentHashMap<String, Future<HashMap<String, Integer>>> corpusHashMap;
    private AtomicInteger corpusSummary;
    private HashMap<String, Integer> corpusKeywordsSummary;

    public ResultRetiever() {
        this.threadPool = Executors.newCachedThreadPool();
        this.service = new ExecutorCompletionService<>(threadPool);
        this.corpusHashMap = new ConcurrentHashMap<>();
        this.corpusSummary = new AtomicInteger(-1);
        this.corpusKeywordsSummary = new HashMap<>();
    }

    public void putCorpusResult(String query, Future<HashMap<String, Integer>> future) {
        this.corpusHashMap.put(query, future);
    }

    public Future<HashMap<String, Integer>> getCorpusResult(String query) {
        return this.corpusHashMap.getOrDefault(query, null);
    }

    public String queryCorpusResult(String query) throws ExecutionException, InterruptedException {
        Future<HashMap<String, Integer>> result = this.corpusHashMap.getOrDefault(query, null);

        if (result == null) return "Corpus doesn't exist!";
        if (result.isDone()) return result.get().toString();
        return "Result is not yet done!";

    }

    public String getSummary(boolean isQuery) throws ExecutionException, InterruptedException {
        if (this.corpusSummary.compareAndSet(-1, 0)) {

            int counter = 0;

            for (String key : this.corpusHashMap.keySet()) {
                if (isQuery && !this.corpusHashMap.get(key).isDone()) {
                    this.corpusSummary.set(-1);
                    return "Some scanning not yet done!";
                }
                while (!this.corpusHashMap.get(key).isDone()) Thread.sleep(10);
                service.submit(new SummaryWorker(this.corpusHashMap.get(key).get()));
                counter++;
            }

            for (String key : this.corpusHashMap.keySet()) {
                HashMap<String, Integer> tempHashMap = this.corpusHashMap.get(key).get();

                for (String keyword : tempHashMap.keySet()) {
                    if (this.corpusKeywordsSummary.containsKey(keyword)) {
                        int temp = this.corpusKeywordsSummary.get(keyword);
                        temp += tempHashMap.get(keyword);
                        this.corpusKeywordsSummary.put(keyword, temp);
                    } else {
                        this.corpusKeywordsSummary.put(keyword, tempHashMap.get(keyword));
                    }
                }
            }

            for (int i = 0; i < counter; i++) {
                Future<Integer> temp = service.take();
                this.corpusSummary.getAndAdd(temp.get());
            }
        }
        String toReturn = "Summary: " + this.corpusSummary.get() + " Summary by keyword: " + this.corpusKeywordsSummary.toString();

        return toReturn;
    }

    public String querySummary() throws ExecutionException, InterruptedException {
        return getSummary(true);
    }

    public void eraseSummary() {
        this.corpusSummary.getAndSet(-1);
        this.corpusKeywordsSummary.clear();
    }

    public void finishWork() {
        this.threadPool.shutdown();
    }


}
