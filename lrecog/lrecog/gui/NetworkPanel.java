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
import lrecog.gfx.*;
import lrecog.nnetwork.*;
import lrecog.tools.*;

public class NetworkPanel extends DefaultPanel
{
  private ErrorGraphPanel errorPanel;

  // ProgressBar
  private JProgressBar netProgressBar;

  // Panels
  private JTextField numLeafImagesField;
  private JTextField numLeafSpeciesField;
  private JTextField maxTokenField;
  private JTextField statusField;

  private JButton trainButton;
  
  private JTextField inputNeuronField;
  private JTextField hiddenNeuronField;
  private JTextField outputNeuronField;
  private JTextField learnRateField;
  private JTextField momentumField;
  private JTextField stepsField;

	public NetworkPanel(LeavesRecognition lrec)
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

    errorPanel = new ErrorGraphPanel(lrec);
    leftPane.add(errorPanel, BorderLayout.NORTH);

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
    //rightPane.setBorder(loweredBorder);

    rightPane.add(createNetworkInfoPanel(), BorderLayout.NORTH);
    rightPane.add(createNetworkOpPanel(), BorderLayout.SOUTH);

    return rightPane;
  }

  /**
  * createNetworkInfoPanel()
  *
  * method that creates the panel for the Network Properties
  */
  private JPanel createNetworkInfoPanel()
  {
    JPanel netInfoPanel = new JPanel();
    netInfoPanel.setLayout(new BorderLayout());
    netInfoPanel.setBorder(new TitledBorder(getString("NETWORK_TITLE_INFO")));

    // Create the labels.
    JLabel numLeafImagesLabel   = new JLabel(getString("NETWORK_LB_LEAFIMAGES"));
    JLabel numLeafSpeciesLabel  = new JLabel(getString("NETWORK_LB_LEAFSPECIES"));
    JLabel maxTokenLabel        = new JLabel(getString("NETWORK_LB_MAXTOKEN"));
    JLabel statusLabel          = new JLabel(getString("NETWORK_LB_STATUS"));

    // Create the TextFields
    numLeafImagesField = new JTextField(getString("NA"));
    numLeafImagesField.setEditable(false);
    numLeafImagesField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    numLeafSpeciesField = new JTextField(getString("NA"));
    numLeafSpeciesField.setEditable(false);
    numLeafSpeciesField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    maxTokenField = new JTextField(getString("NA"));
    maxTokenField.setEditable(false);
    maxTokenField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    statusField = new JTextField(getString("NOT_TRAINED_YET"));
    statusField.setForeground(Color.red);
    statusField.setEditable(false);
    statusField.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

    // connect the labels with the fields
    numLeafImagesLabel.setLabelFor(numLeafImagesField);
    numLeafSpeciesLabel.setLabelFor(numLeafSpeciesField);
    maxTokenLabel.setLabelFor(maxTokenField);
    statusLabel.setLabelFor(statusField);

    // Layout the text labels in a panel.
    JPanel labelPane = new JPanel();
    labelPane.setLayout(new GridLayout(0, 1));
    labelPane.add(numLeafImagesLabel);
    labelPane.add(numLeafSpeciesLabel);
    labelPane.add(maxTokenLabel);
    labelPane.add(statusLabel);

    // Layout the text fields in a panel.
    JPanel fieldPane = new JPanel();
    fieldPane.setLayout(new GridLayout(0, 1));
    fieldPane.add(numLeafImagesField);
    fieldPane.add(numLeafSpeciesField);
    fieldPane.add(maxTokenField);
    fieldPane.add(statusField);


    netInfoPanel.add(labelPane, BorderLayout.WEST);
    netInfoPanel.add(fieldPane, BorderLayout.CENTER);

    return netInfoPanel;
  }

  /**
  * createNetworkOpPanel()
  *
  * method that creates the panel for the Network Operations
  */
  private JPanel createNetworkOpPanel()
  {
    JPanel netOpPanel = new JPanel();
    netOpPanel.setLayout(new BorderLayout());
    netOpPanel.setBorder(new TitledBorder(getString("NETWORK_TITLE_OPS")));

    // Create the labels.
    JLabel inputNeuronLabel   = new JLabel(getString("NETWORK_LB_INPUT"));
    JLabel hiddenNeuronLabel  = new JLabel(getString("NETWORK_LB_HIDDEN"));
    JLabel outputNeuronLabel  = new JLabel(getString("NETWORK_LB_OUTPUT"));
    JLabel learnRateLabel     = new JLabel(getString("NETWORK_LB_LEARNRATE"));
    JLabel momentumLabel      = new JLabel(getString("NETWORK_LB_MOMENTUM"));
    JLabel stepsLabel         = new JLabel(getString("NETWORK_LB_STEPS"));

    // Create the TextFields
    inputNeuronField = new JTextField("10");
    hiddenNeuronField= new JTextField("5");
    outputNeuronField= new JTextField("2");
    learnRateField   = new JTextField("0.3");
    momentumField    = new JTextField("1.0");
    stepsField       = new JTextField("100");

    // connect the labels with the fields
    inputNeuronLabel.setLabelFor(inputNeuronField);
    hiddenNeuronLabel.setLabelFor(hiddenNeuronField);
    outputNeuronLabel.setLabelFor(outputNeuronField);
    learnRateLabel.setLabelFor(learnRateField);
    momentumLabel.setLabelFor(momentumField);
    stepsLabel.setLabelFor(stepsField);

    // Layout the text labels in a panel.
    JPanel labelPane = new JPanel();
    labelPane.setLayout(new GridLayout(0, 1));
    labelPane.add(inputNeuronLabel);
    labelPane.add(hiddenNeuronLabel);
    labelPane.add(outputNeuronLabel);
    labelPane.add(learnRateLabel);
    labelPane.add(momentumLabel);
    labelPane.add(stepsLabel);

    // Layout the text fields in a panel.
    JPanel fieldPane = new JPanel();
    fieldPane.setLayout(new GridLayout(0, 1));
    fieldPane.add(inputNeuronField);
    fieldPane.add(hiddenNeuronField);
    fieldPane.add(outputNeuronField);
    fieldPane.add(learnRateField);
    fieldPane.add(momentumField);
    fieldPane.add(stepsField);

    // create the Control Panel with the progress bar and
    // the buttons
    JPanel contrPane = createNetworkControlPanel();


    netOpPanel.add(labelPane, BorderLayout.WEST);
    netOpPanel.add(fieldPane, BorderLayout.CENTER);
    netOpPanel.add(contrPane, BorderLayout.SOUTH);

    return netOpPanel;
  }

  /**
  * createNetworkControlPanel()
  *
  * method that creates the panel for the Network Properties
  */
  private JPanel createNetworkControlPanel()
  {
    JPanel netContrPanel = new JPanel();
    netContrPanel.setLayout(new BorderLayout());

    // create the progress bar
    netProgressBar = new JProgressBar(0, 50);
    netProgressBar.setValue(0);
    netProgressBar.setStringPainted(true);

    // create the buttons for the controls
    trainButton = new JButton(getString("NETWORK_BT_TRAIN"));
    trainButton.setEnabled(false);
    trainButton.setMargin(new Insets(0, 1, 0, 0));
    trainButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        trainNetwork();
        projectEnv.setModified();
        lrec.updateProjectEnv();
      }
    });

    JButton defaultButton = new JButton(getString("NETWORK_BT_DEFAULT"));
    defaultButton.setMargin(new Insets(0, 1, 0, 0));
    defaultButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        // the maxToken is the number of found lines multiplied by
        // two because we have a cosinus & sinus value for this line
        // to identify the direction
        int maxToken = projectEnv.getMaxToken()*2;

        // Now update the Fields
        inputNeuronField.setText(""+maxToken);
        hiddenNeuronField.setText("20");
        outputNeuronField.setText(""+projectEnv.numLeafSpecies());
        learnRateField.setText("0.3");
        momentumField.setText("1.0");
        stepsField.setText("500");
        netProgressBar.setValue(0);

        // clear the BackProp of the ProjectEnv
        projectEnv.setNetwork(null);

        projectEnv.setModified();
        lrec.updateProjectEnv();
      }
    });

    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
    buttonPane.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 10));
    buttonPane.add(trainButton);
    buttonPane.add(Box.createRigidArea(new Dimension(3, 0)));
    buttonPane.add(defaultButton);
    buttonPane.add(Box.createHorizontalGlue());

    netContrPanel.add(netProgressBar,   BorderLayout.CENTER);
    netContrPanel.add(buttonPane,       BorderLayout.SOUTH);

    return netContrPanel;
  }

  /**
  * trainNetwork()
  *
  * method to get the network properties from the TextFields and
  * starts training the network
  */
  public void trainNetwork()
  {
    // get the properties of the network first
    int inputs  = Integer.parseInt(inputNeuronField.getText());
    int hiddens = Integer.parseInt(hiddenNeuronField.getText());
    int outputs = Integer.parseInt(outputNeuronField.getText());
    double learnRate = Double.parseDouble(learnRateField.getText());
    double momentum  = Double.parseDouble(momentumField.getText());
    int steps = Integer.parseInt(stepsField.getText());

    // change progressbar
    netProgressBar.setMaximum(steps);

    // Now we create a new BackProp object
    // for doing the neuronal network stuff
    BackProp nNetwork = new BackProp(inputs, hiddens, outputs);

    // Set the network in the project environment
    projectEnv.setNetwork(nNetwork);

    // set learnrate & momentum to user settings
    nNetwork.setAlpha(learnRate);
    nNetwork.setMomentum(momentum);

    ArrayList leafSpecies = projectEnv.getLeafSpecies();

    Random rand = new Random();

    for(int i=0; i < leafSpecies.size(); i++)
    {
      LeafSpecies lSpecies = (LeafSpecies)leafSpecies.get(i);

      double[] outputVals = new double[outputs];

      // create a random ID as long as the output neurons
      for(int j=0; j < outputs; j++)
      {
        if(j <= i)
        {
          outputVals[j] = 1.0;
        }
        else outputVals[j] = 0.0;

        //System.out.println("output["+i+"]: "+outputVals[j]);
      }

      lSpecies.setID(outputVals);
    }

    errorPanel.clear();

    double sumError;

    // Now we create the ArrayList of all Images that
    // will be shuffled later
    ArrayList imageList = new ArrayList();

    for(int j=0; j < leafSpecies.size(); j++)
    {
      LeafSpecies lSpecies = (LeafSpecies)leafSpecies.get(j);

      for(int k=0; k < lSpecies.numImages(); k++)
      {
        LeafImage lImage = (LeafImage)lSpecies.getImage(k);

        // set the Species of this image now!
        lImage.setSpecies(lSpecies);

        imageList.add(lImage);
      }
    }

    // steps for training
    for(int i=0; i < steps; i++)
    {
      sumError = 0.0;

      // now we have to shuffle the tokenList to get
      // a randomness in the training process
      Collections.shuffle(imageList);

      for(int j=0; j < imageList.size(); j++)
      {
        LeafImage lImage = (LeafImage)imageList.get(j);

        sumError += nNetwork.learnVector(lImage.getTokens(inputs), lImage.getSpecies().getID());
      }

      errorPanel.addError(sumError);
      netProgressBar.setValue(i+1);
      lrec.refresh();
    }
  }

  public void updateProjectEnv()
  {
    numLeafSpeciesField.setText(""+projectEnv.numLeafSpecies());
    numLeafImagesField.setText(""+projectEnv.numLeafImages());
    maxTokenField.setText(""+projectEnv.getMaxToken());

    BackProp nNetwork = projectEnv.getNetwork();
    if(nNetwork != null)
    {
      inputNeuronField.setText(""+nNetwork.numInput());
      hiddenNeuronField.setText(""+nNetwork.numHidden());
      outputNeuronField.setText(""+nNetwork.numOutput());
      learnRateField.setText(""+nNetwork.getAlpha());
      momentumField.setText(""+nNetwork.getMomentum());
    }

    if(projectEnv.getMaxToken() > 0)
    {
      trainButton.setEnabled(true);
    }
    else trainButton.setEnabled(false);

    if(projectEnv.getNetwork() != null)
    {
      statusField.setForeground(Color.blue);
      statusField.setText(getString("TRAINED"));
    }
    else
    {
      statusField.setForeground(Color.red);
      statusField.setText(getString("NOT_TRAINED_YET"));
    }
  }
}
