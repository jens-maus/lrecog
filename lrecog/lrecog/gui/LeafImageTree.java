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
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import lrecog.*;
import lrecog.gfx.*;

/**
* LeafImageTree class
*
* special class that extends the JTree class with special Drag&Drop
* features
*/
public class LeafImageTree extends JTree implements DragSourceListener,
                                                    DragGestureListener,
											                              Autoscroll
{
	public static final DataFlavor TREEPATH_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "TreePath");

	private TreePath		  sourcePath;				       // The path being dragged
	private BufferedImage	imgGhost;					       // The 'drag image'
	private Point			    ptOffset = new Point();  // Where, in the drag image, the mouse was clicked

  /**
  * constructor
  */
	public LeafImageTree(DefaultTreeModel treeModel)
	{
    // First we have to call the constructor of the super class with
    // the treeModel
    super(treeModel);
    
  	// I like this look
		putClientProperty("JTree.lineStyle", "Angled");
    setEditable(false);
    setShowsRootHandles(false);
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Make this JTree a drag source
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);

		// Also, make this JTree a drag target
		DropTarget dropTarget = new DropTarget(this, new OwnDropTargetListener());
		dropTarget.setDefaultActions(DnDConstants.ACTION_MOVE);
	}

  /**
  * Interface implementation for DragGestureListener
  */
	public void dragGestureRecognized(DragGestureEvent e)
	{
		Point ptDragOrigin = e.getDragOrigin();
		TreePath path = getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);

		if (path == null) return;

	  // Ignore user trying to drag the root node
		if (isRootPath(path)) return;

		// Work out the offset of the drag point from the TreePath bounding rectangle origin
		Rectangle raPath = getPathBounds(path);
		ptOffset.setLocation(ptDragOrigin.x-raPath.x, ptDragOrigin.y-raPath.y);
			
		// Get the cell renderer (which is a JLabel) for the path being dragged
		JLabel lbl = (JLabel) getCellRenderer().getTreeCellRendererComponent
								(
									this, 											  // tree
									path.getLastPathComponent(),	// value
									false,											  // isSelected	(dont want a colored background)
									isExpanded(path), 						// isExpanded
									getModel().isLeaf(path.getLastPathComponent()), // isLeaf
									0, 												    // row			(not important for rendering)
									false											    // hasFocus		(dont want a focus rectangle)
								);
		lbl.setSize((int)raPath.getWidth(), (int)raPath.getHeight()); // <-- The layout manager would normally do this

		// Get a buffered image of the selection for dragging a ghost image
		imgGhost = new BufferedImage((int)raPath.getWidth(), (int)raPath.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g2 = imgGhost.createGraphics();

		// Ask the cell renderer to paint itself into the BufferedImage
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));		// Make the image ghostlike
		lbl.paint(g2);

		// Now paint a gradient UNDER the ghosted JLabel text (but not under the icon if any)
		// Note: this will need tweaking if your icon is not positioned to the left of the text
		Icon icon = lbl.getIcon();
		int nStartOfText = (icon == null) ? 0 : icon.getIconWidth()+lbl.getIconTextGap();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.5f));	// Make the gradient ghostlike
		g2.setPaint(new GradientPaint(nStartOfText,	0, SystemColor.controlShadow, 
									  getWidth(),	0, new Color(255,255,255,0)));
		g2.fillRect(nStartOfText, 0, getWidth(), imgGhost.getHeight());

		g2.dispose();

		setSelectionPath(path);	// Select this path in the tree
		
		// Wrap the path being transferred into a Transferable object
		Transferable transferable = new TransferableTreePath(path);

		// Remember the path being dragged (because if it is being moved, we will have to delete it later)
		sourcePath = path;
		
		// We pass our drag image just in case it IS supported by the platform
		e.startDrag(null, imgGhost, new Point(5,5), transferable, this);
	}	
	
  /**
  * Interface implementation for DragSourceListener
  */
	public void dragEnter(DragSourceDragEvent e)
	{
	}	
	public void dragOver(DragSourceDragEvent e)
	{
	}	
	public void dragExit(DragSourceEvent e)
	{
	}	
	public void dropActionChanged(DragSourceDragEvent e)
	{
	}	
	public void dragDropEnd(DragSourceDropEvent e)
	{
		if (e.getDropSuccess())
		{
			int nAction = e.getDropAction();
			if (nAction == DnDConstants.ACTION_MOVE)
			{
				sourcePath = null;
			}
		}
	}	

  /**
  * This represents a TreePath (a node in a JTree) that can be transferred between a drag source and a drop target.
  */
  class TransferableTreePath implements Transferable
  {
	  // The type of DnD object being dragged...
	  //public final DataFlavor TREEPATH_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "TreePath");

	  private TreePath		_path;

	  private DataFlavor[]	_flavors = { TREEPATH_FLAVOR };

  	/**
	  * Constructs a transferrable tree path object for the specified path.
	  */
  	public TransferableTreePath(TreePath path)
	  {
		  _path = path;
	  }

	  // Transferable interface methods...
	  public DataFlavor[] getTransferDataFlavors()
	  {
		  return _flavors;
	  }

  	public boolean isDataFlavorSupported(DataFlavor flavor)
  	{
	  	return java.util.Arrays.asList(_flavors).contains(flavor);
	  }

  	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
	  {
		  if (flavor.isMimeTypeEqual(TREEPATH_FLAVOR.getMimeType())) // DataFlavor.javaJVMLocalObjectMimeType))
			  return _path;
		  else
			  throw new UnsupportedFlavorException(flavor);
	  }
  }

  /**
  * class DropTargetListener
  *
  * special inner class that implements the DropTargetListener interface
  */
	class OwnDropTargetListener implements DropTargetListener
	{
		// Fields...
		private TreePath		_pathLast		= null;
		private Rectangle2D 	_raCueLine		= new Rectangle2D.Float();
		private Rectangle2D 	_raGhost		= new Rectangle2D.Float();
		private Color			_colorCueLine;
		private Point			_ptLast			= new Point();
		private Timer			_timerHover;
		private int				_nLeftRight		= 0;	// Cumulative left/right mouse movement
		private int			 	_nShift			= 0;

		// Constructor...
		public OwnDropTargetListener()
		{
			_colorCueLine = new Color(
										SystemColor.controlShadow.getRed(),
										SystemColor.controlShadow.getGreen(),
										SystemColor.controlShadow.getBlue(),
										64
									  );

			// Set up a hover timer, so that a node will be automatically expanded or collapsed
			// if the user lingers on it for more than a short time
			_timerHover = new Timer(1000, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					_nLeftRight = 0;	// Reset left/right movement trend
					if (isRootPath(_pathLast))
						return;	// Do nothing if we are hovering over the root node
					if (isExpanded(_pathLast))
						collapsePath(_pathLast);
					else
						expandPath(_pathLast);
				}
			});
			_timerHover.setRepeats(false);	// Set timer to one-shot mode
		}
		
		// DropTargetListener interface
		public void dragEnter(DropTargetDragEvent e)
		{
			if (!isDragAcceptable(e))
				e.rejectDrag();
			else
				e.acceptDrag(e.getDropAction());	
		}
		
		public void dragExit(DropTargetEvent e)
		{
			if (!DragSource.isDragImageSupported())
			{
				repaint(_raGhost.getBounds());				
			}
		}

		/**
		* This is where the ghost image is drawn
		*/		
		public void dragOver(DropTargetDragEvent e)
		{
			// Even if the mouse is not moving, this method is still invoked 10 times per second
			Point pt = e.getLocation();
			if (pt.equals(_ptLast))
				return;
			
			// Try to determine whether the user is flicking the cursor right or left
			int nDeltaLeftRight = pt.x - _ptLast.x;
			if ( (_nLeftRight > 0 && nDeltaLeftRight < 0) || (_nLeftRight < 0 && nDeltaLeftRight > 0) )
				_nLeftRight = 0;
			_nLeftRight += nDeltaLeftRight;	


			_ptLast = pt;	
			

			Graphics2D g2 = (Graphics2D) getGraphics();

			// If a drag image is not supported by the platform, then draw my own drag image
			if (!DragSource.isDragImageSupported())
			{
				paintImmediately(_raGhost.getBounds());	// Rub out the last ghost image and cue line
				// And remember where we are about to draw the new ghost image
				_raGhost.setRect(pt.x - ptOffset.x, pt.y - ptOffset.y, imgGhost.getWidth(), imgGhost.getHeight());
				g2.drawImage(imgGhost, AffineTransform.getTranslateInstance(_raGhost.getX(), _raGhost.getY()), null);
			}
			else	// Just rub out the last cue line
				paintImmediately(_raCueLine.getBounds());				
			
			
			
			TreePath path = getClosestPathForLocation(pt.x, pt.y);
			if (!(path == _pathLast))			
			{
				_nLeftRight = 0; 	// We've moved up or down, so reset left/right movement trend
				_pathLast = path;
				_timerHover.restart();
			}

			// In any case draw (over the ghost image if necessary) a cue line indicating where a drop will occur
			Rectangle raPath = getPathBounds(path);
			_raCueLine.setRect(0,  raPath.y+(int)raPath.getHeight(), getWidth(), 2);

			g2.setColor(_colorCueLine);
			g2.fill(_raCueLine);
			
			// Now superimpose the left/right movement indicator if necessary
			if (_nLeftRight > 20)
			{
				//g2.drawImage(_imgRight, AffineTransform.getTranslateInstance(pt.x - _ptOffset.x, pt.y - _ptOffset.y), null);
				_nShift = +1;
			}
			else if (_nLeftRight < -20)
			{
				//g2.drawImage(_imgLeft, AffineTransform.getTranslateInstance(pt.x - _ptOffset.x, pt.y - _ptOffset.y), null);
				_nShift = -1;
			}
			else
				_nShift = 0;
			

			// And include the cue line in the area to be rubbed out next time
			_raGhost = _raGhost.createUnion(_raCueLine);	
		}
		
		public void dropActionChanged(DropTargetDragEvent e)
		{
			if (!isDragAcceptable(e))
				e.rejectDrag();
			else
				e.acceptDrag(e.getDropAction());	
		}
		
		public void drop(DropTargetDropEvent e)
		{
			_timerHover.stop();	// Prevent hover timer from doing an unwanted expandPath or collapsePath
			
			if (!isDropAcceptable(e))
			{
				e.rejectDrop();
				return;
			}
			
			e.acceptDrop(e.getDropAction());
			
			Transferable transferable = e.getTransferable();
			
			DataFlavor[] flavors = transferable.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++ )
			{
				DataFlavor flavor = flavors[i];
				if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType))
				{
					try
					{
						Point pt = e.getLocation();
						TreePath pathTarget = getClosestPathForLocation(pt.x, pt.y);
						TreePath pathSource = (TreePath) transferable.getTransferData(flavor);

						//System.out.println("DROPPING: "+pathSource.getLastPathComponent()+" ONTO: "+pathTarget.getLastPathComponent());
						DefaultTreeModel model = (DefaultTreeModel)getModel();
						TreePath pathNewChild = null;
						
            DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode)(pathSource.getLastPathComponent());
            DefaultMutableTreeNode destNode   = (DefaultMutableTreeNode)(pathTarget.getLastPathComponent());

            // if the destNode doesn't allow children we drop the
            // source on the parent of it
            if(destNode.getAllowsChildren() == false)
            {
              destNode = (DefaultMutableTreeNode)destNode.getParent();
            }

            if(sourceNode.getAllowsChildren() == true && destNode != sourceNode.getRoot())
            {
              destNode = (DefaultMutableTreeNode)destNode.getRoot();
            }

            // if the destNode again doesn't allow children or
            // the sourceNode allows children we skip this drop part
            if(destNode.getAllowsChildren() == true && sourceNode.getAllowsChildren() == false && destNode != destNode.getRoot())
            {
              LeafImage srcImage      = (LeafImage)sourceNode.getUserObject();
              LeafSpecies destSpecies = (LeafSpecies)destNode.getUserObject();

              if(sourceNode.getParent() != sourceNode.getRoot())
              {
                LeafSpecies srcSpecies  = (LeafSpecies)((DefaultMutableTreeNode)sourceNode.getParent()).getUserObject();
                srcSpecies.removeImage(srcImage);
              }

              destSpecies.addImage(srcImage);

              model.removeNodeFromParent(sourceNode);
              model.insertNodeInto(sourceNode, destNode, destNode.getChildCount());
              model.reload();

              // Make sure the user can see the lovely new node.
              setSelectionPath(new TreePath(sourceNode.getPath()));
            }

            break;
					}
					catch (UnsupportedFlavorException ufe)
					{
						System.out.println(ufe);
						e.dropComplete(false);
						return;
					}
					catch (IOException ioe)
					{
						System.out.println(ioe);
						e.dropComplete(false);
						return;
					}
				}
			}
			
			e.dropComplete(true);
		}
		
		
		
		// Helpers...
		public boolean isDragAcceptable(DropTargetDragEvent e)
		{
			// Only accept COPY or MOVE gestures (ie LINK is not supported)
			if ((e.getDropAction() & DnDConstants.ACTION_MOVE) == 0)
				return false;

			// Only accept this particular flavor	
			if (!e.isDataFlavorSupported(LeafImageTree.TREEPATH_FLAVOR))
				return false;
				
			return true;
		}

		public boolean isDropAcceptable(DropTargetDropEvent e)
		{
			// Only accept COPY or MOVE gestures (ie LINK is not supported)
			if ((e.getDropAction() & DnDConstants.ACTION_MOVE) == 0)
				return false;

			// Only accept this particular flavor	
			if (!e.isDataFlavorSupported(LeafImageTree.TREEPATH_FLAVOR))
				return false;

			return true;
		}
		

	}

