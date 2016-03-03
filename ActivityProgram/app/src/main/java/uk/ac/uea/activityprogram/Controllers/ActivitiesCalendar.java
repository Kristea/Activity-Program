package uk.ac.uea.activityprogram.Controllers;

import android.content.Intent;
import android.graphics.RectF;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.ac.uea.activityprogram.Models.AppUser;
import uk.ac.uea.activityprogram.R;
import uk.ac.uea.framework.implementation.Event;


/**
 * The UEA activities calendar, extends the Base Calendar allowing to have a calendar interface with several views.
 * Shows the events for the users selected school and allows to subscribe to and view more details about them.
 *
 */
public class ActivitiesCalendar extends BaseCalendar {

    private AppUser user;
    private Calendar date;

    /**
     * Using Calendar library method
     *
     * Populate the calendar with events based on year and month,when month or year changes to
     * allow calendar scrolling
     *
     * @param newYear year of the events required by the view.
     * @param newMonth month of the events required by the view. 1 based (not like JAVA API) --> January = 1 and December = 12
     * @return a list of the events happening during the specified month
     */
    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with events from our database of events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        user = (AppUser)getIntent().getSerializableExtra("User");
        AppDBHandler dbHandler = AppDBHandler.getInstance(this);
        ArrayList<Event> allEvents = dbHandler.getAllEvents();

        int hour,min,day,month,year;
        String description,descriptionFull,location;
        Calendar startTime;
        Calendar endTime;
        for(Event e: allEvents){
            if(e.getId().contains(user.getSettings().getActivityProgram())) {
                startTime = Calendar.getInstance();
                //split up time from storage
                hour = Integer.parseInt(e.getTime().substring(0, e.getTime().indexOf(":")));
                min = Integer.parseInt(e.getTime().substring(e.getTime().indexOf(":") + 1));
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, min);

                //split up date
                String[] date = e.getDate().split("/");
                day = Integer.parseInt(date[0]);
                month = Integer.parseInt(date[1]);
                year = Integer.parseInt(date[2]);
                startTime.set(Calendar.DAY_OF_MONTH, day);
                startTime.set(Calendar.MONTH, month - 1);
                startTime.set(Calendar.YEAR, year);

                //end time will most likely be same day
                endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR, 1); //set duration of event
                endTime.set(Calendar.MONTH, month - 1);


                //create the Week view event
                WeekViewEvent event = new WeekViewEvent(e.hashCode(), getEventTitle(startTime), startTime, endTime);

                //set colour depending on event type(ie business, cmp, arts etc)
                if (e.getId().contains("cmp")) {
                    event.setColor(getResources().getColor(R.color.event_color_01));
                } else if (e.getId().contains("bis")) {
                    event.setColor(getResources().getColor(R.color.event_color_02));
                } else if (e.getId().contains("art")) {
                    event.setColor(getResources().getColor(R.color.event_color_03));
                } else {
                    event.setColor(getResources().getColor(R.color.event_color_04));
                }
                descriptionFull = e.getDescription();
                //split location and description by @ if location specified
                if (descriptionFull.contains("@")) {
                    description = descriptionFull.substring(0, descriptionFull.indexOf(" @"));
                    location = descriptionFull.substring(descriptionFull.indexOf("@") + 1);
                } else {
                    description = descriptionFull;
                    location = "TBA";
                }


                //set display name of event
                event.setName(e.getName() + ",\n" + description + "#" + e.getId());
                event.setLocation(location);
                events.add(event);
            }
        }

        // Return only the events that matches newYear and newMonth.
        List<WeekViewEvent> matchedEvents = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent event : events) {
            if (eventMatches(event, newYear, newMonth)) {
                matchedEvents.add(event);
            }
        }
        return matchedEvents;

    }

    /**
     * Checks if an event falls into a specific year and month.
     * @param event The event to check for.
     * @param year The year.
     * @param month The month.
     * @return True if the event matches the year and month.
     */
    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    /**
     * What happens when an event is long pressed
     * if event isn't in users subscribed events -> subscribe,
     * else notify user they are already subscribed
     *
     * @param event
     * @param eventRect
     */
    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        AppDBHandler dbHandler = AppDBHandler.getInstance(this);
        //create a new arraylist and populate with user events
        ArrayList<Event> userEvents = user.getSubscribedEvents();

        //initialise variables
        String eventID =  event.getName().substring(event.getName().indexOf("#")+1);
        String eventPrintName = event.getName().substring(0, event.getName().indexOf(","));
        Date newDate = new Date(event.getStartTime().getTimeInMillis());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String changed = df.format(newDate);
        String[] splitDate = changed.split(" ");
        String eventDescription = event.getName().substring(event.getName().indexOf(",")+1,event.getName().indexOf("#"));

        Boolean subscribe = true;

        for(Event e: userEvents) {
            if (eventID.compareTo(e.getId()) == 0) {
                subscribe = false;
                break;
            }
        }
        if(subscribe){
            Toast.makeText(this, "Subscribed to event : " + eventPrintName + " on " +event.getStartTime().getTime() , Toast.LENGTH_LONG).show();
            //need to refresh database or page so that table is updated
            dbHandler.insertEventSubscription(user.getEmail(), eventID);
            user.getSubscribedEvents().add(new Event(eventID, eventPrintName,eventDescription,splitDate[0],splitDate[1]));


        }else {
            Toast.makeText(this, "Already Subscribed to : " + eventPrintName, Toast.LENGTH_SHORT).show();
        }


    }
    /**
     * What happens on long pressing an empty slot- nothing in our app
     * @param time the time that is passed in
     */
    @Override
    public void onEmptyViewLongPress(Calendar time) {

    }

    /**
     * What happens when back button is pressed in android
     * Saves user and events objects and passes them back within an intent to previous screen using setResult
     */
    @Override
    public void onBackPressed(){
        ArrayList<Event> events = (ArrayList<Event>)getIntent().getSerializableExtra("Events");
        Intent newIntent = new Intent(this, HomeScreen.class);
        newIntent.putExtra("Events",events);
        newIntent.putExtra("User", user);
        setResult(RESULT_OK, newIntent);
        super.onBackPressed();
        finish();
    }
}

