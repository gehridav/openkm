package com.davidgehrig.openkm.cron.converter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface DocumentConverter  {
    public void rotateAndConvertToPdf(Set<Path> pages, String pdfFileName) throws IOException, InterruptedException;
}
