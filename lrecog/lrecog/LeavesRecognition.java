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

package lrecog;

import java.io.*;
import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;

import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

import lrecog.gui.*;
import lrecog.themes.*;
import lrecog.tools.*;

public class LeavesRecognition extends JPanel
{
  public static final double VERSION = 1.0;
  public static final String DATE = "27-August-2001";

  // the global project environment
  private ProjectEnv  projectEnv = null;

  // Used only if LRec is used as an applet
  private LeavesRecognitionApplet applet = null;

  // The ImageChooser for the Applet mode
  private ImageChooser imgChooser = null;

  // The preferred size of the demo
  private int PREFERRED_WIDTH = 640;
  private int PREFERRED_HEIGHT = 510;

  // Box spacers
  private Dimension HGAP = new Dimension(1,5);
  private Dimension VGAP = new Dimension(5,1);

  // Used only if swingset is an application
  private static JFrame frame = null;

  // Menus
  private JMenuBar menuBar = null;
  private JMenu themesMenu = null;
  private JMenuItem menuItemSave = null;
  private ButtonGroup lafMenuGroup = new ButtonGroup();
  private ButtonGroup themesMenuGroup = new ButtonGroup();

  // The tab pane that holds the different operations
  private JTabbedPane tabbedPane = null;

  private ImageProcPanel imgProcPanel;
  private NetworkPanel networkPanel;
  private RecognitionPanel recogPanel;

  // About Box
  private JDialog aboutBox = null;

  // Status Bar
  private JTextField statusField = null;

  // Possible Look & Feels
  private String mac      = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
  private String metal    = "javax.swing.plaf.metal.MetalLookAndFeel";
  private String motif    = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
  private String windows  = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

  // The current Look & Feel
  private String currentLookAndFeel = metal;

  // Resource bundle for internationalized and accessible text
  private ResourceBundle bundle = null;

  /**
  * Leaves Recognition Constructor
  */
  public LeavesRecognition(LeavesRecognitionApplet applet)
  {
	  // Note that the applet may null if this is started as an application
	  this.applet = applet;

	  frame = createFrame();

    // create a new Project Environment
    projectEnv = new ProjectEnv(this);

	  // setLayout(new BorderLayout());
	  setLayout(new BorderLayout());

	  // set the preferred size of the demo
	  setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

  	initialize();

    showLRec();
  }


  /**
  * Leaves Recognition MAIN
  * Allows to start this as a standalone application.
  */
  public static void main(String[] args)
  {
	  LeavesRecognition lrec = new LeavesRecognition(null);
  }

  /**
  * createImageChooser()
  *
  * method to create the Image Chooser
  * this method will be called from the applet, it can't be called
  * from the constructor itself
  */
  public void createImageChooser()
  {
    imgChooser = new ImageChooser(this);
  }

  /**
  * Create a frame for LRec to reside in if brought up
  * as an application.
  */
  public JFrame createFrame()
  {
	  JFrame frame = new JFrame();

    if(!isApplet()) frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    else  frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	  WindowListener l = new WindowAdapter()
    {
	    public void windowClosing(WindowEvent e)
      {
        exitApp();
      }
    };
	  frame.addWindowListener(l);
	  return frame;
  }

  /**
  * Creates an icon from an image contained in the "images" directory.
  */
  public ImageIcon createImageIcon(String filename, String description)
  {
    URL fileURL = getClass().getResource("/resources/images/"+filename);

    if(fileURL == null)
    {
      System.err.println("Warning: ImageIcon file ["+filename+"] not found.");
      return null;
    }

	  return new ImageIcon(fileURL, description);
  }

  /**
  * Determines if this is an applet or application
  */
  public boolean isApplet()
  {
	  return (applet != null);
  }

  /**
  * Returns the frame instance
  */
  public JFrame getFrame()
  {
	  return frame;
  }

  /**
  * Returns the Image Chooser instance
  */
  public ImageChooser getImageChooser()
  {
	  return imgChooser;
  }

  /**
  * Returns the Project Environment instance
  */
  public ProjectEnv getProjectEnv()
  {
	  return projectEnv;
  }

  /**
  * Returns the Image Processing Panel instance
  */
  public ImageProcPanel getImageProcPanel()
  {
	  return imgProcPanel;
  }

  /**
  * Returns the Network Panel instance
  */
  public NetworkPanel getNetworkPanel()
  {
	  return networkPanel;
  }

  /**
  * Returns the Network Panel instance
  */
  public RecognitionPanel getRecogPanel()
  {
	  return recogPanel;
  }

