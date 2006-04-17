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

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.Toolkit.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
* class that represents a image of a single leaf with all
* tokens and information about this image.
*/
public class LeafImage
{
  private Image image = null;
  private ArrayList tokens = null;
  private File filename = null;
  private LeafSpecies species = null;

  public LeafImage(File fileopen)
  {
    if(fileopen.isFile())
    {
      filename = fileopen;
      image = new ImageIcon(fileopen.getPath()).getImage();
    }
    
    tokens = new ArrayList();
  }

  public LeafImage(URL urlopen)
  {
    filename = new File(urlopen.toString());
    image = new ImageIcon(urlopen).getImage();
    tokens = new ArrayList();
  }

  public LeafImage(Image img)
  {
    image = img;
    tokens = new ArrayList();
  }

  public LeafImage(Image img, String filename)
  {
    this(img);

    this.filename = new File(filename);
  }

  public void setImage(Image img)
  {
    this.image = img;
  }

  public void setSpecies(LeafSpecies species)
  {
    this.species = species;
  }

  public LeafSpecies getSpecies()
  {
    return species;
  }

  public Image getImage()
  {
    return image;
  }

  public int getWidth()
  {
    return image.getWidth(null);
  }

  public int getHeight()
  {
    return image.getHeight(null);
  }

  public void addToken(LeafToken token)
  {
    tokens.add(token);
  }

  public void setTokens(ArrayList tokens)
  {
    this.tokens = tokens;
  }

  /**
  * getTokens()
  *
  * returns the tokens in a double[] vector
  */
  public double[] getTokens(int inputs)
  {
    double[] tokenVector = new double[inputs];

    for(int i=0; i < inputs; i++)
    {
      if(i < tokens.size())
      {
        LeafToken token = (LeafToken)tokens.get(i);

        // Fill the tokenVector first with the cosinus
        // and then with the sinus value
        if(i%2 == 0)  tokenVector[i] = token.getCOS();
        else          tokenVector[i] = token.getSIN();

      }
      else tokenVector[i] = -1.0;
    }

    return tokenVector;
  }

  /**
  * getTokens()
  *
  * returns the tokens in a double[] vector
  */
  public double[] getTokens()
  {
    return getTokens(tokens.size()*2);
  }

  public LeafToken getToken(int idx)
  {
    return (LeafToken)tokens.get(idx);
  }

  public int numTokens()
  {
    return tokens.size();
  }

  public void clearTokens()
  {
    tokens.clear();
  }

  public File getFileName()
  {
    return filename;
  }

  /**
  * toString() method
  *
  * this method is needed for the JTree to display the correct name
  * of this object
  */
  public String toString()
  {
    return filename.getName();
  }
}