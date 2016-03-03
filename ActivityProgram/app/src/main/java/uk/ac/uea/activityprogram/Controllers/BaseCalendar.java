package uk.ac.uea.activityprogram.Controllers;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import uk.ac.uea.activityprogram.R;

/**
 * This is a base activity which contains week view and all the codes necessary to initialize the
 * week view for the calendar.
 *
 *
 * Created by Raquib-ul-Alam Kanak on 1/3/2014. - Used and edited by team Jamdroid.
 * Website: http://alamkanak.github.io
 */
public abstract class BaseCalendar extends Activity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    private Calendar date;

    /**
     * What happens when the calendar page is created
     *
     * @param savedInstanceState the saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_calendar);

        date = (Calendar)getIntent().getSerializableExtra("Openday");
        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        mWeekView.goToDate(date);
        mWeekView.goToHour(10);


    }

    /**
     * Create menu which gives options for calendar views
     * @param menu the menu
     * @return a boolean of if an item was picked
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar, menu);
        return true;
    }

    /**
     * Method that gets what item was selected in the upper menu
     * @param item item selected in menu
     * @return a boolean of if an item was picked
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" d/M", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");

                /*if (hour > 11){
                    s = hour-12 + "PM";
                }else if(hour ==0){
                    s = "12 AM";
                }
                else{
                    s = hour + "PM";
                }
                return s;*/
            }
        });
    }

    /**
     * Create an event title including the time and date of event + name in database.
     * @param time time of event
     * @return String of the event title
     */
    protected String getEventTitle(Calendar time) {
        return String.format("Event at %02d:%02d %d/%s", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE),time.get(Calendar.DAY_OF_MONTH),time.get(Calendar.MONTH)+1);
    }


    /**
     * What happens when an event is clicked - bring up popup with more info (allow to close)
     * @param event event that was clicked
     * @param eventRect event rectangle that was clicked
     */
    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        String[] full = event.getName().split("#");
        String name = full[0];
        String code = full[1];
        Toast.makeText(this, "Details : \n" + name + "Location : " + event.getLocation() + "\nTime : " + event.getStartTime().getTime(), Toast.LENGTH_LONG).show();
    }


    /**
     * What happens when an event is long pressed
     * @param event event that was long pressed
     * @param eventRect event rectangle that was long pressed
     */
    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
    }

    /**
     * Getter method to get the weekview
     * @return the weekview object
     */
    public WeekView getWeekView() {

        return mWeekView;
    }



}
