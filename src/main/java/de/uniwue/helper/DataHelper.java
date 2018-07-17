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
import java.util.stream.Stream;

import org.springframework.web.util.HtmlUtils;

import de.uniwue.model.LineData;

/**
 * Helper class to load data
 */
public class DataHelper {
    /**
     * Directory to the necessary files
     */
    private String dataDir;

    /**
     * Type of the data directory (flat | pages)
     */
    private String dirType;

    /**
     * Stores the content of all files (sorted) in the data directory
     */
    private TreeMap<String, LineData> data = new TreeMap<String, LineData>();

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
     * @param dataDir Directory to the necessary files
     * @param dirType Type of the data directory (flat | pages)
     * @param pageId Id of the current page if directory type = "pages" 
     * @throws IOException 
     */
    public DataHelper(String dataDir, String dirType, String pageId) throws IOException {
        // Adjust path to point to a directory of a specific page
        if (dirType.equals("pages")) {
            File pagesDir = new File(dataDir);
            if (!pagesDir.exists())
                throw new IOException();

            // If no pageId is set, use first page directory
            if (pageId == null || pageId.equals("null")) {
                String[] pages = getSubDirectories(dataDir);
                if (pages.length == 0)
                    throw new IOException();

                pageId = pages[0];
            }

            dataDir += File.separator + pageId;
        }

        this.dirType = dirType;
        this.dataDir = dataDir;
    }

    /**
     * Fetches and returns the data of a given directory as LineData objects
     *
     * @param dataDir Directory to get line data from
     * @return Content of all files (sorted) in the given directory
     * @throws IOException
     */
    private TreeMap<String, LineData> getDirectoryLineData(String dataDir) throws IOException {
        final File contentDir = new File(dataDir);
        if (!contentDir.exists() || !contentDir.isDirectory())
            throw new IOException();

        TreeMap<String, LineData> data = new TreeMap<String, LineData>();
        for (final File fileEntry : contentDir.listFiles()) {
            if (!fileEntry.isFile())
                continue;

            String fileName    = fileEntry.getName();
            int extensionStart = fileName.indexOf(".");
            // Skip files that do not have any file ending
            if (extensionStart == -1 || extensionStart == 0)
                continue;

            String lineId = fileName.substring(0 , extensionStart);
            if (!data.containsKey(lineId)) {
                data.put(lineId, new LineData(lineId, dataDir));
            }

            LineData lineData = data.get(lineId);
            // Set appropriate line data for each file type
            if (fileName.endsWith(".png")) {
                lineData.setImage(Base64.getEncoder().encodeToString(Files.readAllBytes(fileEntry.toPath())));
            }
            else if (fileName.endsWith(".gt.txt")) {
                // Escape HTML characters to ensure that text is correctly displayed
            	try (Stream<String> s = Files.lines(fileEntry.toPath())) {
                    lineData.setGt(HtmlUtils.htmlEscape(s.findFirst().orElse("")));
                }
            }
            else if (fileName.endsWith(".txt")) {
                // Escape HTML characters to ensure that text is correctly displayed
                try (Stream<String> s = Files.lines(fileEntry.toPath())) {
                    lineData.setOcr(HtmlUtils.htmlEscape(s.findFirst().orElse("")));
                }
            }
        }

        return data;
    }

    /**
     * Loads the content of all files in LineData objects
     *
     * @return Content of all files (sorted) in the data directory
     * @throws IOException 
     */
    private TreeMap<String, LineData> loadData() throws IOException {
        if (dirType.equals("pages")) {
            final File pagesDir = new File(dataDir);
            if (!pagesDir.exists() || !pagesDir.isDirectory())
                throw new IOException();

            TreeMap<String, LineData> data = new TreeMap<String, LineData>();
            // Only scan sub-directories (segments) in case of a page directory
            for (String segmentDirPath : pagesDir.list()) {
                segmentDirPath = dataDir + File.separator + segmentDirPath;
                final File segmentDir = new File(segmentDirPath);
                if (!segmentDir.isDirectory())
                    continue;

                data.putAll(getDirectoryLineData(segmentDirPath));
            }
            return data;
        }
        else
            return getDirectoryLineData(dataDir);
    }

    /**
     * Gets the content of all files in the data directory
     * The content is represented by line data objects
     *
     * @return Array that holds all line data
     * @throws IOException
     */
    public ArrayList<LineData> getData() throws IOException {
        data = loadData();
        return new ArrayList<LineData>(data.values());
    }

    /**
     * Saves the ground truth
     *
     * @param lineId line Id
     * @param gtText ground truth text
     */
    public void saveGroundTruthData(String lineId, String gtText) throws IOException {
        Path pathToFile = Paths.get(data.get(lineId).getDirectory() + File.separator + lineId + ".gt.txt");
        BufferedWriter writer = Files.newBufferedWriter(pathToFile);
        // Store unescaped HTML characters to text file for general usage
        writer.write(HtmlUtils.htmlUnescape(gtText));
        writer.close();
    }

    /**
     * Returns sub-directories if directory type = "pages"
     *
     * @param pagesDir Pages directory
     * @return String array containing the pages of the data directory
     * @throws IOException
     */
    public String[] getPages(String pagesDir) throws IOException {
        if (dirType.equals("pages"))
            return getSubDirectories(pagesDir);
        return new String[0];
    }
}
