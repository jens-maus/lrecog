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

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class FileOpenDialog extends JDialog
{
  private JList fileList;
	private JTextField fileText;
	private JComboBox c_type;
	private Vector currentList;
	private String selection;
	private boolean state;
	private Dimension screen;
	private Hashtable inhalt;

	public static boolean APPROVE_OPTION = true;
	public static boolean CANCEL_OPTION = false;

	public FileOpenDialog(final Hashtable inhalt, JFrame f, boolean modal)
  {
	  super(f, modal);
		this.inhalt = inhalt;
		//JButton
		JButton open = new JButton("Open");

		open.addActionListener(new ActionListener()
    {
			public void actionPerformed(ActionEvent ae)
      {
				String str = fileText.getText();
				if(isElementOfList(str))
        {
					state = APPROVE_OPTION;
					dispose();
					clear();
				}
        else
        {
		  		int len = str.length();
					if(len != 0)
          {
						String message = "'"+str+"'" + " does not exist!";
						JOptionPane.showMessageDialog(null,message,"Error",
						JOptionPane.ERROR_MESSAGE);
						clear();
					}
				}
			}
    });

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
    {
		  public void actionPerformed(ActionEvent ae)
      {
				state = CANCEL_OPTION;
				dispose();
				clear();
			}
		});

		//JComboBox
		Set s = inhalt.keySet();
		TreeSet t = new TreeSet(s);
		Vector v = new Vector(t);
		c_type = new JComboBox(v);
		c_type.setSelectedItem(v.get(0));
		currentList = (Vector)inhalt.get(v.get(0));

		c_type.addActionListener(new ActionListener()
    {
		  public void actionPerformed(ActionEvent ae)
      {
			  String str = (String)c_type.getSelectedItem();
				currentList = (Vector)inhalt.get(str);
				fileList.setListData(currentList);
      }
    });

		//JList
		fileList = new JList(currentList);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.addListSelectionListener(new ListSelectionListener()
    {
			public void valueChanged(ListSelectionEvent lse)
      {
				String str = (String)fileList.getSelectedValue();
				fileText.setText(str);
			}
		});

		fileList.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        if(e.getClickCount() == 2)
        {
          String str = (String)fileList.getSelectedValue();
          selection = str;
          state = APPROVE_OPTION;
			  	dispose();
					clear();
        }
      }
    });

		JScrollPane sp = new JScrollPane(fileList);
		Border b = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(12, 12, 2, 12), BorderFactory.createEtchedBorder());
		sp.setBorder(b);

		//JLabel
		JLabel l_name = new JLabel("File name", JLabel.LEFT);
		JLabel l_type = new JLabel("File type", JLabel.LEFT);

		//JTextField
		fileText = new JTextField();
		fileText.addActionListener(new ActionListener()
    {
			public void actionPerformed(ActionEvent ae)
      {
				String str = fileText.getText();
				if(isElementOfList(str))
        {
					state = APPROVE_OPTION;
					dispose();
					clear();
				}
				else
        {
					String message = "'"+str+"'" + " does not exist!";
					JOptionPane.showMessageDialog(null,message,"Error",
					JOptionPane.ERROR_MESSAGE);
					clear();
				}
			}
		});


		Container c = getContentPane();

		JPanel pan2 = new JPanel();
		pan2.setLayout(new GridLayout(0, 1, 6, 12));
		pan2.add(l_name);
		pan2.add(l_type);

		JPanel pan = new JPanel();
		pan.setBorder(new EmptyBorder(6, 6, 6, 6));
		pan.add(pan2, BorderLayout.WEST);

		pan2 = new JPanel();
		pan2.setLayout(new GridLayout(0, 1, 6, 6));
		pan2.add(fileText);
		pan2.add(c_type);
		pan.add(pan2, BorderLayout.CENTER);

		pan2 = new JPanel();
		pan2.setLayout(new GridLayout(0, 1, 6, 6));
		pan2.add(open);
		pan2.add(cancel);
		pan.add(pan2, BorderLayout.EAST);

		pan2 = new JPanel();
		pan2.add(pan, BorderLayout.WEST);

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(sp);
		p.add(pan);
		c.add(p);

		//Eigenschaften des Frames
		setTitle("Open");
		pack();
		Dimension d=getPreferredSize();
		screen = getToolkit().getScreenSize();
		screen.height /= 2;
		screen.height -= (d.height/2);
		screen.width /= 2;
		screen.width -= (d.width/2);
		setLocation(screen.width, screen.height);

		addWindowListener(new WindowAdapter()
    {
			public void windowClosing(WindowEvent we)
      {
				state = CANCEL_OPTION;
				dispose();
				clear();
			}
		});
		//TLRecMenu.registerFrame(this);
  }

	private void clear()
  {
		fileList.clearSelection();
		fileText.setText(null);
	}

	private boolean isElementOfList(String str)
  {
		if(str != null)
    {
	  	int i = 0;
			boolean found = false;
			while(!found && (i < currentList.size()))
      {
				String file = (String)currentList.get(i);
				if(file.equals(str))
        {
					found = true;
					selection = str;
				}
				i++;
			}
			return found;
		}
		return false;
  }

	public boolean showDialog()
  {
		pack();
		setLocation(screen.width, screen.height);
		setVisible(true);
		return state;
	}

	public String getSelectedItem()
  {
		return selection;
	}

	public String getFirstItem()
  {
		ListModel lm = fileList.getModel();
		int i = fileList.getFirstVisibleIndex();
		return (String)lm.getElementAt(i);
	}
}