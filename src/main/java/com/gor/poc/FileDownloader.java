package com.gor.poc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader {
  public static void main(String[] args) {
    // This is the REST API which will serve you the file
    String fileUrl = "http://localhost:5000/download_whole"; 
    String savePath = ""; 

    // Begin tme 
    long startTime = System.currentTimeMillis();
    try {
      // Define the URL 
      URL url = new URL(fileUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        
        // Get the file name from the header
        String fileName = connection.getHeaderField("Content-Disposition");
        fileName = fileName.substring(fileName.lastIndexOf("=") + 1);
        String filePath = savePath + "/" + fileName;

        // Get the chunk size from the response headers
        String chunkSizeHeader = connection.getHeaderField("X-Chunk-Size");
        System.out.println("Chunk size: " + chunkSizeHeader);
        int chunkSize = Integer.parseInt(chunkSizeHeader);

        // Create an inout stream to download the file
        InputStream inputStream = connection.getInputStream();
        FileOutputStream outputStream = new FileOutputStream("./" + filePath);

        // Read the data in chunks and save it to the file
        byte[] buffer = new byte[chunkSize];
        int bytesRead;

        // Check if the server supports chunked transfer encoding
        String transferEncoding = connection.getHeaderField("Transfer-Encoding");
        boolean isChunked = "chunked".equalsIgnoreCase(transferEncoding);

        if (isChunked) {
          // Read and write the response data in chunks
          // System.out.println("Downloading file in chunks...");
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            // System.out.println(bytesRead + " bytes read");
            outputStream.write(buffer, 0, bytesRead);
          }
        } else {
          // Read and write the entire response data
          // System.out.println("Downloading file as whole");
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            // System.out.println(bytesRead + " bytes read");
            outputStream.write(buffer, 0, bytesRead);
          }
        }

        outputStream.close();
        inputStream.close();

        // End time
        long endTime = System.currentTimeMillis();
        System.out.println("Total time taken for chunk size " +chunkSize+ " : " + (endTime - startTime) + "ms");

        System.out.println("File downloaded successfully!");
      } else {
        System.out.println("File download failed. Server returned response code: " + responseCode);
      }

      connection.disconnect();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

