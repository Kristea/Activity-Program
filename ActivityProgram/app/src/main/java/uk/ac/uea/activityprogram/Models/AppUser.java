
package uk.ac.uea.activityprogram.Models;

import java.io.Serializable;
import java.util.ArrayList;

import uk.ac.uea.activityprogram.Controllers.ActivityMemento;
import uk.ac.uea.framework.implementation.Originator;
import uk.ac.uea.framework.implementation.CareTaker;
import uk.ac.uea.framework.implementation.Event;
import uk.ac.uea.framework.implementation.User;

/**
 * Created by Kristiana on 18/11/2015.
 *
 * This class contains and allows manipulation of specific user data for the app, such as what events they are subscribed
 * to and their personalised settingsActivity
 * Also allows for the Memento Design pattern implementation and contains getters and setter for the Memento , Originator and Caretaker
 */
public class AppUser extends User implements Serializable{

    private ArrayList<Event> subscribedEvents;
    private Settings settings;
    private ActivityMemento mementoState;
    private Originator originator;
    private CareTaker caretaker;

    /**
     * Constructor for appUser
     */
    public AppUser(){}

    /**
     * Get the arraylist of subscribed events
     * @return arraylist of events
     */
    public ArrayList<Event> getSubscribedEvents() {
        return subscribedEvents;
    }

    /**
     * Set the arraylist of subscribed events
     * @param subscribedEvents arraylist of subscribed events
     */
    public void setSubscribedEvents(ArrayList<Event> subscribedEvents) {
        this.subscribedEvents = subscribedEvents;
    }

    /**
     * Getter for settings object
     * @return Settings object
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Setter for settings object
     * @param settings settings object
     */
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    /**
     * Get memento state object
     * @return  ActivityMemento object
     */
    public ActivityMemento getMementoState() {
        return mementoState;
    }

    /**
     * Set the memento state  using originator and caretaker
     * @param mementoState the ActivityMemento object
     */
    public void setMementoState(ActivityMemento mementoState) {
        this.mementoState = mementoState;
        originator.setState(mementoState);
        caretaker.addMemento(originator.saveToMemory());
    }

    /**
     *Get the originator object
     * @return Originator object
     */
    public Originator getOriginator() {
        return originator;
    }

    /**
     *Set the Originator object
     * @param originator Originator object
     */
    public void setOriginator(Originator originator) {
        this.originator = originator;
    }

    /**
     *Get the CareTaker object
     * @return CareTaker
     */
    public CareTaker getCaretaker() {
        return caretaker;
    }

    /**
     * Set the CareTaker object
     * @param caretaker CareTaker object
     */
    public void setCaretaker(CareTaker caretaker) {
        this.caretaker = caretaker;
    }
}
