package sherp.simpleimage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Thread-accelerated image processing class.
 *
 *
 * @author GitHub/sherpavel
 */
public class ImageProcessing extends Image {
    private static int threadsCount = 2;

    /**
     * Returns an {@code ImgeProcessing} object with an {@code Image} from {@code BufferedImage}.
     *
     * @param image {@code BufferedImage} object
     */
    public ImageProcessing(BufferedImage image) {
        super(image);
    }

    /**
     * Returns an {@code ImageProcessing} object with a copy of an {@code Image} object.
     *
     * @param image {@code Image} object
     */
    public ImageProcessing(Image image) {
        super(image.image);
    }

    /**
     * Allocates a given amount of threads during an active processing activity.
     * This setting affects all the {@code ImageProcessing} instances.
     *
     * @param threadsCount amount of dedicated thread
     */
    public static void setThreadsCount(int threadsCount) {
        if (threadsCount < 0)
            throw new IllegalArgumentException("Less than 0");
        ImageProcessing.threadsCount = threadsCount;
    }

    /**
     * Merges the current photo with another one. For each pixel it takes an average between the two images.
     * The return image is the same size as current.
     * If the image, current one is being merged with, is of a different size, it will be scaled up/down to fit.
     * Throws {@code RuntimeException} if merge fails due to multithreading error.
     *
     * @param otherImage image to be merged with
     * @return new merged image
     * @throws RuntimeException if merge fails due to multithreading error
     */
    public ImageProcessing merge(Image otherImage) {
        if (width != otherImage.width || height != otherImage.height)
            otherImage = otherImage.resize(width, height);

        Image finalOtherImage = otherImage.copy();
        Image resultImg = new Image(width, height);

        ThreadedMatrix threadedMatrix = new ThreadedMatrix(threadsCount);
        threadedMatrix.setTask(width, height, (x, y) ->
                resultImg.setRGB(x, y,
                (getRed(x, y) + finalOtherImage.getRed(x, y)) / 2,
                (getGreen(x, y) + finalOtherImage.getGreen(x, y)) / 2,
                (getBlue(x, y) + finalOtherImage.getBlue(x, y)) / 2)
        );

        try {
            threadedMatrix.start();
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread interrupted");
        }

        return new ImageProcessing(resultImg);
    }

