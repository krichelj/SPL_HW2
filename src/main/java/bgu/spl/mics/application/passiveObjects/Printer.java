package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Aid class for printing
 */
public class Printer {

    /**
     * A static function that performs a {@link Serializable} object printing
     */
    public static <T extends Serializable> void print (T objectToPrint, String filename){

        try {

            FileOutputStream outputFile = new FileOutputStream(filename);
            ObjectOutputStream outputStream = new ObjectOutputStream(outputFile);
            outputStream.writeObject(objectToPrint);
            outputFile.close();
            outputStream.close();

        } catch (IOException inputOutputException) {
            inputOutputException.printStackTrace();
        }
    }
}