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
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.image.*;

import lrecog.gfx.*;

public class ImagePanel extends JPanel
{
	private LeafImage actualImage;
  private int width;
  private int height;

  /**
  * Constructor to display the image scaled with respect to the
  * aspect ratio if it's hugher than maxwidth or maxheight
  */
	public ImagePanel(LeafImage img, int maxwidth, int maxheight)
  {
		this.actualImage = img;

    // scale the image with aspect ratio
    if(actualImage != null)calcRatio(maxwidth, maxheight);
    else
    {
      width = maxwidth;
      height = maxheight;
    }

	  // set the preferred size of the panel
	  setPreferredSize(new Dimension(width+2, height+2));
	  setMinimumSize(new Dimension(width+2, height+2));
	  setMaximumSize(new Dimension(width+2, height+2));
  }

	public ImagePanel()
  {
		this.actualImage = null;
	}

	public void setImage(LeafImage img, int maxwidth, int maxheight)
  {
		this.actualImage = img;

    // scale the image with aspect ratio
    calcRatio(maxwidth, maxheight);

	  // set the preferred size of the panel
	  setPreferredSize(new Dimension(width+2, height+2));
	  setMinimumSize(new Dimension(width+2, height+2));

		repaint();
	}

  /**
  * setImage()
  *
  * this method will just change the image without modifing the sizes
  */
	public void setImage(Image img)
  {
		this.actualImage = new LeafImage(img);

    if(width == 0 || height == 0) return;

	  // set the preferred size of the panel
	  setPreferredSize(new Dimension(width+2, height+2));
	  setMinimumSize(new Dimension(width+2, height+2));

		repaint();
	}

	public void paintComponent(Graphics g)
  {
    super.paintComponent(g);

    // if this component is empty we just draw a text that no Image was loaded
		if(actualImage != null)
    {
		  g.drawImage(actualImage.getImage(), 0, 0, width, height, new Color(220,220,220), null);
      g.setColor(new Color(0, 0, 0));
		  g.drawLine(0, 0, 0, height - 1);
		  g.drawLine(0, 0, width -1, 0);
		  g.drawLine(width - 1, 0, width - 1, height - 1);
		  g.drawLine(0, height - 1, width - 1, height - 1);
    }
	}

  /**
  * calcRatio()
  *
  * method that calculate the new sizes of this image
  * with respect to a maximum and a aspect ratio
  */
  private void calcRatio(int maxwidth, int maxheight)
  {
    LeafImage img = actualImage;

    if(img.getWidth() == 0 || img.getHeight() == 0) return;

    int xdiff = img.getWidth()-maxwidth;
    int ydiff = img.getHeight()-maxheight;

    width = img.getWidth();
    height = img.getHeight();

    if(!(xdiff <= 0 && ydiff <= 0))
    {
      width = maxwidth;
      height = (int)(((1.0/(float)img.getWidth())*(float)maxwidth)*((float)img.getHeight()));
    }
  }
}