  /**
  * Bring up the SwingSet2 demo by showing the frame (only
  * applicable if coming up as an application, not an applet);
  */
  public void showLRec()
  {
    if(getFrame() != null)
    {
      // put swingset in a frame and show it
      JFrame f = getFrame();
      f.setTitle(getString("WIN_TITLE")+" v"+VERSION+" by Jens Langner");
      f.getContentPane().add(this, BorderLayout.CENTER);
      f.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      getFrame().setLocation(screenSize.width/2 - f.getSize().width/2, screenSize.height/2 - f.getSize().height/2);
      getFrame().show();
    }
  }

  /**
  * main init method that creates all necessary GUI components
  */
  public void initialize()
  {
	  JPanel top = new JPanel();
	  top.setLayout(new BorderLayout());
	  add(top, BorderLayout.NORTH);

	  menuBar = createMenus();
	  top.add(menuBar, BorderLayout.NORTH);

	  tabbedPane = new JTabbedPane();

    ImageIcon procIcon    = createImageIcon("icon_imgproc.gif", getString("TAB_IMGPROC_DESC"));
    ImageIcon networkIcon = createImageIcon("icon_network.gif", getString("TAB_NNETWORK_DESC"));
    ImageIcon recogIcon   = createImageIcon("icon_recog.gif",   getString("TAB_RECOG_DESC"));

    // now we add all the different Pane Panels to this main
    // pane.
    imgProcPanel  = new ImageProcPanel(this);
    networkPanel  = new NetworkPanel(this);
    recogPanel    = new RecognitionPanel(this);

    tabbedPane.addTab(getString("TAB_IMGPROC"),   procIcon,   imgProcPanel, getString("TAB_IMGPROC_DESC"));
    tabbedPane.addTab(getString("TAB_NNETWORK"),  networkIcon,networkPanel, getString("TAB_NNETWORK_DESC"));
    tabbedPane.addTab(getString("TAB_RECOG"),     recogIcon,  recogPanel,   getString("TAB_RECOG_DESC"));
    tabbedPane.setSelectedIndex(0);
    
	  add(tabbedPane, BorderLayout.CENTER);

	  statusField = new JTextField("");
	  statusField.setEditable(false);
	  add(statusField, BorderLayout.SOUTH);
  }

  /**
  * Create menus
  */
  public JMenuBar createMenus()
  {
	  JMenuItem mi;
	  // ***** create the menubar ****
	  JMenuBar menuBar = new JMenuBar();
	  menuBar.getAccessibleContext().setAccessibleName(getString("MN_MENU_DESC"));

	  // ***** create Project Menu
	  JMenu projectMenu = (JMenu)menuBar.add(new JMenu(getString("MN_PROJECT")));
    projectMenu.setMnemonic(getMnemonic("MN_PROJECT"));
	  projectMenu.getAccessibleContext().setAccessibleDescription(getString("MN_PROJECT_DESC"));
    createMenuItem(projectMenu, "MN_ABOUT", "MN_ABOUT", "MN_ABOUT_DESC", new AboutAction(this));
    projectMenu.addSeparator();

    mi = createMenuItem(projectMenu, "MN_OPEN", "MN_OPEN", "MN_OPEN_DESC", new OpenAction(this));
    if(isApplet()) mi.setEnabled(false);

    menuItemSave = createMenuItem(projectMenu, "MN_SAVE", "MN_SAVE", "MN_SAVE_DESC", new SaveAction(this));
    menuItemSave.setEnabled(false);

    mi = createMenuItem(projectMenu, "MN_SAVEAS", "MN_SAVEAS", "MN_SAVEAS_DESC", new SaveAsAction(this));
    if(isApplet()) mi.setEnabled(false);

    projectMenu.addSeparator();
    createMenuItem(projectMenu, "MN_EXIT", "MN_EXIT", "MN_EXIT_DESC", new ExitAction(this));


  	// ***** create laf switcher menu
	  JMenu lafMenu = (JMenu)menuBar.add(new JMenu(getString("MN_LAF")));
    lafMenu.setMnemonic(getMnemonic("MN_LAF"));
	  lafMenu.getAccessibleContext().setAccessibleDescription(getString("MN_LAF_DESC"));

	  mi = createLafMenuItem(lafMenu, "MN_LAF_JAVA", "MN_LAF_JAVA", "MN_LAF_JAVA_DESC", metal);
	  mi.setSelected(true); // this is the default l&f

	  createLafMenuItem(lafMenu, "MN_LAF_MAC",      "MN_LAF_MAC",     "MN_LAF_MAC_DESC",      mac);
	  createLafMenuItem(lafMenu, "MN_LAF_MOTIF",    "MN_LAF_MOTIF",   "MN_LAF_MOTIF_DESC",    motif);
	  createLafMenuItem(lafMenu, "MN_LAF_WINDOWS",  "MN_LAF_WINDOWS", "MN_LAF_WINDOWS_DESC",  windows);

	  // ***** create themes menu
	  themesMenu = (JMenu) menuBar.add(new JMenu(getString("MN_THEMES")));
    themesMenu.setMnemonic(getMnemonic("MN_THEMES"));
	  themesMenu.getAccessibleContext().setAccessibleDescription(getString("MN_THEMES_DESC"));

	  mi = createThemesMenuItem(themesMenu, "MN_THEME_DEFAULT", "MN_THEME_DEFAULT", "MN_THEME_DEFAULT_DESC", new DefaultMetalTheme());
	  mi.setSelected(true); // This is the default theme

	  createThemesMenuItem(themesMenu, "MN_THEME_AQUA",     "MN_THEME_AQUA",      "MN_THEME_AQUA_DESC",       new AquaTheme());
	  createThemesMenuItem(themesMenu, "MN_THEME_CHAROCOAL","MN_THEME_CHAROCOAL", "MN_THEME_CHAROCOAL_DESC",  new CharcoalTheme());
	  createThemesMenuItem(themesMenu, "MN_THEME_CONTRAST", "MN_THEME_CONTRAST",  "MN_THEME_CONTRAST_DESC",   new ContrastTheme());
	  createThemesMenuItem(themesMenu, "MN_THEME_EMERALD",  "MN_THEME_EMERALD",   "MN_THEME_EMERALD_DESC",    new EmeraldTheme());
	  createThemesMenuItem(themesMenu, "MN_THEME_RUBY",     "MN_THEME_RUBY",      "MN_THEME_RUBY_DESC",       new RubyTheme());

	  return menuBar;
  }

