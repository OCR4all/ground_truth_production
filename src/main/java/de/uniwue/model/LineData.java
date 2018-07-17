package de.uniwue.model;

/**
 * Represents the data of a line
 * This includes: id, image, Ground Truth, Ground Truth correction
 */
public class LineData {
    /**
     * Identifier of the line
     * e.g. 0003__001__paragraph__000
     *      page  paragraph       line
     */
    private String id;

    /**
     * Directory in which the files are stored
     */
    private String directory;

    /**
     * Image of the line as Base64 string
     */
    private String image;

    /**
     * OCR text of the line
     */
    private String ocr;

    /**
     * Ground Truth of the line
     */
    private String gt;

    /**
     * Constructor
     *
     * @param id Line Identifier
     */
    public LineData(String id, String dir) {
        this.id = id;
        this.directory = dir;
    }

    /**
     * Gets the identifier of the line
     *
     * @return Line Identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the identifier of the line
     *
     * @param id Line Identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the directory of the line
     *
     * @return Line directory
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Sets the directory of the line
     *
     * @param directory Line directory
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * Gets the image of the line
     *
     * @return String Line image as Base64
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the image of the line
     *
     * @param image Line image as Base64
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Gets the OCR text of the line
     *
     * @return String OCR text
     */
    public String getOcr() {
        return ocr;
    }

    /**
     * Sets the OCR text of the line
     *
     * @param ocr OCR text
     */
    public void setOcr(String ocr) {
        this.ocr = ocr;
    }

    /**
     * Gets the Ground Truth text of the line
     *
     * @return String Ground Truth text
     */
    public String getGt() {
        return gt;
    }

    /**
     * Sets the Ground Truth text of the line
     *
     * @param gt Ground Truth text
     */
    public void setGt(String gt) {
        this.gt = gt;
    }
}
