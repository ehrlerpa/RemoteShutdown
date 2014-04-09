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

import de.patrickehrler.remoteshutdown.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_settings);
	}
	
}