  /**
  * Creates a generic menu item
  */
  public JMenuItem createMenuItem(JMenu menu, String label, String mnemonic,
			                            String accessibleDescription, Action action)
  {
    JMenuItem mi = (JMenuItem) menu.add(new JMenuItem(getString(label)));
    
	  mi.setMnemonic(getMnemonic(mnemonic));
	  mi.getAccessibleContext().setAccessibleDescription(getString(accessibleDescription));
	  mi.addActionListener(action);
	  if(action == null)
    {
	    mi.setEnabled(false);
	  }

	  return mi;
  }

  /**
  * Creates a JRadioButtonMenuItem for the Themes menu
  */
  public JMenuItem createThemesMenuItem(JMenu menu, String label, String mnemonic,
	                           		        String accessibleDescription, DefaultMetalTheme theme)
  {
    JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menu.add(new JRadioButtonMenuItem(getString(label)));
	  themesMenuGroup.add(mi);
	  mi.setMnemonic(getMnemonic(mnemonic));
	  mi.getAccessibleContext().setAccessibleDescription(getString(accessibleDescription));
	  mi.addActionListener(new ChangeThemeAction(this, theme));

	  return mi;
  }

  /**
  * Creates a JRadioButtonMenuItem for the Look and Feel menu
  */
  public JMenuItem createLafMenuItem(JMenu menu, String label, String mnemonic,
	                                   String accessibleDescription, String laf)
  {
    JMenuItem mi = (JRadioButtonMenuItem) menu.add(new JRadioButtonMenuItem(getString(label)));
	  lafMenuGroup.add(mi);
	  mi.setMnemonic(getMnemonic(mnemonic));
	  mi.getAccessibleContext().setAccessibleDescription(getString(accessibleDescription));
	  mi.addActionListener(new ChangeLookAndFeelAction(this, laf));

	  mi.setEnabled(isAvailableLookAndFeel(laf));

	  return mi;
  }

  /**
  * setStatusField
  */
  public void setStatusField(String text)
  {
    statusField.setText(text);
  }

  /**
  * Stores the current L&F, and calls updateLookAndFeel, below
  */
  public void setLookAndFeel(String laf)
  {
	  if(currentLookAndFeel != laf)
    {
	    currentLookAndFeel = laf;
	    themesMenu.setEnabled(laf == metal);
	    updateLookAndFeel();
	  }
  }

  /**
  * special method to refresh the Window
  */
  public void refresh()
  {
    frame.update(frame.getGraphics());
  }

