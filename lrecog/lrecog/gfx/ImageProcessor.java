/*
 * Leaves Recognition - a neuronal network based recognition of leaf images
 * Copyright (C) 2001 Jens Langner, LightSpeed Communications GbR
 *
 * LightSpeed Communications GbR
 * Lannerstrasse 1
 * 01219 Dresden
 * Germany
 * http://www.light-speed.de/
 * <Jens.Langner@light-speed.de>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Id$
 */

package lrecog.gfx;

import java.awt.*;
import java.awt.image.*;
import java.awt.Toolkit.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;

import lrecog.tools.*;

/**
* ImageProcessor class
*
* This class provides all needed image processing
* methods like EdgeDetection (Prewitt), Thinning
* and calculation of the Cosinus vectors
*
*/
public class ImageProcessor extends Panel
{
	private int Pixels[];					      // PixelArray for representing the Picture
	private ArrayList alltokens=null;   // private ArrayList that has all tokens in it
	private int width, height;		      // Size of the Picture

  /**
  * Special values for processing Image algorithms
  */
	public static final int PIXEL_MASK = 0x000000ff;        // special ColorMask for getting gray pixels

	// Color information
	// Colors are: Alpha(00-ff) Red(00-ff) Green(00-ff) Blue(00-ff)
	private static final int COLOR_BACKGROUND = 0xffffffff; // Background color (white)
	private static final int COLOR_FOREGROUND = 0xff000000; // Foreground color (black)
	private static final int COLOR_SKELETON   = 0xff0000ff; // Color of the skeleton lines
	private static final int COLOR_REMOVABLE  = 0xff00ffff; // Color of marking pixels for erase.
  private static final int COLOR_GOODLINE	  = 0xff00ff00; // Color Line OK (green)
	private static final int COLOR_BADLINE	  = 0xffff0000;	// Color Line too short (red)
	private static final int COLOR_DONE	      = 0xfffffe00;	// Mark already processed pixels
  private static final int COLOR_POINT_MARK = 0xffff00ff; // Color to mark special points
  private static final int COLOR_POINT_DONE = 0xffffff00; // Color to mark points that we already processed

	private static int LINE_MIN = 20;   // to recognize a line it have to have this minimum of pixels
  private static int POINT_DIFF = 20; // Distance of the special points

  // special values for the markPoints() method
  private static long points_pos;
  private static int root_x, root_y;

	/**
  * Constructor
  */
	public ImageProcessor(int _w, int _h)
	{
		width  = _w;
		height = _h;
		Pixels = new int[width*height];
	}

	/**
  * Constructor, to directly process a Image
  */
	public ImageProcessor(Image myImage)
	{
		width  = myImage.getWidth(null);
		height = myImage.getHeight(null);
		Pixels = new int[width*height];

		PixelGrabber myPixelGrabber = new PixelGrabber(myImage, 0, 0, width, height, Pixels, 0, width);
		try
    {
			myPixelGrabber.grabPixels();
		}
    catch(Exception e)
    {
      System.out.println(e);
    }
	}

  /**
  * getImage()
  *
  * get the actual processed Image
  */
	public Image getImage(Component theComponent)
	{
		Image myImage;
		MediaTracker myMediaTracker = new MediaTracker(theComponent);
		myImage = createImage(new MemoryImageSource(width, height, Pixels, 0, width));

		myMediaTracker.addImage(myImage,0);
		try
    {
			// wait until Object is in the tracker list
      myMediaTracker.waitForAll();
		}
    catch (Exception e)
    {
      System.out.println(e);
    }

		return myImage;
	}


	/**
  * Clear()
  *
  * clear the whole pixel array with white pixels
  */
	public void Clear()
	{
		for(int i=0; i < height*width; i++)
    {
			Pixels[i] = COLOR_BACKGROUND;
		}
	}

	/**
  * getWidth()
  *
  * return the width of the picture
  */
	public int getWidth()
	{
		return width;
	}

