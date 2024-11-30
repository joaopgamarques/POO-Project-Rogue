package pt.iscte.poo.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import pt.iscte.poo.gui.ImageMatrixGUI;

public class HallOfFame {

    private static HallOfFame INSTANCE; // Singleton instance of the HallOfFame class.
    private static int RECORDS = 5; // Maximum number of records to store.

    // Private constructor to prevent external instantiation.
    private HallOfFame() {
    }

    // Retrieves the singleton instance of HallOfFame.
    public static HallOfFame getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HallOfFame();
        }
        return INSTANCE;
    }

    // Adds the current player's record, updates the hall of fame, and displays the records.
    public void print() {
        String player = ImageMatrixGUI.getInstance().askUser("Player's name?");
        int score = Engine.getInstance().getScore();
        Record record = new Record(player, score);

        File filename = new File(System.getProperty("user.dir").concat("\\records\\records.txt"));
        if (!(filename.exists() && filename.isFile())) {
            return;
        }
        
        List<Record> records = read(filename); // Read existing records.
        records.add(record); // Add the new record.
        records.sort((Record a, Record b) -> Integer.compare(b.getScore(), a.getScore())); // Sort by score (descending).
        
        if (records.size() > RECORDS) {
            records.remove(RECORDS); // Keep only the top records.
        }

        write(filename, records); // Write updated records to file.
        display(records); // Display the records.
    }

    // Displays the hall of fame records in the GUI.
    private static void display(List<Record> records) {
        StringBuilder message = new StringBuilder();
        message.append("Hall of Fame:\n");
        records.forEach(record -> message.append(record.player + " " + record.score + "\n"));
        ImageMatrixGUI.getInstance().setMessage(message.toString());
    }

    // Reads the records from a file and returns a list of Record objects.
    private static List<Record> read(File filename) {
        List<Record> records = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(filename);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] info = line.split(",");
                records.add(HallOfFame.getInstance().new Record(info[0], Integer.parseInt(info[1])));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File Not Found!");
        }

        return records;
    }

    // Writes the list of records to a file.
    private static void write(File filename, List<Record> records) {
        try {
            PrintWriter writer = new PrintWriter(filename);
            records.forEach(record -> writer.println(record.toString()));
            writer.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
        }

        removeLastEmptyLine(filename); // Removes any trailing empty line from the file.
    }

    // Removes the last line from a file to avoid trailing empty lines.
    private static void removeLastEmptyLine(File filename) {
        try {
            RandomAccessFile file = new RandomAccessFile(filename, "rw");
            long length = file.length();
            file.setLength(length - 2); // Adjusts the file size to remove the last two bytes.
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner class: Represents a record in the hall of fame.
    private class Record {

        private String player; // The name of the player.
        private int score; // The score achieved by the player.

        // Constructs a Record object with the player's name and score.
        public Record(String player, int score) {
            this.player = player;
            this.score = score;
        }

        // Returns the score of the record.
        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            // Returns the record as a comma-separated string.
            return player + "," + score;
        }
    }
}
