/*******************************************************************************
 * Copyright (c) 2017-2022 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.pv;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collection;
import java.util.Set;
import java.util.prefs.Preferences;

import org.junit.jupiter.api.Test;
import org.phoebus.pv.PVPool.TypedName;
import org.phoebus.pv.RefCountMap.ReferencedEntry;

/** @author Kay Kasemir */
@SuppressWarnings("nls")
public class PVPoolTest
{
    @Test
    public void listPrefixes()
    {
        final Collection<String> prefs = PVPool.getSupportedPrefixes();
        System.out.println("Prefixes: " + prefs);
        assertThat(prefs, hasItem("eq"));
        assertThat(prefs, hasItem("loc"));
        assertThat(prefs, hasItem("sim"));
        assertThat(prefs, hasItem("sys"));
    }

    @Test
    public void analyzePVs()
    {
        TypedName type_name = TypedName.analyze("pva://ramp");
        assertThat(type_name.type, equalTo("pva"));
        assertThat(type_name.name, equalTo("ramp"));
        assertThat(type_name.toString(), equalTo("pva://ramp"));

        type_name = TypedName.analyze("ramp");
        assertThat(type_name.type, equalTo(PVPool.default_type));
        assertThat(type_name.name, equalTo("ramp"));
        assertThat(type_name.toString(), equalTo(PVPool.default_type + "://ramp"));
    }

    @Test
    public void equivalentPVs()
    {
        // Given "ramp" or "ca://ramp", all the other variants should be considered
        final String[] equivalent_pv_prefixes = new String[] { "ca", "pva" };
        Set<String> pvs = PVPool.getNameVariants("pva://ramp", equivalent_pv_prefixes);
        assertThat(pvs.size(), equalTo(3));
        assertThat(pvs, hasItem("ramp"));
        assertThat(pvs, hasItem("ca://ramp"));
        assertThat(pvs, hasItem("pva://ramp"));

        // Repeat to verify trimming of pv name
        pvs = PVPool.getNameVariants("pva://ramp\n", equivalent_pv_prefixes);
        assertThat(pvs.size(), equalTo(3));
        assertThat(pvs, hasItem("ramp"));
        assertThat(pvs, hasItem("ca://ramp"));
        assertThat(pvs, hasItem("pva://ramp"));

        pvs = PVPool.getNameVariants("pva://ramp ", equivalent_pv_prefixes);
        assertThat(pvs.size(), equalTo(3));
        assertThat(pvs, hasItem("ramp"));
        assertThat(pvs, hasItem("ca://ramp"));
        assertThat(pvs, hasItem("pva://ramp"));


        // For loc or sim which are not in the equivalent list, pass name through
        pvs = PVPool.getNameVariants("loc://ramp", equivalent_pv_prefixes);
        assertThat(pvs.size(), equalTo(1));
        assertThat(pvs, hasItem("loc://ramp"));

        // Repeat to verify trimming of pv name
        pvs = PVPool.getNameVariants("loc://ramp\n", equivalent_pv_prefixes);
        assertThat(pvs.size(), equalTo(1));
        assertThat(pvs, hasItem("loc://ramp"));

        pvs = PVPool.getNameVariants("loc://ramp ", equivalent_pv_prefixes);
        assertThat(pvs.size(), equalTo(1));
        assertThat(pvs, hasItem("loc://ramp"));

        pvs = PVPool.getNameVariants("sim://ramp", equivalent_pv_prefixes);
        assertThat(pvs.size(), equalTo(1));
        assertThat(pvs, hasItem("sim://ramp"));

        // Repeat to verify trimming of pv name
        pvs = PVPool.getNameVariants("sim://ramp\n", equivalent_pv_prefixes);
        assertThat(pvs.size(), equalTo(1));
        assertThat(pvs, hasItem("sim://ramp"));

        pvs = PVPool.getNameVariants("sim://ramp ", equivalent_pv_prefixes);
        assertThat(pvs.size(), equalTo(1));
        assertThat(pvs, hasItem("sim://ramp"));

    }
    
