package com.davidgehrig.openkm.cron;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.core.ItemExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


/**
 * Loads files from the file system and stores them in the given openKM folder.
 */
public class DocumentImporter {

    private static Logger log = LoggerFactory.getLogger(DocumentImporter.class);


    public static void triggerImport(String okmPath, String folderpath, String token) {

        OKMDocument document = OKMDocument.getInstance();
        OKMFolder folder = OKMFolder.getInstance();

        try {
            log.info("Scanning " + folderpath);

            List<Path> documents = XeroxFileLoader.loadStableDocuments(folderpath,2);

            if (!documents.isEmpty()) {

                for (Path file : documents) {

                    try {
                        document.createSimple(token, okmPath + file.getFileName(), Files.newInputStream(file));
                        log.debug("Created " + okmPath + file.getFileName());
                    } catch (ItemExistsException e) {
                        log.warn("Document"+ file.getFileName()+ "already in System");
                    }

                    deleteFile(file);
                }
            }
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
    }

    private static void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("Couldn't delete file with path: " + path, e);
        }
    }

}
