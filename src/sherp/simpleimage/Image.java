package sherp.simpleimage;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * {@code Image} is a class that stores and works with images and image files.
 * In its core it stores an image as {@link java.awt.image.BufferedImage BufferedImage} object
 * and all methods interact with it.
 *
 * @author GitHub/sherpavel
 */
public class Image {
    BufferedImage image;
    int width;
    int height;

    /**
     * Creates an {@code Image} from {@code BufferedImage}.
     *
     * @param image image
     */
    public Image(BufferedImage image) {
        this.image = Objects.requireNonNull(image);
        width = image.getWidth();
        height = image.getHeight();
    }

    /**
     * Creates an {@code Image} with the given dimensions.
     *
     * @param width image width
     * @param height image height
     * @throws IllegalArgumentException if either dimension is less than 0
     */
    public Image(int width, int height) {
        if (width < 0 || height < 0)
            throw new IllegalArgumentException("Dimensions less then 0");
        this.width = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Creates an {@code Image} from the specified file.
     *
     * @param file image file
     * @return {@code Image} instance
     * @throws IOException if reading file produces error
     */
    public static Image readFile(File file) throws IOException {
        Objects.requireNonNull(file);
        if (!file.exists() || !file.isFile())
            throw new IOException("File not found");
        return new Image(ImageIO.read(file));
    }

    /**
     * Creates an {@code Image} from the specified file.
     *
     * @param filepath image filepath (relative or absolute)
     * @return {@code Image} instance
     * @throws IOException if reading file produces error
     */
    public static Image readFile(String filepath) throws IOException {
        return readFile(new File(Objects.requireNonNull(filepath)));
    }

    /**
     * Returns {@link java.awt.image.BufferedImage BufferedImage} object.
     *
     * @return {@code BufferedImage} image
     */
    public BufferedImage getBufferedImage() {
        return image;
    }

    /**
     * Returns image width.
     *
     * @return image width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Returns image height.
     *
     * @return image height
     */
    public int getHeight() {
        return this.height;
    }

    //Private stuff
    private int rgb(int a, int r, int g, int b) {
        int p = 0;
        p = p | ((a % 256) << 24); p = p | ((r % 256) << 16); p = p | ((g % 256) << 8); p = p | (b % 256);
        return p;
    }
    private int alpha(int p) {
        return (p>>24) & 0xff;
    }
    private int red(int p) {
        return (p>>16) & 0xff;
    }
    private int green(int p) {
        return (p>>8) & 0xff;
    }
    private int blue(int p) {
        return (p) & 0xff;
    }

    private boolean isOutOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= width || y >= height;
    }

    private void throwErrorIfOutOfBounds(int x, int y) {
        if (isOutOfBounds(x, y))
            throw new IllegalArgumentException(x + ":" + y + " out of bounds");
    }
    //=========================================================================


    // Getters

//    /**
//     * Returns an image alpha value at {@code (x, y)} in the range of {@code [0, 255]}.
//     *
//     * @param x image X coordinate
//     * @param y image Y coordinate
//     * @return alpha value {@code [0, 255]}
//     * @throws IllegalArgumentException if coordinates are out of bounds
//     */
//    public int getAlpha(int x, int y) {
//        throwErrorIfOutOfBounds(x, y);
//        return alpha(image.getRGB(x, y));
//    }

