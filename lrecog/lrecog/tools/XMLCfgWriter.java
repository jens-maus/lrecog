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

import org.xml.sax.*;

/**
* special class for loading/saving and administrating
* the project relevant data
*/
public class XMLCfgWriter
{
    /** Print writer. */
    protected PrintWriter fOut;

    /** Canonical output. */
    protected boolean fCanonical;

    /** Element depth. */
    protected int fElementDepth;

    /** Sets the output stream for printing. */
    public void setOutput(OutputStream stream)
    {
      try
      {
        java.io.Writer writer = new OutputStreamWriter(stream, "UTF-8");
        fOut = new PrintWriter(writer);
      }
      catch(UnsupportedEncodingException e)
      {
        System.err.println(e);
      }
    }

    //
    // ContentHandler methods
    //

    /** Start document. */
    public void startDocument()
    {
      fElementDepth = 0;

      fOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      fOut.println("<lrecog version=\"1.0\">");
      fOut.flush();
    }

    /** Start document. */
    public void endDocument()
    {
      if(fElementDepth != 0) System.err.println("Error: ElementDepth > 0 on end of document!");

      fOut.println("</lrecog>");
      fOut.flush();
    }

    /** Start element. */
    public void startElement(String element, Attributes attrs)
    {
      fElementDepth++;
      fOut.print('<');
      fOut.print(element);

      if(attrs != null)
      {
        int len = attrs.getLength();

        for (int i = 0; i < len; i++)
        {
          fOut.print(' ');
          fOut.print(attrs.getQName(i));
          fOut.print("=\"");
          normalizeAndPrint(attrs.getValue(i));
          fOut.print('"');
        }
      }
      fOut.print('>');
      fOut.flush();
    }

    /** End element. */
    public void endElement(String element)
    {
      fElementDepth--;
      fOut.print("</");
      fOut.print(element);
      fOut.print('>');
      fOut.flush();
    }

    /** Characters. */
    public void characters(String s)
    {
        normalizeAndPrint(s);
        fOut.flush();
    }

    /** Normalizes and prints the given string. */
    protected void normalizeAndPrint(String s)
    {
      int len = (s != null) ? s.length() : 0;
      for(int i = 0; i < len; i++)
      {
        char c = s.charAt(i);
        normalizeAndPrint(c);
      }
    }

    /** Normalizes and print the given character. */
    protected void normalizeAndPrint(char c)
    {
      switch(c)
      {
        case '<':
        {
          fOut.print("&lt;");
        }
        break;

        case '>':
        {
          fOut.print("&gt;");
        }
        break;

        case '&':
        {
          fOut.print("&amp;");
        }
        break;

        case '"':
        {
          fOut.print("&quot;");
        }
        break;

        case '\r':
        case '\n':
        {
          fOut.print("&#");
          fOut.print(Integer.toString(c));
          fOut.print(';');
        }
        break;

        default:
        {
          fOut.print(c);
        }
      }
    }
}
