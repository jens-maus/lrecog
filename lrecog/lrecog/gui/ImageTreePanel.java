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
import javax.swing.border.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.image.*;
import java.io.*;

import lrecog.*;
import lrecog.gfx.*;

public class ImageTreePanel extends DefaultPanel
{
  private DefaultMutableTreeNode rootNode;
  private DefaultTreeModel treeModel;
  private LeafImageTree tree;
  private Toolkit toolkit = Toolkit.getDefaultToolkit();

 	public ImageTreePanel(LeavesRecognition lrec)
  {
    super(lrec);

	  setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0), getString("IMGTREE_TITLE_INFO"), TitledBorder.LEFT, TitledBorder.TOP));

    rootNode = new DefaultMutableTreeNode(getString("IMGTREE_LB_ROOTNODE"));
    treeModel = new DefaultTreeModel(rootNode);
    // define that the model should ask if a node allows childs or not.
    treeModel.setAsksAllowsChildren(true);

    // Now we create the JTree
    tree = new LeafImageTree(treeModel);

    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    ImageIcon leafIcon = createImageIcon("icon_leaf.gif", getString("IMGTREE_LEAFIMG_DESC"));
    if(leafIcon != null) renderer.setLeafIcon(leafIcon);
    tree.setCellRenderer(renderer);

    //Listen for when the selection changes.
    tree.addTreeSelectionListener(new TreeSelectionListener()
    {
      public void valueChanged(TreeSelectionEvent e)
      {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        if(node == null) return;

        Object nodeInfo = node.getUserObject();

        // if AllowsChildren is false it is a real leaf image
        if(node.getAllowsChildren() == false)
        {
          LeafImage limage = (LeafImage)nodeInfo;
          getImageProcPanel().setActualImage(limage);
        }
      }
    });


    JScrollPane scrollPane = new JScrollPane(tree);
    setLayout(new GridLayout(1,0));
    add(scrollPane);
	}

  public ImageProcPanel getImageProcPanel()
  {
    return lrec.getImageProcPanel();
  }

  /** Remove all nodes except the root node. */
  public void clear()
  {
    rootNode.removeAllChildren();
    treeModel.reload();
  }

  /**
  * renameCurrentNode()
  *
  * method to rename the current selected node
  */
  public void renameCurrentNode()
  {
    TreePath currentSelection = tree.getSelectionPath();
    if(currentSelection != null)
    {
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());

      // Only Species are allowed to be edited
      if(currentNode.getAllowsChildren() == true && currentNode.getParent() == rootNode)
      {
        LeafSpecies lSpecies = (LeafSpecies)currentNode.getUserObject();

        String newName = (String)JOptionPane.showInputDialog(null, getString("WIN_RENAME_ENTER"), getString("WIN_RENAME_TITLE"), JOptionPane.QUESTION_MESSAGE, null, null, lSpecies.getName());

        if(newName != null)
        {
          lSpecies.setName(newName);
          update(getGraphics());
          return;
        }
        else return;
      }
    }

    // Either there was no selection, or the root was selected.
    toolkit.beep();
  }

  /** Remove the currently selected node. */
  public LeafSpecies removeCurrentNode()
  {
    TreePath currentSelection = tree.getSelectionPath();
    if(currentSelection != null)
    {
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
      DefaultMutableTreeNode parent = (DefaultMutableTreeNode)(currentNode.getParent());
      if(parent != null)
      {
        if(parent != rootNode)
        {
          LeafSpecies lSpecies = (LeafSpecies)parent.getUserObject();
          LeafImage lImage = (LeafImage)currentNode.getUserObject();

          lSpecies.removeImage(lImage);

          treeModel.removeNodeFromParent(currentNode);
          return null;
        }
        else
        {
          treeModel.removeNodeFromParent(currentNode);
          return (LeafSpecies)currentNode.getUserObject();
        }
      }
    }

    // Either there was no selection, or the root was selected.
    toolkit.beep();

    return null;
  }

  /**
  * addChild()
  *
  * method to add a object to the JTree
  */
  public Object addChild(Object child)
  {
    DefaultMutableTreeNode parentNode = null;

    // lets see if something is selected and if so we add this node
    // to the selected one.
    TreePath parentPath = tree.getSelectionPath();

    if(parentPath == null)
    {
      // nothing is selected, so we add it to the rootNode
      parentNode = rootNode;
    }
    else
    {
      // ok, something is selected, lets now check if this Node allows
      // children
      parentNode = (DefaultMutableTreeNode)(parentPath.getLastPathComponent());

      if(parentNode.getAllowsChildren() == false)
      {
        // if this node doesn't allow a child get it's parent
        parentNode = (DefaultMutableTreeNode)parentNode.getParent();
      }
    }

    // then we add this object to the parentNode
    return addChild(parentNode, child);
  }

  /**
  * addChild()
  *
  * overloaded method that allows to directly add a child to a
  * parentNode
  */
  public Object addChild(DefaultMutableTreeNode parent, Object child)
  {
    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
    childNode.setAllowsChildren(false);

    // to be save we check if parent was null and if so we add
    // the child to the root node.
    if(parent == null)
    {
      parent = rootNode;
    }

    treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
    treeModel.reload();

    // Make sure the user can see the lovely new node.
    tree.scrollPathToVisible(new TreePath(childNode.getPath()));

    if(parent != rootNode) return parent.getUserObject();
    else return null;
  }

  /**
  * addSpecies()
  *
  * method that adds a new Species to the tree.
  * Here we only allow to add a species to the rootNode
  */
  public DefaultMutableTreeNode addSpecies(Object species)
  {
    DefaultMutableTreeNode speciesNode = new DefaultMutableTreeNode(species);
    speciesNode.setAllowsChildren(true);

    treeModel.insertNodeInto(speciesNode, rootNode, rootNode.getChildCount());
    treeModel.reload();

    // Make sure the user can see the lovely new node.
    tree.scrollPathToVisible(new TreePath(speciesNode.getPath()));

    return speciesNode;
  }

  /**
  * getParentOfCurrent()
  *
  * method to return the parent treenode of the current selected node
  */
  public Object getParentOfCurrent()
  {
    TreePath currentSelection = tree.getSelectionPath();
    if(currentSelection != null)
    {
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());

      if(currentNode.getParent() != currentNode.getRoot())
      {
        return ((DefaultMutableTreeNode)currentNode.getParent()).getUserObject();
      }
    }

    return null;
  }
}
