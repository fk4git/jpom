package org.jpom;

import java.awt.MenuItem;
import java.awt.TrayIcon;
import java.util.TimerTask;
import java.awt.Image;

/*
 * Task, der jede Sekunde die verbleibende Zeit aktualisiert
 */
public class RemindTask extends TimerTask {
    
	public TrayIcon icon;
	public Image image;
	public MenuItem infoItem;
	public long target;
	String message = "Finished compiling sources.";
	String msgHeader = "Compile successful";
	String msgText = "All Sources have been compiled";
	String msgNotStarted = "not started";
	String time;
	public RemindTask(TrayIcon icon, Image image,MenuItem infoItem,long target)
	{
		this.icon=icon;
		this.image = image;
		this.infoItem = infoItem;
		this.target=target;
	}
	
	public void run() {
		//berechne Diff zum Ziel
		long diff = target-System.currentTimeMillis();
		if (diff >0)
		{
			//formatiere diff
			time =formatMMSS(diff);
			icon.setToolTip(time);
			infoItem.setLabel(time);
		}
		else
		{
			//Abschluss
	        icon.setImage(image);
	        //JOptionPane.showMessageDialog(null,message,"Compile successful",JOptionPane.INFORMATION_MESSAGE);
	        icon.displayMessage(msgHeader, 
	                msgText,
	                TrayIcon.MessageType.INFO);

	        infoItem.setLabel("00:00");
	        icon.setToolTip(msgNotStarted);
	        this.cancel();
		}

      }
	
	public String formatMMSS(long timeMillis)
	{
		long time = timeMillis / 1000;
		String seconds = Integer.toString((int)(time % 60));
		String minutes = Integer.toString((int)((time % 3600) / 60));
		if (seconds.length() < 2) {
			seconds = "0" + seconds;
		}
		if (minutes.length() < 2) {
			minutes = "0" + minutes;
		}
		return minutes+":"+seconds;
    }
}