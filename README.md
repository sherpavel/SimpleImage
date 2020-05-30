# Simple Image ![GitHub release (latest by date)](https://img.shields.io/github/v/release/sherpavel/SimpleImage?style=flat-square) ![GitHub](https://img.shields.io/github/license/sherpavel/SimpleImage?style=flat-square)
> Simple, easy to use image library with multithreaded processing.


## Table of contents
* [General info](#general-info)
* [Setup](#setup)
* [Features](#features)
* [Code examples](#code-examples)
* [__Image processing__](#processing)
* [Status](#status)
* [Contact](#contact)


## General info
This project aims to simplify work with images, image files and image processing. 
Unlike `BufferedImage`, `SimpleImage` provides easy access to all of RBG channels separately, quick image resizing, 
simple processing and saving image into a file.


## Setup
### Method 1
1. Download `SimpleImage.jar`
2. In your IDE locate a setting for adding external .jar file
4. Add `SimpleImage.jar` to your project's dependencies
5. Create your images!

### Method 2
1. Clone or download files from GitHub
2. Place `sherp` folder from the downloaded `src` in your project's source
3. Create your images!


## Features
* Multithreading acceleration
* Works with image kernels
* Thread safe methods


## Code Examples
### Creating an image:
With specific dimensions:
```
Image image = new Image(200, 100);
```
From an existing `BufferedImage` object:
```
Image image = new Image(bufferedImage);
```
### Reading an image from a file:
```
Image image = Image.readFile(filename: "example image.jpg");
```
or from Java `File`:
```
Image image = Image.readFile(File: exampleFile);
```
Saving an image to a file requires separating filename from file extension:
```
exampleImage.save(name: "example image", extension: ImageFormat.JPG);
```
### Color values
Getting and setting color values are done through according getters and setters.
All colors are in the range of [0 - 255].
```
int redColor = exampleImage.getRed(x: 10, y: 10);
exampleImage.setRed(x: 10, y: 10, value: 150);
```
### Resizing
Resizing is simply done with `.resize(width, height)` method.


## Image processing
### Multithreading
Image processing requires the use of a separate class `ImageProcessing`. 
This class extends `Image`, thus all the previous functionality is still accessible. 
To get `ImageProcessing` instance from an `Image` object, use:
```
ImageProcessing imageProcessing = new ImageProcessing(Image: exampleImage);
```
Image processing uses multithreading to accelerate calculations. By default, the amount of dedicated threads is set to 2.
To set custom amount use:
```
ImageProcessing.setThreadsCount(integer > 1);
``` 
### Kernels
Kernel typically is a small matrix (filter) that is applied to each pixel and computes a weighted average, 
depending on the kernel setup.  
Processing class has a collection of pre-made kernels like box blur and outline, 
as well as a separate method for creating custom size box blur kernels and a 
[Sobel filter](https://en.wikipedia.org/wiki/Sobel_operator) function.  
`ImageProcessing` supports multiple kernels' application at once. 
To average the final result, one of the following mean functions is used (list will expand in future):
```
ImageProcessing.ARITHMETIC_MEAN
ImageProcessing.GEOMETRIC_MEAN
ImageProcessing.ROOT_MEAN_SQUARE
```
The kernel applicator supports both monochrome and color application. 
For color variant the three color channels are split, calculated separately and then put back together in the final image.  
To apply a kernel use:
```
image = exampleImage.applyKernel(monochrome: true/false, 
    meanFunction: ImageProcessing.ARITHMETIC, 
    kernels: kernel1, kernel2, ...);
```


## Status

###Project is _in progress_

### Todo list
* Gaussian blur


## Contact
Created by [@sherpavel](https://www.linkedin.com/in/sherpavel/) - feel free to contact me!
