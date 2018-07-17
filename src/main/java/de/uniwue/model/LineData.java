package de.uniwue.model;

import com.google.protobuf.util.JsonFormat;
import de.uniwue.helper.Calamari;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

/**
 * Represents the data of a line
 * This includes: id, image, Ground Truth, Ground Truth correction
 */
public class LineData {
    /**
     * Identifier of the line
     * e.g. 0003__001__paragraph__000
     * *page id*__*reading order id*__*segment type*__*line id*
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

    private LinkedList<Character> characters;
    private float minConf = 100;
    private float maxAltConf = -1;

    /**
     * Constructor
     *
     * @param id Line Identifier
     */
    public LineData(String id, String dir) {
        this.id = id;
        this.directory = dir;
    }

    public void loadJson(File jsonFile) throws IOException {
        String jsonText = new String(Files.readAllBytes(jsonFile.toPath()));
        Calamari.Predictions.Builder builder = Calamari.Predictions.newBuilder();
        JsonFormat.parser().merge(jsonText, builder);
        Calamari.Predictions predictions = builder.build();
        //TODO: voted always first?
        Calamari.Prediction prediction = predictions.getPredictions(0);

        float minConf = 100;
        float maxAltConf = 0;
        LinkedList<Character> characters = new LinkedList<Character>();

        for (Calamari.PredictionPosition position : prediction.getPositionsList()) {
            Calamari.PredictionCharacter topResult = position.getCharsList().get(0);
            float conf = topResult.getProbability() * 100;
            Character character = new Character(topResult.getChar(), conf);

            if (conf < minConf) {
                minConf = conf;
            }

            if (position.getCharsList().size() > 1) {
                Calamari.PredictionCharacter topAlternative = position.getCharsList().get(1);
                float altConf = topAlternative.getProbability() * 100;
                character.setConfTopAlt(altConf);

                if (altConf > maxAltConf) {
                    maxAltConf = altConf;
                }
            }

//            System.out.println(character.toString());
            characters.add(character);
        }

        setCharacters(characters);
        setMinConf(minConf);
        setMaxAltConf(maxAltConf);
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

    public LinkedList<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(LinkedList<Character> characters) {
        LinkedList<Character> cleaned = new LinkedList<Character>();

        //remove leading whitespaces
        while(characters.size() > 0 && characters.get(0).getCharacter().equals(" ")) {
            characters.removeFirst();
        }

        //remove trailing whitespaces
        while(characters.size() > 0 && characters.getLast().getCharacter().equals(" ")) {
            characters.removeLast();
        }

        Character lastChar = null;

        for(int i = 0; i < characters.size(); i++) {
            Character currChar = characters.get(i);

            if(i > 0) {
                lastChar = characters.get(i - 1);

                //combine adjacent whitespaces
                if(currChar.getCharacter().equals(" ") && lastChar.getCharacter().equals(" ")) {
                    lastChar.setConfidence(100);
                    lastChar.setConfTopAlt(0);
                }
            } else {
                cleaned.add(currChar);
            }
        }

//        for(int i = 0; i < cleaned.size(); i++) {
//            System.out.println(cleaned.get(i).toString());
//        }

        this.characters = cleaned;
    }

    public float getMinConf() {
        return minConf;
    }

    public void setMinConf(float minConf) {
        this.minConf = minConf;
    }

    public float getMaxAltConf() {
        return maxAltConf;
    }

    public void setMaxAltConf(float maxAltConf) {
        this.maxAltConf = maxAltConf;
    }
}