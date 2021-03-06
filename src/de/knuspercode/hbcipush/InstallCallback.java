/*
 *  HbciPush
 *  Copyright (C) 2011 Philipp Waldhauer
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */
package de.knuspercode.hbcipush;

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.passport.HBCIPassport;

/**
 * Callback that forwards all questions to the user. 
 * Used for creating the profile file.
 * @author pwaldhauer
 */
public class InstallCallback extends HBCICallbackConsole {

    @Override
    public void callback(HBCIPassport passport, int reason, String msg, int dataType, StringBuffer retData) {

        switch (reason) {
            case NEED_CONNECTION:
            case CLOSE_CONNECTION:
                break;
            default:
                super.callback(passport, reason, msg, dataType, retData);
        }
    }
}