	/**
  * getHeight()
  *
  * return the height of the picture
  */
	public int getHeight()
	{
		return height;
	}

	/**
  * setPixel()
  *
  * set the color of a specified pixel in the Array
  */
	public void setPixel(int x, int y, int color)
	{
		if(x<0 || y<0 || x>=width || y>=height)
    {
			return; // Point in not within the picture
    }
		Pixels[y*width + x] = color;
	}

	/**
  * setGrayPixel()
  *
  * set the specified pixel in the Array with a specified
  * amount of gray color
  */
	public void setGrayPixel(int x, int y, int gray)
	{
		if(x<0 || y<0 || x>=width || y>=height)
    {
			return; // Point is not within the picture
    }
		if(gray<0)	gray = 0;
		if(gray>255)	gray = 255;

		Pixels[y*width+x] = COLOR_FOREGROUND | gray | gray << 8 | gray << 16;
	}

	/**
  * getPixel()
  *
  * return the color of the specified pixel in the array
  */
	public int getPixel(int x, int y)
	{
		if(x<0)		x=0;
		if(y<0)		y=0;
		if(x>=width)	x=width-1;
		if(y>=height)	y=height-1;

		return Pixels[y*width+x];
	}

	/**
  * getGrayPixel()
  *
  * return the amount of gray color for the specified pixel
  * in the array.
  */
	public int getGrayPixel(int x, int y)
	{
		if(x<0)		x=0;
		if(y<0)		y=0;
		if(x>=width)	x=width-1;
		if(y>=height)	y=height-1;

		return  (Pixels[y*width+x]         & PIXEL_MASK) / 3 +
			      ((Pixels[y*width+x] >>  8) & PIXEL_MASK) / 3 +
			      ((Pixels[y*width+x] >> 16) & PIXEL_MASK) / 3;
	}

	/**
  * drawCirlce()
  *
  * draw a circle with radius r at the specified position
  * and with the specified color
  */
	public void drawCircle(int x, int y, int r, int color)
	{
		for (int i=0; i < 360; i+=5)
    {
      setPixel((int)Math.round(x+Math.sin(i)*r), (int)Math.round(y+Math.cos(i)*r), color);
		}
	}

	/**
  * drawSquare()
  *
  * draw a square with the length of r at a specified position
  * and with the specified color
  */
	public void drawSquare(int x, int y, int r, int color)
	{
    int i;

		for(i=x-(r/2); i<=x+(r/2); i++)
    {
			setPixel(i, y-(r/2), color);
			setPixel(i, y+(r/2), color);
		}

		for(i=y-(r/2); i<=y+(r/2); i++)
    {
			setPixel(x-(r/2), i, color);
			setPixel(x+(r/2), i, color);
		}
	}

	/**
  * drawLine()
  *
  * draws a line from x0, y0 to x1, y1 with the well-known
  * Bresenham Algorithm with the specified color.
  */
  void drawLine(int x0, int y0, int x1, int y1, int color)
  {
    int ex = x1 - x0;
    int ey = y1 - y0;
    int dx, dy, height;

    if (ex > 0)
    {
      dx = 1;
    }
    else if (ex < 0)
    {
      dx = -1;
      ex = -ex;
    }
    else dx = 0;

    if (ey > 0)
    {
      dy = 1;
    }
    else if (ey < 0)
    {
      dy = -1;
      ey = -ey;
    }
    else dy = 0;

    int x = x0, y = y0;
    if (ex > ey)
    {
      height = 2*ey -ex;
      while(x != x1)
      {
        if(height >= 0)
        {
          height -= 2*ex;
          y += dy;
        }
        height += 2*ey;
        x += dx;
        setPixel(x, y, color);
      }
    }
    else
    {
      height = 2*ex -ey;
      while(y != y1)
      {
        if(height >= 0)
        {
          height -= 2*ey;
          x += dx;
        }
        height += 2*ex;
        y += dy;
        setPixel(x, y, color);
      }
    }
  }

