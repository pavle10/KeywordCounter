package com.keywords.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class SystemConfig {
    private InputStream inputStream;
    private ArrayList<String> keywords;
    private String fileCorpusPrefix;
    private long dirCrawlerSleepTime; // in milliseconds
    private long fileScanningSizeLimit; // in bytes
    private int hopCount;
    private long urlRefreshTime; // in milliseconds

    public void setConfig() throws IOException {

        try {
            Properties properties = new Properties();
            String fileName = Constants.CONFIG_FILE_NAME;

            inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

            if (inputStream == null) throw new FileNotFoundException("Property file not found");

            properties.load(inputStream);

            keywords = new ArrayList<String>();
            for (String keyword : properties.getProperty(Constants.KEYWORDS).split(",")) keywords.add(keyword);
            fileCorpusPrefix = properties.getProperty(Constants.FILE_CORPUS_PREFIX);
            dirCrawlerSleepTime = Long.parseLong(properties.getProperty(Constants.DIR_CRAWLER_SLEEP_TIME));
            fileScanningSizeLimit = Long.parseLong(properties.getProperty(Constants.FILE_SCANNING_SIZE_LIMIT));
            hopCount = Integer.parseInt(properties.getProperty(Constants.HOP_COUNT));
            urlRefreshTime = Long.parseLong(properties.getProperty(Constants.URL_REFRESH_TIME));

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public String getFileCorpusPrefix() {
        return fileCorpusPrefix;
    }

    public long getDirCrawlerSleepTime() {
        return dirCrawlerSleepTime;
    }

    public long getFileScanningSizeLimit() {
        return fileScanningSizeLimit;
    }

    public int getHopCount() {
        return hopCount;
    }

    public long getUrlRefreshTime() {
        return urlRefreshTime;
    }
}
