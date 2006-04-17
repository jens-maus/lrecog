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
import javax.swing.table.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;

import lrecog.*;
import lrecog.gfx.*;
import lrecog.nnetwork.*;
import lrecog.tools.*;
import lrecog.tools.ImageFilter;

public class RecogTablePanel extends DefaultPanel
{
  private RecognitionPanel recogPanel;
  private JTable table;
  private RecogTableModel recogTableModel;

 	public RecogTablePanel(LeavesRecognition lrec)
  {
    super(lrec);
    this.recogPanel = lrec.getRecogPanel();

    setLayout(new GridLayout(1,0));

    // Show colors by rendering them in their own color.
    DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer()
    {
	    public void setValue(Object value)
      {
        if(value instanceof RecogResult)
        {
	        RecogResult rResult = (RecogResult)value;

          if(rResult.percentage >= 90.0) setForeground(Color.red);
          else if(rResult.percentage >= 80.0) setForeground(Color.blue);
          else if(rResult.percentage > 50.0) setForeground(Color.green);
          else setForeground(Color.black);

          String tmpString = new String(""+rResult.percentage);

          setText(tmpString.substring(0, tmpString.indexOf(".")+4)+" %");
        }
        else if(value instanceof LeafSpecies)
        {
          LeafSpecies lSpecies = (LeafSpecies)value;

          setForeground(Color.black);
          setText(lSpecies.getName());
        }
        else
        {
          super.setValue(value);
        }
      }
    };

    // Create the JTable now
    recogTableModel = new RecogTableModel();
    table = new JTable(recogTableModel);
    table.setRowSelectionAllowed(false);
    table.setColumnSelectionAllowed(false);
    table.setShowGrid(false);

    Class cClass = table.getColumnClass(0);
    table.setDefaultRenderer(cClass, colorRenderer);

    JScrollPane scrollPane = new JScrollPane(table);

    add(scrollPane);
	}

  public void addResult(LeafSpecies lSpecies, double perc)
  {
    recogTableModel.add(new RecogResult(lSpecies, perc));
  }

  public void clear()
  {
    recogTableModel.clear();
  }

  /**
  * RecogTableModel
  *
  * special inner class that represents the Table Model
  */
  class RecogTableModel extends AbstractTableModel
  {
    private final int COLUMS = 2;
    private ArrayList resultList = new ArrayList();

    public String getColumnName(int col)
    {
      if(col == 0) return getString("RECOGTABLE_SPECIES");
      else if(col == 1) return "%";
      return "ERROR";
    }

    public void add(RecogResult result)
    {
      resultList.add(result);
      fireTableStructureChanged();
    }

    public void clear()
    {
      resultList.clear();
      fireTableStructureChanged();
    }

    public int getColumnCount()
    {
      return COLUMS;
    }

    public int getRowCount()
    {
      return resultList.size();
    }

    /**
    * isCellEditable()
    *
    * we never allow editing of any cell
    */
    public boolean isCellEditable(int row, int col)
    {
      return false;
    }

    public Object getValueAt(int row, int col)
    {
      RecogResult rResult = (RecogResult)resultList.get(row);

      if(rResult != null)
      {
        if(col == 0) return rResult.lSpecies;
        else if(col == 1) return rResult;
      }
      return "ERROR";
    }
  }

  /**
  * class RecogResult
  *
  * special inner class that represents a recoginition result
  * that should be placed in the Table
  */
  class RecogResult
  {
    protected LeafSpecies lSpecies;
    protected double percentage;

    public RecogResult(LeafSpecies lSpecies, double percent)
    {
      this.lSpecies = lSpecies;
      this.percentage = percent;
    }
  }
}
