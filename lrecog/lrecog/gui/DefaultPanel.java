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
import java.awt.image.*;
import java.net.*;

import lrecog.*;
import lrecog.tools.*;

public class DefaultPanel extends JPanel
{
  EmptyBorder border5   = new EmptyBorder(5, 5, 5, 5);
  EmptyBorder border10  = new EmptyBorder(10, 10, 10, 10);
  Border loweredBorder  = new CompoundBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED), border5);

  // Premade convenience dimensions, for use wherever you need 'em.
  public static Dimension HGAP2 = new Dimension(2,1);
  public static Dimension VGAP2 = new Dimension(1,2);

  public static Dimension HGAP5 = new Dimension(5,1);
  public static Dimension VGAP5 = new Dimension(1,5);

  public static Dimension HGAP10 = new Dimension(10,1);
  public static Dimension VGAP10 = new Dimension(1,10);

  public static Dimension HGAP15 = new Dimension(15,1);
  public static Dimension VGAP15 = new Dimension(1,15);

  public static Dimension HGAP20 = new Dimension(20,1);
  public static Dimension VGAP20 = new Dimension(1,20);

  public static Dimension HGAP25 = new Dimension(25,1);
  public static Dimension VGAP25 = new Dimension(1,25);

  public static Dimension HGAP30 = new Dimension(30,1);
  public static Dimension VGAP30 = new Dimension(1,30);

  protected LeavesRecognition lrec;
  protected ProjectEnv projectEnv;

  public DefaultPanel(LeavesRecognition lrec)
  {
    this.lrec = lrec;
    this.projectEnv = lrec.getProjectEnv();
  }

  public JPanel createHorizontalPanel(boolean threeD)
  {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    p.setAlignmentY(TOP_ALIGNMENT);
    p.setAlignmentX(LEFT_ALIGNMENT);
    if(threeD)
    {
      p.setBorder(loweredBorder);
    }
    return p;
  }

  public JPanel createVerticalPanel(boolean threeD)
  {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    p.setAlignmentY(TOP_ALIGNMENT);
    p.setAlignmentX(LEFT_ALIGNMENT);
    if(threeD)
    {
      p.setBorder(loweredBorder);
    }
    return p;
  }

  /**
  * Creates an icon from an image contained in the "images" directory.
  */
  public ImageIcon createImageIcon(String filename, String description)
  {
    URL fileURL = getClass().getResource("/resources/images/"+filename);

    if(fileURL == null)
    {
      System.err.println("Warning: ImageIcon file ["+filename+"] not found.");
      return null;
    }

	  return new ImageIcon(fileURL, description);
  }

  /**
  * This method returns a string from the lrecog resource bundle.
  */
  public String getString(String key)
  {
    return lrec.getString(key);
  }
}
