package com.keywords.utils;

public class Constants {
    // Config related
    public static final String RESOURCES = "com/keywords/resources/";
    public static final String CONFIG_FILE_NAME = RESOURCES + "app.properties";
    public static final String KEYWORDS = "keywords";
    public static final String FILE_CORPUS_PREFIX = "file_corpus_prefix";
    public static final String DIR_CRAWLER_SLEEP_TIME = "dir_crawler_sleep_time";
    public static final String FILE_SCANNING_SIZE_LIMIT = "file_scanning_size_limit";
    public static final String HOP_COUNT = "hop_count";
    public static final String URL_REFRESH_TIME = "url_refresh_time";

    // Commands
    public static final String ADD_DIR = "ad";
    public static final String ADD_WEB = "aw";
    public static final String GET_RESULT = "get";
    public static final String QUERY_RESULT = "query";
    public static final String CWS = "cws";
    public static final String CFS = "cfs";
    public static final String STOP = "stop";

    // Scanning jobs
    public static final int JOBQUEUE_LIMIT = 100;
    public static final String FILE_JOB = "FILE|";
    public static final String WEB_JOB = "WEB|";

}
