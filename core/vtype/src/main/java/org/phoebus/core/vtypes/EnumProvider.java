package org.phoebus.core.vtypes;

import org.epics.vtype.EnumDisplay;

public interface EnumProvider {

    /**
     * Returns the display information, including all possible choice names.
     * 
     * @return the enum display
     */
    public EnumDisplay getEnumDisplay();
    
    /**
     * To get the value as index
     * @return
     */
    public int getIndex();
    
    
}
