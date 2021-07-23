import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
// import javax.swing.BoxLayout; //_boxL
import javax.swing.Box;
import java.awt.BorderLayout;  //_BL
import java.awt.FlowLayout; //_FL
import java.awt.GridBagLayout; //_GbL
import java.awt.GridBagConstraints;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.KeyStroke;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.Timer;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Container;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.Toolkit;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.Event;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import java.io.*;
import java.io.IOException;
// import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileReader;
// import java.util.*;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/* run from command line (JDK 11) with: java MenuSwing.java

user customization via ui.properties file
BFrame: MenuBar with mnuLabel, SplitPane split(gameMainPanel, rightSidePanel), gameSouthPanel

Hotkeys:
F11: toggle fullscreen Mode (maximised undecorated window (removes titlebar decoration))
CTRL+F11: Iconify (when you de-iconify re-apply fullscreen)
ALT+F4:   Close Window (X)
CTRL+Alt (on key release): Toggle menuBar
	! when menuBar is not visible, menu commands are not available
Alt (on key release): Show menuBar 
F10: Open First Menu
Alt + _Mnemonic: Menu Commands 
CTRL+X or click flag/round label: Toggle sidebar: user-customizable hotkey in ui.properties
*/

class BFrame extends JFrame {
	private static final String version = "0.1";
	private static final Dimension defaultFrameSize = new Dimension(1280, 720);
	public boolean fullscreenMode; //default: false
	// final Action fullscreenToggleAction = null;
	private BMenuBar menuBar;
    final JLabel status = new JLabel("statusLabel"); //default: JLabel.LEFT alignment
	final Timer statusTimer;
	
	final JPanel rightSidePanel = new JPanel();
	final JSplitPane split; //was gameCenterPanel
	final JButton btnExpandLeft, btnExpand;
	int dividerSize;
	final UiProperties ui;

	public static void main(final String args[]) { //throws IOException 
		UiProperties ui = new UiProperties();
		System.setProperty("sun.java2d.uiScale", ui.dpiScale);
		SwingUtilities.invokeLater( () -> {
		  try {
		    // UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");				  
		    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		    // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");			  
		    // UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel");
			// UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
			// UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceTwilightLookAndFeel");
			// UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceNightShadeLookAndFeel");				
          } catch (Exception e) { pn(e); pn("LookAndFeel failed to initialize");
        } 
		if (ui.defaultFont !=null) { //Nimbus: new Font("SansSerif", Font.PLAIN, 15)
			pn(ui.defaultFont);		
			UIManager.getLookAndFeelDefaults().put("defaultFont", ui.defaultFont); 
		}
		new BFrame(ui);
	  });
	}    

  public BFrame(UiProperties ui) {
    super();
	this.ui = ui;
	this.setTitle("TripleA Frame (Mockup v"+ version +") - "+ ui.gameName);
	this.setIconImage(ui.appIcon.getImage());
	this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	fullscreenMode = ui.fullscreenMode;
	this.setUndecorated(fullscreenMode); 
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.addWindowListener( new WindowAdapter() {
		public void windowDeiconified(WindowEvent e) { 
			if (fullscreenMode) { BFrame.this.setExtendedState(JFrame.MAXIMIZED_BOTH);}
		}
	});	
	
	final JPanel gameMainPanel = new JPanel( new FlowLayout());
	gameMainPanel.setBackground(Color.darkGray);
	JLabel gameMainLabel = new JLabel(" <Alt+F4> to Quit ");
	gameMainLabel.setBackground(Color.pink); gameMainLabel.setOpaque(true);
	gameMainPanel.add(gameMainLabel); 
	rightSidePanel.setToolTipText("rightSidePanel");
	rightSidePanel.setOpaque(false);
    rightSidePanel.setPreferredSize(new Dimension(240, 720));
	split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gameMainPanel, rightSidePanel);
	split.setOneTouchExpandable(true);
	dividerSize = split.getDividerSize();
	pn("split.getDividerSize()" + dividerSize);
	split.setResizeWeight(1.0);
	split.resetToPreferredSizes();	

	final JPanel gameSouthPanel = new JPanel( new BorderLayout());
	final JPanel resourceBar = new JPanel( new GridBagLayout());
    final JLabel puLabel = new JLabel("39 (+29)"); 
	puLabel.setIcon(ui.puIcon);
	puLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
	resourceBar.add(puLabel);
	