  /**
  * edgeDetect()
  *
  * method that processes a special edge detection on the whole
  * array of the Picture.
  * This edge detection is processed on the gray color amount of the
  * pixels.
  *
  * For the edge detection the "Prewitt" Alogrithm is used:
  *
  *     (1 0 -1)          (-1 -1 -1)
  * 1/3*(1 0 -1)      1/3*( 0  0  0)
  *     (1 0 -1)          ( 1  1  1)
  */
	public void edgeDetect(int threshold)
	{
		int x, y, sum;
    int max=0, min=0;

		// first we create a clear buffer Picture with the
    // same height & width
		ImageProcessor Source = new ImageProcessor(width, height);
    Source.Clear();

		// Now we process all source pixels of the Array
    // with the "Prewitt" Algorithm
		for(x=width-2; x > 0; x--)
    {
			for(y=height-2; y > 0; y--)
      {
	  	  int dx  = (getGrayPixel(x-1, y+1) + getGrayPixel(x, y+1) + getGrayPixel(x+1, y+1))
                  -
                  (getGrayPixel(x-1, y-1) + getGrayPixel(x, y-1) + getGrayPixel(x+1, y-1));

        int dy  = (getGrayPixel(x+1, y-1) + getGrayPixel(x+1, y) + getGrayPixel(x+1, y+1))
                  -
                  (getGrayPixel(x-1, y-1) + getGrayPixel(x-1, y) + getGrayPixel(x-1, y+1));

	      int z = (int)(Math.sqrt(dx*dx + dy*dy)/3);

	      max = z > max ? z : max;
	      min = z < min ? z : min;

        // Now we set the pixel of the buffer
	      Source.setPixel(x, y, z);
			}
		}

		float a, b;

    // special values for rescaling the picture
		a = 255f / (max-min);
		b = a * min;

    // now we copy back every pixel of the buffer to the original
    // Pixel array
    for(x=width-1; x >= 0; x--)
    {
			for(y=height-1; y >= 0; y--)
      {
        setPixel(x, y, (int)(((a*Source.getPixel(x, y)+b) > threshold ? COLOR_FOREGROUND : COLOR_BACKGROUND)));
      }
    }
	}

	/**
  * thinning()
  *
  * Method to process the thinning of a previous edge detected picture.
  * This algorithm will break down neighbour pixels to a line with one
  * pixel width
  */
	public void thinning()
  {
		boolean remain, skel;
		int j;
		int x,y;
		remain = true;

		while(remain)
    {
		  remain = false;
      for(j=0; j<=6; j+=2) // j = 0, 2, 4, 6
      {
			  for(x=0; x<width; x++)
        {
				  for(y=0; y<height; y++)
          {
					  if(getPixel(x, y) == COLOR_FOREGROUND && (Pixels[neighbour(x,y,j)] == COLOR_BACKGROUND))
            {
						  if(matchPatterns(x,y))
              {
							  setPixel(x, y, COLOR_SKELETON);   // set Pixel with special SKELETON color
              }
              else
              {
							  setPixel(x, y, COLOR_REMOVABLE);  // set Pixel as a REMOVABLE pixel
                remain = true;
						  }
					  }
				  }
			  }

		  	// Now we process the pixel array and change (remove)
        // pixels that were previously marked as REMOVABLE
			  for(x=0; x<width; x++)
        {
				  for(y=0; y<height; y++)
          {
					  if(getPixel(x, y) == COLOR_REMOVABLE)
            {
						  setPixel(x, y, COLOR_BACKGROUND);
            }
				  }
			  }
      }
		}
	}

  /*
  * neighbour()
  *
  * special method that returns the offset of a neighbour
  * pixel to x,y in the specified direction j
  *
  */
	private int neighbour(int x, int y, int j)
	{
		switch(j)
    {
			case 0:	x++; 		    break;
			case 1:	x++;	y--;	break;
			case 2:	y--;	      break;
			case 3:	x--; 	y--;	break;
			case 4:	x--;		    break;
			case 5:	x--; 	y++;	break;
			case 6:	y++;		    break;
			case 7:	x++; 	y++;	break;
		}

		if(x>=width-1)	x = width-1;
		if(x<0)		      x = 0;
		if(y>=height-1)	y = height-1;
		if(y<0)		      y = 0;

		return y*width+x;
	}

