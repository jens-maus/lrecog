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

package lrecog.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.image.*;

import java.util.*;

public class ErrorGraphDraw extends JPanel
{
	public static final int block = 500;

	private int	border 			= 10;
	private int	leftBorder 	= 30;
	private int	bottomBorder= 30;
	private int	fontSize		= 10;
  private int width       = 370; // size of the graph
  private int height      = 230;
	private double minVal   = 0.0;
	private double maxVal   = 1.1;
	private double minLim   = 0.0;

	private double	error[];
	private int		  nElements;

  public ErrorGraphDraw()
  {
    clear();
    setPreferredSize(new Dimension(width+1, height+1));
    setMinimumSize(new Dimension(width+1, height+1));
    setMaximumSize(new Dimension(width+1, height+1));
    setLayout(new GridLayout(1,0));
  }

	public void addError(double err)
  {
		if(nElements == error.length)
    {
			double temp[] = error;
			error = new double[error.length + block];
			System.arraycopy(temp,0,error,0,nElements);
    }

		error[nElements] = err;

		if(maxVal < err) maxVal = err;

		nElements++;

    repaint();
  }

	public void clear()
  {
		nElements = 1;
		error = new double[block];
		error[0] = 0.0;
		minVal = 0.0;
		maxVal = 1.1;
	}

  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);

		int stepX	  = (width-border-leftBorder)/nElements;
		int	graphH 	= height-border-bottomBorder;
		int	graphY	= border;
    double x;
		int	y;

		if(stepX == 0) clear();
    
		// create an image to avoid flicks
		Image image = createImage(width, height);
		Graphics g2 = image.getGraphics();

		g2.setFont(new Font("Helvetica", Font.PLAIN , fontSize));

		g2.setColor(Color.black);
		g2.fillRect(0, 0, width, height);

		g2.setColor(Color.white);
		g2.drawLine(leftBorder, border, leftBorder, height-(bottomBorder*3/4));
		g2.drawLine(leftBorder*3/4, height-bottomBorder, width-border, height-bottomBorder);
		
		g2.setColor(Color.lightGray);

		x = 0.0;
		while((double)x < maxVal)
    {
			y = scale((double)x, minVal, maxVal, 0, graphH);
			g2.drawLine(leftBorder-2, height-bottomBorder-y, width-border, height-bottomBorder-y);
			g2.drawString(""+(float)x, 2, height-bottomBorder-y+fontSize/2);
      if(maxVal > 4.0)
      {
        x+=2.0;
      }
      else if(maxVal > 8.0)
      {
        x+=4.0;
      }
      else x+=0.2;
		}

		if(minLim < maxVal)
    {
			g2.setColor(Color.red);
			y = scale(minLim, minVal, maxVal, 0, graphH);
			g2.drawLine(leftBorder+1, height-bottomBorder-y, width-border,height-bottomBorder-y);
			g2.drawString(""+minLim, 2, height-bottomBorder-y+fontSize/2);
		}

		g2.setColor(Color.yellow);
		for(int i=2; i < nElements; i++)
    {
			int y1 = scale(error[i-1], minVal, maxVal, 0, 0+graphH);
			int y2 = scale(error[i], minVal, maxVal, 0, graphH);

			g2.drawLine(leftBorder+1+((i-1)*stepX), height-bottomBorder-y1, leftBorder+1+(i*stepX), height-bottomBorder-y2);
		}

		g.drawImage(image, 0, 0, this);
  }

	public static int scale(double x, double xMin, double xMax, int toMin, int toMax)
  {
		return (int)((x-xMin)/(xMax-xMin)*(double)(toMax-toMin))+toMin;
	}
}
