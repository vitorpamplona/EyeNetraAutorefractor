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

import com.vitorpamplona.core.models.AstigmaticLensParams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class EventHistory implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<Event> eventHistory;

    public EventHistory() {
    }

    public List<Event> eventHistory() {
        if (eventHistory == null)
            eventHistory = new ArrayList<Event>();

        return eventHistory;
    }

    public List<Event> getEventHistory() {
        return eventHistory();
    }

    public void setEventHistory(List<Event> eventHistory) {
        this.eventHistory = eventHistory;
    }

    public String exportEventHistory(Date date) {
        StringBuilder st = new StringBuilder();
        for (Event e : eventHistory()) {
            st.append(e.toString(date) + ";\n");
        }
        return st.toString();
    }

    public Event addNewStep(float angle, float power, AstigmaticLensParams fittedData) {
        Event event = new SearchStep(new Date(System.currentTimeMillis()), angle, power, fittedData);
        eventHistory().add(event);
        return event;
    }

    public void addActions(String str) {
        StringTokenizer msgs = new StringTokenizer(str, "\n");
        while (msgs.hasMoreTokens()) {
            addAction(msgs.nextToken());
        }
    }

    public Event addAction(String string) {
        Event event = new Action(new Date(System.currentTimeMillis()), string);
        eventHistory().add(event);
        return event;
    }

    public Event changeAPD(float pd) {
        Event event = new IPDChange(new Date(System.currentTimeMillis()), pd);
        eventHistory().add(event);
        return event;
    }

    public Event inputAPD(float pd, Boolean right) {
        Event event = new IPDInput(new Date(System.currentTimeMillis()), pd, right);
        eventHistory().add(event);
        return event;
    }

    public Event requestSetPowerTo(float power) {
        Event event = new RequestSetPowerTo(new Date(System.currentTimeMillis()), power);
        eventHistory().add(event);
        return event;
    }

    public Event changeDeviceAPD(float pd) {
        Event event = new IPDChange(new Date(System.currentTimeMillis()), pd);
        eventHistory().add(event);
        return event;
    }

    public Event decrease() {
        Event event = new DecreaseDistance(new Date(System.currentTimeMillis()));
        eventHistory().add(event);
        return event;
    }

    public Event increase() {
        Event event = new IncreaseDistance(new Date(System.currentTimeMillis()));
        eventHistory().add(event);
        return event;
    }

    public Event nextAngle() {
        Event event = new NextAngle(new Date(System.currentTimeMillis()));
        eventHistory().add(event);
        return event;
    }

    public Event restart() {
        Event event = new Restart(new Date(System.currentTimeMillis()));
        eventHistory().add(event);
        return event;
    }

    public Event cancelTest(String why) {
        Event event = new Cancel(new Date(System.currentTimeMillis()), why);
        eventHistory().add(event);
        return event;
    }

    public Event testFinished() {
        Event event = new Finish(new Date(System.currentTimeMillis()));
        eventHistory().add(event);
        return event;
    }

    public Event askSetupNewAngle(float angle) {
        Event event = new AskSetupNewAngle(new Date(System.currentTimeMillis()), angle);
        eventHistory().add(event);
        return event;
    }

    public Event oneTwo(String desc) {
        OneTwo event = new OneTwo(new Date(System.currentTimeMillis()), desc);
        eventHistory().add(event);
        return event;
    }


    public Event addDeviceID(int serial) {
        DeviceID event = new DeviceID(new Date(System.currentTimeMillis()), serial);
        eventHistory().add(event);
        return event;
    }

    public Event orientation(boolean correct) {
        DeviceOrientation event = new DeviceOrientation(new Date(System.currentTimeMillis()), correct);
        eventHistory().add(event);
        return event;
    }

    public Event bluetoothState(String status) {
        ConnectionState event = new ConnectionState(new Date(System.currentTimeMillis()), status);
        eventHistory().add(event);
        return event;
    }

    public Event requestBatteryUpdate() {
        RequestBatteryUpdate event = new RequestBatteryUpdate(new Date(System.currentTimeMillis()));
        eventHistory().add(event);
        return event;
    }

    public Event logSlidingChange(float angle, float pd) {
        SlidingChange event = new SlidingChange(new Date(System.currentTimeMillis()), angle, pd);
        eventHistory().add(event);
        return event;
    }

    public Event addVersions(String deviceVersion, String controllerVersion) {
        Event event = new AppVersions(System.currentTimeMillis(), deviceVersion, controllerVersion);
        eventHistory().add(event);
        return event;
    }

}