  /**
  * matchPatterns()
  *
  * special method that checks if a specified pattern
  * matches the actual position x,y of the array.
  * this method is mainly used by the thinning() algorithm
  */
	private boolean matchPatterns(int x, int y)
	{
		if(x>=width-1)	x = width-1;
		if(x<0)		      x = 0;
		if(y>=height-1)	y = height-1;
		if(y<0)		      y = 0;

		if( Pixels[neighbour(x,y,0)] == COLOR_BACKGROUND &&
			  Pixels[neighbour(x,y,4)] == COLOR_BACKGROUND &&
			(	Pixels[neighbour(x,y,1)] != COLOR_BACKGROUND ||		// A A A
				Pixels[neighbour(x,y,2)] != COLOR_BACKGROUND ||		// 0 P 0
				Pixels[neighbour(x,y,3)] != COLOR_BACKGROUND)&&		// B B B
			(	Pixels[neighbour(x,y,5)] != COLOR_BACKGROUND ||
				Pixels[neighbour(x,y,6)] != COLOR_BACKGROUND ||
				Pixels[neighbour(x,y,7)] != COLOR_BACKGROUND ))
    {
			return true;
		}
    else if ( Pixels[neighbour(x,y,2)] == COLOR_BACKGROUND &&
					    Pixels[neighbour(x,y,6)] == COLOR_BACKGROUND &&
					  (	Pixels[neighbour(x,y,7)] != COLOR_BACKGROUND ||	// B 0 A
						  Pixels[neighbour(x,y,0)] != COLOR_BACKGROUND ||	// B P A
						  Pixels[neighbour(x,y,1)] != COLOR_BACKGROUND)&&	// B 0 A
					  (	Pixels[neighbour(x,y,3)] != COLOR_BACKGROUND ||
						  Pixels[neighbour(x,y,4)] != COLOR_BACKGROUND ||
						  Pixels[neighbour(x,y,5)] != COLOR_BACKGROUND ))
    {
			return true;
		}
    else if ( Pixels[neighbour(x,y,7)] == COLOR_SKELETON &&
					    Pixels[neighbour(x,y,0)] == COLOR_BACKGROUND &&
					    Pixels[neighbour(x,y,6)] == COLOR_BACKGROUND &&
					  (	Pixels[neighbour(x,y,1)] != COLOR_BACKGROUND ||	// A A A
						  Pixels[neighbour(x,y,2)] != COLOR_BACKGROUND ||	// A P 0
						  Pixels[neighbour(x,y,3)] != COLOR_BACKGROUND ||	// A 0 2
						  Pixels[neighbour(x,y,4)] != COLOR_BACKGROUND ||
						  Pixels[neighbour(x,y,5)] != COLOR_BACKGROUND ))
    {
			return true;
		}
    else if ( Pixels[neighbour(x,y,5)] == COLOR_SKELETON &&
					    Pixels[neighbour(x,y,4)] == COLOR_BACKGROUND &&
					    Pixels[neighbour(x,y,6)] == COLOR_BACKGROUND &&
					  (	Pixels[neighbour(x,y,7)] != COLOR_BACKGROUND ||	// A A A
						  Pixels[neighbour(x,y,0)] != COLOR_BACKGROUND ||	// 0 P A
						  Pixels[neighbour(x,y,1)] != COLOR_BACKGROUND ||	// 2 0 A
						  Pixels[neighbour(x,y,2)] != COLOR_BACKGROUND ||
						  Pixels[neighbour(x,y,3)] != COLOR_BACKGROUND ))
    {
			return true;
		}
    else if ( Pixels[neighbour(x,y,3)] == COLOR_SKELETON &&
					    Pixels[neighbour(x,y,2)] == COLOR_BACKGROUND &&
					    Pixels[neighbour(x,y,4)] == COLOR_BACKGROUND &&
					  (	Pixels[neighbour(x,y,5)] != COLOR_BACKGROUND ||	// 2 0 A
						  Pixels[neighbour(x,y,6)] != COLOR_BACKGROUND ||	// 0 P A
						  Pixels[neighbour(x,y,7)] != COLOR_BACKGROUND ||	// A A A
						  Pixels[neighbour(x,y,0)] != COLOR_BACKGROUND ||
						  Pixels[neighbour(x,y,1)] != COLOR_BACKGROUND ))
    {
			return true;
		}
    else if ( Pixels[neighbour(x,y,1)] == COLOR_SKELETON &&
					    Pixels[neighbour(x,y,0)] == COLOR_BACKGROUND &&
					    Pixels[neighbour(x,y,2)] == COLOR_BACKGROUND &&
					  (	Pixels[neighbour(x,y,3)] != COLOR_BACKGROUND ||	// A 0 2
						  Pixels[neighbour(x,y,4)] != COLOR_BACKGROUND ||	// A P 0
						  Pixels[neighbour(x,y,5)] != COLOR_BACKGROUND ||	// A A A
						  Pixels[neighbour(x,y,6)] != COLOR_BACKGROUND ||
						  Pixels[neighbour(x,y,7)] != COLOR_BACKGROUND ))
    {
			return true;
		}
    else
    {
			return false;
		}
	}

