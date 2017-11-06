package de.uniwue.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.TreeMap;
import de.uniwue.model.LineData;

/**
 * Helper class to load Ground Truth data
 */
public class GroundTruthDataHelper {
    /**
     * Path to the content directory (gtc directory)
     */
    private String path;

    /**
     * Stores the content of all files (sorted) in the gtc directory
     */
    private TreeMap<String, LineData> gtData = new TreeMap<String, LineData>();

    /**
     * Constructor
     *
     * @param path Path to the content directory
     */
    public GroundTruthDataHelper(String path) {
        this.path = path;
    }

    /**
     * Loads the content of all files in LineData objects
     *
     * @throws IOException 
     */
    private void loadGroundTruthData() throws IOException {
        final File contentDir = new File(path);
        if (!contentDir.exists()) 
            return;

        gtData = new TreeMap<String, LineData>();
        for (final File fileEntry : contentDir.listFiles()) {
            if (!fileEntry.isFile())
                continue;

            String fileName    = fileEntry.getName();
            int extensionStart = fileName.indexOf(".");
            // Skip files that do not have any file ending
            if (extensionStart == -1 || extensionStart == 0)
                continue;

            String lineId = fileName.substring(0 , extensionStart);
            if (!gtData.containsKey(lineId)) {
                gtData.put(lineId, new LineData(lineId));
            }

            LineData lineData = gtData.get(lineId);
            // Set appropriate line data for each file type
            if (fileName.endsWith("nrm.png")) {
                lineData.setImage(Base64.getEncoder().encodeToString(Files.readAllBytes(fileEntry.toPath())));
            }
            else if (fileName.endsWith(".gt.txt")) {
                lineData.setGroundTruth(Files.lines(fileEntry.toPath()).findFirst().orElse(""));
            }
            else if (fileName.endsWith(".txt")) {
                lineData.setGroundTruthCorrection(Files.lines(fileEntry.toPath()).findAny().orElse(""));
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
    public ArrayList<LineData> getGroundTruthData() throws IOException {
        if (gtData.isEmpty())
            loadGroundTruthData();

        return new ArrayList<LineData>(gtData.values());
    }

    /**
     * Saves the groundTruthData correction
     *
     * @param lineId Ground Truth line Id
     * @param gtcText Corrected Ground Truth text
     */
    public void saveGroundTruthData(String lineId, String gtcText) throws IOException {
        Path pathToFile = Paths.get(path + File.separator + lineId + ".txt");
        BufferedWriter writer = Files.newBufferedWriter(pathToFile);
        writer.write(gtcText);
        writer.close();
    }
}
