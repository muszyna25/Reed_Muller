import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;

public class Pgm {
    private final int    MAXSHADE = 63;
    private final int    MINSHADE = 0;

    private String       Type;
    private final String CREATOR  = "# CREATOR: ReedMuller ";

    private int[][]      pixels = null;
    private int          shade  = 0;
    private int          width  = 0;
    private int          height = 0;

    /**
     * Basic empty constructor.
     * Sets up shades, width and height to 0.
     * No pixels array is build, nor initialised.
     */
    public Pgm() {
        shade = width = height = 0;
    }

    /**
     * Full constructor. Builds up an empty Pmg object, able to store a .pgm
     * image, using the parameters to set up the size and the shades.
     * 
     * @param inShade
     *            The number of shades of the image.
     * @param inWidth
     *            The image width.
     * @param inHeight
     *            The image height.
     */
    public Pgm(int inShade, int inWidth, int inHeight) {
        if (shade <= MAXSHADE && shade <= MINSHADE) {
            pixels = new int[inWidth][inHeight];
            width = inWidth;
            height = inHeight;
            shade = inShade;
        }
    }

    /* Getters */
    /**
     * Gets the pgm image type.
     * 
     * @return The pgm image type.
     */
    public String getType() {
        return Type;
    }

    /**
     * Gets the CREATOR string.
     * 
     * @return The CREATOR string
     */
    public String getCREATOR() {
        return CREATOR;
    }
    
    /**
     * Gets the image height.
     * 
     * @return The image height.
     */
    public Integer getHeight() {
        return this.height;
    }

    /**
     * Gets the image width.
     * 
     * @return The image width.
     */
    public Integer getWidth() {
        return this.width;
    }

    /**
     * Gets the numbers of gray shades.
     * @return The numbers of gray shades.
     */
    public Integer getShade() {
        return this.shade;
    }

    /**
     * Gets an array of all the pixels.
     * @return The array of all the pixels.
     */
    public int[][] getPixels() {
        return this.pixels;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public int getPixel(int a, int b) {
        
        int tmp = 0;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if(pixels[x][y] == pixels[a][b])
                    tmp = pixels[x][y];
            }
        }
        return tmp;
    }

    /* Setters */
    /**
     * Sets the pgm image type.
     * 
     * @param type
     *            The pgm image type to be set.
     */
    public void setType(String type) {
        Type = type;
    }

    /**
     * Sets the image height.
     * 
     * @param height
     *            The image height to be set.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets the image width.
     * @param width The image width to be set.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the number of gray shades used in the image.
     * 
     * @param depth
     *            The number of gray shades to be set.
     */
    public void setDepth(int depth) {
        this.shade = depth;
    }
    
    /**
     * Sets a shade to a pixel.
     * 
     * @param x
     *            The x coordinate of the pixel
     * @param y
     *            The y coordinate of the pixel
     * @param shade
     *            The shade to be set
     */
    public void setPixel(int x, int y, int shade) {
        //if (x <= this.width && y <= this.height && shade <= MAXSHADE
          //      && shade <= MINSHADE) {
            this.pixels[x][y] = shade;
        //}
    }

    /**
     * Sets the pixels of the images form an array of pixels shades
     * 
     * @param array
     *            The array of pixels shades to be set.
     */
    public void setPixels(int array[][]) {
        this.pixels = array;
    }

    /**
     * Function which reads PGM image as numbers.
     * 
     * @param filename
     *            The filename.
     * @param verbose
     *            Prints out the pixels values if true.
     */
    public void fromFile(String filename, boolean verbose) {
        String line;
        StringTokenizer st;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new BufferedInputStream(new FileInputStream(filename))));

            DataInputStream dis = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(filename)));

            // Reads PGM image header
            // Reads and save the Pgm type.
            line = br.readLine();
            this.setType(line);
            dis.skip((line + "\n").getBytes().length);

            // Skips comments
            do {
                line = br.readLine();
                dis.skip((line + "\n").getBytes().length);
            } while (line.charAt(0) == '#');

            // The current line is the dimensions
            st = new StringTokenizer(line);
            this.width = Integer.parseInt(st.nextToken());
            this.height = Integer.parseInt(st.nextToken());
            
            pixels = new int[this.width][this.height];
            
            // Next line has pixel shade
            line = br.readLine();
            dis.skip((line + "\n").getBytes().length);
            st = new StringTokenizer(line);
            this.shade = Integer.parseInt(st.nextToken());

            if (verbose){
                System.out.println("Image width:\t" + width + "\n"
                        + "Image height:\t" + height + "\n"
                        + "gray shades:\t" + shade + "\n");
    
            }
            // Reads pixels from now on
            line = br.readLine();
            st = new StringTokenizer(line);
            // Borken loops !
            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    this.pixels[x][y] = Integer.parseInt(st.nextToken());
                    if (verbose){
                        System.out.format("%3d ", pixels[x][y]);
                    }
                }
                if (verbose){
                    System.out.println("");
                }
            }
            if (verbose){
                System.out.println("\n");
            }

            br.close();
            dis.close();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: image in " + filename + " too big");
        } catch (FileNotFoundException e) {
            System.out.println("Error: file " + filename + " not found");
        } catch (IOException e) {
            System.out.println("Error: end of stream encountered when reading "
                    + filename);
        }
    }

    /**
     * Saves the stored image as a .pgm image file.
     * 
     * @param filename
     *            The filename to which the image will be saved.
     * @throws IOException
     */
    public void toFile(String filename) throws IOException {
        File f = new File(filename);

        try {
            if (!f.exists()){
                f.createNewFile();
            }

            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            
            // Writes the header
            bw.write(Type + "\n");
            bw.write(CREATOR + "\n");
            bw.write(this.width+ " " + this.height + "\n");
            bw.write(this.shade + "\n");

            // Puts the pixels inside the file
            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    bw.write(String.valueOf(this.pixels[x][y])+ " ");
                }
                bw.write("\n");
            }

            bw.close();

            System.out.println("File: " + filename + " saved!");

        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Unable to save the image as a file.");
        }

    }

}
