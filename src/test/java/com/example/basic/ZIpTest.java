package com.example.basic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author han
 * @date 2024/5/30
 */
public class ZIpTest {

    public static void main(String[] args) throws Exception {
        String path = "C:\\wst_han\\temp/daolv", zipPath = "D:/HotelSummary";
        File folder = new File(path);
        for (File file : folder.listFiles()) {
            if (file.getName().equals("json")) {
                continue;
            }
            if (file.getName().equals("0")) {

                String zipFilePath = zipPath + "/" + file.getName() + ".zip";

                FileOutputStream fos = new FileOutputStream(zipFilePath);
                ZipOutputStream zos = new ZipOutputStream(fos);

                for (File file1 : file.listFiles()) {
                    FileInputStream fis = null;
                    try {

//                    String relativePath = file1.getName();
                        ZipEntry ze = new ZipEntry(file1.getName());
                        zos.putNextEntry(ze);

                        fis = new FileInputStream(file1);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, length);
                        }
                    } finally {
                        fis.close();
                        zos.closeEntry();
                        zos.flush();
                    }
                }
            }
        }
    }
}
