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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * Singleton object to persist entries.
 * @author pwaldhauer
 */
public final class EntryPersistor {

    private Connection connection = null;
    private static EntryPersistor instance = null;

    /**
     * Gets the singleton instance.
     * @return the singleton instance
     */
    public static EntryPersistor getInstance() {
        if (instance == null) {
            instance = new EntryPersistor();
        }

        return instance;
    }

    /**
     * Contructs the persistor, creates connection to database.
     */
    private EntryPersistor() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:konto.db");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Closes the connection.
     */
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Drops and creates the entries table.
     * Be careful.
     */
    public void install() {
        try {
            Statement stat = connection.createStatement();
            stat.executeUpdate("drop table if exists entries;");
            stat.executeUpdate("create table entries (id INTEGER PRIMARY KEY AUTOINCREMENT, bookDate DATETIME NOT NULL, usage TEXT NOT NULL, value INT NOT NULL);");
            stat.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the date of the last added entry.
     * @return the date of the last added entry
     */
    public Date getLastEntryDate() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT bookDate FROM entries ORDER BY bookDate DESC LIMIT 1");
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                java.sql.Date bookDate = result.getDate(1);
                Date date = new Date(bookDate.getTime());

                result.close();
                stmt.close();
                return date;
            }

            result.close();
            stmt.close();
            return null;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Persists the given entry. 
     * First checks if the entry already exists.
     * @param entry entry to persist
     */
    public void persist(Entry entry) {
        try {
            if (exists(entry)) {
                return;
            }

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO entries(bookDate, usage, value) VALUES (?, ?, ?)");
            stmt.setDate(1, new java.sql.Date(entry.getBookDate().getTime()));
            stmt.setString(2, entry.getUsage());
            stmt.setLong(3, entry.getValue());

            stmt.executeUpdate();

            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Check if a given entry already exists in the database.
     * We check for entries with the same date, usage and value.
     * @param entry the entry
     * @return true if it already exists, false if not
     */
    public boolean exists(Entry entry) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT id FROM entries WHERE bookDate = ? AND usage = ? AND value = ?");
            stmt.setDate(1, new java.sql.Date(entry.getBookDate().getTime()));
            stmt.setString(2, entry.getUsage());
            stmt.setLong(3, entry.getValue());

            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                result.close();
                stmt.close();

                return true;
            }

            result.close();
            stmt.close();
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
