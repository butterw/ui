package games.strategy.triplea.ui.menubar.help;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Desktop;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import java.nio.file.Path;
import games.strategy.triplea.UrlConstants;
import games.strategy.triplea.ResourceLoader;
import org.triplea.config.product.ProductVersionReader;

/* Help > About... Dialog with some links and info */
class AboutTriplea extends JEditorPane {
  private static final long serialVersionUID = -4703734404422047487L; //How to generate proper value ?	
	
  AboutTriplea() {
    this.setEditable(false);
    this.setContentType("text/html");
	this.setOpaque(false); //applies LookandFeel background
	this.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE); //fix for applying font on Metal

	//"1920x1080 @100%dpi" //"1536x864 @125%dpi, images and text are scaled."  
	Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize(); 
	int hidpi = AboutTriplea.getDpi();
	String dpiStr = String.format("%dx%d  @%d%%dpi", screenSize.width, screenSize.height, hidpi);
	if (hidpi!=100) dpiStr+= ", images and text are scaled.";  
	
    final String about = "<h1 style='text-align:center;'> TripleA v"
			+ new ProductVersionReader().getVersion() + "-bu" + "</h1>"
			+ "<h2 style='text-align:center;'>Open Source Turn-Based Grand Strategy Game" + "</h2>" 
			+ "<br>"
			+ "<div style='border-left:5px solid #d3d3d3; color:#ff0000;background-color:#d3d3d3;'>"
			+ "Web: <a href=UrlConstants.RELEASE_NOTES>https://triplea-game.org/</a>"
			+ " |&nbsp <a href=UrlConstants.TRIPLEA_FORUM>Forum</a>"			
			+ " |&nbsp <a href='https://github.com/triplea-game/triplea'>Github</a>"
			+ " |&nbsp <a href=UrlConstants.PAYPAL_DONATE>Donate</a>" 
			+ "</div><br>"
			+ "<i>Display: "+ dpiStr + "<br>"
			+ "LookAndFeel: "+ UIManager.getLookAndFeel().getClass().getSimpleName().replaceAll("LookAndFeel", "") +"</i>"
			+ "<hr>"
			+ "<h3>Copyright (C) 2001-2021 TripleA contributors.</h3>"
			+ "<p style='text-align:justify';>"
			+ "This program is free software: you can redistribute it and/or modify "
            + "it under the terms of the <b>GNU General Public License</b> as published by "
            + "the Free Software Foundation, either version 3 of the License, or (at your option) any later version. " 
			+ "Complete license notice available <a href=UrlConstants.LICENSE_NOTICE>here.</a>" +"<br>"
			+ "<font size='-1'>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; "
			+ "without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE." + "</p></font>"
			+ "<br>";

	this.setText(about); 
    this.addHyperlinkListener(e -> {
      if (javax.swing.event.HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
        Desktop desktop = Desktop.getDesktop();
        try {          
			desktop.browse(e.getURL().toURI());
        } catch (Exception ex) { System.out.println(ex); }
      }
    });


	final JScrollPane scroll = new JScrollPane(this);
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	scroll.setPreferredSize(new Dimension(550, 425));
	this.setCaretPosition(0);

	// JOptionPane.showOptionDialog(null, scroll, "About...", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon(ResourceLoader.loadImageAssert(Path.of("icons", "triplea_icon_128_128.png"))), new Object[]{}, null); //without OK button
	JOptionPane.showMessageDialog(null, scroll, "About...", JOptionPane.PLAIN_MESSAGE, 
		new ImageIcon(ResourceLoader.loadImageAssert(Path.of("icons", "triplea_icon_128_128.png"))));		
	}

    public static int getDpi() {
		java.awt.GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		java.awt.GraphicsConfiguration gc = gd.getDefaultConfiguration();
		java.awt.geom.AffineTransform gc2 = gc.getDefaultTransform();
		return (int) Math.round(100*Math.max(gc2.getScaleX(), gc2.getScaleY()));
	}
}