  /**
  * special method to update the other tabbes with data
  */
  public void updateProjectEnv()
  {
    imgProcPanel.updateProjectEnv();
    networkPanel.updateProjectEnv();
    recogPanel.updateProjectEnv();

    // activate SAVE Menuitem if needed
    if(!isApplet() && projectEnv.getFileName() != null)
    {
      menuItemSave.setEnabled(true);
    }
    else
    {
      menuItemSave.setEnabled(false);
    }
  }

  /**
  * special method to update the tree Panel with new date
  */
  public void updateTreePanel()
  {
    imgProcPanel.reloadTree();
  }

  /**
  * Sets the current L&F on each demo module
  */
  public void updateLookAndFeel()
  {
	  try
    {
	    UIManager.setLookAndFeel(currentLookAndFeel);
	    SwingUtilities.updateComponentTreeUI(this);
	  }
    catch (Exception ex)
    {
	    System.out.println("Failed loading L&F: " + currentLookAndFeel);
	    System.out.println(ex);
	  }
  }

  /**
  * isAvailableLookAndFeel
  *
  * special method that identifies which L&F is available at
  * this system
  */
  protected boolean isAvailableLookAndFeel(String laf)
  {
    try
    {
      Class lnfClass = Class.forName(laf);
      LookAndFeel newLAF = (LookAndFeel)(lnfClass.newInstance());
      return newLAF.isSupportedLookAndFeel();
    }
    catch(Exception e)
    {
      return false;
    }
  }

  /**
  * AboutAction class
  * special inner class
  */
  class AboutAction extends AbstractAction
  {
	  LeavesRecognition lrec;
    protected AboutAction(LeavesRecognition lrec)
    {
      super("AboutAction");
	    this.lrec = lrec;
    }

    public void actionPerformed(ActionEvent e)
    {
      ImageIcon icon = createImageIcon("icon_about.gif", "");

      String message =  getString("WIN_TITLE")+" v"+VERSION+"\n"+
                        "["+DATE+"]\n\n"+
			  			          "Copyright (c) 2001 by\n"+
                        "LightSpeed Communications GbR\n"+
                        "Lannerstrasse 1\n"+
                        "01219 Dresden - Germany\n"+
                        "http://www.light-speed.de/\n\n"+
                        "Author:\n"+
                        "Jens.Langner@light-speed.de";

      JOptionPane.showMessageDialog(null, message, getString("WIN_ABOUT")+" v"+VERSION, JOptionPane.INFORMATION_MESSAGE, icon);
	  }
  }

  /**
  * OpenAction class
  * special inner class to execute a action if the user
  * selected Open from the project menu
  */
  class OpenAction extends AbstractAction
  {
	  LeavesRecognition lrec;
    protected OpenAction(LeavesRecognition lrec)
    {
      super("OpenAction");
	    this.lrec = lrec;
    }

    public void actionPerformed(ActionEvent e)
    {
      // lets first check if there is already a opened
      // project that needs saving
      if(projectEnv.wasModified())
      {
        if(JOptionPane.showOptionDialog(  null,
                                          getString("WIN_OPEN_TEXT"),
                                          getString("WIN_OPEN_TITLE"),
                                          JOptionPane.YES_NO_OPTION,
                                          JOptionPane.QUESTION_MESSAGE,
                                          null, null, null) == 1)
        {
          return;
        }
      }

      // Create a file chooser
      JFileChooser fc = new JFileChooser();
      fc.addChoosableFileFilter(new XMLFilter());

      int returnVal = fc.showOpenDialog(lrec);

      if (returnVal == JFileChooser.APPROVE_OPTION)
      {
        File file = fc.getSelectedFile();

        setStatusField(getString("STATUS_LOAD_XML"));
        refresh();

        projectEnv.Open(file);

        // After opening the new prefsfile we have to
        // propage it to the other classes
        imgProcPanel.reloadTree();

        updateProjectEnv();

        setStatusField(getString("STATUS_FINISHED"));
      }
	  }
  }

  /**
  * Save class
  * special inner class to execute a action if the user
  * selected Save from the project menu
  */
  class SaveAction extends AbstractAction
  {
	  LeavesRecognition lrec;
    protected SaveAction(LeavesRecognition lrec)
    {
      super("SaveAction");
	    this.lrec = lrec;
    }

    public void actionPerformed(ActionEvent e)
    {
      File filename = projectEnv.getFileName();

      if(filename != null)
      {
        setStatusField(getString("STATUS_SAVE_XML"));
        refresh();
        projectEnv.Save(filename);

        updateProjectEnv();

        setStatusField(getString("STATUS_FINISHED"));
      }
	  }
  }

