package com.davidgehrig.openkm.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class XeroxFileLoader {
    private static Logger log = LoggerFactory.getLogger(XeroxFileLoader.class);

    public static List<Path> loadFolders(String pathString) {
        List<Path> directories = new ArrayList<>();
        try {
            directories.addAll(Files.walk(Paths.get(pathString))
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            log.error("Troubles loading the directories: ", e);
        }
        return directories;
    }

    public static List<Path> loadDocuments(String pathString) {
        List<Path> documents = new ArrayList<>();
        try {
            documents.addAll(Files.walk(Paths.get(pathString))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            log.error("Troubles loading the directories: ", e);
        }
        return documents;
    }

    public static List<Path> loadStableDocuments(String pathString, int waitingTime) {
        List<Path> documents = loadDocuments(pathString);

        boolean hasChanged = true;

        while (hasChanged) {
            try {
                TimeUnit.SECONDS.sleep(waitingTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            List<Path> documentsAfterSleep = loadDocuments(pathString);

            if (!documents.equals(documentsAfterSleep)) {
                log.info("Content of folder has changed, wait another %ss", waitingTime);
            } else {
                hasChanged = false;
            }
        }
        return documents;
    }
}