  /**
  * checkLines()
  *
  * Method that checks every pixel of the array if it is a skeleton
  * pixel and then start the line checking algorithm to filer out too
  * short lines which are really no line at all.
  */
	public void checkLines(int minline)
  {
		int x, y;
		int length = 0;

    LINE_MIN = minline;

		for(y=0; y<height; y++)
    {
			for(x=0; x<width; x++)
      {
				if(getPixel(x, y) == COLOR_SKELETON)
        {
					length = checkLineLength(x, y, 1);

					if (length > LINE_MIN)
          {
						paintLines(x, y, COLOR_GOODLINE);
					}
          else
					{
          	paintLines(x, y, COLOR_BADLINE);
					}
          length = 0;
				}
			}
    }
	}

  /**
  * checkLineLength()
  *
  * checks the length of skeleton lines. The actual pixel will be
  * marked as processed and then the length of the line will be incremented
  * until the line ends.
  */
	private int checkLineLength(int x, int y, int length)
  {
    // we mark this pixel as "processed"
		setPixel(x, y, COLOR_DONE);

		try
    {
		  if(Pixels[neighbour(x, y, 0)] == COLOR_SKELETON)
			  length = checkLineLength(x+1, y,    length+1);
		  if(Pixels[neighbour(x, y, 1)] == COLOR_SKELETON)
			  length = checkLineLength(x+1, y-1,  length+1);
		  if(Pixels[neighbour(x, y, 2)] == COLOR_SKELETON)
			  length = checkLineLength(x,   y-1,  length+1);
		  if(Pixels[neighbour(x, y, 3)] == COLOR_SKELETON)
			  length = checkLineLength(x-1, y-1,  length+1);
		  if(Pixels[neighbour(x, y, 4)] == COLOR_SKELETON)
			  length = checkLineLength(x-1, y  ,  length+1);
		  if(Pixels[neighbour(x, y, 5)] == COLOR_SKELETON)
			  length = checkLineLength(x-1, y+1,  length+1);
		  if(Pixels[neighbour(x, y, 6)] == COLOR_SKELETON)
			  length = checkLineLength(x  , y+1,  length+1);
		  if(Pixels[neighbour(x, y, 7)] == COLOR_SKELETON)
			  length = checkLineLength(x+1, y+1,  length+1);
		}
    catch(Exception e)
    {
			 System.out.println("Maybe Stackoverflow!!\n"+e);
		}
		return length;
	}

