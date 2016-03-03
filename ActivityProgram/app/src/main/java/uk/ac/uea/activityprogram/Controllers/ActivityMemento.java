package uk.ac.uea.activityprogram.Controllers;

import java.io.Serializable;

import uk.ac.uea.activityprogram.Models.Settings;
import uk.ac.uea.framework.MementoState;

/**
 * Created by Kristiana on 12/01/2016.
 *
 * An implementation class of the Memento design pattern,
 * allows to save the Settings state when a user is created  and then to retrieve the default settings
 * if the user wants to return to them
 */
public class ActivityMemento extends MementoState implements Serializable {

    Settings defSettings;

    /**
     * Setter method for the memento  state of settings
     * @param settings the settings to be saved
     */
    public ActivityMemento(Settings settings){
        defSettings = settings;
    }


    /**
     * Getter method for the Default settings
     * @return default settings
     */
    public Settings getDefSettings() {
        return defSettings;
    }

    /**
     * Setter method for the settings
     * @param defSettings the default settings object
     */
    public void setDefSettings(Settings defSettings) {
        this.defSettings = defSettings;
    }

    /**
     * The restore method that restores the default state of the setting
     * @param dbHandler the db handler object
     * @param defaultSettings the default settings to be retrieved
     * @param useremail the user email the settings need to be retrieved for
     */
    public void restoreState(AppDBHandler dbHandler, ActivityMemento defaultSettings, String useremail){
        dbHandler.updateActivityprogram(useremail, defaultSettings.getDefSettings().getActivityProgram());
        dbHandler.updateSychInterval(useremail, Integer.toString(defaultSettings.getDefSettings().getSynchInterval()));
        dbHandler.updateNotifSound(useremail, Boolean.toString(defaultSettings.getDefSettings().getNotifSound()));
        dbHandler.updateNotifSound(useremail, Boolean.toString(defaultSettings.getDefSettings().getNotifVibration()));
    }
}
