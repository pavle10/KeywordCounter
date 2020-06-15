package com.keywords.directory_crawler;

public class Directory {
    private String absolutPath;
    private String name;

    public Directory(String absolutPath) {
        this.absolutPath = absolutPath;

        String[] temp = absolutPath.split("\\\\");
        this.name = temp[temp.length - 1];
    }

    public String getAbsolutPath() {
        return absolutPath;
    }

    public String getName() {
        return name;
    }
}
