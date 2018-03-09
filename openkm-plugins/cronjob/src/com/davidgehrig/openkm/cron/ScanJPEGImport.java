package com.davidgehrig.openkm.cron;

import com.davidgehrig.openkm.cron.converter.DocumentConverter;
import com.davidgehrig.openkm.cron.converter.ImageMagickDocumentConverter;
import com.openkm.module.db.stuff.DbSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanJPEGImport {
    private static Logger log = LoggerFactory.getLogger(ScanJPEGImport.class);


    public static void main(String[] args) {
        cronTask();
    }

    public static String cronTask() {
        DocumentConverter documentConverter = new ImageMagickDocumentConverter();
        DocumentImporter.triggerImport("/okm:root/Post-Eingang/", "/opt/openkm/repository/scans", DbSessionManager.getInstance().getSystemToken(), documentConverter);
        return "";
    }


}