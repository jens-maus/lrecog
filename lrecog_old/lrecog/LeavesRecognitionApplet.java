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

package lrecog;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;
import java.io.*;

import lrecog.gui.*;
import lrecog.tools.*;

public class LeavesRecognitionApplet extends JApplet
{
	private Vector images;    // All image files
	private String imageDir;  // Directory of the Images
	private URL codebase;
  
	private JButton startButton;
  private JLabel statusLabel;
  private LeavesRecognition lrec;
  private ProjectEnv projectEnv;
  private LeavesRecognitionApplet applet;

	/**
  * Main Applet init() method
  */
	public void init()
  {
    this.applet = this;

		images    = getImageNames();
		imageDir  = getImageDir();
		codebase  = getCodeBase();

		startButton = new JButton("Start LeavesRecognition Applet");
    startButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            AppletContext ac = getAppletContext();
            ac.showStatus("Loading Applet, please wait.... ");
            statusLabel.setText("Loading Applet, please wait...");
            statusLabel.update(statusLabel.getGraphics());

            lrec = new LeavesRecognition(applet);
            projectEnv = lrec.getProjectEnv();
            projectEnv.setCodeBase(codebase);
            projectEnv.setImageVector(images);
            projectEnv.setImageDir(imageDir);

            // Create the Image Chooser now
            lrec.createImageChooser();

            // Now we load the default config defined in the
            // CONFIG TAG
            ac.showStatus("Loading XML Config...");
            statusLabel.setText("Loading XML Config...");
            statusLabel.update(statusLabel.getGraphics());
            loadConfig();

					  if(lrec != null)
            {
              ac.showStatus("... Applet loaded & started.");
              statusLabel.setText("... Applet loaded & started.");
            }
					}
				});
      }
    });

    statusLabel = new JLabel();

    JPanel appPanel = new JPanel();
    appPanel.setLayout(new BorderLayout());
    appPanel.setBackground(getColor());
    appPanel.add(startButton, BorderLayout.NORTH);
    appPanel.add(statusLabel, BorderLayout.CENTER);

    getContentPane().add(appPanel, BorderLayout.CENTER);
  }

	/**
  * loadConfig()
  *
  * method to get the config file name from the PARAMETERS
  * and load the config into the project environment
  */
	private void loadConfig()
  {
		String configFile;
		if((configFile = getParameter("CONFIG")) != null)
    {
      projectEnv.Open(new File(codebase+configFile));
      lrec.updateProjectEnv();
      lrec.updateTreePanel();
    }
  }

  /**
  * getColor()
  *
  * method to get the COLOR that is set in the PARAM list
  */
  private Color getColor()
  {
    String c = getParameter("COLOR");
    int r = 0, g=0, b =0;

    if(c != null)
    {
      r = Integer.parseInt(c.substring(0,2), 16);
    	g = Integer.parseInt(c.substring(2,4), 16);
    	b = Integer.parseInt(c.substring(4,6), 16);

    	return new Color(r, g, b);
    }
    else return Color.white;
  }

  /**
  * getImageNames()
  *
  * method to get every IMAGE parameter and put it into a vector
  */
	private Vector getImageNames()
  {
    Vector images = new Vector(1);
    int i = 0;
    String param = "IMAGE" + i;
    String value;

		while((value = getParameter(param)) != null)
    {
      images.addElement(value);
      i++;
      param = "IMAGE" + i;
    }

    images.trimToSize();

    return images;
  }

  /**
  * getImageDir()
  *
  * method to get the IMAGEDIR Parameter
  */
	private String getImageDir()
  {
		String dir;
		if((dir = getParameter("IMAGEDIR")) != null)  dir = dir + "/";
    else dir = "";

    return dir;
  }

  /**
  * getParameterInfo()
  *
  * special method for Java-Applets
  * future Browsers should read out this parameter info to get
  * information about the possible parameters for this applet
  */
  public String[][] getParameterInfo()
  {
    String[][] info = {
      // Parameter Name     Kind of Value   Description
        {"config",          "URL",          "a URL to the default config xml file"},
        {"color",           "int",          "the background color for the applet #ffffff"},
        {"imageX",          "String",       "Filename of a image to add as a leaf image where X is a number starting from 0"},
        {"imagedir",        "String",       "The directory this images resists."},
    };
    return info;
  }

  /**
  * getAppletInfo()
  *
  * special method for Java-Applets
  * future Browsers should read out this method to get information about
  * this applet
  */
  public String getAppletInfo()
  {
    return "LeavesRecognition Applet, Copyright(c) 2001 by Jens Langner";
  }
}