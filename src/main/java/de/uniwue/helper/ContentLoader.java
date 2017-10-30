package de.uniwue.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.TreeMap;

import de.uniwue.model.LineData;

/**
 * Helper class for content loading
 */
public class ContentLoader {
    /**
     * Path to the content directory (gtc directory)
     */
    private String path;

    /**
     * Stores the content of all files (sorted) in the gtc directory
     */
    private TreeMap<String, LineData> content = new TreeMap<String, LineData>();

    /**
     * Constructor
     *
     * @param path Path to the content directory
     */
    public ContentLoader(String path) {
        this.path = path;
    }

    /**
     * Loads the content of all files in LineData objects
     *
     * @throws IOException 
     */
    private void loadContent() throws IOException {
        final File contentDir = new File(path);
        if (!contentDir.exists()) 
            return;

        content = new TreeMap<String, LineData>();
        for (final File fileEntry : contentDir.listFiles()) {
            if (fileEntry.isFile()) {
                String fileName = fileEntry.getName();

                int extensionStart = fileName.indexOf(".");
                // Skip files that do not have any file ending
                if (extensionStart == -1 || extensionStart == 0)
                    break;

                String lineId = fileName.substring(0 , extensionStart);
                if (!content.containsKey(lineId)) {
                    content.put(lineId, new LineData(lineId));
                }

                LineData lineData = content.get(lineId);
                // Set appropriate line data for each file type
                if (fileName.endsWith("nrm.png")) {
                    lineData.setImage(Base64.getEncoder().encodeToString(Files.readAllBytes(fileEntry.toPath())));
                }
                else if (fileName.endsWith(".gt.txt")) {
                    lineData.setGroundTruth(Files.lines(fileEntry.toPath()).findFirst().get());
                }
                else if (fileName.endsWith(".txt")) {
                    lineData.setGroundTruthCorrection(Files.lines(fileEntry.toPath()).findFirst().get());
                }
            }
        }
    }

    /**
     * Gets the content of all files in the gtc directory
     * The content is represented by line data objects
     *
     * @return Array that holds all line data
     * @throws IOException
     */
    public ArrayList<LineData> getContent() throws IOException {
        if (content.isEmpty())
            loadContent();

        return new ArrayList<LineData>(content.values());
    }
}
