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

import java.util.Date;

/**
 * Represents an entry.
 * @author pwaldhauer
 */
public class Entry {

    private Date bookDate = null;
    private String usage = null;
    private Long value = null;

    /**
     * Sets the book date.
     * @param bookDate the book date
     */
    public void setBookDate(Date bookDate) {
        this.bookDate = bookDate;
    }

    /**
     * Gets the book date.
     * @return the book date
     */
    public Date getBookDate() {
        return bookDate;
    }

    /**
     * Sets the usage.
     * @param usage the usage
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Gets the usage.
     * @return the usage
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Sets the value in Cents.
     * @param value the value
     */
    public void setValue(Long value) {
        this.value = value;
    }

    /**
     * Gets the value in Cents.
     * @return the value
     */
    public Long getValue() {
        return value;
    }

    /**
     * Gets a human readable string representation of this entry.
     * @return a human readable string representation of this entry
     */
    @Override
    public String toString() {
        return "Entry (Date: " + bookDate.toString() + ", Value: " + value.toString() + ", Usage: " + usage + ")";
    }
}
