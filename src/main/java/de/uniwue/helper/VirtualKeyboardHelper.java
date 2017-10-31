package de.uniwue.helper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to load the default virtual keyboard
 */
public class VirtualKeyboardHelper {
    /**
     * ClassLoader object to access resource files (configuration storage)
     */
    private ClassLoader classLoader;

    /**
     * Keys of the virtual keyboard (complete)
     */
    private List<List<String>> keysComplete = new ArrayList<List<String>>();

    /**
     * Keys of the virtual keyboard (frequently used ones)
     */
    private List<List<String>> keysFrequent = new ArrayList<List<String>>();

    /**
     * Constructor
     */
    public VirtualKeyboardHelper() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    /**
     * Load the keys that are stored in the given file
     * The file contains the characters that should be displayed on the virtual keyboard
     * Characters need to be separated with tabs and new lines
     * Files need to be located in src/main/resources
     *
     * @param fileName File name of the keyboard characters
     * @return
     */
    private List<List<String>> loadKeysFromFile(String fileName) {
        List<List<String>> keys = new ArrayList<List<String>>();

        InputStream is = classLoader.getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(s -> {
            String[] splitStrings = s.split("\t", -1);

            List<String> keyLine = new ArrayList<String>();
            for(String split : splitStrings) {
                keyLine.add(split);
            }
            keys.add(keyLine);
        });

        return keys;
    }

    /**
     * Get the virtual keyboard keys (complete)
     *
     * @return Keys of the virtual keyboard (complete)
     */
    private List<List<String>> getKeysComplete() {
        if (keysComplete.isEmpty())
            keysComplete = loadKeysFromFile("virtualKeyboardComplete.txt");
        return keysComplete;
    }

    /**
     * Get the virtual keyboard keys (frequently used ones)
     *
     * @return Keys of the virtual keyboard (frequently used ones)
     */
    private List<List<String>> getKeysFrequent() {
        if (keysFrequent.isEmpty())
            keysFrequent = loadKeysFromFile("virtualKeyboardFrequent.txt");
        return keysFrequent;
    }

    /**
     * Gets the keys of the given type
     *
     * @param keyType Determines which keys to get
     * @return Keys of the given type
     */
    public List<List<String>> getKeys(String keyType) {
        switch(keyType) {
            case "complete": return getKeysComplete();
            case "frequent": return getKeysFrequent();
            default:         return null;
        }
    }
}
