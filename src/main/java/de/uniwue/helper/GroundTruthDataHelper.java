package de.uniwue.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.TreeMap;

import de.uniwue.model.LineData;

/**
 * Helper class to load Ground Truth data
 */
public class GroundTruthDataHelper {
    /**
     * Directory to the necessary ground truth files
     */
    private String gtcDir;

    /**
     * Type of the ground truth directory (flat | pages)
     */
    private String dirType;

    /**
     * Stores the content of all files (sorted) in the gtc directory
     */
    private TreeMap<String, LineData> gtData  = new TreeMap<String, LineData>();

    /**
     * Finds and returns sub-directories of a given directory
     *
     * @param parentDirPath Directory path from which sub-directories are fetched
     * @return String array of sub-directory names
     * @throws IOException
     */
    private String[] getSubDirectories(String parentDirPath) throws IOException {
        File parentDir = new File(parentDirPath);
        if (!parentDir.exists())
            throw new IOException();

        String[] subDirs = parentDir.list();
        Arrays.sort(subDirs);
        return subDirs;
    }

    /**
     * Constructor
     *
     * @param gtcDir Directory to the necessary ground truth files
     * @param dirType Type of the ground truth directory (flat | pages)
     * @param pageId Id of the current page if directory type = "pages" 
     * @throws IOException 
     */
    public GroundTruthDataHelper(String gtcDir, String dirType, String pageId) throws IOException {
        // Adjust path to point to a directory of a specific page
        if (dirType.equals("pages")) {
            File pagesDir = new File(gtcDir);
            if (!pagesDir.exists())
                throw new IOException();

            // If no pageId is set, use first page directory
            if (pageId == null || pageId.equals("null")) {
                String[] pages = getSubDirectories(gtcDir);
                if (pages.length == 0)
                    throw new IOException();

                pageId = pages[0];
            }

            gtcDir += File.separator + pageId;
        }

        this.dirType = dirType;
        this.gtcDir  = gtcDir;
    }

    /**
     * Fetches and returns the ground truth data of a given directory as LineData objects
     *
     * @param dataDir Directory to get ground truth data from
     * @return Content of all files (sorted) in the given directory
     * @throws IOException
     */
    private TreeMap<String, LineData> getDirectoryGroundTruthData(String dataDir) throws IOException {
        final File contentDir = new File(dataDir);
        if (!contentDir.exists() || !contentDir.isDirectory())
            throw new IOException();

        TreeMap<String, LineData> gtData = new TreeMap<String, LineData>();
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
                gtData.put(lineId, new LineData(lineId, dataDir));
            }

            LineData lineData = gtData.get(lineId);
            // Set appropriate line data for each file type
            if (fileName.endsWith(".png")) {
                lineData.setImage(Base64.getEncoder().encodeToString(Files.readAllBytes(fileEntry.toPath())));
            }
            else if (fileName.endsWith(".gt.txt")) {
                lineData.setGroundTruthCorrection(Files.lines(fileEntry.toPath()).findFirst().orElse(""));
            }
            else if (fileName.endsWith(".txt")) {
                lineData.setGroundTruth(Files.lines(fileEntry.toPath()).findFirst().orElse(""));
            }
        }

        return gtData;
    }

    /**
     * Loads the content of all files in LineData objects
     *
     * @return Content of all files (sorted) in the gtc directory
     * @throws IOException 
     */
    private TreeMap<String, LineData> loadGroundTruthData() throws IOException {
        if (dirType.equals("pages")) {
            final File pagesDir = new File(gtcDir);
            if (!pagesDir.exists() || !pagesDir.isDirectory())
                throw new IOException();

            TreeMap<String, LineData> gtData = new TreeMap<String, LineData>();
            // Only scan sub-directories (segments) in case of a page directory
            for (String segmentDirPath : pagesDir.list()) {
                segmentDirPath = gtcDir + File.separator + segmentDirPath;
                final File segmentDir = new File(segmentDirPath);
                if (!segmentDir.isDirectory())
                    continue;

                gtData.putAll(getDirectoryGroundTruthData(segmentDirPath));
            }
            return gtData;
        }
        else
            return getDirectoryGroundTruthData(gtcDir);
    }

    /**
     * Gets the content of all files in the gtc directory
     * The content is represented by line data objects
     *
     * @return Array that holds all line data
     * @throws IOException
     */
    public ArrayList<LineData> getGroundTruthData() throws IOException {
        gtData = loadGroundTruthData();
        return new ArrayList<LineData>(gtData.values());
    }

    /**
     * Saves the groundTruthData correction
     *
     * @param lineId Ground Truth line Id
     * @param gtcText Corrected Ground Truth text
     */
    public void saveGroundTruthData(String lineId, String gtcText) throws IOException {
        Path pathToFile = Paths.get(gtData.get(lineId).getDirectory() + File.separator + lineId + ".gt.txt");
        BufferedWriter writer = Files.newBufferedWriter(pathToFile);
        writer.write(gtcText);
        writer.close();
    }

    /**
     * Returns sub-directories if directory type = "pages"
     *
     * @param pagesDir Pages directory
     * @return String array containing the pages of the ground truth directory
     * @throws IOException
     */
    public String[] getPages(String pagesDir) throws IOException {
        if (dirType.equals("pages"))
            return getSubDirectories(pagesDir);
        return new String[0];
    }
}