    /**
     * Returns an image red color value at {@code (x, y)} in the range of {@code [0, 255]}.
     *
     * @param x image X coordinate
     * @param y image Y coordinate
     * @return red color value {@code [0, 255]}
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public int getRed(int x, int y) {
        throwErrorIfOutOfBounds(x, y);
        return red(image.getRGB(x, y));
    }

    /**
     * Returns an image green color value at {@code (x, y)} in the range of {@code [0, 255]}.
     *
     * @param x image X coordinate
     * @param y image Y coordinate
     * @return green color value {@code [0, 255]}
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public int getGreen(int x, int y) {
        throwErrorIfOutOfBounds(x, y);
        return green(image.getRGB(x, y));
    }

    /**
     * Returns an image blue color value at {@code (x, y)} in the range of {@code [0, 255]}.
     *
     * @param x image X coordinate
     * @param y image Y coordinate
     * @return blue color value {@code [0, 255]}
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public int getBlue(int x, int y) {
        throwErrorIfOutOfBounds(x, y);
        return blue(image.getRGB(x, y));
    }

    /**
     * Returns an array of RGB colors. {@code [red, green, blue]}. Each channel is in the range of {@code [0, 255]}.
     *
     * @param x image X coordinate
     * @param y image Y coordinate
     * @return integer RGB array, each value {@code [0, 255]}
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public int[] getRGB(int x, int y) {
        throwErrorIfOutOfBounds(x, y);
        return new int[]{
                getRed(x, y),
                getGreen(x, y),
                getBlue(x, y)};
    }

//    /**
//     * Returns an array of Alpha RGB colors. {@code [alpha, red, green, blue]}. Each value is in the range of {@code [0, 255]}.
//     *
//     * @param x image X coordinate
//     * @param y image Y coordinate
//     * @return integer ARGB array, each value {@code [0, 255]}
//     * @throws IllegalArgumentException if coordinates are out of bounds
//     */
//    public int[] getARGB(int x, int y) {
//        throwErrorIfOutOfBounds(x, y);
//        return new int[]{
//                getAlpha(x, y),
//                getRed(x, y),
//                getGreen(x, y),
//                getBlue(x, y)};
//    }

    /**
     * Returns an integer corresponding to the monochrome color value at {@code (x, y)} in the range of {@code [0, 255]}. Calculated according to formula: 3% red + 59% green + 11% blue.
     *
     * @param x image X coordinate
     * @param y image Y coordinate
     * @return integer ARGB array, each value {@code [0, 255]}
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public int getMonochrome(int x, int y) {
        throwErrorIfOutOfBounds(x, y);
        return (int) (0.3 * getRed(x, y) + 0.59 * getGreen(x, y) + 0.11 * getBlue(x, y));
    }


    // Setters

    /**
     * Sets a pixel RGB value at {@code (x, y)}.
     *
     * @param x image X coordinate
     * @param y image Y coordinate
     * @param rgb RGB value
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public void setRGB(int x, int y, int rgb) {
        throwErrorIfOutOfBounds(x, y);
        image.setRGB(x, y, rgb);
    }

    /**
     * Sets a pixel RGB value from red, green and blue values separately. Each colors' value is in the range of {@code [0, 255]}.
     *
     * @param x image X coordinate
     * @param y image Y coordinate
     * @param r red color value
     * @param g green color value
     * @param b blue color value
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public void setRGB(int x, int y, int r, int g, int b) {
        throwErrorIfOutOfBounds(x, y);
        image.setRGB(x, y, rgb(255, r, g, b));
    }

//    /**
//     * Sets a pixel ARGB value from alpha, red, green and blue values separately. Each colors' value is in the range of {@code [0, 255]}.
//     *
//     * @param x image X coordinate
//     * @param y image Y coordinate
//     * @param a alpha value
//     * @param r red color value
//     * @param g green color value
//     * @param b blue color value
//     * @throws IllegalArgumentException if coordinates are out of bounds
//     */
//    public void setARGB(int x, int y, int a, int r, int g, int b) {
//        throwErrorIfOutOfBounds(x, y);
//        image.setRGB(x, y, rgb(a, r, g, b));
//    }

