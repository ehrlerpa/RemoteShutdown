/*
 * 	Copyright (C) 2014 Patrick Ehrler <kontakt@patrickehrler.de>
 * 
 *  
 *  This file is part of RemoteShutdown.
 *  
 *	RemoteShutdown is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  RemoteShutdown is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with RemoteShutdown.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */

package de.patrickehrler.remoteshutdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class RemoteShutdown extends Activity implements OnClickListener{
	
	public Button btnShutdown;
	public Button btnRestart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remoteshutdown);
		
		btnShutdown = (Button)findViewById(R.id.button_shutdown);
        btnRestart = (Button)findViewById(R.id.button_reboot);
        
        btnShutdown.setOnClickListener(this);
        btnRestart.setOnClickListener(this);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.remoteshutdown, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.menu_settings:
	        	menu_settings();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	
	private void menu_settings() {
		Intent intent = new Intent(this, Settings.class);
		startActivity(intent);
	}
	

	@Override
	public void onClick(View v) {	 
		String command;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		switch(v.getId()){
			case R.id.button_shutdown:
				command = sp.getString("pref_textbox_command_shutdown", "shutdown -h now");
				System.out.println(command);
				if(command==""){
					command = "shutdown -h now";
				}
				commandMethod(command);

			break;
			case R.id.button_reboot:
				command = "reboot";
				command = sp.getString("pref_textbox_command_reboot", "shutdown -r");
				if(command==""){
					command = "shutdown -r now";
				}
				commandMethod(command);
			break;
		}	
	}
	

	private void commandMethod(String command) {
		Context context = getApplicationContext();//Toast
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String hostname = sp.getString("pref_textbox_url", "");
        String username = sp.getString("pref_textbox_username", "");
        String password = sp.getString("pref_textbox_password", "");
        String portS = sp.getString("pref_textbox_port", "22");
        
        if(portS==""){
        	portS="22";
        }
        
        int port = Integer.parseInt(portS);//convert to Integer
        boolean sudo = sp.getBoolean("pref_checkbox_suOnOff", false);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        try{
			Connection conn = new Connection(hostname,port);
            conn.connect(); //connect
            boolean authentificate = conn.authenticateWithPassword(username, password);

            if(authentificate==true){
            	Session sess = conn.openSession();
            
            	String command2;
            	if(sudo==true){
            		command2 = "echo " + password +" | /usr/bin/sudo -S " + command;
            	}else{
            		command2 = command;
            	}
            	
            	sess.execCommand(command2);
            
            	BufferedReader reader = new BufferedReader(new InputStreamReader(new StreamGobbler(sess.getStdout())));
            	String line = reader.readLine();
            	while (line != null) {
            		line = reader.readLine();
            		Toast.makeText(context, line, Toast.LENGTH_SHORT).show();	
            	}
            	reader.close();            
            
            	Toast.makeText(context, getString(R.string.prg_toast_sent), Toast.LENGTH_SHORT).show();
            
            	sess.close();
            	conn.close();
            }else{
            	errorPopup(getString(R.string.prg_errorPopup_title_error),getString(R.string.prg_errorPopup_message_checkLoginDetails),getString(R.string.prg_errorPopup_button_close));
            	conn.close();
            }
        }catch (IOException e){
        	e.printStackTrace(System.err);
        	errorPopup(null,null,null);
        }
            
	}

	
	private void errorPopup(String title, String message, String negativeButton) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if(title==null&&message==null&&negativeButton==null){
			builder.setTitle(getString(R.string.prg_errorPopup_title_error))
            .setMessage(getString(R.string.prg_errorPopup_message_problemMaybeInternetConnection))
            .setCancelable(true)
            .setNegativeButton(getString(R.string.prg_errorPopup_button_close),new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
		}else{
			builder.setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setNegativeButton(negativeButton,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
		}
        AlertDialog alert = builder.create();
        alert.show();
	}

}