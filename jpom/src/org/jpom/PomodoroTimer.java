package org.jpom;  

/*
 * Einfacher Pomodoro Timer, der im System Try gestartet wird.
 * In der Start und Stop Methode wird ein TaskTimer gestartet/gestoppt, der jede Sekunde 
 * die Zeit-Anzeige aktualisiert und nach Ablauf der Zeit ein JOptionPane startet
 */

import java.awt.*;
import java.util.Timer;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;

 /**
  * @author fk4git
  * Version 1.06: 	Simple Mode nur fuer Start/Stop/Exit
  *
  */

public class PomodoroTimer {
	public static Timer t;
	public static boolean simpleMode = false;
	public static String SimpleModeText = "simple";
	public static RemindTask task;
	public static int pomCounter = 0;
	public static int durationSec = 1500;
	public static int breakDurationSec = 300;
	//public static String[] top3 = new String[3];
	public static String message ="not intialized yet";
	public static String menuStart = "Start";
	public static String menuStop = "Stop";
	public static String menuTime = "00:00";
	public static String menuExit = "Exit";
	public static String menuBreak = "Break";
	public static String menuResetCounter = "Reset POM Counter";
	public static String counterText = "Count: ";
	public static String menuCounter = counterText +pomCounter;
    public static Image startImage ;//= createImage("green_people.gif", "tray icon");
    public static Image stopImage ;//= createImage("red_people.gif", "tray icon");
    public static final PopupMenu popup = new PopupMenu();
    public static final MenuItem startItem = new MenuItem(menuStart);
    public static final MenuItem stopItem = new MenuItem(menuStop);
    public static final MenuItem breakItem = new MenuItem(menuBreak);
    public static final MenuItem counterItem = new MenuItem(menuCounter);
    public static final MenuItem resetCounterItem = new MenuItem(menuResetCounter);
    public static final MenuItem infoItem = new MenuItem(menuTime);
    public static final MenuItem exitItem = new MenuItem(menuExit);
    public static final MenuItem exitItem2 = new MenuItem(menuExit);
    public static  TrayIcon trayIcon;
    public static final SystemTray tray = SystemTray.getSystemTray();
	
  //  private static Logger logger = Logger.getRootLogger();
    
	public static void main(String[] args) {
    	
    	//setup logging
    	try{
//    	PatternLayout layout = new PatternLayout( "%d{ISO8601} %-5p [%t] %c: %m%n" );
//        FileAppender fileAppender = new FileAppender( layout, "logs/JPOM.log" );
//        logger.addAppender( fileAppender );
//        logger.setLevel( Level.WARN);
    	}
    	catch (Exception fileapp )
    	{
    		JOptionPane.showMessageDialog(null,
					fileapp.toString());
    	}
		
        startImage = createImage("green_people.gif", "tray icon");
        stopImage = createImage("red_people.gif", "tray icon");
    	
		if (args.length>0)
    	{
    		try{
    		// Pruefe auf Argument DEBUG	
//    			for(int i=0;i<args.length;i++)
//     		   	{
//     			   if ("debug".equalsIgnoreCase(args[i]))
//     				   logger.setLevel(Level.DEBUG);
//     			   else if ("info".equalsIgnoreCase(args[i]))
//    				   logger.setLevel(Level.INFO);
//     		   	}
     			
    		// Dauer einer POM Einheit
    		durationSec = Integer.parseInt(args[0]);
//    		logger.debug("Initalisierter Wert f�r Pom-Dauer: "+durationSec);
    		//Pr�fe alle Argumente auf den String "simple"
    		for(int i=0;i<args.length;i++)
    		   {
//    			logger.debug("Lese Argument "+(i+1)+" mit Wert: "+args[i]);
    			   if (SimpleModeText.equalsIgnoreCase(args[i]))
    			   {
    				   simpleMode=true;
 //   				   logger.info("Simple Modus aktiviert.");
    			   }
    			}
    		}
    		catch (Exception numberReader)
    		{
 //   			logger.error(numberReader.toString());
    			JOptionPane.showMessageDialog(null,
    					numberReader.toString());
    		}
    	}//args >0
    	
 
        //    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        initLaF();

        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
//            logger.warn("SystemTray is not supported.");
            return;
        }
        
