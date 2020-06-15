package com.keywords.job.scanning_job;

import com.keywords.directory_crawler.Directory;
import com.keywords.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Future;

public class FileJob implements IScanningJob {
    private String corpus;
    private String path;
    private ArrayList<File> files;

    public FileJob(Directory directory) {
        corpus = directory.getName();
        path = directory.getAbsolutPath();
        initFiles();
    }

    @Override
    public ScanningType getType() {
        return ScanningType.FILE;
    }

    @Override
    public String getQuery() {
        return Constants.FILE_JOB + corpus;
    }

    @Override
    public Future<Map<String, Integer>> initiate() {
        return null;
    }

    public ArrayList<File> getFiles() { return this.files; }

    private void initFiles() {
        this.files = new ArrayList<>();

        File corpus = new File(this.path);
        File[] files = corpus.listFiles();

        if (files != null) {
            for (File file : files) {
                this.files.add(file);
            }
        }
    }
}
