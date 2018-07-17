package de.uniwue.model;

public class Character {

    private String character;
    private float confidence;
    private float confTopAlt;

    public Character(String character, float confidence) {
        setCharacter(character);
        setConfidence(confidence);
    }

    @Override
    public String toString() {
        return "Character{" +
                "character='" + character + '\'' +
                ", confidence=" + confidence +
                ", confTopAlt=" + confTopAlt +
                '}';
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public float getConfTopAlt() {
        return confTopAlt;
    }

    public void setConfTopAlt(float confTopAlt) {
        this.confTopAlt = confTopAlt;
    }
}