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

package lrecog.tools;

import javax.swing.*;
import java.beans.*;
import java.awt.*;
import java.io.File;

/**
* special class for loading/saving and administrating
* the project relevant data
*/

public class ImagePreview extends JComponent implements PropertyChangeListener
{
  ImageIcon thumbnail = null;
  File file = null;

  public ImagePreview(JFileChooser fc)
  {
    setPreferredSize(new Dimension(100, 50));
    fc.addPropertyChangeListener(this);
  }

  public void loadImage()
  {
    if(file == null)
    {
      return;
    }

    ImageIcon tmpIcon = new ImageIcon(file.getPath());
    if (tmpIcon.getIconWidth() > 90)
    {
      thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(90, -1, Image.SCALE_DEFAULT));
    }
    else
    {
      thumbnail = tmpIcon;
    }
  }

  public void propertyChange(PropertyChangeEvent e)
  {
    String prop = e.getPropertyName();
    if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
    {
      file = (File) e.getNewValue();
      if (isShowing())
      {
        loadImage();
        repaint();
      }
    }
  }

  public void paintComponent(Graphics g)
  {
    if(thumbnail == null)
    {
      loadImage();
    }

    if(thumbnail != null)
    {
      int x = getWidth()/2 - thumbnail.getIconWidth()/2;
      int y = getHeight()/2 - thumbnail.getIconHeight()/2;

      if(y < 0)
      {
        y = 0;
      }

      if(x < 5)
      {
        x = 5;
      }
      thumbnail.paintIcon(this, g, x, y);
    }
  }
}