        trayIcon = new TrayIcon(startImage);
        
        //Add components to popup menu
        popup.add(startItem);
        popup.add(stopItem);
        if(!simpleMode){
        popup.add(breakItem);
        popup.addSeparator();
        popup.add(counterItem);
        popup.add(resetCounterItem);
        }
        popup.addSeparator();
        popup.add(infoItem);
        popup.add(exitItem);

        
        stopItem.setEnabled(false);
 //       logger.debug("Inital StopItem enabled: "+stopItem.isEnabled());
        trayIcon.setPopupMenu(popup);
        trayIcon.setToolTip(message);
      
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
  //          logger.warn("TrayIcon could not be added.");
            return;
        }
        
        
       trayIcon.addActionListener(new ActionListener() {
    	   public void actionPerformed(ActionEvent e) {
    		 if (task!=null)
    		   stopPom();
    		 else
    		   startPom();
    	   }
       });
        
        startItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            startPom();
            }
        });
        
        
        
        breakItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            	breakPom();
            	}
        });
        
        //
        counterItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	pomCounter++;
            	counterItem.setLabel( counterText + pomCounter);
            	}
        });
        
        resetCounterItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	pomCounter=0;
            	counterItem.setLabel( counterText + pomCounter);
            	}
        });
        
        
        stopItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	stopPom();
            }
            
        });
                
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
        
    }

    //Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = PomodoroTimer.class.getResource(path);

        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
    
    public static void startPom()
    {
    	trayIcon.setImage(stopImage);
    	stopItem.setEnabled(true);

       	try{
       		// bereits existierenden Task stoppen
       		if (task!=null) task.cancel();
       		t = new Timer();
       		// Differenz zur Zielzeit
       		long target = System.currentTimeMillis()+durationSec* 1000;
       		task = new RemindTask(trayIcon,startImage,infoItem,target);
       		t.scheduleAtFixedRate(task, 0l,1000l);
       	}
       	catch (Exception e1)
       	{
       		JOptionPane.showMessageDialog(null,
                   e1.toString());}
       }
    
    public static void stopPom()
    {
    	trayIcon.setImage(startImage);
    	trayIcon.setToolTip("00:00");
    	infoItem.setLabel("00:00");
    	stopItem.setEnabled(false);
    	try{
    		if (task != null)
    			task.cancel();
    		task =null;
    	}
    	catch (Exception e1)
    	{
    		JOptionPane.showMessageDialog(null,
                e1.toString());}
    }
    
    public static void breakPom()
    {
    	// Image wechseln
    	trayIcon.setImage(stopImage);
    	stopItem.setEnabled(true);

    	try{
    		// bereits existierenden Task stoppen
    		if (task!=null) task.cancel();
    		t = new Timer();
    		// Differenz zur Zielzeit
    		long target = System.currentTimeMillis()+breakDurationSec* 1000;
    		task = new RemindTask(trayIcon,startImage,infoItem,target);
    		t.scheduleAtFixedRate(task, 0l,1000l);
    	}
    	catch (Exception e1)
    	{
    		JOptionPane.showMessageDialog(null,
                e1.toString());}
    
    }   
    public static void initLaF()
    {
    	final String lafWindows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    	final String lafGTK     = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
    	final String lafMetal   = "com.sun.java.swing.plaf.metal.MetalLookAndFeel";
    	final String lafMotif   = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

    	try {
    		String operatingSystem;
    		operatingSystem = System.getProperty("os.name");

    		if (operatingSystem.equalsIgnoreCase("linux"))
    		{
    			UIManager.setLookAndFeel(lafGTK);
    		} else if (operatingSystem.startsWith("Windows")) {
    			UIManager.setLookAndFeel(lafWindows);
    		} else if (operatingSystem.startsWith("solaris")) {
    			UIManager.setLookAndFeel(lafMotif);
    		} else {
    			UIManager.setLookAndFeel(lafMetal);
    		}
    	} catch (UnsupportedLookAndFeelException e) {
    		// Add exception code here
    	} catch (ClassNotFoundException e) {
    	} catch (InstantiationException e) {
    	} catch (IllegalAccessException e) {
    	}
    }
    
}