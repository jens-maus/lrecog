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
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import lrecog.*;
import lrecog.gfx.*;
import lrecog.tools.*;
import lrecog.tools.ImageFilter;

public class ImageProcPanel extends DefaultPanel
{
  private ImageTreePanel treePanel;

  // now the displayed images
  private LeafImage actualImage = null;
  private ImagePanel orgImagePanel;
  private ImagePanel procImagePanel;

  // global operations labels
  private JButton tokenButton;
  private JProgressBar imgControlBar;
  private JTextField imgNameField;
  private JTextField imgSizeField;
  private JTextField imgClassField;
  private JTextField imgStatusField;
  private JSlider thresholdSlider;
  private JSlider distanceSlider;
  private JSlider minlineSlider;

  private static final int ORGIMG_MAXWIDTH  = 100;
  private static final int ORGIMG_MAXHEIGHT = 400;
  private static final int PRCIMG_MAXWIDTH  = 270;
  private static final int PRCIMG_MAXHEIGHT = 400;

	public ImageProcPanel(LeavesRecognition lrec)
  {
    super(lrec);

	  setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    setBorder(border5);

    JPanel leftScrollPane       = createLeftPanel();
    JSplitPane rightScrollPane  = createRightPanel();

    //Create a split pane with the two scroll panes in it.
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                               leftScrollPane, rightScrollPane);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(390);

    //Provide minimum sizes for the two components in the split pane
    Dimension minimumSize = new Dimension(100, 50);
    leftScrollPane.setMinimumSize(minimumSize);

