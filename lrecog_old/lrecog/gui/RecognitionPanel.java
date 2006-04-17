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

public class RecognitionPanel extends DefaultPanel
{
  private LeafImage actualImage = null;
  private ImagePanel recogImagePanel;
  private RecogTablePanel recogTablePanel;

  private JButton recogButton;
  private JButton resetButton;

  private JTextField imageNameField;
  private JTextField imageTokenField;
  private JTextField statusField;

  private JProgressBar recogProgressBar;
  private JSlider thresholdSlider;
  private JSlider distanceSlider;
  private JSlider minlineSlider;

  private static final int IMG_MAXWIDTH  = 370;
  private static final int IMG_MAXHEIGHT = 400;

 	public RecognitionPanel(LeavesRecognition lrec)
  {
    super(lrec);

	  setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    setBorder(border5);

    JPanel leftPane  = createLeftPanel();
    JPanel rightPane = createRightPanel();

    //Create a split pane with the two scroll panes in it.
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                          leftPane, rightPane);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(390);

    //Provide minimum sizes for the two components in the split pane
    Dimension minimumSize = new Dimension(100, 50);
    leftPane.setMinimumSize(minimumSize);

    add(splitPane);
	}

  /**
  * createLeftPanel()
  *
  * method that create the left Panel with the errorgraph
  */
  private JPanel createLeftPanel()
  {
    JPanel leftPane = new JPanel();
    leftPane.setLayout(new BorderLayout());
    leftPane.setBorder(loweredBorder);

    recogImagePanel = new ImagePanel(actualImage, IMG_MAXWIDTH, IMG_MAXHEIGHT);

    leftPane.add(recogImagePanel, BorderLayout.WEST);

    return leftPane;
  }

  /**
  * createRightPanel()
  *
  * method that create the right Panel with the network
  * properties
  */
  private JPanel createRightPanel()
  {
    JPanel rightPane = new JPanel();
    rightPane.setLayout(new BorderLayout());

    rightPane.add(createRecogInfoPanel(),     BorderLayout.NORTH);
    rightPane.add(createRecogResultPanel(),   BorderLayout.CENTER);
    rightPane.add(createRecogControlPanel(),  BorderLayout.SOUTH);

    return rightPane;
  }

  /**
  * createRecogInfoPanel()
  *
  * method that creates the panel for the Network Properties
  */
  private JPanel createRecogInfoPanel()
  {
    JPanel recogInfoPanel = new JPanel();
    recogInfoPanel.setLayout(new BorderLayout());
    recogInfoPanel.setBorder(new TitledBorder(getString("RECOG_TITLE_INFO")));

    // Create the labels.
    JLabel imageNameLabel   = new JLabel(getString("RECOG_LB_IMAGE"));
    JLabel imageTokenLabel  = new JLabel(getString("RECOG_LB_TOKEN"));
    JLabel statusLabel      = new JLabel(getString("RECOG_LB_STATUS"));

    // Create the TextFields
    imageNameField = new JTextField(getString("NA"));
    imageNameField.setEditable(false);
    imageNameField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    imageTokenField = new JTextField(getString("NA"));
    imageTokenField.setEditable(false);
    imageTokenField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    statusField = new JTextField(getString("NOT_RECOGNIZED_YET"));
    statusField.setForeground(Color.red);
    statusField.setEditable(false);
    statusField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    // connect the labels with the fields
    imageNameLabel.setLabelFor(imageNameField);
    imageTokenLabel.setLabelFor(imageTokenField);
    statusLabel.setLabelFor(statusField);

    // Layout the text labels in a panel.
    JPanel labelPane = new JPanel();
    labelPane.setLayout(new GridLayout(0, 1));
    labelPane.add(imageNameLabel);
    labelPane.add(imageTokenLabel);
    labelPane.add(statusLabel);

    // Layout the text fields in a panel.
    JPanel fieldPane = new JPanel();
    fieldPane.setLayout(new GridLayout(0, 1));
    fieldPane.add(imageNameField);
    fieldPane.add(imageTokenField);
    fieldPane.add(statusField);


    recogInfoPanel.add(labelPane, BorderLayout.WEST);
    recogInfoPanel.add(fieldPane, BorderLayout.CENTER);

    return recogInfoPanel;
  }

  /**
  * createRecogResultPanel()
  *
  * method that creates the panel for the recognition results
  */
  private JPanel createRecogResultPanel()
  {
    JPanel recogResultPanel = new JPanel();
    recogResultPanel.setLayout(new BorderLayout());
    recogResultPanel.setBorder(new TitledBorder(getString("RECOG_TITLE_RESULT")));

    recogTablePanel = new RecogTablePanel(lrec);

    recogResultPanel.add(recogTablePanel, BorderLayout.CENTER);

    return recogResultPanel;
  }

  /**
  * createRecogControlPanel()
  *
  * method that creates the panel for the Recognition controls
  */
  private JPanel createRecogControlPanel()
  {
    JPanel recogControlPanel = new JPanel();
    recogControlPanel.setLayout(new BorderLayout());
    recogControlPanel.setBorder(new TitledBorder(getString("RECOG_TITLE_CONTROL")));

    // create the sliders
    JPanel sliderPanel = createSliderPanel();

    // create the progress bar
    recogProgressBar = new JProgressBar(0, 100);
    recogProgressBar.setValue(0);
    recogProgressBar.setStringPainted(true);

    // create the buttons for the controls
    JButton loadButton = new JButton(getString("RECOG_BT_LOADIMAGE"));
    loadButton.setMargin(new Insets(0, 1, 0, 0));
    loadButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        LeafImage lImage = null;

        if(lrec.isApplet())
        {
          lImage = lrec.getImageChooser().open();
        }
        else
        {
          // Create a file chooser
          JFileChooser fc = new JFileChooser();
          fc.addChoosableFileFilter(new ImageFilter());
          fc.setAccessory(new ImagePreview(fc));

          int returnVal = fc.showOpenDialog(lrec);

          if(returnVal == JFileChooser.APPROVE_OPTION)
          {
            File file = fc.getSelectedFile();

            lImage = new LeafImage(file);


          }
        }

        if(lImage != null && lImage.getImage() != null)
        {
          // We display the image now
          actualImage = lImage;
          recogImagePanel.setImage(lImage, IMG_MAXWIDTH, IMG_MAXHEIGHT);

          imageNameField.setText(lImage.toString());
          imageTokenField.setText("0");
          statusField.setForeground(Color.red);
          statusField.setText(getString("NOT_RECOGNIZED_YET"));

          updateProjectEnv();

          lrec.refresh();
        }
      }
    });

    recogButton = new JButton(getString("RECOG_BT_RECOGNIZE"));
    if(!lrec.isApplet()) recogButton.setEnabled(false);
    recogButton.setMargin(new Insets(0, 1, 0, 0));
    recogButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        // Clear the recognition panel now
        recogTablePanel.clear();

        // Now we perform the Image processing
        ImageProcessor imgProc = new ImageProcessor(actualImage.getImage());

        recogProgressBar.setValue(1);

        lrec.setStatusField(getString("STATUS_EDGEDETECT"));
        lrec.refresh();
    		imgProc.edgeDetect(thresholdSlider.getValue()*10);
        recogImagePanel.setImage(imgProc.getImage(recogImagePanel));
        recogProgressBar.setValue(16);

        lrec.setStatusField(getString("STATUS_THINNING"));
        lrec.refresh();
		    imgProc.thinning();
		    recogImagePanel.setImage(imgProc.getImage(recogImagePanel));
        recogProgressBar.setValue(32);

        lrec.setStatusField(getString("STATUS_LINECHECK"));
        lrec.refresh();
		    imgProc.checkLines(minlineSlider.getValue()*10);
        recogImagePanel.setImage(imgProc.getImage(recogImagePanel));
        recogProgressBar.setValue(48);

        lrec.setStatusField(getString("STATUS_DISTANCE"));
        lrec.refresh();
        imgProc.markPoints(distanceSlider.getValue()*10);
		    recogImagePanel.setImage(imgProc.getImage(recogImagePanel));
        recogProgressBar.setValue(64);

        // now we calculate the tokens of the image by calculating
        // the angles
        lrec.setStatusField(getString("STATUS_TOKENS"));
        lrec.refresh();
        imgProc.calcAngels();
		    recogImagePanel.setImage(imgProc.getImage(recogImagePanel));
        recogProgressBar.setValue(96);

        // set the TextField for the amount of tokens
        ArrayList leafTokens = imgProc.getTokens();
        actualImage.setTokens(leafTokens);

        lrec.setStatusField(getString("STATUS_RECOGIMAGE"));

        // Now we recognize the image with the trained neutronal network
        BackProp nNetwork = projectEnv.getNetwork();

        double[] resultID = nNetwork.propagate(actualImage.getTokens(nNetwork.numInput()));

        /*
        for(int i=0; i < nNetwork.numOutput(); i++)
        {
          System.out.println("OUT["+i+"]: "+test[i]);
        }
        */

        ArrayList leafSpecies = projectEnv.getLeafSpecies();
        double error = 0.0;

        for(int i=0; i < leafSpecies.size(); i++, error=0.0)
        {
          LeafSpecies lSpecies = (LeafSpecies)leafSpecies.get(i);

          //System.out.println("LeafSpecies "+i+" ["+lSpecies.getName()+"]:");

          double[] ID = lSpecies.getID();

          for(int j=0; j < nNetwork.numOutput(); j++)
          {
            //System.out.println(" ["+j+"]: "+ID[j]);

            if(ID[j] >= resultID[j])
            {
              error += ID[j] - resultID[j];
            }
            else error += resultID[j] - ID[j];

          }

          //System.out.println("Perc.: "+(100.0-(100/leafSpecies.size())*error)+"%");

          recogTablePanel.addResult(lSpecies, (100.0-(100/leafSpecies.size())*error));
        }

        recogProgressBar.setValue(100);

        imageTokenField.setText(""+actualImage.numTokens());
        statusField.setForeground(Color.blue);
        statusField.setText(getString("RECOGNIZED"));
        lrec.setStatusField(getString("STATUS_FINISHED"));
      }
    });

    resetButton = new JButton("Reset");
    if(!lrec.isApplet()) resetButton.setEnabled(false);
    resetButton.setMargin(new Insets(0, 1, 0, 0));
    resetButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
		    recogImagePanel.setImage(actualImage, IMG_MAXWIDTH, IMG_MAXHEIGHT);
        actualImage.clearTokens();
        lrec.setStatusField(getString("STATUS_IMGRESET"));
        imageTokenField.setText("0");
        statusField.setForeground(Color.red);
        statusField.setText(getString("NOT_RECOGNIZED_YET"));
        recogProgressBar.setValue(0);
        recogTablePanel.clear();
      }
    });

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 10));
    buttonPane.add(loadButton);
    buttonPane.add(Box.createRigidArea(new Dimension(3, 0)));
    buttonPane.add(recogButton);
    buttonPane.add(Box.createRigidArea(new Dimension(3, 0)));
    buttonPane.add(resetButton);
    buttonPane.add(Box.createHorizontalGlue());

    recogControlPanel.add(sliderPanel,      BorderLayout.NORTH);
    recogControlPanel.add(recogProgressBar, BorderLayout.CENTER);
    recogControlPanel.add(buttonPane,       BorderLayout.SOUTH);

    return recogControlPanel;
  }

  /**
  * createSliderPanel()
  *
  * method that creates the sliders for the operations
  */
  private JPanel createSliderPanel()
  {
    JPanel sliderPanel = new JPanel();
    sliderPanel.setLayout(new BorderLayout());

    // create the labels for the sliders
    JLabel thresholdSliderLabel = new JLabel(getString("RECOG_LB_THRESHOLD"));
    JLabel distanceSliderLabel  = new JLabel(getString("RECOG_LB_DISTANCE"));
    JLabel minlineSliderLabel   = new JLabel(getString("RECOG_LB_MINLINE"));

    // Layout the text labels in a panel.
    JPanel labelPane = new JPanel();
    labelPane.setLayout(new GridLayout(0, 1));
    labelPane.add(thresholdSliderLabel);
    labelPane.add(distanceSliderLabel);
    labelPane.add(minlineSliderLabel);

    // now we create the sliders for the different operations
    thresholdSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
    //thresholdSlider.addChangeListener(new SliderListener());
    thresholdSlider.setMajorTickSpacing(1);
    thresholdSlider.setSnapToTicks(true);

    distanceSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 2);
    distanceSlider.setMajorTickSpacing(1);
    distanceSlider.setSnapToTicks(true);

    minlineSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 2);
    minlineSlider.setMajorTickSpacing(1);
    minlineSlider.setSnapToTicks(true);

    // Layout the slider in a panel
    JPanel sliderGroup = new JPanel();
    sliderGroup.setLayout(new GridLayout(0, 1));
    sliderGroup.add(thresholdSlider);
    sliderGroup.add(distanceSlider);
    sliderGroup.add(minlineSlider);
    sliderGroup.setPreferredSize(new Dimension(125, 0));

    sliderPanel.add(labelPane, BorderLayout.WEST);
    sliderPanel.add(sliderGroup, BorderLayout.CENTER);

    return sliderPanel;
  }

  public void updateProjectEnv()
  {
    if(projectEnv.getNetwork() != null && actualImage != null)
    {
      recogButton.setEnabled(true);
      resetButton.setEnabled(true);
    }
    else
    {
      recogButton.setEnabled(false);
      resetButton.setEnabled(false);
    }
  }
}
