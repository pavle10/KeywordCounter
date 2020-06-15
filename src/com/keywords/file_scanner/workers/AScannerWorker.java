package com.keywords.file_scanner.workers;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AScannerWorker implements Callable<HashMap<String, Integer>> {

    protected HashMap<String, Integer> keywordsCount;
    protected AtomicBoolean isFinished;
    protected AtomicInteger counter;
}
