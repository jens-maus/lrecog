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
import java.awt.image.*;

import java.util.*;

import lrecog.*;

public class ErrorGraphPanel extends DefaultPanel
{
	private ErrorGraphDraw	graphPanel;
  private JTextField errorField;
  private JTextField stepField;
  private int steps = 0;

  public ErrorGraphPanel(LeavesRecognition lrec)
  {
    super(lrec);
    
    setLayout(new BorderLayout());
    setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), getString("ERROR_TITLE_INFO"), TitledBorder.LEFT, TitledBorder.TOP));

		graphPanel = new ErrorGraphDraw();
		add(graphPanel, BorderLayout.CENTER);

		JButton clearButton = new JButton(getString("ERROR_BT_CLEAR"));
    clearButton.setMargin(new Insets(0, 1, 0, 0));
    clearButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        clear();
      }
    });

    // Create the labels.
    JLabel errorLabel = new JLabel(getString("ERROR_LB_ERROR"));
    JLabel stepLabel  = new JLabel(getString("ERROR_LB_STEP"));

    // Create the TextFields
    errorField = new JTextField(getString("NA"));
    errorField.setEditable(false);
    errorField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    stepField = new JTextField(""+steps);
    stepField.setEditable(false);
    stepField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    // connect the labels with the fields
    errorLabel.setLabelFor(errorField);
    stepLabel.setLabelFor(stepField);

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 4));
    buttonPane.add(clearButton);
    buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    buttonPane.add(errorLabel);
    buttonPane.add(Box.createRigidArea(new Dimension(3, 0)));
    buttonPane.add(errorField);
    buttonPane.add(Box.createRigidArea(new Dimension(3, 0)));
    buttonPane.add(stepLabel);
    buttonPane.add(Box.createRigidArea(new Dimension(3, 0)));
    buttonPane.add(stepField);
    buttonPane.add(Box.createHorizontalGlue());

		add(buttonPane, BorderLayout.SOUTH);
  }

  public void addError(double err)
  {
    graphPanel.addError(err);
    errorField.setText(""+err);
    stepField.setText(""+(++steps));
  }

  public void clear()
  {
    graphPanel.clear();
    graphPanel.repaint();
    errorField.setText(getString("NA"));
    stepField.setText("0");
    steps = 0;
    repaint();
  }
}
