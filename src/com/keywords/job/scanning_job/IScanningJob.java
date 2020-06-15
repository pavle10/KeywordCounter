package com.keywords.job.scanning_job;

import java.util.Map;
import java.util.concurrent.Future;

public interface IScanningJob {
    ScanningType getType();
    String getQuery();
    Future<Map<String, Integer>> initiate();
}
