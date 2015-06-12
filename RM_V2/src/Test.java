import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {

        String filePath;
        // filePath = "1.pgm";
        // filePath = "grays10x10_64.pgm";
        filePath = "lena_128x128_64.pgm";

        Pgm img = new Pgm();
        img.fromFile(filePath, false);
        int r = M.log2(img.getShade()) - 1;
        System.out.println(r);
        ReedMuller rm = new ReedMuller(r);

        Pgm img2 = rm.encode(img, false);
        img2.toFile("codedImage.pgm");

        Pgm img2n = rm.noise(img2, 0, false);
        img2n.toFile("noisedImage.pgm");

        Pgm img2d = rm.denoiseDFT(img2n, false);
        img2d.toFile("denoisedImage.pgm");

        Pgm img3 = rm.decode(img2d, false);
        img3.toFile("decodedImage.pgm");

    }
}