  /**
  * paintLines()
  *
  * special method that processes to a specified beginning of
  * a line and then change the color to the specified one.
  */
	private void paintLines(int x, int y, int color)
  {
		setPixel(x, y, color);

		try
    {
		  if(Pixels[neighbour(x, y, 0)]  == COLOR_DONE) paintLines(x+1,  y,    color);
		  if(Pixels[neighbour(x, y, 1)]  == COLOR_DONE) paintLines(x+1,  y-1,  color);
		  if(Pixels[neighbour(x, y, 2)]  == COLOR_DONE) paintLines(x,    y-1,  color);
		  if(Pixels[neighbour(x, y, 3)]  == COLOR_DONE) paintLines(x-1,  y-1,  color);
		  if(Pixels[neighbour(x, y, 4)]  == COLOR_DONE) paintLines(x-1,  y,    color);
		  if(Pixels[neighbour(x, y, 5)]  == COLOR_DONE) paintLines(x-1,  y+1,  color);
		  if(Pixels[neighbour(x, y, 6)]  == COLOR_DONE) paintLines(x,    y+1,  color);
		  if(Pixels[neighbour(x, y, 7)]  == COLOR_DONE) paintLines(x+1,  y+1,  color);
		}
    catch(Exception e)
    {
			 System.out.println("Maybe Stackoverflow\n"+e);
		}
	}

  /**
  * markPoints()
  *
  * method to process the thinned lines and mark every POINT_DIFFth pixel
  * and write this in a special Node object to calculate the cosinus
  * direction later
  */
	public void markPoints(int distance)
  {
		int x, y;
		int length = 0;
    POINT_DIFF = distance;
    points_pos = POINT_DIFF-1;

		for(y=0; y<height; y++)
    {
			for(x=0; x<width; x++)
      {
				if(getPixel(x, y) == COLOR_GOODLINE)
        {
    			paintPoints(x, y);
 				}
			}
    }
	}

  /**
  * paintPoints()
  *
  * special method to process through a thinned line and mark every
  * pixel with a distance of POINT_DIFF
  */
	private boolean paintPoints(int x, int y)
  {
    boolean result = false;

    // increment this global variable first
    points_pos++;

    // if the distance is POINT_DIFF we mark this point !
    if(points_pos == POINT_DIFF)
    {
      setPixel(x, y, COLOR_POINT_MARK);
      points_pos = 0;
    }
    else setPixel(x, y, COLOR_POINT_DONE);

		try
    {
		  if(Pixels[neighbour(x, y, 0)]  == COLOR_GOODLINE) result = paintPoints(x+1,  y  );
		  if(Pixels[neighbour(x, y, 1)]  == COLOR_GOODLINE) result = paintPoints(x+1,  y-1);
		  if(Pixels[neighbour(x, y, 2)]  == COLOR_GOODLINE) result = paintPoints(x,    y-1);
		  if(Pixels[neighbour(x, y, 3)]  == COLOR_GOODLINE) result = paintPoints(x-1,  y-1);
		  if(Pixels[neighbour(x, y, 4)]  == COLOR_GOODLINE) result = paintPoints(x-1,  y  );
		  if(Pixels[neighbour(x, y, 5)]  == COLOR_GOODLINE) result = paintPoints(x-1,  y+1);
		  if(Pixels[neighbour(x, y, 6)]  == COLOR_GOODLINE) result = paintPoints(x,    y+1);
		  if(Pixels[neighbour(x, y, 7)]  == COLOR_GOODLINE) result = paintPoints(x+1,  y+1);
		}
    catch(Exception e)
    {
			 System.out.println("Maybe Stackoverflow\n"+e);
		}

    if(result == false) setPixel(x, y, COLOR_POINT_MARK);

    return true;
	}


