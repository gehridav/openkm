package com.davidgehrig.openkm.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * File loader for files scanned by a OKI scanner
 */
public class FileLoader {

    private static Logger log = LoggerFactory.getLogger(FileLoader.class);

    public static Map<String,Set<Path>> loadFile(String pathString) {
        final Path p = Paths.get(pathString);
        Map<String,Set<Path>> orderedDocuments = new HashMap<>();
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(p, "*.jpg")) {

            for(Path path : stream){
                if (path.getFileName().toString().length() > 20){

                    String documentId = path.getFileName().toString().substring(16,21);

                    if (orderedDocuments.containsKey(documentId)){
                        orderedDocuments.get(documentId).add(path);
                    } else {
                        Set<Path> pages = new TreeSet<>();
                        pages.add(path);
                        orderedDocuments.put(documentId, pages);
                    }
                }
            }

        } catch (IOException e) {
            log.error("Troubles loading the file: ", e);
        }

        return orderedDocuments;

    }
}
