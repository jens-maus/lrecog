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
import java.io.*;
import java.net.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import lrecog.*;
import lrecog.gfx.*;
import lrecog.nnetwork.*;

/**
* special class for loading/saving and administrating
* the project relevant data
*/
public class ProjectEnv
{
  private boolean modified = false;

  private File configfile = null;

  private LeavesRecognition lrec = null;

  // the following 3 variables are only used in
  // applet mode
  private URL codeBase = null;
  private Vector imageVector = null;
  private String imageDir = null;
  
  private ArrayList leafSpecies;

  // the neuronal network config
  private BackProp nNetwork = null;
  private int numInputNeurons;
  private int numHiddenNeurons;
  private int numOutputNeurons;

  public ProjectEnv(LeavesRecognition lrec)
  {
    this.lrec = lrec;
    leafSpecies = new ArrayList(); // create a new List of Images
  }

  public void addLeafSpecies(LeafSpecies lSpecies)
  {
    leafSpecies.add(lSpecies);
  }

  public void removeLeafSpecies(LeafSpecies lSpecies)
  {
    leafSpecies.remove(lSpecies);
  }

  public ArrayList getLeafSpecies()
  {
    return leafSpecies;
  }

  public LeavesRecognition getLeavesRecognition()
  {
    return lrec;
  }

  public int numLeafSpecies()
  {
    return leafSpecies.size();
  }

  public int numLeafImages()
  {
    int images = 0;

    for(int i=0; i < leafSpecies.size(); i++)
    {
      LeafSpecies lSpecies = (LeafSpecies)leafSpecies.get(i);

      images += lSpecies.numImages();
    }
    
    return images;
  }

  public void setNetwork(BackProp nNetwork)
  {
    this.nNetwork = nNetwork;
  }

  public void setImageVector(Vector images)
  {
    imageVector = images;
  }

  public void setImageDir(String imageDir)
  {
    this.imageDir = imageDir;
  }

  public void setModified()
  {
    modified = true;
  }

  public void setCodeBase(URL cbase)
  {
    codeBase = cbase;
  }

  public URL getCodeBase()
  {
    return codeBase;
  }

  public boolean wasModified()
  {
    return modified;
  }

  public BackProp getNetwork()
  {
    return nNetwork;
  }

  public File getFileName()
  {
    return configfile;
  }

  public Vector getImageVector()
  {
    return imageVector;
  }

  public String getImageDir()
  {
    return imageDir;
  }

  /**
  * method that opens a project file and parses through it's
  * contents to initialize the Project environment
  */
  public boolean Open(File fileopen)
  {
    XMLReader parser = null;
    XMLCfgReader cfgReader = new XMLCfgReader(this);

    try
    {
      parser = XMLReaderFactory.createXMLReader();
    }
    catch(Exception e)
    {
      // fall back on Xerces parser by name
      try
      {
        parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
      }
      catch (Exception ee)
      {
        System.err.println("Couldn't locate a SAX parser");
        return false;
      }
    }

    // Install the Document Handler
    parser.setContentHandler(cfgReader);

    // command line should offer URIs or file names
    try
    {
      InputStream fIn;

      if(lrec.isApplet() == false)
      {
        fIn = new FileInputStream(fileopen);
      }
      else
      {
        URL nURL = new URL(fileopen.toString());
        fIn = nURL.openStream();
      }

      parser.parse(new InputSource(fIn));
    }
    catch(SAXParseException e)
    {
      // well-formedness error
      System.out.println(fileopen+" is not well formed.");
      System.out.println(e.getMessage()+" at line "+e.getLineNumber()+", column "+e.getColumnNumber());
    }
    catch(SAXException e)
    {
      // some other kind of error
      System.out.println(e.getMessage());
    }
    catch(IOException e)
    {
      System.out.println("Could not report on "+fileopen+" because of the IOException "+e);
    }

    // Now that we load the cfg we can set the new leafGroup
    leafSpecies = cfgReader.getLeafSpecies();

    configfile = fileopen;
    modified = false;

    return true;
  }

