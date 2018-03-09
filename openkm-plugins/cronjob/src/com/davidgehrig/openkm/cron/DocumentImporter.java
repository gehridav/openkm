package com.davidgehrig.openkm.cron;

import com.davidgehrig.openkm.cron.converter.DocumentConverter;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.core.ItemExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Loads jpg files from the file system, rotates those to fix the scanner misalignment and then converts the pages to
 * one PDF file. Stores the pdfs in the given openKM folder.
 */
public class DocumentImporter {

    private static Logger log = LoggerFactory.getLogger(DocumentImporter.class);

    private static Map<String, Set<Path>> waitForDocumentChanges(String path, Map<String, Set<Path>> documents) {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Map<String, Set<Path>> documentsAfter5s = FileLoader.loadFile(path);

        if (!documents.equals(documentsAfter5s)) {
            log.info("Content of folder has changed, wait another 5s");
            documentsAfter5s = waitForDocumentChanges(path, documentsAfter5s);
        }

        return documentsAfter5s;
    }

    public static void triggerImport(String okmPath, String fldpath, String token, DocumentConverter documentConverter) {

        OKMDocument document = OKMDocument.getInstance();
        OKMFolder folder = OKMFolder.getInstance();

        try {
            log.info("Scanning " + fldpath);

            Map<String, Set<Path>> documents = FileLoader.loadFile(fldpath);

            if (!documents.isEmpty()) {

                documents = waitForDocumentChanges(fldpath, documents);

                for (Map.Entry<String, Set<Path>> entry : documents.entrySet()) {


                    Set<Path> pages = entry.getValue();
                    String pdfFileName = pages.iterator().next().toString().replace(".jpg", ".pdf");

                    documentConverter.rotateAndConvertToPdf(pages, pdfFileName);

                    Path rotatedFile = Paths.get(pdfFileName);
                    try {
                        document.createSimple(token, okmPath + rotatedFile.getFileName(), Files.newInputStream(rotatedFile));
                        log.debug("Created " + okmPath + rotatedFile.getFileName());
                    } catch (ItemExistsException e) {
                        log.warn("Document"+ rotatedFile.getFileName()+ "already in System");
                    }

                    deleteFiles(pages);
                    deleteFile(rotatedFile);
                }
            }
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
    }

    private static void deleteFiles(Set<Path> pages) {
        pages.stream().forEach(path -> deleteFile(path));
    }

    private static void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("Couldn't delete file with path: " + path, e);
        }
    }

}