  /**
  * SaveAs class
  * special inner class to execute a action if the user
  * selected SaveAs from the project menu
  */
  class SaveAsAction extends AbstractAction
  {
	  LeavesRecognition lrec;
    protected SaveAsAction(LeavesRecognition lrec)
    {
      super("SaveAsAction");
	    this.lrec = lrec;
    }

    public void actionPerformed(ActionEvent e)
    {
      // Create a file chooser
      JFileChooser fc = new JFileChooser();
      fc.addChoosableFileFilter(new XMLFilter());

      int returnVal = fc.showSaveDialog(lrec);

      if (returnVal == JFileChooser.APPROVE_OPTION)
      {
        File file = fc.getSelectedFile();

        setStatusField(getString("STATUS_SAVE_XML"));
        refresh();
        projectEnv.Save(file);
        setStatusField(getString("STATUS_FINISHED"));
      }
	  }
  }

  /**
  * ExitAction class
  * special inner class
  */
  class ExitAction extends AbstractAction
  {
	  LeavesRecognition lrec;

    protected ExitAction(LeavesRecognition lrec)
    {
      super("ExitAction");
	    this.lrec = lrec;
    }

    public void actionPerformed(ActionEvent e)
    {
      exitApp();
    }
  }

  /**
  * OkAction class
  * special inner class
  */
  class OkAction extends AbstractAction
  {
	  JDialog aboutBox;

    protected OkAction(JDialog aboutBox)
    {
      super("OkAction");
	    this.aboutBox = aboutBox;
    }

    public void actionPerformed(ActionEvent e)
    {
	    aboutBox.setVisible(false);
	  }
  }

  /**
  * ChangeLookAndFeelAction
  * special inner class
  */
  class ChangeLookAndFeelAction extends AbstractAction
  {
	  LeavesRecognition lrec;
	  String laf;

    protected ChangeLookAndFeelAction(LeavesRecognition lrec, String laf)
    {
      super("ChangeLaF");
	    this.lrec = lrec;
	    this.laf = laf;
    }

    public void actionPerformed(ActionEvent e)
    {
	    lrec.setLookAndFeel(laf);
	  }
  }

  /**
  * ChangeThemeAction
  * special inner class
  */
  class ChangeThemeAction extends AbstractAction
  {
	  LeavesRecognition lrec;
	  DefaultMetalTheme theme;

    protected ChangeThemeAction(LeavesRecognition lrec, DefaultMetalTheme theme)
    {
      super("ChangeTheme");
	    this.lrec   = lrec;
	    this.theme  = theme;
    }

    public void actionPerformed(ActionEvent e)
    {
	    MetalLookAndFeel.setCurrentTheme(theme);
	    lrec.updateLookAndFeel();
	  }
  }

  /**
  * This method returns a string from the lrecog resource bundle.
  */
  public String getString(String key)
  {
	  String value = null;
	  try
    {
	    value = getResourceBundle().getString(key);
	  }
    catch(MissingResourceException e)
    {
	    System.err.println("java.util.MissingResourceException: Couldn't find value for: " + key);
	  }

	  if(value == null)
    {
	    value = "Could not find resource: " + key + "  ";
	  }
	  return value;
  }

  /**
  * Returns the resource bundle associated with this demo. Used
  * to get accessable and internationalized strings.
  */
  public ResourceBundle getResourceBundle()
  {
	  if(bundle == null)
    {
	    bundle = ResourceBundle.getBundle("resources.lrecog");
	  }

	  return bundle;
  }

  /**
  * Returns a mnemonic from the resource bundle. Typically used as
  * keyboard shortcuts in menu items.
  */
  public char getMnemonic(String key)
  {
	  return (getString(key)).charAt(0);
  }

  /**
  * exitApp()
  *
  * method to check the actual status of the project and pop up
  * a requester if it wasn't saved yet !
  */
  public void exitApp()
  {
    if(!isApplet())
    {
      if(projectEnv.wasModified())
      {
        if(JOptionPane.showOptionDialog(  null,
                                          getString("WIN_EXIT_TEXT"),
                                          getString("WIN_EXIT_TITLE"),
                                          JOptionPane.YES_NO_OPTION,
                                          JOptionPane.QUESTION_MESSAGE,
                                          null, null, null) == 1)
        {
          return;
        }
      }

      System.exit(0);
    }
    else
    {
      frame.hide();
      frame.dispose();
    }
  }
}
