/*******************************************************************************
 * Copyright (c) 2024 aquenos GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.phoebus.pv.test;

import org.epics.vtype.Alarm;
import org.epics.vtype.Time;
import org.epics.vtype.VString;
import org.phoebus.pv.PV;

/**
 * Dummy PV implementation that is never going to connect.
 */
public class TestPV extends PV {

    protected TestPV(String name) {
        super(name);
        notifyListenersOfValue(VString.of(name, Alarm.none(), Time.now()));
    }
}