    add(splitPane);
 	}

  /**
  * createLeftPanel()
  *
  * method that create the left Panel with the images
  */
  private JPanel createLeftPanel()
  {
    JPanel leftPane = new JPanel();
    leftPane.setLayout(new BorderLayout());
    leftPane.setBorder(loweredBorder);

    orgImagePanel = new ImagePanel(actualImage, ORGIMG_MAXWIDTH, ORGIMG_MAXHEIGHT);
    leftPane.add(orgImagePanel, BorderLayout.WEST);

    procImagePanel = new ImagePanel(actualImage, PRCIMG_MAXWIDTH, PRCIMG_MAXHEIGHT);
    leftPane.add(procImagePanel, BorderLayout.CENTER);

    return leftPane;
  }

  /**
  * createRightPanel()
  *
  * method that create the right Panel with the JTree
  * and the buttons.
  */
  private JSplitPane createRightPanel()
  {
    JPanel topScrollPane = createJTreePanel();
    JPanel bottomScrollPane = createImageOpPanel();

    // create a split pane with the two scroll panes in it.
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                          topScrollPane, bottomScrollPane);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(200);

    //Provide minimum sizes for the two components in the split pane
    Dimension minimumSize = new Dimension(50, 150);
    topScrollPane.setMinimumSize(minimumSize);
    bottomScrollPane.setMinimumSize(minimumSize);

    return splitPane;
  }

  /**
  * createJTreePanel()
  *
  * method that creates the Panel with the JTree and the
  * command buttons
  */
  private JPanel createJTreePanel()
  {
    JPanel rootPane = new JPanel();
    rootPane.setLayout(new BorderLayout());

    treePanel = new ImageTreePanel(lrec);
    rootPane.add(treePanel, BorderLayout.CENTER);

    JButton addImageButton = new JButton(getString("IMGPROC_BT_ADDIMG"));
    addImageButton.setMargin(new Insets(0, 1, 0, 0));
    addImageButton.addActionListener(new ActionListener()
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
          // We add the leaf image to the tree and to the
          // project enviroment
          LeafSpecies lSpecies = (LeafSpecies)treePanel.addChild(lImage);

          // now add this Leaf image to the species
          if(lSpecies != null) lSpecies.addImage(lImage);

          projectEnv.setModified();
          lrec.updateProjectEnv();
        }
      }
    });

    JButton addSpeciesButton = new JButton(getString("IMGPROC_BT_NEWSPECIES"));
    addSpeciesButton.setMargin(new Insets(0, 1, 0, 0));
    addSpeciesButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        LeafSpecies lSpecies = new LeafSpecies(getString("SPECIES")+" "+(projectEnv.numLeafSpecies()+1));

        treePanel.addSpecies(lSpecies);
        projectEnv.addLeafSpecies(lSpecies);

        projectEnv.setModified();
        lrec.updateProjectEnv();
      }
    });

    JButton removeButton = new JButton(getString("IMGPROC_BT_DELETE"));
    removeButton.setMargin(new Insets(0, 1, 0, 0));
    removeButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        LeafSpecies lSpecies = treePanel.removeCurrentNode();

        if(lSpecies != null)
        {
          projectEnv.removeLeafSpecies(lSpecies);

          projectEnv.setModified();
          lrec.updateProjectEnv();
        }
      }
    });

    JButton renameButton = new JButton(getString("IMGPROC_BT_RENAME"));
    renameButton.setMargin(new Insets(0, 1, 0, 0));
    renameButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        treePanel.renameCurrentNode();

        projectEnv.setModified();
        lrec.updateProjectEnv();
      }
    });

    JPanel button1Pane = new JPanel();
    button1Pane.setLayout(new BoxLayout(button1Pane, BoxLayout.X_AXIS));
    button1Pane.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 4));
    button1Pane.add(Box.createHorizontalGlue());
    button1Pane.add(addImageButton);
    button1Pane.add(Box.createRigidArea(new Dimension(3, 0)));
    button1Pane.add(addSpeciesButton);

    JPanel button2Pane = new JPanel();
    button2Pane.setLayout(new BoxLayout(button2Pane, BoxLayout.X_AXIS));
    button2Pane.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 4));
    button2Pane.add(Box.createHorizontalGlue());
    button2Pane.add(renameButton);
    button2Pane.add(Box.createRigidArea(new Dimension(3, 0)));
    button2Pane.add(removeButton);

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));
    buttonPane.add(button1Pane);
    buttonPane.add(button2Pane);

    rootPane.add(buttonPane,  BorderLayout.SOUTH);

    return rootPane;
  }

  /**
  * createImageOpPanel()
  *
  * method that creates the panel for the Image operations
  */
  private JPanel createImageOpPanel()
  {
    JPanel imgOpPanel = new JPanel();
    imgOpPanel.setLayout(new BorderLayout());
	  imgOpPanel.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), getString("IMGPROC_TITLE_OPS"), TitledBorder.LEFT, TitledBorder.TOP));

    JPanel imgInfoPanel    = createImageInfoPanel();
    JPanel imgControlPanel = createImageControlPanel();

    imgOpPanel.add(imgInfoPanel, BorderLayout.NORTH);
    imgOpPanel.add(imgControlPanel, BorderLayout.SOUTH);

    return imgOpPanel;
  }

  /**
  * createImageInfoPanel()
  *
  * method that creates the panel for the Image information
  */
  private JPanel createImageInfoPanel()
  {
    JPanel imgInfoPanel = new JPanel();
    imgInfoPanel.setLayout(new BorderLayout());

    // Create the labels.
    JLabel imgNameLabel   = new JLabel(getString("IMGPROC_LB_NAME"));
    JLabel imgSizeLabel   = new JLabel(getString("IMGPROC_LB_SIZE"));
    JLabel imgClassLabel  = new JLabel(getString("IMGPROC_LB_CLASS"));
    JLabel imgStatusLabel = new JLabel(getString("IMGPROC_LB_TOKENS"));

    // Create the TextFields
    imgNameField   = new JTextField(getString("NA"));
    imgNameField.setEditable(false);
    imgNameField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    imgSizeField   = new JTextField(getString("NA"));
    imgSizeField.setEditable(false);
    imgSizeField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    imgClassField   = new JTextField(getString("NA"));
    imgClassField.setEditable(false);
    imgClassField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    imgStatusField = new JTextField(getString("NONE_FOUND_YET"));
    imgStatusField.setEditable(false);
    imgStatusField.setForeground(Color.red);
    imgStatusField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    // connect the labels with the fields
    imgNameLabel.setLabelFor(imgNameField);
    imgSizeLabel.setLabelFor(imgSizeField);
    imgClassLabel.setLabelFor(imgClassField);
    imgStatusLabel.setLabelFor(imgStatusField);

    // Layout the text labels in a panel.
    JPanel labelPane = new JPanel();
    labelPane.setLayout(new GridLayout(0, 1));
    labelPane.add(imgNameLabel);
    labelPane.add(imgSizeLabel);
    labelPane.add(imgClassLabel);
    labelPane.add(imgStatusLabel);

    // Layout the text fields in a panel.
    JPanel fieldPane = new JPanel();
    fieldPane.setLayout(new GridLayout(0, 1));
    fieldPane.add(imgNameField);
    fieldPane.add(imgSizeField);
    fieldPane.add(imgClassField);
    fieldPane.add(imgStatusField);


    imgInfoPanel.add(labelPane, BorderLayout.WEST);
    imgInfoPanel.add(fieldPane, BorderLayout.CENTER);

    return imgInfoPanel;
  }

  /**
  * createImageControlPanel()
  *
  * method that creates the panel for the Image controls
  */
  private JPanel createImageControlPanel()
  {
    JPanel imgControlPanel = new JPanel();
    imgControlPanel.setLayout(new BorderLayout());

    // create the sliders
    JPanel sliderPanel = createSliderPanel();

    // create the progress bar
    imgControlBar = new JProgressBar(0, 100);
    imgControlBar.setValue(0);
    imgControlBar.setStringPainted(true);

    // create the buttons for the controls
    tokenButton = new JButton(getString("IMGPROC_BT_FINDTOKENS"));
    tokenButton.setMargin(new Insets(0, 1, 0, 0));
    tokenButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if(actualImage == null) return;

        // first we disable the button
        tokenButton.setEnabled(false);

        // Now we perform the Image processing
        ImageProcessor imgProc = new ImageProcessor(actualImage.getImage());

        imgControlBar.setValue(1);

        lrec.setStatusField(getString("STATUS_EDGEDETECT"));
        lrec.refresh();
    		imgProc.edgeDetect(thresholdSlider.getValue()*10);
        procImagePanel.setImage(imgProc.getImage(procImagePanel));
        imgControlBar.setValue(20);

        lrec.setStatusField(getString("STATUS_THINNING"));
        lrec.refresh();
		    imgProc.thinning();
		    procImagePanel.setImage(imgProc.getImage(procImagePanel));
        imgControlBar.setValue(40);

        lrec.setStatusField(getString("STATUS_LINECHECK"));
        lrec.refresh();
		    imgProc.checkLines(minlineSlider.getValue()*10);
        procImagePanel.setImage(imgProc.getImage(procImagePanel));
        imgControlBar.setValue(60);

        lrec.setStatusField(getString("STATUS_DISTANCE"));
        lrec.refresh();
        imgProc.markPoints(distanceSlider.getValue()*10);
		    procImagePanel.setImage(imgProc.getImage(procImagePanel));
        imgControlBar.setValue(80);

        // now we calculate the tokens of the image by calculating
        // the angles
        lrec.setStatusField(getString("STATUS_TOKENS"));
        lrec.refresh();
        imgProc.calcAngels();
		    procImagePanel.setImage(imgProc.getImage(procImagePanel));
        imgControlBar.setValue(100);

        // set the TextField for the amount of tokens
        ArrayList leafTokens = imgProc.getTokens();
        actualImage.setTokens(leafTokens);

        updateProjectEnv();
        
        lrec.setStatusField(getString("STATUS_FINISHED"));

        // at the end we enable the button again
        tokenButton.setEnabled(true);

        projectEnv.setModified();
        lrec.updateProjectEnv();
      }
    });

    JButton resetButton = new JButton(getString("IMGPROC_BT_RESET"));
    resetButton.setMargin(new Insets(0, 1, 0, 0));
    resetButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if(actualImage == null) return;

		    procImagePanel.setImage(actualImage, PRCIMG_MAXWIDTH, PRCIMG_MAXHEIGHT);
        actualImage.clearTokens();
        lrec.setStatusField(getString("STATUS_IMGRESET"));
        imgControlBar.setValue(0);

        projectEnv.setModified();
        lrec.updateProjectEnv();
      }
    });

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 10));
    buttonPane.add(tokenButton);
    buttonPane.add(Box.createRigidArea(new Dimension(3, 0)));
    buttonPane.add(resetButton);
    buttonPane.add(Box.createHorizontalGlue());

    imgControlPanel.add(sliderPanel,    BorderLayout.NORTH);
    imgControlPanel.add(imgControlBar,  BorderLayout.CENTER);
    imgControlPanel.add(buttonPane,     BorderLayout.SOUTH);

    return imgControlPanel;
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
    JLabel thresholdSliderLabel = new JLabel(getString("IMGPROC_LB_THRESHOLD"));
    JLabel distanceSliderLabel  = new JLabel(getString("IMGPROC_LB_DISTANCE"));
    JLabel minlineSliderLabel   = new JLabel(getString("IMGPROC_LB_MINLINE"));

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

  public void setActualImage(LeafImage limage)
  {
    actualImage = limage;
    orgImagePanel.setImage(limage, ORGIMG_MAXWIDTH, ORGIMG_MAXHEIGHT);
    procImagePanel.setImage(limage, PRCIMG_MAXWIDTH, PRCIMG_MAXHEIGHT);

    // now set the status stuff
    imgNameField.setText(limage.toString());
    imgSizeField.setText(limage.getImage().getWidth(null)+"x"+limage.getImage().getHeight(null));

    updateProjectEnv();

    lrec.refresh();
  }

  public void reloadTree()
  {
    ArrayList lSpecies = projectEnv.getLeafSpecies();

    // first clear the JTree
    treePanel.clear();

    // now fill the panel with new entries
    for(int i = 0; i < lSpecies.size(); i++)
    {
      LeafSpecies lNewSpecies = (LeafSpecies)lSpecies.get(i);

      DefaultMutableTreeNode speciesTree = treePanel.addSpecies(lNewSpecies);

      for(int j = 0; j < lNewSpecies.numImages(); j++)
      {
        LeafImage lImage = (LeafImage)lNewSpecies.getImage(j);

        treePanel.addChild(speciesTree, lImage);
      }
    }
  }

  public void updateProjectEnv()
  {
    if(actualImage != null && actualImage.numTokens() > 0)
    {
      imgStatusField.setForeground(Color.blue);
      imgStatusField.setText(actualImage.numTokens()+" "+getString("FOUND"));

      LeafSpecies lSpecies = (LeafSpecies)treePanel.getParentOfCurrent();

      if(lSpecies != null)
      {
        imgClassField.setText(lSpecies.getName());
      }
      else
      {
        imgClassField.setText(getString("NA"));
      }
    }
    else
    {
      imgStatusField.setForeground(Color.red);
      imgStatusField.setText(getString("NONE_FOUND_YET"));

      imgClassField.setText(getString("NA"));
    }
  }
}
