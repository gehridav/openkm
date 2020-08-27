package com.davidgehrig.openkm.cron.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ImageMagickDocumentConverter implements DocumentConverter{

    private static Logger log = LoggerFactory.getLogger(ImageMagickDocumentConverter.class);

    public void rotateAndConvertToPdf(Set<Path> pages, String pdfFileName) throws IOException, InterruptedException {
        List<String> command = new ArrayList();
        command.add("/usr/bin/convert");
        command.addAll(pages.stream().map(Path::toString).collect(Collectors.toList()));
        command.add("-rotate");
        command.add("0.3");
        command.add(pdfFileName);

        log.debug("Rotate and Create PDF: " + command);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        Process p = pb.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        while ((line = br.readLine()) != null) {
            log.debug("++ " + line);
        }
        log.debug("Convert-Code: " + p.waitFor());
    }

}
