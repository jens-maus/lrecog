# Leaves Recognition
LeavesRecognition (lrecog) is a neuronal network based java application/applet to recognize images of leaves accordingly to a previously trained Backpropagation Network.
The intention of this application is to give the user the ability to administrate a hierarchical list of leaf images, where he can performe some sort of edge detection to identify the individual tokens of every image. This tokens will then be the basis of the neuronal network calculations to make it possible to recognize a unknown leaf image and specify the species it belongs to.
The main purpose of this application/documentation should be to show that the outer frame of a leaf and a Backpropagation Network is enough to give a reasonable statement about the species it belongs to.

## Development
The development started in July 2001 based on a study project of Jens Maus at the University of Applied Sciences Dresden (HTW) and was completed in late August.
To realize the above mentioned purpose and to make it possible to use this application on nearly every operating system, the choosen programming language for the implementation is Java.
Beside the fact of writing a java based application to realize this purpose, one addtional feature is that it could also be used as a java applet to directly give the user the ability to start it via a java enabled internet browser.

## Theoretical Background

### Image edge detection
One of the main tasks of this application is the detection of specific tokens in a leaf image. This tokens will then be the basis of the neuronal network calculations. Assuming that the image is a full 2D scan of a single leaf like in the examples below, we considered to use the well-known Prewitt Edge detection algorithm which I want to explain further:

Prewitt edge detection produces an image where higher grey-level values indicate the presence of an edge between two objects. The Prewitt Edge Detection filter computes the root mean square of two 3x3 templates. It is one of the most popular 3x3 edge detection filters.
The Prewitt edge detection filter uses these two 3x3 templates to calculate the gradient value:
```
      -1  0  1      1  1  1
      -1  0  1      0  0  0
      -1  0  1     -1 -1 -1
          X            Y
```
      
Now consider the following 3x3 image window:
```
      +------------+
      | a1  a2  a3 |
      | a4  a5  a6 |
      | a7  a8  a9 |
      +------------+
```
where:
* `a1 .. a9` - are the grey levels of each pixel in the filter window
* `X = -1*a1 + 1*a3 - 1*a4 + 1*a6 - 1*a7 + 1*a9`
* `Y = 1*a1 + 1*a2 + 1*a3 - 1*a7 - 1*a8 - 1*a9`
* Prewitt gradient = `SQRT(X*X + Y*Y)`

All pixels are filtered. In order to filter pixels located near the edge of an image, edge pixels values are replicated to give sufficient data. 

