package com.reach.ekg.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class FileWriter {

    public static void writeResult(String path, Object result) {
        File file = new File(path);
        int i = 0;
        while (file.exists()) {
            i++;
            String[] parts = path.split("\\.");
            String newPath = String.format("%s (%d).%s", parts[0], i, parts[1]);
            file = new File(newPath);
        }

        System.out.println("Writing to file: " + file.getPath());
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, result);
        } catch (IOException e) {
            System.out.println("Writing file failed: " + e.getMessage());
        }
    }

}
