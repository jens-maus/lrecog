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

import java.util.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;

import lrecog.*;
import lrecog.gfx.*;
import lrecog.gui.*;

/**
* special class to generate a picturechooser if this App
* was started in Applet mode
*/
public class ImageChooser
{
  private LeavesRecognition lrec;
	private ProjectEnv projectEnv;

	private Vector imageVector;
	private Hashtable imageTable;
	private Image img;
	private FileOpenDialog fileOpenDialog;
	private String first;

	public ImageChooser(LeavesRecognition lrec)
  {
    this.lrec = lrec;
    this.projectEnv = lrec.getProjectEnv();

		Vector images   = projectEnv.getImageVector();
		String imageDir = projectEnv.getImageDir();
		URL codebase    = projectEnv.getCodeBase();

		imageVector = new Vector();

    // Load image as (Name, Picture) in Hashtable
		imageTable = new Hashtable();
		SortedSet filetypes = new TreeSet();

		for(int i=0; i < images.size(); i++)
    {
			String name = (String)images.get(i);
			String path = imageDir+name;

			try
      {
				Image img = new ImageIcon(new URL(codebase, path)).getImage();

				// If we can't load a picture we ignore it !
				if(img.getWidth(null) != -1)
        {
					imageTable.put(name, img);
					int index = name.lastIndexOf(".");
					String type = name.substring(index);
					filetypes.add(type);
				}
        else
        {
          System.out.println("Image "+name+" couldn't be loaded...");
        }

			} catch(MalformedURLException m)
      {
				System.out.println("URL for the image "+name+" badly specified");
			}
		}		

    // Now we sort the filename with respect of the file extensions
    // as (Extension, Vector of filenames) in a separate Hashtable
		Vector[] files = new Vector[filetypes.size()];
		Hashtable content = new Hashtable();
		Set s = imageTable.keySet();
		TreeSet set = new TreeSet(s);

    int i=0;
		for(Iterator j=filetypes.iterator();  j.hasNext(); i++)
    {
			files[i] = new Vector();
			String a = (String)j.next();

			for(Iterator z = set.iterator(); z.hasNext(); )
      {
				String name = (String)z.next();
				int index = name.lastIndexOf(".");
				String type = name.substring(index);
				if(a.equalsIgnoreCase(type)) files[i].add(name);
			}

			if(a.equalsIgnoreCase(".gif")) content.put("GIF Image Files (*.gif)", files[i]);
			else if(a.equalsIgnoreCase(".jpg")) content.put("JPEG Image Files (*.jpg)", files[i]);
			else content.put(a, files[i]);
		}

    if(content.size() > 0)
    {
		  fileOpenDialog = new FileOpenDialog(content, lrec.getFrame(), true);
		  first = fileOpenDialog.getFirstItem();
		  img = (Image)imageTable.get(first);
    }
	}

  /**
  * open()
  *
  * Method that opens the File Open Dialog to let the user select a
  * image file
  */
	public LeafImage open()
  {
    if(fileOpenDialog == null) return null;
    
		boolean b = fileOpenDialog.showDialog();

		if(b == FileOpenDialog.APPROVE_OPTION)
    {
			final String s = fileOpenDialog.getSelectedItem();
			img = (Image)imageTable.get(s);

      return new LeafImage(img, s);
		}

    return null;
	}

  /**
  * getLeafImage()
  *
  * special method that returns a LeafImage with the passed name
  * in imageName
  */
	public LeafImage getLeafImage(String imageName)
  {
    img = (Image)imageTable.get(imageName);

    if(img != null) return new LeafImage(img, imageName);
    else return null;
  }

	public String getFirstImageName()
  {
		return first;
	}
}