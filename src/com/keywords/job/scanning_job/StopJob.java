package com.keywords.job.scanning_job;

import java.util.Map;
import java.util.concurrent.Future;

public class StopJob implements IScanningJob {
    @Override
    public ScanningType getType() {
        return ScanningType.STOP_JOB;
    }

    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public Future<Map<String, Integer>> initiate() {
        return null;
    }
}
