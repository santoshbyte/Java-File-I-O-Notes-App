import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

// NotesApp: A text-based notes manager using Java File I/O
public class NotesApp {

    private static final String NOTES_FILE = "notes.txt";
    private static final Logger LOGGER = Logger.getLogger(NotesApp.class.getName());

    static {
        try {
            LogManager.getLogManager().reset();
            FileHandler handler = new FileHandler("notesapp.log", true);
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Logging setup failed: " + e.getMessage());
        }
    }

    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        NotesApp app = new NotesApp();
        app.start();
    }

    public void start() 
    {
        createFileIfNotExists();
        while (true) {
            printMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1 -> addNote();
                case 2 -> viewNotes();
                case 3 -> searchNotes();
                case 4 -> deleteNotes();
                case 5 -> {
                    System.out.println("Exiting Notes App. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n========= Notes App =========");
        System.out.println("1. Add Note");
        System.out.println("2. View All Notes");
        System.out.println("3. Search Notes");
        System.out.println("4. Delete All Notes");
        System.out.println("5. Exit");
        System.out.print("Enter choice: ");
    }

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid input for menu choice.");
            return -1;
        }
    }

    private void createFileIfNotExists() {
        try {
            if (!Files.exists(Paths.get(NOTES_FILE))) {
                Files.createFile(Paths.get(NOTES_FILE));
                LOGGER.info("Notes file created successfully.");
            }
        } catch (IOException e) {
            LOGGER.severe("Error creating notes file: " + e.getMessage());
        }
    }

    private void addNote() {
        System.out.println("Enter your note (single line): ");
        String note = scanner.nextLine();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try (FileWriter writer = new FileWriter(NOTES_FILE, true);
             BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write("[" + timestamp + "] " + note);
            bw.newLine();
            System.out.println("Note added successfully!");
            LOGGER.info("Note added: " + note);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing to file", e);
        }
    }

    private void viewNotes() {
        System.out.println("\n===== All Notes =====");
        try (FileReader fr = new FileReader(NOTES_FILE);
             BufferedReader br = new BufferedReader(fr)) {

            String line;
            boolean empty = true;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                empty = false;
            }
            if (empty) {
                System.out.println("No notes found!");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading notes", e);
        }
    }

    private void searchNotes() {
        System.out.print("Enter keyword to search: ");
        String keyword = scanner.nextLine().toLowerCase();
        System.out.println("\n===== Search Results =====");

        try (BufferedReader br = new BufferedReader(new FileReader(NOTES_FILE))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (line.toLowerCase().contains(keyword)) {
                    System.out.println(line);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No notes matched your keyword.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error searching notes", e);
        }
    }

    private void deleteNotes() {
        System.out.print("Are you sure you want to delete all notes? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            try (FileWriter writer = new FileWriter(NOTES_FILE, false)) {
                // Overwrites file with empty content
                System.out.println("All notes deleted!");
                LOGGER.warning("All notes deleted by user.");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error deleting notes", e);
            }
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }
}