// Autoscroll Interface...
// The following code was borrowed from the book:
//		Java Swing
//		By Robert Eckstein, Marc Loy & Dave Wood
//		Paperback - 1221 pages 1 Ed edition (September 1998) 
//		O'Reilly & Associates; ISBN: 156592455X 
//
// The relevant chapter of which can be found at:
//		http://www.oreilly.com/catalog/jswing/chapter/dnd.beta.pdf
	
	private static final int AUTOSCROLL_MARGIN = 12;
	// Ok, we’ve been told to scroll because the mouse cursor is in our
	// scroll zone.
	public void autoscroll(Point pt) 
	{
		// Figure out which row we’re on.
		int nRow = getRowForLocation(pt.x, pt.y);
		
		// If we are not on a row then ignore this autoscroll request
		if (nRow < 0)
			return;

		Rectangle raOuter = getBounds();
		// Now decide if the row is at the top of the screen or at the
		// bottom. We do this to make the previous row (or the next
		// row) visible as appropriate. If we’re at the absolute top or
		// bottom, just return the first or last row respectively.
		
		nRow =	(pt.y + raOuter.y <= AUTOSCROLL_MARGIN)			// Is row at top of screen? 
				 ?
				(nRow <= 0 ? 0 : nRow - 1)						// Yes, scroll up one row
				 :
				(nRow < getRowCount() - 1 ? nRow + 1 : nRow);	// No, scroll down one row

		scrollRowToVisible(nRow);
	}
	// Calculate the insets for the *JTREE*, not the viewport
	// the tree is in. This makes it a bit messy.
	public Insets getAutoscrollInsets()
	{
		Rectangle raOuter = getBounds();
		Rectangle raInner = getParent().getBounds();
		return new Insets(
			raInner.y - raOuter.y + AUTOSCROLL_MARGIN, raInner.x - raOuter.x + AUTOSCROLL_MARGIN,
			raOuter.height - raInner.height - raInner.y + raOuter.y + AUTOSCROLL_MARGIN,
			raOuter.width - raInner.width - raInner.x + raOuter.x + AUTOSCROLL_MARGIN);
	}

  // More helpers...
	private TreePath getChildPath(TreePath pathParent, int nChildIndex)
	{
		TreeModel model =  getModel();
		return pathParent.pathByAddingChild(model.getChild(pathParent.getLastPathComponent(), nChildIndex));
	}


	private boolean isRootPath(TreePath path)
	{
		return isRootVisible() && getRowForPath(path) == 0;
	}
}
