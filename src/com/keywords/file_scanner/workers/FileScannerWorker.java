package com.keywords.file_scanner.workers;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FileScannerWorker extends AScannerWorker {
    private ArrayList<File> files;
    private ArrayList<String> keywords;

    public FileScannerWorker(HashMap<String, Integer> keywordsCount,
                             AtomicBoolean isFinished,
                             ArrayList<File> files,
                             ArrayList<String> keywords,
                             AtomicInteger counter) {
        this.keywordsCount = keywordsCount;
        this.isFinished = isFinished;
        this.files = files;
        this.keywords = keywords;
        this.counter = counter;
    }

    @Override
    public HashMap<String, Integer> call() throws Exception {
        calculateNumberOfKeywords();

        return null;
    }

    private void calculateNumberOfKeywords() {
        HashMap<String, Integer> hashMap = new HashMap<>();

        for (File file: this.files) {

            try {
                FileInputStream fileStream = new FileInputStream(file);
                InputStreamReader input = new InputStreamReader(fileStream);
                BufferedReader reader = new BufferedReader(input);
                String line;

                while ((line = reader.readLine()) != null) {
                    String [] data = line.split("\\s+");

                    for (int i = 0; i < data.length; i++) {
                        String temp = data[i].toLowerCase();
                        if (keywords.contains(temp)) {
                            if (hashMap.containsKey(temp)) {
                                int curr = hashMap.get(temp);
                                curr++;
                                hashMap.put(temp, curr);
                            }
                            else {
                                hashMap.put(temp, 1);
                            }
                        }
                    }
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        synchronized (this.keywordsCount) {

            for (String key : hashMap.keySet()) {
                if (this.keywordsCount.containsKey(key)) {
                    int curr = this.keywordsCount.get(key);
                    curr += hashMap.get(key);
                    this.keywordsCount.put(key, curr);
                }
                else {
                    this.keywordsCount.put(key, hashMap.get(key));
                }
            }
        }

        this.counter.getAndDecrement();
        if (this.counter.get() == 0) this.isFinished.getAndSet(true);

    }
}