	final JPanel territoryInfo = new JPanel( new GridBagLayout()); //TerritoryBar
    territoryInfo.setPreferredSize(new Dimension(0, 0));
    final JLabel territoryLabel = new JLabel(ui.territoryText);//+" "//was message!
	pn("ui.territoryText: "+ ui.territoryText);
	// territoryLabel.setFont(
        // territoryLabel.getFont().deriveFont(Map.of(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD)));
    territoryLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
	
    territoryInfo.add( territoryLabel, new GridBagConstraints( 0, 0,
        1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    final JLabel resourceLabel = new JLabel("2");
	resourceLabel.setIcon(ui.puIcon);
	resourceLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    territoryInfo.add( resourceLabel, new GridBagConstraints( 1, 0,
        1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    //bu: GridBagConstraint( ?

    final JPanel bottomMessagePanel = new JPanel( new GridBagLayout());
    bottomMessagePanel.setBorder(BorderFactory.createEmptyBorder());
    bottomMessagePanel.add( resourceBar,   new GridBagConstraints( 0, 0,
		1, 1, 0, 1, GridBagConstraints.WEST,   GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    bottomMessagePanel.add( territoryInfo, new GridBagConstraints( 1, 0,
		1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));			
    bottomMessagePanel.add( status,        new GridBagConstraints( 2, 0,
        1, 1, 1, 1, GridBagConstraints.EAST,   GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    status.setPreferredSize(new Dimension(0, 0));
	status.setText(ui.statusText); //"100% Map Zoom"
	status.setHorizontalAlignment(SwingConstants.RIGHT); 
	/*clear initial status text after 5s (delay_ms=5000) */
	statusTimer = new Timer(ui.statusTimer_ms, e -> status.setText("")); 
	statusTimer.setRepeats(false); //statusTimer
	statusTimer.start();
	
    final JLabel player = new JLabel(ui.playerText);
    final JLabel step   = new JLabel(ui.stepText); //"Combat Move "
    step.setHorizontalTextPosition(SwingConstants.LEADING);
	final JLabel round  = new JLabel(ui.roundText); //"Round:1"
	round.setIcon(ui.playerIcon); 
	round.addMouseListener( new MouseAdapter() {
		@Override  public void mouseClicked(MouseEvent e) { //any mouse button
			toggleSidePanel();			
        }
    });	

    final JPanel stepPanel = new JPanel();
    stepPanel.setLayout(new GridBagLayout());
    int count = 0;
	stepPanel.add(player, gridBagConstraint(count++));
    stepPanel.add(step,   gridBagConstraint(count++));
    stepPanel.add(round,  gridBagConstraint(count++));

	final Border raisedBorder = new EtchedBorder(EtchedBorder.RAISED);
	JComponent[] eltArray = {resourceBar, territoryInfo, player, step, round}; //stepPanel, 
	for (JComponent elt: eltArray) {
		elt.setBorder(raisedBorder);
	}  

	status.setBorder(
		BorderFactory.createCompoundBorder(raisedBorder, BorderFactory.createEmptyBorder(0, 10, 0, 5)));
    gameSouthPanel.add(bottomMessagePanel, BorderLayout.CENTER);
    gameSouthPanel.add(stepPanel, 		   BorderLayout.EAST);

	Container contentPane = this.getContentPane();
	contentPane.setBackground(Color.RED);
	contentPane.add(split,          BorderLayout.CENTER);
	contentPane.add(gameSouthPanel, BorderLayout.SOUTH);
    SwingUtilities.invokeLater(() -> {
		menuBar = new BMenuBar(this, ui);
		this.setJMenuBar(menuBar);
	});
	addKeyBinding(this.getRootPane(), "TOGGLE_MENUBAR", HotKey.TOGGLE_MENUBAR, e -> toggleMenuBar());
	
	setSize(defaultFrameSize); // frame.pack();
	BasicSplitPaneUI splitUI = (BasicSplitPaneUI) split.getUI();
	btnExpandLeft = (JButton) splitUI.getDivider().getComponent(0);
	btnExpand     = (JButton) splitUI.getDivider().getComponent(1);
	this.setLocationByPlatform(true);
	this.setVisible(true);
	// pn(split.getDividerLocation());
  }	  

	public void iconify() { // maximized frame 
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setExtendedState(JFrame.ICONIFIED | getExtendedState());
	}

	public void toggleSidePanel() { 
	  SwingUtilities.invokeLater( () -> {
		pn(split.getDividerLocation() +" "+ split.getLastDividerLocation()); 
		//DividerLocation is not initially known (-1) 
		if (split.getDividerSize() !=0){   
			// rightSidePanel.setMinimumSize(new Dimension());
			split.setRightComponent(null);
			split.setDividerSize(0); btnExpand.doClick();
			this.setInfoText("<"+ HotKey.keyStrokeText(ui.TOGGLE_SIDEPANEL) +"> toggles Right SidePanel");			
		} else {
			pn(split.getDividerLocation() +" "+ split.getLastDividerLocation());
			split.setRightComponent(rightSidePanel);
			btnExpandLeft.doClick(); 
			split.setDividerSize(dividerSize); 			
		}
	  });
	}

	public void resetSplit() {
		this.setInfoText("resetSplit()");			
		split.setRightComponent(rightSidePanel);
		split.setDividerSize(dividerSize);
		split.resetToPreferredSizes(); 
	}

	public void toggleMenuBar() {
		SwingUtilities.invokeLater( () -> {
			this.setInfoText("<Ctrl+Alt released> toggles MenuBar");
			if (menuBar.isVisible()) { menuBar.setVisible(false); }	
			// this.setJMenuBar(null);	// pack();
			else { 			
				menuBar.mnuLabel.setText("");
				menuBar.setVisible(true);		
			}
		});
	}

	public void toggleFullscreen() { 
		setInfoText("<F11> toggles Fullscreen Mode");
		fullscreenMode = !fullscreenMode;
		// pn("toggleFullscreen() "+ fullscreenMode);
		this.dispose();
		this.setUndecorated(fullscreenMode);
		if (!fullscreenMode) { setSize(defaultFrameSize); }
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		this.setVisible(true);
		this.repaint();
    }
	
	public void setInfoText(String s) {
		status.setText(s);
		status.setHorizontalAlignment(JLabel.LEFT);
		statusTimer.restart(); //if (timer.isRunning())
		menuBar.mnuLabel.setText(s);
	}

  public static final void addKeyBinding(JComponent c, String key, Action action) {
    c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
    c.getActionMap().put(key, action);
    c.setFocusable(true);
  }
  public static final void addKeyBinding(JComponent c, String key, KeyStroke keyStroke, Action action) {
    c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, key);
    c.getActionMap().put(key, action);
    c.setFocusable(true);
  }
  public static final void addKeyBinding(JComponent c, String name, KeyStroke keyStroke, ActionListener lambda) {
    c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, name);
    c.getActionMap().put(name, SwingAction.of(name, lambda));
    c.setFocusable(true);
  }
  public static void pn(Object obj) {	System.out.println(obj); }

  private static GridBagConstraints gridBagConstraint(final int columnNumber) {
	  return new GridBagConstraints( columnNumber, 0,
		  1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
  }
  
}


/* returns a swing Action object from a lambda/
Action myAction = 
SwingAction.of("actionName", e -> { myMethod() });
SwingAction.of("actionName", hotKey, e -> { myMethod() });
// mnuHelp.add(SwingAction.of("About...", e -> new AboutTriplea())).setMnemonic('A'); 
*/
final class SwingAction {
	public static Action of(final String name, final ActionListener swingAction) {
		return new AbstractAction(name) {
			@Override  public void actionPerformed(final ActionEvent e) {swingAction.actionPerformed(e); }
		};
	}

	public static Action of(final String name, final KeyStroke hotKey, final ActionListener swingAction) {
		Action action = of(name, swingAction); // of(name, e -> runner.run())
		action.putValue(Action.ACCELERATOR_KEY, hotKey);
		return action;
	}

	public static Action of(final String name, final Runnable runner) {
		return of(name, e -> runner.run());
	}
}

/** Loads the configuration file "ui.properties" and sets ui fields. 
- handles default values if a property is not found (null) or have invalid values.
- property key should be unique, otherwise uses last key
- properties are String, ! leading spaces get trimmed
- parses the value String and provides a ready to use object, ex: 
	- String iconName >> ImageIcon icon
	- String hotkey   >> KeyStroke
*/

class UiProperties {
	Properties props;
	String gameName; //"World War II v5 1942c 2nd Ed";
	String mnuLabelText;
	final String dpiScale;
	Font defaultFont = null;
	final String appIconName, playerIconName, puIconName, aboutIconName;
	final ImageIcon appIcon, playerIcon, puIcon, aboutIcon;
	String territoryText, statusText, playerText, stepText, roundText;
	public KeyStroke TOGGLE_SIDEPANEL;
	public boolean fullscreenMode;
	final int statusTimer_ms;

   	public Properties getUiProperties() throws IOException {
		Properties properties = new Properties();
		try {
			FileReader reader = new FileReader("ui.properties");
			properties.load(reader);
			pn("getUiProperties(): loaded properties from ui.properties file"); 
		} catch(IOException ex) {	
			InputStream inputStream = getClass().getResourceAsStream("/ui.defaults"); 
			// inputstream returns null if file not found. 
			// The problem was getting ui.properties to load from the dir outside the .jar
			BufferedReader reader0 = new BufferedReader(new InputStreamReader(inputStream));
			properties.load(reader0);
			pn("getUiProperties(): ui.properties file not found, loaded default properties from ui.defaults"); 
		}
		return properties;
	}	
   	public static void pn(Object obj) {	System.out.println(obj); }

    UiProperties() {
	  try { this.props = getUiProperties();
	  } catch(IOException ex) {System.out.println(ex); }	
	
	// ui = new HashMap<String, String>();
	// for (String key: props.stringPropertyNames()) {
		// ui.put(key, props.getProperty(key));
	// }
	// s == null || s.isBlank(); //Check if String s is null or empty

	dpiScale = props.getProperty("dpiScale", "1.0");
	String fontStr = props.getProperty("defaultFont"); //ex: "Arial-15", "Segoe UI" 
	if (fontStr !=null) {
		pn("fontStr: "+ fontStr);
		defaultFont = Font.decode(fontStr);
	}
	appIconName    = props.getProperty("appIcon", "B-32.png");
	playerIconName = props.getProperty("playerIcon", "French-32.png");
	puIconName     = props.getProperty("puIcon", "PUs-20.png");
	aboutIconName  = props.getProperty("aboutIcon", "triplea_icon_128_128.png");
	// appIcon    = new ImageIcon(appIconName);
	appIcon = new ImageIcon(
		Toolkit.getDefaultToolkit().getImage(getClass().getResource(appIconName)));	
	playerIcon = new ImageIcon(playerIconName);
	puIcon     = new ImageIcon(puIconName);
	aboutIcon  = new ImageIcon(aboutIconName);
	
	gameName   = props.getProperty("gameName",   "gameName");
	territoryText = props.getProperty("territoryText", "Southern France");
	playerText = props.getProperty("playerText", "French"); //FR
	stepText   = props.getProperty("stepText",   "Non-Combat Move");    
	roundText  = props.getProperty("roundText",  "Round:1");
	statusText = props.getProperty("statusText", "Map Zoom: 100%");
	mnuLabelText = props.getProperty("mnuLabelText", "mnuLabelText");

	/*Parse Values*/
	fullscreenMode = Boolean.parseBoolean(props.getProperty("fullscreenMode", "false"));
	statusTimer_ms = Integer.parseInt(props.getProperty("statusTimer_ms", "5000")); 

	/* User configurable hotkeys (!req correct hotkey String syntax) in ui.properties
	default hotkeys are defined in HotKey class (with KeyStroke.getKeyStroke overloaded method)
	todo: create a table of all possible hotkeys, mapping String hotkeyName to KeyStroke
	*/ 
	TOGGLE_SIDEPANEL = getHotKey("TOGGLE_SIDEPANEL", HotKey.TOGGLE_SIDEPANEL);

	if (!fullscreenMode) pn("fullscreenMode=false"); 
	pn("TOGGLE_SIDEPANEL: "+ TOGGLE_SIDEPANEL);
  }	  
  private KeyStroke getHotKey(String hotkeyName, KeyStroke defaultHotKey) {
	String s = props.getProperty(hotkeyName);
	if (s ==null) return defaultHotKey;
	pn(hotkeyName +" hotKey: "+ s);
	pn(HotKey.keyStrokeText(KeyStroke.getKeyStroke(s)));
	return KeyStroke.getKeyStroke(s); 	
  }	
}

  
class BMenuBar extends JMenuBar {
	public final JLabel mnuLabel = new JLabel("", JLabel.RIGHT);

	/* for easy menu modification: code should match displayed menu  
	add(mni = new Menuitem("Save as...", 'S')); //text, mnemonic
	hotkey, selected/enabled, listener
	vs void addSaveAs();
	vs Actions
	listener/actions can be in external class

	Custom constructor class with mnemonic, 
	ex: JMenu mnu = new Menu("View", 'V');
	*/
	class Menu extends JMenu { 
	  // class JMenu extends javax.swing.JMenu {
		Menu(String title, char mnemo) { 
			super(title);
			this.setMnemonic(mnemo);
		}
	}
	
	class ChBoxMenuItem extends JCheckBoxMenuItem {
	  // 	todo? mni custom constructor with hotkey OR @Override mnu.add(mni, Keystroke) 
		ChBoxMenuItem(String title, char mnemo) {
			super(title);
			this.setMnemonic(mnemo);
		}	
		ChBoxMenuItem(String title, char mnemo, KeyStroke keyStroke) {
			this(title, mnemo);
			this.setAccelerator(keyStroke);
		}	
	}
   	public static void pn(Object obj) {	System.out.println(obj); }
	
	BMenuBar(BFrame bframe, UiProperties ui) { //"_File" "Window" "View" "Game" "Export" "Tools" "Help" 
	  super();	
	  final JMenu mnuFile = new Menu("File", 'F'); 
	  final JMenuItem mniSave, mniLeaveGame, mniExit; 
	  mnuFile.add(mniSave = new JMenuItem("Save...", 'S')).setAccelerator(HotKey.SAVE);
      mnuFile.addSeparator();	//---------------------
	  mnuFile.add(mniLeaveGame = new JMenuItem("> Leave Game", 'L')).setAccelerator(HotKey.LEAVE_GAME);
      mnuFile.add(mniExit = new JMenuItem("Exit Program", 'X')).setAccelerator(HotKey.EXIT);
	  mniExit.addActionListener(e -> System.exit(0));
	  
	  final JMenu mnuWindow = new Menu("Window", 'W');
  	  final JCheckBoxMenuItem mnxShowCommentLog, mnxFullscreen, mnxToggleSidePanel;
	  final JMenuItem mniResetSidePanel, mniIconify, mniToggleMenuBar; 
	  // mnuGame.add(bframe.fullscreenToggleAction).setMnemonic('F');
	  mnxFullscreen      = new ChBoxMenuItem("Fullscreen [  ]", 'F', HotKey.TOGGLE_FULLSCREEN);
	  mnxToggleSidePanel = new ChBoxMenuItem("SidePanel < >",   'S', ui.TOGGLE_SIDEPANEL);
	  mnuWindow.add(mnxFullscreen).setSelected(bframe.fullscreenMode);
	  mnuWindow.add(mniIconify = new JMenuItem("IconifyMax", 'I')).setAccelerator(HotKey.ICONIFY);
	  mnuWindow.add(mniToggleMenuBar   = new JMenuItem("MenuBar")).setAccelerator(HotKey.TOGGLE_MENUBAR);
	  mnuWindow.add(mnxToggleSidePanel).setSelected(true);
	  mnuWindow.add(mniResetSidePanel  = new JMenuItem("Reset SidePanel", 'R'));
      mnuWindow.addSeparator();	//---------------------
	  mnuWindow.add(mnxShowCommentLog = new ChBoxMenuItem("Comment Log", 'L')); 
	  mnxFullscreen.addActionListener(e -> bframe.toggleFullscreen() );  
	  mnxToggleSidePanel.addActionListener(e -> bframe.toggleSidePanel() );
	  mniResetSidePanel.addActionListener(e -> bframe.resetSplit() );
	  mniIconify.addActionListener(e -> bframe.iconify() ); 

	  final JMenu mnuView = new Menu("View", 'V'); //Map Display Options
	  final JMenuItem mniMapZoom;
	  final JCheckBoxMenuItem mnxLockMap, mnxShowUnits, mnxShowMapDetails, mnxShowMapTooltips, mnxShowMapBlends, mnxTerritoryEffects; 
	  final JMenu mnsUnitSize, mnsFlagDisplay, mnsMapSkins;
	  mnsUnitSize    = new Menu("Unit Size", 'S');
	  mnsFlagDisplay = new Menu("Flag Display Mode", 'N');
	  mnsMapSkins    = new Menu("Map Skins", 'K');
	  mnxLockMap 		  = new ChBoxMenuItem("Lock Map", 'M', HotKey.LOCK_MAP);
	  mnxShowMapTooltips  = new ChBoxMenuItem("Show Map Tooltips", 'T');
	  mnxShowMapDetails   = new ChBoxMenuItem("Show Map Details",  'D');
	  mnxShowMapBlends    = new ChBoxMenuItem("Show Map Blends",   'B');
	  mnxTerritoryEffects = new ChBoxMenuItem("Show TerritoryEffects", 'E');
	  mnuView.add(mniMapZoom = new JMenuItem("Map Zoom (%)", 'Z'));
  	  mnuView.add(mnxLockMap); //.setAccelerator(HotKey.LOCK_MAP);
	  mnuView.add(mnxShowUnits = new ChBoxMenuItem("Show Units", 'U')).setSelected(true);
	  mnuView.add(mnxShowMapTooltips);
	  mnuView.add(mnsUnitSize);
	  mnuView.add(mnxTerritoryEffects);
	  mnuView.add(mnxShowMapDetails);
	  mnuView.add(mnsFlagDisplay);
	  mnuView.add(new JMenuItem("Map Font and Color", 'C'));
	  mniMapZoom.addActionListener(e -> {
		  bframe.status.setText("80%"); 
		  bframe.status.setHorizontalAlignment(JLabel.RIGHT);
		  mnuLabel.setText("80% Map Zoom");
	  });
	  mnxShowUnits.addItemListener(e -> pn("ItemListener, Show Units: "+ mnxShowUnits.getState()));

	  final JMenu mnuGame = new Menu("Game", 'G'); //Game Options
	  final JMenuItem showHistory, showCurrentGame, mniPolitics;
	  final JCheckBoxMenuItem mnxEnableSound, mnxEditMode;
	  showCurrentGame    = new JMenuItem("Show Current Game", 'G'); showCurrentGame.setEnabled(false);
	  showHistory        = new JMenuItem("Show History", 	  'H'); showHistory.setDisplayedMnemonicIndex(5);
	  mniPolitics        = new JMenuItem("Politics/War", 	  'W');
	  mnuGame.add(mnxEditMode = new ChBoxMenuItem("Edit Mode", 'E')).setAccelerator(HotKey.EDIT_MODE);
	  mnuGame.addSeparator(); 	//---------------------
	  mnuGame.add(showCurrentGame).setAccelerator(HotKey.SHOW_CURRENT_GAME);  
	  mnuGame.add(showHistory).setAccelerator(HotKey.SHOW_HISTORY);
	  mnuGame.addSeparator();	//---------------------
	  mnuGame.add(mnxEnableSound = new ChBoxMenuItem("Enable Sound", 'N'));
	  mnuGame.add(new JMenuItem("Sound Options", 'S'));
      mnuGame.addSeparator();	//---------------------
	  mnuGame.add(new JMenuItem("# Engine Settings", 'I'));
      mnuGame.addSeparator();	//---------------------
  	  mnuGame.add(new JMenuItem("Show Map Options", 'O')).setDisplayedMnemonicIndex(9);
  	  mnuGame.add(mniPolitics).setAccelerator(HotKey.POLITICS);
	  mnuGame.add(new Menu("User Notifications", 'U')); 
	  mnuGame.add(new JMenuItem("Show Dice Stats", 'D'));
	
	  final Menu mnuExport = new Menu("Export", 'E');
	  mnuExport.add(new JMenuItem("Export Setup Charts", 'C'));
	  mnuExport.add(new JMenuItem("Game .xml (Beta)",  'X'));
	  mnuExport.add(new JMenuItem("Short Game Stats",  'S'));
	  mnuExport.add(new JMenuItem("Full  Game Stats",  'F'));
	  mnuExport.add(new JMenuItem("Unit Types",		   'U'));
	  mnuExport.add(new JMenuItem("Gameboard Picture", 'P'));

	  final Menu mnuTools = new Menu("Tools", 'T'); //was "Debug"
	  final JMenuItem mniFindTerritory;
	  mnuTools.add(new JMenuItem("AI Logs (Hard AI)", 'A'));
	  mnuTools.add(new JMenuItem("Console", 	      'C')); //console has been removed in v2.6
	  mnuTools.add(mniFindTerritory = new JMenuItem("Find Territory...", 'F'));
	  mniFindTerritory.setAccelerator(HotKey.FIND_TERRITORY);

	  final Menu mnuHelp = new Menu("Help", 'H');
	  final JMenuItem mniAbout;
	  mnuHelp.add(new JMenuItem("Movement/Selection", 'M'));
	  mnuHelp.add(new JMenuItem("Game Units", 		  'U'));
	  mnuHelp.add(new JMenuItem("Game Notes", 		  'N'));
	  mnuHelp.addSeparator(); //------------------------------
	  mnuHelp.add(new JMenuItem("Browse Notes",       'W'));
	  mnuHelp.add(new JMenuItem("Send Bug Report",    'B'));
	  mnuHelp.add(mniAbout = new JMenuItem("About...", 'A')); 
	  mniAbout.addActionListener(e -> new AboutTriplea(bframe, ui) );
	  
	  mnuLabel.setText(ui.mnuLabelText); 
	  mnuLabel.setOpaque(true); mnuLabel.setBackground(Color.pink);
	  // mnuLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
	  
      add(mnuFile);
      add(mnuWindow);
	  add(mnuView);
	  add(mnuGame);
	  add(mnuExport);
	  add(mnuTools);
	  add(mnuHelp);
	  add(Box.createHorizontalGlue());
 	  add(mnuLabel); 
      // add(customMenu); //Right side menu
	  // add(Box.createHorizontalStrut(10));
	}

	
class AboutTriplea extends JEditorPane {	
  AboutTriplea(BFrame bframe, UiProperties ui) { 
	super();
    this.setEditable(false);
    this.setContentType("text/html");
	this.setOpaque(false);
	this.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
	//1920x1080 @100%dpi //1536x864 @125%dpi, images and text are scaled. //Font: Segoe UI, 15 
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
	int hidpi = getDpi();
	String dpiStr = String.format("%dx%d  @%d%%dpi", screenSize.width, screenSize.height, hidpi);
	if (hidpi!= 100) dpiStr+=", images and text are scaled.";  
	final Font font = (Font) UIManager.get("Label.font");	
	//≤
    final String about = "<h1 style='text-align:center;'> TripleA frame Mockup</h1>"
			+ "<h2 style='text-align:center;'>Open Source Turn-Based Grand Strategy Game" + "</h2>" 
			+ "<br>"
			+ "<div style='border-left:5px solid #d3d3d3; color:#ff0000;background-color:#d3d3d3;'>"
			+ "Web: <a href='https://triplea-game.org/'>https://triplea-game.org/</a>"
			+ " |&nbsp <a href='https://forums.triplea-game.org/'>Forum</a>"			
			+ " |&nbsp <a href='https://github.com/triplea-game/triplea'>Github</a>"
			+ " |&nbsp <a href='https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=GKZL7598EDZLN'>Donate</a>"
			+ " |&nbsp <a href='https://forums.triplea-game.org/post/50630'>What's New</a>"
			+ "</div><br>"
			+ "<i>Display: " + dpiStr + "<br>"
			+ "LookAndFeel: " + UIManager.getLookAndFeel().getClass().getSimpleName().replaceAll("LookAndFeel", "") 
			+ " | "+ "Font: "+ font.getName() +"-"+ font.getSize() +"</i>"
			+ "<hr>"
			+ "<h3>Copyright (C) &#8804; 2021 by butterw.</h3>"
			+ "<p style='text-align:justify';>"
			+ "This program is free software: you can redistribute it and/or modify it under the terms of the <b>GNU General Public License</b> as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. Complete license notice available " 
				+ "<a href='https://github.com/triplea-game/triplea/blob/master/README.md#license'>here.</a>" +"<br>"
			+ "<font size='-1'>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE." + "</p></font>"
			+ "<br>";
	this.setText(about); 
    this.addHyperlinkListener(e -> {
      if (javax.swing.event.HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
        Desktop desktop = Desktop.getDesktop();
        try { desktop.browse(e.getURL().toURI());
        } catch (Exception ex) { ex.printStackTrace(); }
      }
    });

	final JScrollPane scroll = new JScrollPane(this); //scroll
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	scroll.setPreferredSize(new Dimension(550, 425));
	this.setCaretPosition(0);

	// JOptionPane.showMessageDialog(bframe, scroll,  "About...", JOptionPane.PLAIN_MESSAGE, new ImageIcon("triplea_icon_128_128.png")); //with OK button.
	JOptionPane.showOptionDialog(bframe, 
		scroll, "About...", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, ui.aboutIcon, new Object[]{}, null); 
	}
}
    public static int getDpi() {
		java.awt.GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		java.awt.GraphicsConfiguration gc = gd.getDefaultConfiguration();
		java.awt.geom.AffineTransform gc2 = gc.getDefaultTransform();
		return (int) Math.round(100*Math.max(gc2.getScaleX(), gc2.getScaleY()));
	}
}	

final class HotKey {	
/* KeyStroke getKeyStroke(String s)
It should be possible to define custom keyboard hotkeys as strings (with the correct syntax) in a .property file.

modifiers: shift | ctrl (or control) | meta | alt | altGraph
    typedID: typed <typedKey>
    typedKey: string of length 1 giving Unicode character.
    pressedReleasedID: pressed (default) or released
    key: KeyEvent key code name, i.e. the name following "VK_"

ex: "INSERT" => getKeyStroke(KeyEvent.VK_INSERT, 0); //VK_CONTEXT_MENU
     "ctrl DELETE" or "control DELETE" => getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK);
     "alt shift X" => getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK);
     "alt shift released X" => getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, true);
     "a" or "typed a" => getKeyStroke('a');
	 
	! mni accelerator "alt F4" overrides default window close X. 
*/
  public static final int MENU = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(); //CTRL (128) or META
  //KeyStroke.getKeyStroke(KeyEvent.VK_ALT, InputEvent.CTRL_DOWN_MASK, true); // (CTRL+Alt) onKeyRelease!
 
  public static final KeyStroke	SAVE 	   = KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU);
  public static final KeyStroke	LEAVE_GAME = null; //KeyStroke.getKeyStroke(""); //(CTRL+Q)
  public static final KeyStroke	EXIT 	   = null; //Exit the application (Alt+F4)
  public static final KeyStroke	EDIT_MODE  = KeyStroke.getKeyStroke(KeyEvent.VK_E, MENU);
  public static final KeyStroke LOCK_MAP   = KeyStroke.getKeyStroke(KeyEvent.VK_L, MENU); // ViewMenu > Lock Map
  public static final KeyStroke	FIND_TERRITORY 	  = KeyStroke.getKeyStroke(KeyEvent.VK_F, MENU);
  public static final KeyStroke	BATTLE_CALCULATOR = KeyStroke.getKeyStroke(KeyEvent.VK_B, MENU);
  public static final KeyStroke	SHOW_CURRENT_GAME = KeyStroke.getKeyStroke(KeyEvent.VK_G, MENU);
  public static final KeyStroke	SHOW_HISTORY = KeyStroke.getKeyStroke(KeyEvent.VK_H, MENU);
  public static final KeyStroke	POLITICS     = KeyStroke.getKeyStroke(KeyEvent.VK_W, MENU);
  public static final KeyStroke	ICONIFY = KeyStroke.getKeyStroke(KeyEvent.VK_F11, MENU);
  public static final KeyStroke	TOGGLE_SIDEPANEL  = KeyStroke.getKeyStroke(KeyEvent.VK_X, MENU);
  public static final KeyStroke	TOGGLE_FULLSCREEN = KeyStroke.getKeyStroke("F11");
  public static final KeyStroke	TOGGLE_MENUBAR    = KeyStroke.getKeyStroke("ctrl released ALT"); 
  public static final KeyStroke	SHOW_MENUBAR 	  = KeyStroke.getKeyStroke("released ALT");

  public static String keyStrokeText(KeyStroke keystroke) { //display string for hotkeys
	String keystrokeText = "";
	if (keystroke !=null) {
		int modifiers = keystroke.getModifiers();
		if (modifiers >0) {
			keystrokeText = KeyEvent.getModifiersExText(modifiers);
			keystrokeText+= "+";
		}
    keystrokeText+= KeyEvent.getKeyText(keystroke.getKeyCode());
	}
	return keystrokeText;
  } 
}
