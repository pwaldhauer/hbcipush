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

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;

/**
 * Main class.
 * Most of the code is based on the HBCI4Java examples. Thanks!
 * 
 * @author pwaldhauer
 */
public class HbciPush {

    private HBCIPassport passport;
    private HBCIHandler hbciHandle;

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream("./hbci.properties"));

        boolean install = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--install")) {
                install = true;
            } else if (args[i].equals("--pin")) {
                properties.put("pin", args[i + 1]);
            } else if (args[i].equals("--passphrase")) {
                properties.put("passphrase", args[i + 1]);
            }
        }

        HbciPush konto = new HbciPush();

        if (install) {
            konto.install(properties);
        } else {
            konto.fetchNewestEntries(properties);
        }
    }

    /**
     * Creates the database and does a demo connection asking the user
     * for all needed bank related data.
     * @param properties properties to use
     */
    public void install(Properties properties) {
        System.out.println("Creating database...");

        EntryPersistor persistor = EntryPersistor.getInstance();
        persistor.install();

        HBCIUtils.init(properties, new InstallCallback());
        passport = AbstractPinTanPassport.getInstance("PinTan");

        try {
            String version = passport.getHBCIVersion();
            hbciHandle = new HBCIHandler((version.length() != 0) ? version : "plus", passport);

            /**
             * Use the first available account
             */
            Konto myaccount = passport.getAccounts()[0];
            HBCIJob auszug = hbciHandle.newJob("KUmsAll");
            auszug.setParam("my", myaccount);
            auszug.addToQueue();

            HBCIExecStatus ret = hbciHandle.execute();
            GVRKUms result = (GVRKUms) auszug.getJobResult();

            if (!result.isOK()) {
                System.out.println(result.getJobStatus().getErrorString());
                System.out.println(ret.getErrorString());
            }
        } finally {
            if (hbciHandle != null) {
                hbciHandle.close();
            }

            if (passport != null) {
                passport.close();
            }

            EntryPersistor.getInstance().disconnect();
        }

    }

    /**
     * Fetches the newest entries and persists them.
     * @param properties properties to use
     */
    public void fetchNewestEntries(Properties properties) {
        EntryPersistor persistor = EntryPersistor.getInstance();

        /**
         * We search for entries made in the last ten days...
         */
        Date date = persistor.getLastEntryDate();

        Date searchDate = new GregorianCalendar(2011, 0, 1).getTime();
        if (date != null) {
            searchDate = new Date(date.getTime() - (86400 * 10));
        }

        HBCIUtils.init(properties, new GivenPinCallback(properties.getProperty("pin"), properties.getProperty("passphrase")));

        passport = AbstractPinTanPassport.getInstance("PinTan");

        try {
            String version = passport.getHBCIVersion();
            hbciHandle = new HBCIHandler((version.length() != 0) ? version : "plus", passport);

            /**
             * Use the first available account
             */
            Konto myaccount = passport.getAccounts()[0];
            HBCIJob auszug = hbciHandle.newJob("KUmsAll");
            auszug.setParam("my", myaccount);


            auszug.setParam("startdate", new SimpleDateFormat("yyyy-MM-dd").format(searchDate));
            auszug.addToQueue();

            HBCIExecStatus ret = hbciHandle.execute();

            GVRKUms result = (GVRKUms) auszug.getJobResult();
            if (result.isOK()) {
                List lines = result.getFlatData();
                for (Iterator j = lines.iterator(); j.hasNext();) {
                    GVRKUms.UmsLine umsatz = (GVRKUms.UmsLine) j.next();

                    Entry entry = new Entry();
                    entry.setBookDate(umsatz.bdate);
                    entry.setValue(umsatz.value.getLongValue());

                    StringBuilder usage = new StringBuilder();

                    if (!umsatz.gvcode.equals("999")) {
                        usage.append(umsatz.other.name);
                        usage.append(" ");

                        if (umsatz.other.name2 != null) {
                            usage.append(umsatz.other.name2);
                            usage.append(" ");
                        }

                        if (umsatz.other.number != null) {
                            usage.append(umsatz.other.number);
                            usage.append(" ");
                        }

                        if (umsatz.other.blz != null) {
                            usage.append("BLZ ");
                            usage.append(umsatz.other.blz);
                            usage.append(" (");
                            usage.append(HBCIUtils.getNameForBLZ(umsatz.other.blz));
                            usage.append(") ");
                        }

                        usage.append(umsatz.text);
                        usage.append(" ");
                    }

                    for (Object line : umsatz.usage) {
                        usage.append((String) line);
                        usage.append(" ");
                    }

                    entry.setUsage(usage.toString());

                    persistor.persist(entry);

                    System.out.println("Entry: " + entry.toString());
                }


            } else {
                System.out.println(result.getJobStatus().getErrorString());
                System.out.println(ret.getErrorString());
            }
        } finally {
            if (hbciHandle != null) {
                hbciHandle.close();
            }

            if (passport != null) {
                passport.close();
            }

            EntryPersistor.getInstance().disconnect();
        }
    }
}