    /**
     * Sets a pixel black and white value from red, green and blue values separately. Each colors' value is in the range of {@code [0, 255]}.
     *
     * @param x image X coordinate
     * @param y image Y coordinate
     * @param m monochrome (black&white) value
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public void setMonochrome(int x, int y, int m) {
        throwErrorIfOutOfBounds(x, y);
        image.setRGB(x, y, rgb(255, m, m, m));
    }

//    /**
//     * Sets a pixel alpha value in the range of {@code [0, 255]}.
//     *
//     * @param x image X coordinate
//     * @param y image Y coordinate
//     * @param alpha alpha value
//     * @throws IllegalArgumentException if coordinates are out of bounds
//     */
//    public void setAlpha(int x, int y, int alpha) {
//        throwErrorIfOutOfBounds(x, y);
//        image.setRGB(x, y, rgb(alpha, getRed(x, y), getGreen(x, y), getBlue(x, y)));
//    }

    /**
     * Sets a pixel red color value in the range of {@code [0, 255]}.
     *
     * @param x image X coordinate
     * @param y image Y coordinate
     * @param r red color value
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public void setRed(int x, int y, int r) {
        throwErrorIfOutOfBounds(x, y);
//        image.setRGB(x, y, rgb(getAlpha(x, y), r, getGreen(x, y), getBlue(x, y)));
        image.setRGB(x, y, rgb(255, r, getGreen(x, y), getBlue(x, y)));
    }

    /**
     * Sets a pixel green color value in the range of {@code [0, 255]}.
     *
     * @param x image X coordinate
     * @param y image Y coordinate
     * @param g green color value
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public void setGreen(int x, int y, int g) {
        throwErrorIfOutOfBounds(x, y);
//        image.setRGB(x, y, rgb(getAlpha(x, y), getRed(x, y), g, getBlue(x, y)));
        image.setRGB(x, y, rgb(255, getRed(x, y), g, getBlue(x, y)));

    }

    /**
     * Sets a pixel blue color value in the range of {@code [0, 255]}.
     *
     * @param x image X coordinate
     * @param y image Y coordinate
     * @param b blue color value
     * @throws IllegalArgumentException if coordinates are out of bounds
     */
    public void setBlue(int x, int y, int b) {
        throwErrorIfOutOfBounds(x, y);
//        image.setRGB(x, y, rgb(getAlpha(x, y), getRed(x, y), getGreen(x, y), b));
        image.setRGB(x, y, rgb(255, getRed(x, y), getGreen(x, y), b));
    }


    // File stuff

//    /**
//     * Saves an image in specified file with given format.
//     *
//     * @param file image file
//     * @param format image format (extension)
//     * @throws IllegalArgumentException if either argument is {@code null}
//     * @throws IOException if IO error occurred while writing to file
//     */
//     public void save(File file, ImageFormat format) throws IOException {
//         ImageIO.write(image, format.name(), file);
//     }

    /**
     * Creates a file with specified name and format and saves an image into it. If file with the same name already exists, it will be overwritten.
     *
     * @param filename image file name
     * @param format image format (extension)
     * @throws IllegalArgumentException if either argument is {@code null}
     * @throws IOException if IO error occurred while writing to file
     */
    public void save(String filename, ImageFormat format) throws IOException {
        if (filename == null)
            throw new IllegalArgumentException("Null filename");
        if (format == null)
            throw new IllegalArgumentException("Null format");

        File imageFile = new File(filename + "." + format.name().toLowerCase());
//        if (imageFile.exists())
//            throw new IOException(imageFile.getName() + " already exists in this directory");

        ImageIO.write(image, format.name(), imageFile);
    }


    // Special functions

    /**
     * Returns a copy of an image.
     *
     * @return image copy
     */
    public Image copy() {
        return new Image(image);
    }

    /**
     * Returns a copy of an image, resized according to new dimensions.
     *
     * @param width new image width
     * @param height new image height
     * @return resized {@code Image} copy
     * @throws IllegalArgumentException if either dimension is less than 0
     */
    public Image resize(int width, int height) {
        if (width < 0 || height < 0)
            throw new IllegalArgumentException("Dimensions less then 0");
        BufferedImage resultImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resultImg.createGraphics();
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return new Image(resultImg);
    }
}
