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
     * Image of the line as Base64 string
     */
    private String image;

    /**
     * Ground Truth text of the line
     */
    private String groundTruth;

    /**
     * Corrected (manual) Ground Truth of the line
     */
    private String groundTruthCorrection;

    /**
     * Constructor
     *
     * @param id Line Identifier
     */
    public LineData(String id) {
        this.id = id;
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
     * Gets the Ground Truth text of the line
     *
     * @return String Ground Truth text
     */
    public String getGroundTruth() {
        return groundTruth;
    }

    /**
     * Sets the Ground Truth text of the line
     *
     * @param groundTruth Ground Truth text
     */
    public void setGroundTruth(String groundTruth) {
        this.groundTruth = groundTruth;
    }

    /**
     * Gets the manually corrected Ground Truth text of the line
     *
     * @return String Corrected Ground Truth text
     */
    public String getGroundTruthCorrection() {
        return groundTruthCorrection;
    }

    /**
     * Sets the manually corrected Ground Truth text of the line
     *
     * @param groundTruthCorrection Corrected Ground Truth text
     */
    public void setGroundTruthCorrection(String groundTruthCorrection) {
        this.groundTruthCorrection = groundTruthCorrection;
    }
}
