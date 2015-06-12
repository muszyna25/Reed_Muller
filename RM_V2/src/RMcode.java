import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class RMcode {
    private final static String DEFAULTSRC = "lena_128x128_64.pgm";
    private static Boolean      hasFile    = false;
    private static FileReader   fr         = null;
    private static RMcode       me;
    public static char[]        operation    = ("shall").toCharArray();

    public static void main(String[] args) {
        me = new RMcode();
        me.chooseFile(DEFAULTSRC, true);
        me.mainMenu();
    }

    private void mainMenu() {
        Boolean end = false;
        String menu = null;

        while (!end) {
            if (hasFile == false) {
                menu = "Choose an option below:\n\n"
                        + " 1 - Open source file\n"
                        + "=====================================\n"
                        + " 0 - Quit\n";
            } else {
                menu = "Choose an option below:\n\n"
                        + " 1 - Open source file\n"
                        + " 2 - Use Reed-Muller\n"
                        + "=====================================\n"
                        + " 0 - Quit\n";
            }

            System.out.println(menu);
            Scanner scan = new Scanner(System.in);

            switch (scan.nextLine()) {
            case "0":
                scan.close();
                end = true;
                break;

            case "1":
                me.chooseFile(scan, true);
                break;

            case "2":
                if (hasFile == false) {
                    // System.out.println("Open a source file first");
                    break;
                }
                me.RMMenu(scan);
                break;

            default:
                break;
            }
        }
    }

    /**
     * Prints out the Searching algorithm-chooser menu.
     * 
     * @param scan
     *            The scanner used to track keyboard entries.
     */
    private void RMMenu(Scanner scan) {
        Boolean end = false;

        String menu = "Choose an option below:\n\n"
                + " 1 - Encode Image\n"
                + " 2 - Noise image\n"
                + " 3 - Denoise image\n"
                + " 4 - Decode image\n"
                + "=====================================\n"
                + " 0 - Return to main menu\n";

        while (!end) {
            System.out.println(menu);
            scan = new Scanner(System.in);

            switch (scan.nextLine()) {
            case "0":
                end = true;
                scan.reset();
                break;

            case "1":
                search(scan, 1);
                hasFile = false;
                end = true;
                break;

            case "2":
                search(scan, 2);
                hasFile = false;
                end = true;
                break;

            case "3":
                search(scan, 3);
                hasFile = false;
                end = true;
                break;

            case "4":
                search(scan, 4);
                hasFile = false;
                end = true;
                break;

            default:
                System.out.println("default");
                break;
            }
        }
    }
    /**
     * Asks for the pattern to be searched.
     * 
     * @param scan
     *            The scanner used to track the keyboard entry,
     */
    private void askForPattern(Scanner scan) {
        System.out.println("Enter the pattern to be searched.\n");
        operation = scan.nextLine().toCharArray();
    }

    /**
     * Launches the pattern searching using the chosen algorithm and the chosen
     * pattern through {@link Psearch#askForPattern(Scanner)}.
     * 
     * @param scan
     *            The scanner used to track the keyboard entry,
     * @param algorithm
     *            The algorithm as Integer, as proposed by the
     *            {@link Psearch#searchMenu}.
     */
    private void search(Scanner scan, Integer algorithm) {
        System.out.println(algorithm);
//        askForPattern(scan);
//        searchAlgorithm = algorithm;
//        readFile();
    }


    /**
     * Loads a file, as long as the filename is as a {@link Scanner} or a
     * {@link String}.
     * 
     * @param o
     *            The filename, as String or Scanner.
     * @param verbose
     *            The verbosity switch. Fails silently if false and prints out a
     *            message if it fails, if true.
     */
    private void chooseFile(Object o, Boolean verbose) {
        try {
            if (o instanceof Scanner) {
                String fileName;
                System.out.println("Choose a file.\n");
                fileName = ((Scanner) o).nextLine();
                fr = new FileReader(fileName);
                System.out.println(fileName + " loaded.\n");
                hasFile = true;
            } else if (o instanceof String) {
                if ((String) o == null || hasFile == false) {
                    fr = new FileReader((String) o);
                    System.out.println((String) o + " loaded.\n");
                    hasFile = true;
                } else {
                    System.out.println("No file loaded.\n");
                    hasFile = false;
                    return;
                }
            } else {
                System.out.println("Unable to load the file.");
                return;
            }
        } catch (FileNotFoundException e) {
            if (verbose) {
                System.out.println("File not found.");
            }
            hasFile = false;
            return;
        }
    }

}
