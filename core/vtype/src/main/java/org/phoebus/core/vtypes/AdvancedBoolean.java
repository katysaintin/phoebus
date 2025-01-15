package org.phoebus.core.vtypes;

import org.epics.vtype.Alarm;
import org.epics.vtype.EnumDisplay;
import org.epics.vtype.Time;
import org.epics.vtype.VBoolean;

public class AdvancedBoolean extends VBoolean implements DescriptionProvider, EnumProvider {
    
    private VBoolean source;
    private String description;
    private EnumDisplay enumDisplay;
    
    private AdvancedBoolean(final VBoolean source , String description , String falseLabel , String trueLabel) {
        this.description = description;
        this.source = source;
        enumDisplay = EnumDisplay.of(falseLabel, trueLabel);
    }
 
    @Override
    public Alarm getAlarm() {
        return source != null ? source.getAlarm() : null;
    }

    @Override
    public Time getTime() {
        return source != null ? source.getTime() : null;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Boolean getValue() {
        return source != null ? source.getValue() : null;
    }

    @Override
    public int getIndex() {
        Boolean value = getValue();
        return value != null && value ? 1 : 0 ;
    }
   
    @Override
    public EnumDisplay getEnumDisplay() {
        return enumDisplay;
    }
  
    
    /**
     * Creates a new VBoolean.
     * 
     * @param value the boolean value
     * @param alarm the alarm
     * @param time the time
     * @param description of the value 
     * @param false label map to enum 0 ONAM
     * @param tue label map to enum 1 ZNAM
     * @return the new value
     */
    public static VBoolean of(final Boolean value, final Alarm alarm, final Time time, String description , String falseLabel , String trueLabel) {
        VBoolean source = VBoolean.of(value, alarm, time);
        return new AdvancedBoolean(source, description , falseLabel , trueLabel);
    }
    
    /**
     * Creates a new VBoolean.
     * 
     * @param value the boolean value
     * @param alarm the alarm
     * @param time the time
     * @return the new value
     */
    public static VBoolean of(final Boolean value, final Alarm alarm, final Time time, String description) {
        return AdvancedBoolean.of(value, alarm, time , description,  "false" , "true");
    }

}