    @Test
    public void creationAndReleasePVTest() throws Exception {
        String disconnectedPVName = "disconnected://missing_PV";
        PV disconnectedPV = PVPool.getPV(disconnectedPVName);
        int disconnectedRef = 0 ;
        String disconnectedRealName = disconnectedPV.getName();
        if(!disconnectedRealName.equals(disconnectedPVName)) {
            System.out.println("\"" + disconnectedRealName + "\" => PV Name is different = \"" + disconnectedPVName + "\"" );
        }
        
        Collection<ReferencedEntry<PV>> pvReferences = PVPool.getPVReferences();
        assertThat(pvReferences.size(), equalTo(1));
        ReferencedEntry<PV> referenceEntry = PVPool.getReferenceEntry(disconnectedPV);
        disconnectedRef = referenceEntry.getReferences();
        
        System.out.println(disconnectedPVName  + " nb reference(s) = " + disconnectedRef);
        assertThat(disconnectedRef, equalTo(1));
        
        //Create a second PV to check if the same PV is return
        disconnectedPV = PVPool.getPV(disconnectedPVName);
        
        //Check if there is 2 references
        pvReferences = PVPool.getPVReferences();
        assertThat(pvReferences.size(), equalTo(1));
        referenceEntry = PVPool.getReferenceEntry(disconnectedPV);
        disconnectedRef = referenceEntry.getReferences();
        System.out.println(disconnectedPVName  + " nb reference(s) = " + disconnectedRef);
        assertThat(disconnectedRef, equalTo(2));
        
        //Release PV once to check if there is one reference
        PVPool.releasePV(disconnectedPV);
        //Check if there is 1 references
        referenceEntry = PVPool.getReferenceEntry(disconnectedPV);
        disconnectedRef = referenceEntry.getReferences();
        System.out.println(disconnectedPVName  + " nb reference(s) = " + disconnectedRef);
        assertThat(disconnectedRef, equalTo(1));
        
        //Create a second PV that have the same name of Disconnected PV
        String testPVName = "test://missing_PV";
        int testRef = 0;
        PV testPV = PVPool.getPV(testPVName);
        
        String testRealName = testPV.getName();
        if(!testRealName.equals(testPVName)) {
            System.out.println("\"" + testRealName + "\" => PV Name is different = \"" + testPVName +"\"" );
        }
        
        if(testRealName.equals(disconnectedRealName)) {
            System.out.println(testPVName + " and " + disconnectedPVName + " have the same name !! =" + testRealName);
        }
    
        assertThat(testRealName, equalTo(disconnectedRealName));
        
        //Check if there is 2 references entry
        pvReferences = PVPool.getPVReferences();
        assertThat(pvReferences.size(), equalTo(2));
        
        referenceEntry = PVPool.getReferenceEntry(disconnectedPV);
        disconnectedRef = referenceEntry.getReferences();
        System.out.println(disconnectedPVName  + " nb reference(s) = " + disconnectedRef);
        
        referenceEntry = PVPool.getReferenceEntry(testPV);
        testRef = referenceEntry.getReferences();
        System.out.println(testPVName  + " nb reference(s) = " + testRef);
    
        assertThat(disconnectedRef, equalTo(1));
        assertThat(testRef, equalTo(1));
        
        //Release disconnectedPV
        PVPool.releasePV(disconnectedPV);
        
        pvReferences = PVPool.getPVReferences();
        assertThat(pvReferences.size(), equalTo(1));
        
        referenceEntry = PVPool.getReferenceEntry(disconnectedPV);
        System.out.println(disconnectedPVName  + " have no reference");
        assertNull(referenceEntry);
        
        //Release testPV
        PVPool.releasePV(testPV);
        
        pvReferences = PVPool.getPVReferences();
        assertThat(pvReferences.size(), equalTo(0));
        
        referenceEntry = PVPool.getReferenceEntry(testPV);
        System.out.println(testPVName  + " have no reference");
        assertNull(referenceEntry);
    }


    @Test
    public void dumpPreferences() throws Exception
    {
        final Preferences prefs = Preferences.userNodeForPackage(PV.class);
        prefs.exportSubtree(System.out);
    }
}