  /**
  * method that saves a project file with all the information from
  * the project environment
  */
  public boolean Save(File filesave)
  {
    // lets define a XMLWriter for the config file
    XMLCfgWriter writer = new XMLCfgWriter();
    AttributesImpl attr = new AttributesImpl();

    // Lets open a output stream
    try
    {
      FileOutputStream fOut = new FileOutputStream(filesave);

      // Now we write out the configuration data
      writer.setOutput(fOut); // do only System.out to display the config
      writer.startDocument();

      // Now we write down the species with all its images and
      // the reocgnized tokens
      for(int i=0; i < leafSpecies.size(); i++)
      {
        LeafSpecies lSpecies = (LeafSpecies)leafSpecies.get(i);

        attr.clear();
        attr.addAttribute(null, null, "name", null,  lSpecies.getName());
        attr.addAttribute(null, null, "IDlen", null, ""+lSpecies.getID().length);
        writer.startElement("leafSpecies", attr);

        // If this Species has an ID we have to write it down now
        if(lSpecies.getID().length > 0);
        {
          double[] ID = lSpecies.getID();

          for(int i2=0; i2 < ID.length; i2++)
          {
            attr.clear();
            attr.addAttribute(null, null, "value", null, ""+ID[i2]);
            writer.startElement("ID", attr);
            writer.endElement("ID");
          }
        }

        for(int j=0; j < lSpecies.numImages(); j++)
        {
          LeafImage limage = (LeafImage)lSpecies.getImage(j);

          attr.clear();
          attr.addAttribute(null, null, "file", null, limage.getFileName().getPath());
          writer.startElement("leafImage", attr);

          for(int k=0; k < limage.numTokens(); k++)
          {
            LeafToken ltoken = limage.getToken(k);

            attr.clear();
            attr.addAttribute(null, null, "x1", null, ""+ltoken.getX1());
            attr.addAttribute(null, null, "y1", null, ""+ltoken.getY1());
            attr.addAttribute(null, null, "x2", null, ""+ltoken.getX2());
            attr.addAttribute(null, null, "y2", null, ""+ltoken.getY2());
            writer.startElement("leafToken", attr);
            //writer.characters(""+ltoken.getCOS());
            writer.endElement("leafToken");
          }
          writer.endElement("leafImage");
        }
        writer.endElement("leafSpecies");
      }

      // if this project environment also has a pretrained network
      // we save the properties of this net also.
      if(nNetwork != null)
      {
        // create the arguments for the network TAG
        attr.clear();
        attr.addAttribute(null, null, "input",    null, ""+nNetwork.numInput());
        attr.addAttribute(null, null, "hidden",   null, ""+nNetwork.numHidden());
        attr.addAttribute(null, null, "output",   null, ""+nNetwork.numOutput());
        attr.addAttribute(null, null, "alpha",    null, ""+nNetwork.getAlpha());
        attr.addAttribute(null, null, "momentum", null, ""+nNetwork.getMomentum());
        writer.startElement("backProp", attr);

        // Now we write the bias and weights of the hidden and output
        // layer to the config file

        // write hidden WEIGHTS & BIAS
        double[] biasH      = nNetwork.getBiasH();
        double[][] hiddenW  = nNetwork.getHiddenW();
        for(int i=0; i < nNetwork.numHidden(); i++)
        {
          attr.clear();
          writer.startElement("hidden", attr);

          for(int j=0; j < nNetwork.numInput(); j++)
          {
            attr.clear();
            attr.addAttribute(null, null, "H", null, ""+hiddenW[i][j]);
            writer.startElement("hiddenW", attr);
            writer.endElement("hiddenW");
          }

          // now write the BIAS for this hidden
          attr.clear();
          attr.addAttribute(null, null, "H", null, ""+biasH[i]);
          writer.startElement("biasH", attr);
          writer.endElement("biasH");

          writer.endElement("hidden");
        }

        // write output WEIGHTS & BIAS
        double[] biasO      = nNetwork.getBiasO();
        double[][] outputW  = nNetwork.getOutputW();
        for(int i=0; i < nNetwork.numOutput(); i++)
        {
          attr.clear();
          writer.startElement("output", attr);

          for(int j=0; j < nNetwork.numHidden(); j++)
          {
            attr.clear();
            attr.addAttribute(null, null, "O", null, ""+outputW[i][j]);
            writer.startElement("outputW", attr);
            writer.endElement("outputW");
          }

          // now write the BIAS for this output
          attr.clear();
          attr.addAttribute(null, null, "O", null, ""+biasO[i]);
          writer.startElement("biasO", attr);
          writer.endElement("biasO");

          writer.endElement("output");
        }

        writer.endElement("backProp");
      }

      writer.endDocument();
    }
    catch(FileNotFoundException e)
    {
      System.err.println("Error: File could not be open.");
    }

    configfile = filesave;
    modified = false;

    return true;
  }

  /**
  * getMaxToken()
  *
  * method that return the highest number of Tokens found for
  * one LeafImage
  */
  public int getMaxToken()
  {
    int maxToken = 0;

    for(int i=0; i < leafSpecies.size(); i++)
    {
      LeafSpecies lSpecies = (LeafSpecies)leafSpecies.get(i);

      for(int j=0; j < lSpecies.numImages(); j++)
      {
        LeafImage lImage = (LeafImage)lSpecies.getImage(j);

        if(lImage.numTokens() > maxToken)
        {
          maxToken = lImage.numTokens();
        }
      }
    }

    return maxToken;
  }
}