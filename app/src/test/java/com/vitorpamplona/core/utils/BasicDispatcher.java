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
package com.vitorpamplona.core.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.vitorpamplona.core.utils.NumberChangeDispatcher.NumberChangedListener;


public class BasicDispatcher {

    LastTriggerState lastState;

    public BasicDispatcher() {
        // TODO Auto-generated constructor stub
    }

    public void testTrigger(NumberChangeDispatcher dispatcher, double saveFrom, double value, int stepSize) {
        lastState = null;
        dispatcher.newValue(value);
        lastState.validateChanged(saveFrom, value, stepSize);
        assertEquals(value, dispatcher.getValue(), 0.01);
    }

    public void testNoTrigger(NumberChangeDispatcher dispatcher, double value) {
        lastState = null;
        dispatcher.newValue(value);
        assertNull(lastState);
        assertEquals(value, dispatcher.getValue(), 0.01);
    }

    class LastTriggerState {
        double newFrom;
        double newTo;
        int newStepsChanged;
        boolean triggered;

        public LastTriggerState(double from, double to, int stepsChanged) {
            newFrom = from;
            newTo = to;
            newStepsChanged = stepsChanged;
            triggered = true;
        }

        public void validateNotChanged() {
            assertFalse(triggered);
            assertEquals(0, newFrom, 0.01f);
            assertEquals(0, newTo, 0.01f);
            assertEquals(0, newStepsChanged);
        }

        public void validateChanged(double from, double to, int stepsChanged) {
            assertTrue("Didn't triggered from value " + from + " to " + to, triggered);
            assertEquals(from, newFrom, 0.01f);
            assertEquals(to, newTo, 0.01f);
            assertEquals(stepsChanged, newStepsChanged);
        }
    }

    public NumberChangedListener getStateListener() {
        return new NumberChangedListener() {
            @Override
            public void changed(double from, double to, int stepsChanged) {
                lastState = new LastTriggerState(from, to, stepsChanged);
            }
        };
    }

}
