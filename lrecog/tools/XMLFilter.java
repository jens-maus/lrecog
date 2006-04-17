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

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
* special class for loading/saving and administrating
* the project relevant data
*/

public class XMLFilter extends FileFilter
{
    // Accept all directories and all gif, jpg, or tiff files.
    public boolean accept(File f)
    {
      if (f.isDirectory())
      {
        return true;
      }

      String extension = Utils.getExtension(f);
	    if(extension != null)
      {
        if(extension.equals(Utils.xml))
        {
          return true;
        }
        else return false;
    	}

      return false;
    }

    // The description of this filter
    public String getDescription()
    {
      return "XML Files (*.xml)";
    }
}
