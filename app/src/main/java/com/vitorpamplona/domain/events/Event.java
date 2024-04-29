/**
 * Copyright (c) 2024 Vitor Pamplona
 *
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact me at vitor@vitorpamplona.com.
 * For AGPL licensing, see below.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * This application has not been clinically tested, approved by or registered in any health agency.
 * Even though this repository grants licenses to use to any person that follow it's license,
 * any clinical or commercial use must additionally follow the laws and regulations of the
 * pertinent jurisdictions. Having a license to use the source code does not imply on having
 * regulatory approvals to use or market any part of this code.
 */
package com.vitorpamplona.domain.events;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Deprecated
    private Date when;
    private long timeInMilliseconds;

    public Event() {
        this.when = null;
    }

    @Deprecated
    public Event(Date when) {
        this.when = when;
        this.timeInMilliseconds = when.getTime();
    }

    public Event(long timeInMilliseconds) {
        this.timeInMilliseconds = timeInMilliseconds;
    }

    public Date getWhen() {
        if (timeInMilliseconds > 0)
            return new Date(timeInMilliseconds);
        else
            return when;
    }

    public String getDesc() {
        return "Event";
    }

    public String getWhenStr() {
        return new SimpleDateFormat("mm:ss.SSS").format(getWhen());
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public void setWhen(long when) {
        this.timeInMilliseconds = when;
    }

    public String toString(Date startingPoint) {
        if (getWhen() != null && startingPoint != null)
            return (getWhen().getTime() - startingPoint.getTime()) / 1000 + ";" + getDesc();
        else
            return "No idea;" + getDesc();
    }

    public long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

    public void setTimeInMilliseconds(long timeInMilliseconds) {
        this.timeInMilliseconds = timeInMilliseconds;
    }
}