    /**
     * Merges all the image files in the directory and filtered by name.
     * For each pixel it takes an average between all the images.
     * Throws {@code RuntimeException} if merge fails due to multithreading error.
     *
     * @param directory path to the images location
     * @param nameFilter filter files by their names
     * @param threadsCount amount of dedicated threads for parallel computing
     * @return new merged image
     * @throws RuntimeException if merge fails due to multithreading error
     */
    public static ImageProcessing mergeAll(String directory, String nameFilter, int threadsCount) {
        if (directory == null)
            throw new IllegalArgumentException("Null path");
        if (nameFilter == null)
            throw new IllegalArgumentException("Null name filter");

        File dir = new File(directory);

        List<Image> images = new ArrayList<>();

        int maxWidth = 0;
        int maxHeight = 0;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile() && file.getName().contains(nameFilter)) {
                Image image;
                try {
                    image = Image.readFile(file);
                } catch (IOException e) {
                    continue;
                }
                images.add(image);
                if (image.getWidth() > maxWidth)
                    maxWidth = image.getWidth();
                if (image.getHeight() > maxHeight)
                    maxHeight = image.getHeight();
            }
        }

        for (int i = 0 ; i < images.size(); i++)
            images.set(i, images.get(i).resize(maxWidth, maxHeight));

        Image resultImg = new Image(maxWidth, maxHeight);

        ThreadedMatrix threadedMatrix = new ThreadedMatrix(threadsCount);
        threadedMatrix.setTask(maxWidth, maxHeight, (x, y) -> {
            int a = 0, r = 0, g = 0, b = 0;
            for (Image image : images) {
                r += image.getRed(x, y);
                g += image.getGreen(x, y);
                b += image.getBlue(x, y);
            }

            resultImg.setRGB(x, y,
                    r / images.size(),
                    g / images.size(),
                    b / images.size());
        });

        try {
            threadedMatrix.start();
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread interrupted");
        }

        return new ImageProcessing(resultImg);
    }


    // Kernel computing

    /**
     * Arithmetic mean.
     */
    public static final int ARITHMETIC_MEAN = 0;
    /**
     * Geometric mean.
     */
    public static final int GEOMETRIC_MEAN = 1;
    /**
     * Root Mean Square.
     */
    public static final int ROOT_MEAN_SQUARE = 2;

    /**
     * Simple 3 by 3 box blur kernel.
     */
    public static final double[][] BOX_3X3_BLUR_KERNEL = {
            {1.0 / 9, 1.0 / 9, 1.0 / 9},
            {1.0 / 9, 1.0 / 9, 1.0 / 9},
            {1.0 / 9, 1.0 / 9, 1.0 / 9},
    };
    /**
     * Simple 5 by 5 box blur kernel.
     */
    public static final double[][] BOX_5X5_BLUR_KERNEL = {
            {1.0/25, 1.0/25, 1.0/25, 1.0/25, 1.0/25},
            {1.0/25, 1.0/25, 1.0/25, 1.0/25, 1.0/25},
            {1.0/25, 1.0/25, 1.0/25, 1.0/25, 1.0/25},
            {1.0/25, 1.0/25, 1.0/25, 1.0/25, 1.0/25},
            {1.0/25, 1.0/25, 1.0/25, 1.0/25, 1.0/25},
    };
    /**
     * Simple outline kernel.
     */
    public static final double[][] outlineKernel = {
            {-1, -1, -1},
            {-1, 8, -1},
            {-1, -1, -1}
    };

    /**
     * Returns a box blur kernel with the specified side length.
     * If an even number is provided, it will be treated as a nearest lowest odd number.
     * Throws {@code RuntimeException} if side size is less than or equal 0.
     *
     * @param side box side size (odd number)
     * @return kernel
     * @throws RuntimeException if side size is less than or equal 0
     */
    public static double[][] boxBlurKernel(int side) {
        if (side < 1)
            throw new RuntimeException("Side less than or equal 0");
        if (side % 2 == 0)
            side -= 1;

        double[][] kernel = new double[side][side];
        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                kernel[i][j] = 1.0 / (side * side);
            }
        }

        return kernel;
    }

    /**
     * Processes image through all the provided kernels and averages each each pass through the mean formula.
     * This methods uses multithreading, so make sure to setup the amount of dedicated thread for faster processing.
     *
     * @param monochrome whether output image will be monochrome or not
     * @param meanFormula type of the averaging (general mean) formula
     * @param kernels list of kernels. Kernel may be rectangular BUT width and height MUST be odd numbers.
     * @return processed image
     * @throws RuntimeException if call fails due to multithreading error
     */
    public ImageProcessing applyKernel(boolean monochrome, int meanFormula, double[][]... kernels) {
        Image resultImg = new Image(width, height);

        if (monochrome) {
            ThreadedMatrix threadedMatrix = new ThreadedMatrix(threadsCount);
            threadedMatrix.setTask(width, height, (x, y) -> {
                double total = 0;
                if (meanFormula == GEOMETRIC_MEAN)
                    total = 1;

                int dx, dy;
                for (double[][] kernel : kernels) {
                    int kdx = kernel[0].length / 2;
                    int kdy = kernel.length / 2;

                    double mag = 0;
                    for (int ky = 0; ky < kernel.length; ky++) {
                        for (int kx = 0; kx < kernel[0].length; kx++) {
                            if ((dx = Math.abs(x + kx - kdx)) >= width)
                                dx -= kx - kdx;
                            if ((dy = Math.abs(y + ky - kdy)) >= height)
                                dy -= ky - kdy;

                            mag += kernel[ky][kx] * getMonochrome(dx, dy);
                        }
                    }

                    switch (meanFormula) {
                        case ARITHMETIC_MEAN:
                            total += mag;
                            break;
                        case GEOMETRIC_MEAN:
                            total *= mag;
                            break;
                        case ROOT_MEAN_SQUARE:
                            total += mag * mag;
                            break;
                        default:
                            throw new RuntimeException("Unknown mean formula");
                    }
                }

                switch (meanFormula) {
                    case ARITHMETIC_MEAN:
                        total /= kernels.length;
                        break;
                    case GEOMETRIC_MEAN:
                        total = Math.pow(total, 1.0 / kernels.length);
                        break;
                    case ROOT_MEAN_SQUARE:
                        total = Math.pow(total / kernels.length, 1.0 / kernels.length);
                        break;
                    default:
                        throw new RuntimeException("Unknown mean formula");
                }

                if (total < 0)
                    total = 0;
                if (total > 255)
                    total = 255;

                resultImg.setMonochrome(x, y, (int) total);
            });

            try {
                threadedMatrix.start();
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread interrupted");
            }
        }

        // Non-monochrome

        else {
            Thread[] colorsThreads = new Thread[3];
            for (int t = 0; t < 3; t++) {
                int thread = t;
                colorsThreads[t] = new Thread(() -> {
                    ThreadedMatrix threadedMatrix = new ThreadedMatrix(threadsCount / 3 + 1);
                    threadedMatrix.setTask(width, height, (x, y) -> {
                        double total = 0;
                        if (meanFormula == GEOMETRIC_MEAN)
                            total = 1;

                        int dx, dy;
                        for (double[][] kernel : kernels) {
                            int kdx = kernel[0].length / 2;
                            int kdy = kernel.length / 2;

                            double mag = 0;
                            for (int ky = 0; ky < kernel.length; ky++) {
                                for (int kx = 0; kx < kernel[0].length; kx++) {
                                    if ((dx = Math.abs(x + kx - kdx)) >= width)
                                        dx -= kx - kdx;
                                    if ((dy = Math.abs(y + ky - kdy)) >= height)
                                        dy -= ky - kdy;

                                    int color = 0;
                                    switch (thread) {
                                        case 0:
                                            color = getRed(dx, dy);
                                            break;
                                        case 1:
                                            color = getGreen(dx, dy);
                                            break;
                                        case 2:
                                            color = getBlue(dx, dy);
                                            break;
                                    }
                                    mag += kernel[ky][kx] * color;
                                }
                            }

                            switch (meanFormula) {
                                case ARITHMETIC_MEAN:
                                    total += mag;
                                    break;
                                case GEOMETRIC_MEAN:
                                    total *= mag;
                                    break;
                                case ROOT_MEAN_SQUARE:
                                    total += mag * mag;
                                    break;
                                default:
                                    throw new RuntimeException("Unknown mean formula");
                            }
                        }

                        switch (meanFormula) {
                            case ARITHMETIC_MEAN:
                                total /= kernels.length;
                                break;
                            case GEOMETRIC_MEAN:
                                total = Math.pow(total, 1.0 / kernels.length);
                                break;
                            case ROOT_MEAN_SQUARE:
                                total = Math.pow(total / kernels.length, 1.0 / kernels.length);
                                break;
                            default:
                                throw new RuntimeException("Unknown mean formula");
                        }

                        if (total < 0)
                            total = 0;
                        if (total > 255)
                            total = 255;

                        switch (thread) {
                            case 0:
                                resultImg.setRed(x, y, (int) total);
                                break;
                            case 1:
                                resultImg.setGreen(x, y, (int) total);
                                break;
                            case 2:
                                resultImg.setBlue(x, y, (int) total);
                                break;
                        }
                    });
                    try {
                        threadedMatrix.start();
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Thread interrupted");
                    }
                });
            }

            for (Thread thread : colorsThreads)
                thread.start();

            try {
                for (Thread thread : colorsThreads)
                    thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread interrupted");
            }
        }

        return new ImageProcessing(resultImg);
    }

    /**
     * Sobel filter function.
     *
     * @return filtered image
     * @throws RuntimeException if call fails due to multithreading error
     */
    public ImageProcessing sobel() {
        int[][] xKernel = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };
        int[][] yKernel = {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };

        Image resultImg = new Image(getWidth() - 2, getHeight() - 2);

        ThreadedMatrix threadedMatrix = new ThreadedMatrix(threadsCount);
        threadedMatrix.setTask(getWidth(), getHeight(), (x, y) -> {
            double Gx = 0;
            double Gy = 0;
            if (x == 0 || x == getWidth()-1 || y == 0 || y == getHeight()-1)
                return;
            for (int ky = 0; ky < 3; ky++) {
                for (int kx = 0; kx < 3; kx++) {
                    Gx += xKernel[ky][kx] * getMonochrome(x + kx - 1, y + ky - 1);
                    Gy += yKernel[ky][kx] * getMonochrome(x + kx - 1, y + ky - 1);
                }
            }
            double G = Math.sqrt(Gx*Gx + Gy*Gy);
            if (G < 0)
                G = 0;
            if (G > 255)
                G = 255;
            resultImg.setMonochrome(x-1, y-1, (int)G);
        });

        try {
            threadedMatrix.start();
        } catch (InterruptedException e) {
            System.err.println("Interrupted");
            return null;
        }

        return new ImageProcessing(resultImg);
    }

    /*
    Gaussian Blur is not working currently
     */