  /**
  * calcAngels()
  *
  * method to process the whole image and search for the marked points
  * and then find the next points that are connected to this points.
  */
	public void calcAngels()
  {
		int x, y;
		int length = 0;
    root_x = root_y = -1;

    // create a new ArrayList to count the tokens
    alltokens = new ArrayList();

		for(y=0; y<height; y++)
    {
			for(x=0; x<width; x++)
      {
				if(getPixel(x, y) == COLOR_POINT_MARK)
        {
    			searchNeighbour(x, y, true);
 				}
			}
    }

    for(int i=0; i < alltokens.size(); i++)
    {
      LeafToken actToken = (LeafToken)alltokens.get(i);
      drawSquare(actToken.getX1(), actToken.getY1(), 5, COLOR_BADLINE);
    }
	}

  /**
  * searchNeighbour()
  *
  * special method that search for neighbour pixels with COLOR_POINT_MARK
  * that are connected through a COLOR_POINT_DONE line.
  * then we draw a direct line through this two points.
  */
	private void searchNeighbour(int x, int y, boolean isRoot)
  {
    if(getPixel(x, y) == COLOR_POINT_MARK)
    {
      if(isRoot == true)
      {
        root_x = x;
        root_y = y;
      }
      else if(isRoot == false &&
              root_x != -1 && root_y != -1)
      {
        // if root and the actual is the same or only one pixel away
        // we skip this action.
        if((root_x+root_y)-(x+y) == 0) return;

        //System.out.println("Line: "+x+"/"+y+" -> "+root_x+"/"+root_y);

        drawLine(root_x, root_y, x, y, COLOR_SKELETON);

        // now we create a new token for this line and add it to the
        // ArrayList
        LeafToken ltoken = new LeafToken(root_x, root_y, x, y);
        alltokens.add(ltoken);

        setPixel(root_x, root_y, COLOR_POINT_MARK);
        setPixel(x, y, COLOR_POINT_MARK);

        return;
      }
    }
    else setPixel(x, y, COLOR_GOODLINE);

		try
    {
		  if( Pixels[neighbour(x, y, 0)]  == COLOR_POINT_DONE ||
          Pixels[neighbour(x, y, 0)]  == COLOR_POINT_MARK   ) searchNeighbour(x+1,  y  , false);
		  if( Pixels[neighbour(x, y, 1)]  == COLOR_POINT_DONE ||
          Pixels[neighbour(x, y, 1)]  == COLOR_POINT_MARK   ) searchNeighbour(x+1,  y-1, false);
		  if( Pixels[neighbour(x, y, 2)]  == COLOR_POINT_DONE ||
          Pixels[neighbour(x, y, 2)]  == COLOR_POINT_MARK   ) searchNeighbour(x,    y-1, false);
		  if( Pixels[neighbour(x, y, 3)]  == COLOR_POINT_DONE ||
          Pixels[neighbour(x, y, 3)]  == COLOR_POINT_MARK   ) searchNeighbour(x-1,  y-1, false);
		  if( Pixels[neighbour(x, y, 4)]  == COLOR_POINT_DONE ||
          Pixels[neighbour(x, y, 4)]  == COLOR_POINT_MARK   ) searchNeighbour(x-1,  y  , false);
		  if( Pixels[neighbour(x, y, 5)]  == COLOR_POINT_DONE ||
          Pixels[neighbour(x, y, 5)]  == COLOR_POINT_MARK   ) searchNeighbour(x-1,  y+1, false);
		  if( Pixels[neighbour(x, y, 6)]  == COLOR_POINT_DONE ||
          Pixels[neighbour(x, y, 6)]  == COLOR_POINT_MARK   ) searchNeighbour(x,    y+1, false);
		  if( Pixels[neighbour(x, y, 7)]  == COLOR_POINT_DONE ||
          Pixels[neighbour(x, y, 7)]  == COLOR_POINT_MARK   ) searchNeighbour(x+1,  y+1, false);
		}
    catch(Exception e)
    {
			 System.out.println("Maybe Stackoverflow\n"+e);
		}

    if(isRoot == true) setPixel(x, y, COLOR_SKELETON);
	}

  /**
  * getTokens()
  *
  * special method that returns a reference to the recognized
  * TokenArray that was calculated after the calcAngels()
  */
  public ArrayList getTokens()
  {
    return alltokens;
  }
}
