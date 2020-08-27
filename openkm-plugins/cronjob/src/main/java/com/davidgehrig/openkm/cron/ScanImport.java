package com.davidgehrig.openkm.cron;

import com.openkm.module.db.stuff.DbSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanImport {
    private static Logger log = LoggerFactory.getLogger(ScanImport.class);


    public static void main(String[] args) {
        cronTask();
    }

    public static String cronTask() {
        DocumentImporter.triggerImport("/okm:root/Post-Eingang/", "/opt/openkm/repository/scans", DbSessionManager.getInstance().getSystemToken());
        return "";
    }


}