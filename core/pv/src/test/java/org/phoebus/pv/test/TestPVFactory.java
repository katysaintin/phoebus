package org.phoebus.pv.test;

import org.phoebus.pv.PV;
import org.phoebus.pv.PVFactory;

    /**
     * Creates dummy PVs for use inside tests.
     *
     * The PVs created do not generate a unique key and will provide the same name of DisconnectedPV
     */
    public class TestPVFactory implements PVFactory {

        @Override
        public String getType() {
            return "test";
        }

        @Override
        public PV createPV(String name, String base_name) throws Exception {
            return new TestPV(base_name);
        }

    }