//    static Image gaussianBlur(Image image, int matrixSize, double standardDeviation) {
//        double stDev = standardDeviation;
//        double[][] kernel = new double[matrixSize][matrixSize];
//
//        int kernelRadius = matrixSize / 2;
//        for (int x = -kernelRadius; x <= kernelRadius; x++) {
//            for (int y = -kernelRadius; y <= kernelRadius; y++) {
//                kernel[y+kernelRadius][x+kernelRadius] = Math.exp(-(x*x+y*y)/(2*stDev*stDev)) / (2*Math.PI*stDev*stDev);
//                System.out.println(kernel[y+kernelRadius][x+kernelRadius]);
//            }
//        }
//
//        final double[][] finalKernel = kernel;
//        int kdx = finalKernel[0].length / 2;
//        int kdy = finalKernel.length / 2;
//
//        Image resultImg = resultImg = new Image(image.getWidth(), image.getHeight());
//
//        ThreadedMatrix threadedMatrix = new ThreadedMatrix(threadsCount);
//        Image finalResultImg = resultImg;
//        threadedMatrix.setTask(image.getWidth(), image.getHeight(), (x, y) -> {
//            double bw = 0;
//            int dx, dy;
//            for (int kx = 0; kx < finalKernel.length; kx++) {
//                for (int ky = 0; ky < finalKernel[0].length; ky++) {
//                    if ((dx = Math.abs(x + kx - kdx)) >= image.getWidth())
//                        return;
//                    if ((dy = Math.abs(y + ky - kdy)) >= image.getHeight())
//                        return;
//                    bw += finalKernel[ky][kx] * image.getMonochrome(dx, dy);
//                }
//            }
//            if (bw < 0)
//                bw = 0;
//            if (bw > 255)
//                bw = 255;
//            finalResultImg.setMonochrome(x, y, (int) bw);
//        });
//
//        try {
//            threadedMatrix.start();
//        } catch (InterruptedException e) {
//            System.err.println("Interrupted");
//        }
//
//        return resultImg;
//    }
}
