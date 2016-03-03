package uk.ac.uea.activityprogram.Models;

import java.io.Serializable;

/**
 * Created by Claudia and Kristiana on 03/01/2016.
 *
 * The settins class that manages the user settings and allows setting manipulation
 */
public class Settings implements Serializable {
    private String activityProgram;
    private int synchInterval;
    private boolean notifSound;
    private boolean notifVibration;

    /**
     * default constructor
     */
    public Settings(){}

    /**
     * Constructor that takest parameters for creating a settings object
     * @param activityProgram string activity program
     * @param synchInterval integer synchronisation interval
     * @param notifSound boolean for notification sound setting
     * @param notifVibration boolean for notification vibration setting
     */
    public Settings(String activityProgram, int synchInterval, boolean notifSound, boolean notifVibration) {
        this.activityProgram = activityProgram;
        this.synchInterval = synchInterval;
        this.notifSound = notifSound;
        this.notifVibration = notifVibration;
    }

    /**
     * Getter for Activity program
     * @return activity program string
     */
    public String getActivityProgram() {
        return activityProgram;
    }

    /**
     * Setter for Activity program
     * @param activityProgram the activity program
     */
    public void setActivityProgram(String activityProgram) {
        this.activityProgram = activityProgram;
    }

    /**
     *  Getter for notification sound
     * @return true or false for  sound
     */
    public boolean getNotifSound() {
        return notifSound;
    }

    /**
     * Setter for notification sound
     * @param notifSound true or false setting for sound
     */
    public void setNotifSound(boolean notifSound) {
        this.notifSound = notifSound;
    }

    /**
     * Getter for the synchronisation interval
     * @return integer synchronisation interval
     */
    public int getSynchInterval() {
        return synchInterval;
    }

    /**
     * Setter for synchronisation interval
     * @param synchInterval integer value of synchronisation interval
     */
    public void setSynchInterval(int synchInterval) {
        //change phone setting
        this.synchInterval = synchInterval;
    }

    /**
     * Getter for notification vibration
     * @return true or false for notification vibration
     */
    public boolean getNotifVibration() {
        return notifVibration;
    }

    /**
     * Setter for notification vibration
     * @param notifVibration true or false setting for notification vibration
     */
    public void setNotifVibration(boolean notifVibration) {
        this.notifVibration = notifVibration;
    }

    /**
     * To string method
     * @return the to string of settings
     */
    @Override
    public String toString() {
        return "Settings{" +
                "activityProgram='" + activityProgram + '\'' +
                ", synchInterval=" + synchInterval +
                ", notifSound=" + notifSound +
                ", notifVibration=" + notifVibration +
                '}';
    }
}