### Thinning
As discussed earlier, the idea of identifying a specific leaf image`s species here is that the outer frame of a leaf is enough to specify the species it belongs to. To accomplish that, it is necessary to identify this outer frame exactly. The previously applied Prewitt Edge detection normally just identify the edges with a preconfigured threshold and after this edge detection we have to perform a thinning algorithm to minimize this threshold-based edge to a one-line frame where we then can apply a sort of token recognition as discussed later.
The used thinning algorithm here processed the image recursivly and minimizes the found lines to a one-pixel wide one by comparing the actual pixel situation with specific patterns and then minimizes it.

### Leaf image token
The central part of this application are the tokens of each leaf image that are found after the image processing is through with it. What exactly stands behind the idea of this tokens and how we defined this tokens should be explained here in detail.
The idea behind the transfer of the leaf image shape into a neuronal network usable form is, that the cosinus and sinus angles of the shape represents the criterias of a recognition pattern.

The right hand image shows a part of a leaf image that was already processed through the above mentioned edge detection and thinning algorithms.

To give you an idea of what you see in this image, here is a short list:
* Green line: The shape of the leaf image after a successfull edge detection & thinning.
* Red Square: This square represents a point on the shape of the leaf image from which we are going to draw a line to the next square.
* Blue line: The compound of the center of two squares from which we are going to calculate the cosinus and sinus angle. Such a blue line is a representation of a leaf token.

If you now take a deeper view on the small triangle zoom on this image you should regonize that it shows a right-angled triangle. This and the summary of all triangles of a leaf image are the representation of the tokens of a leaf from which we can start the neuronal network calculations.	Expl. Token

Detail of Token	On the left hand side you see a small image of the right-angled triangle which represents a token of a single leaf image. Here it should be clear now that the angles A and B are the two necessary parts which will be fit into the neuronal network layers.

With this two angles we can exactly represent the direction of the hypotenuse from point P1 to P2 which is absolutly necessary for the representation of a leaf image.

### Neuronal Network
Another main part of this work is the integration of a feed-forward backpropagation neuronal network. As described earlier the inputs for this neuronal network are the individual tokens of a leaf image, and as a token normally consists of a cosinus and sinus angle, the amount of input layers for this network are the amount of tokens multiplied by two.

The image on the right side should give you an idea of the neuronal network that takes place in the LeavesRecognition application.
We have choosen a feed-forward backpropagation network because it was part of the task to show that just a backpropagation network and the shape of a leaf image is enough to specify the species of a leaf.
The implemented network also just have one input, hidden and output layer to simplify and speed-up the calculations on that java implementation.

To fill the input neurons of the network, we use the previous calculated leaf tokens like dicussed in section 2.3. The number of output neurons is normally specified by the amount of different species because we use a encoded form to specify the outputs.
All other behaviour of the network is specified by the normal mathematical principals of a backpropagation network. If you want to get an idea of how such a backpropagation network works, please refer to deeper explainations about the backpropagation algorithm.

## Implementation

### Features
To give you an idea of what the final implemenation of this application can give you, please see the following list:

* Usable as Java Application or Applet
* Configurable Neuronal Network properties
* XML based config file
* Configurable image processing properties (Threshold/Distance/min.Line)
* Application mode features loading/saving of projects
* Applet mode features the loading of a default config
* Multilingual (English/German incl.)
* Changeable Look&Feel
* Themes system

### Requirements
As every software product, this application also have some sort of requirements to successfully use it:

* Operating system with a Java Virtual Machine (JVM)
* Java Runtime Environment (JRE) v1.3.1 or higher
* Installed Java-Plug-In v1.3.1 or higher for Applet mode

### Starting as an Applet
To get a impression of what this application is for and how it would look like if you install it as an real java application on your system, it is also possible to use it as a Java Applet from within every java enabled browser.
As a Applet has only restricted access to your file system we have predefined some default configuration which will be loaded as soon as you start the applet. In this Applet-Mode you are not able to save or load your project files nor are you able to load images from your own filesystem.
This Applet mode should only give you the possibility to use the application without any installation. But if you encounter that you want to use all features (load/save projects) then you have to install the Application jar file, like described in the next section.

### Starting as an Application
To start LeavesRecognition as a standalone application you need to download the installation archive which you can find in the download section of this documentation. Installation as a standalone application should normally very easy as long as you already have a preinstalled java runtime environment (JRE) on the operating system you are actually using. If you need to install the java environment first, please refer to http://java.sun.com/j2se/ for any further information.

After downloading the installation zip archive you can extract the archives content to a directory of your choice. We have provided and included a startup-script for Linux (bash) and Windows (bat) which you should be able to execute. This script should automatically start the application with all necessary data.

Included in another archive are also some sample images of leaves which you can use for working with the application. Please refer to Chapter 4 (Usage) that explains how to use LeavesRecognition.

## Usage
The LeavesRecognition application is splitted into three main tabbed panes which represent the three main operations a user can perform. One is the Image Processing in which the leaf images will be loaded and ordered in a hierarchical list. The second one is to perform the neuronal network calculations on the previously processed images. And the third one is to recognize a existing leaf image and calculate the amount of recognized tokens to specify the species this image can belong to.

### Image Processing
One of the most important parts of the whole application is the image processing. Without finding any usefull tokens in the leaf images, the neuronal network calculation. So we spent lots of our efforts in the edge detection and thinning algorithms. 

By clicking on the right image you see a snapshot of the image processing tab, where a user can perform different operations to a list of loaded images.
The small left image in this tab is a view of the original image like in the image file and the big one in the center of this window is the image after the user had pressed "Find Token" to process the different image processing operations including edge detection and thinning.
There are 3 configurable sliders on this tab where a user can define the Threshold for the edge detection, the distance of the tokens (red square) and a minimum amount of pixels a line have to be to be recognized as a part of the shape.
At the right upper side of this window you should see a hierarchical JTree where you can add and delete images to this tree. Please note that you can move images from one species to another by Drag&Drop.	Image Processing Tab

So if you have added a image to a species node, this image is recognized as part of this Species and will be included in the neuronal network calculations on the second tab. For a good quality result you should normally add at least 5 images of a species to give the neuronal network enough tokens to find the specific shape of this leaf species.

### Neuronal Network
A central part of the application is the neuronal network. Based on the information gained from the image processing the application can calculate the neuronal network weights and output a error graph to display the overall error of the learning process. 

Like in the first described "Image Processing" tab, the neuronal network tab has also some sort of configuration. Mainly this configuration are the properties of the neuronal network. If you are not so familar with the background of neuronal network you can simply press "Set Default" and use the default properties the application suggests for your environment.

Based on the amount of images and network properties you normally need to specify around 500-1000 training steps to get a good result in the recognition later. If the error rate drops below 0.01 you normally should encounter no problem in recognizing different leaf images.
The amount of input neurons for the network is normally twice the amount of tokens because of the sinus & cosinus value for one token.	Neuronal Network Tab

You should keep the learning rate and number of hidden neurons low, because then you normally get out a good training phase for the network. Additionally of specifing the input and hidden neurons you are also able to specify the output neurons, what is normally not needed. The amount of output neurons is normally the amount of different leaf species which are in the JTree Panel of the image processing.

### Recognition
The last of three parts in this application is the recognition tab. Here you can load a image and process a recognition of this leaf image to specify the possible species it can belong to. 

Like in the first "Image processing" tab you are also able to control the Threshold, Distance and min.Line of the image processing that takes place on the loaded image as soon as you press "Recognize".

After this image processing, the LeavesRecognition application is going to use the recognized tokens of this new image as the values to identify the species this image belongs to.

The results of this recognition will be displayed in a short precentage JTable on the right side of the GUI. Here you can see which of the species that are in the trained network are most similar to the loaded leaf image.	Recognition Tab

Speaking of the results this application produces, we can say that on a well trained neuronal network it normally should point at one specific species. Also an interesting point of view is the neighbourhood of species which can somehow be explained with the results of the neuronal network of this application.

## Copyright
This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

## Disclaimer
The Authors, assumes no responsibility for errors or omissions in these materials.
THESE MATERIALS ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. The Authors further does
not warrant the accuracy or completeness of the information, text, graphics,links or other items contained within these
materials. The Authors shall not be liable for any special, indirect, incidental, or consequential damages, including without
limitation, lost revenues or lost profits, which may result from the use of these materials. The information on this server is
subject to change without notice and does not represent a commitment on the part of the Authors in the future.

## Authors
* Jens Maus - mail@jens-maus.de
