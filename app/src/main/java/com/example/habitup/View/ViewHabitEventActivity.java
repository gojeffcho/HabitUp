package com.example.habitup.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitup.Controller.ElasticSearchController;
import com.example.habitup.Controller.HabitUpApplication;
import com.example.habitup.Controller.HabitUpController;
import com.example.habitup.Model.Habit;
import com.example.habitup.Model.HabitEvent;
import com.example.habitup.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * This is the activity where the user can see their habit event history, which displays
 * a list of all the habit events completed, sorted by most recent date. To view an event's
 * details, the user must click on an event. To either edit or delete an event, the user must
 * click and hold on an event, which will open a context menu.
 * <p>
 * The user can filter the events either by comment text, habit type, or both. Once text is
 * entered in the comment field or a habit is selected from the dropdown menu, the matching
 * events will immediately be displayed.
 * <p>
 * The drawer navigation menu can be accessed here.
 *
 * @author Shari Barboza
 */
public class ViewHabitEventActivity extends BaseActivity {

    private final Context context = ViewHabitEventActivity.this;
    public static final int NEW_EVENT = 1;
    public static final int VIEW_EVENT = 2;
    public static final int EDIT_EVENT = 3;
    public static final String HABIT_EVENT_UID = "HABIT_EVENT_UID";
    public static final String HABIT_EVENT_HID = "HABIT_EVENT_HID";
    public static final String HABIT_EVENT_EID = "HABIT_EVENT_EID";
    public static final String HABIT_EVENT_ACTION = "HABIT_EVENT_ACTION";

    // Position of event in list view
    private int position = -1;

    private ArrayList<HabitEvent> events;
    private RecyclerView eventListView;
    private EventListAdapter eventAdapter;
    private EditText commentFilter;
    private ArrayList<Habit> habitTypes;
    private Spinner habitSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("HabitUpDEBUG", "ViewHabitEventActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        events = new ArrayList<>();
        eventListView = (RecyclerView) findViewById(R.id.event_list);
        eventListView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        eventListView.addItemDecoration(itemDecoration);
    }

    /**
     * Invoked whenever the activity starts
     */
    @Override
    public void onStart() {
        super.onStart();

        // Retrieve events from ES for user
        ElasticSearchController.GetHabitEventsByUidTask getHabitEvents = new ElasticSearchController.GetHabitEventsByUidTask();
        getHabitEvents.execute(HabitUpApplication.getCurrentUIDAsString());
        try {
            events.clear();
            events.addAll(getHabitEvents.get());
        } catch (Exception e) {
            Log.i("HabitUpDEBUG", "ViewHabitEvent - Couldn't get HabitEvents");
        }

        eventListView = (RecyclerView) findViewById(R.id.event_list);
        eventListView.setHasFixedSize(true);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        eventListView.addItemDecoration(itemDecoration);

        eventAdapter = new EventListAdapter(this, events);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);

        eventListView.setAdapter(eventAdapter);
        eventListView.setLayoutManager(layoutManager);

        eventAdapter.notifyDataSetChanged();

        eventAdapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                eventAdapter.setPosition(eventListView.getChildAdapterPosition(v));
                return false;
            }
        });

        eventAdapter.setOnItemClickListener(new EventListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                position = pos;
                goToEditActivity(VIEW_EVENT);
            }
        });

        commentFilter = (EditText) findViewById(R.id.filter_comment);
        commentFilter.setText("");
        commentFilter.setOnClickListener(spinCl);
        commentFilter.setOnKeyListener(cFilter);

        // Sort by completedate
        Collections.sort(events);

        // Display if there are no events
        if (events.size() == 0) {
            TextView subHeading = (TextView) findViewById(R.id.select_event);
            subHeading.setText("You currently have no habit events.");
        }

        // Set up habit type filter spinner
        habitSpinner = (Spinner) findViewById(R.id.filter_habit_spinner);
        ArrayAdapter<String> habitAdapter = new ArrayAdapter<>(this, R.layout.habit_spinner);
        habitAdapter.add("All Habit Types");

        // Set up habit types list
        ElasticSearchController.GetUserHabitsTask userHabits = new ElasticSearchController.GetUserHabitsTask();
        userHabits.execute(HabitUpApplication.getCurrentUIDAsString());

        habitTypes = null;

        try {
            habitTypes = userHabits.get();
        } catch (Exception e) {
            Log.i("HabitUpDEBUG", "ViewHabitEvent, couldn't get HabitTypes for user");
            habitTypes = new ArrayList<>();
        }

        // Populate spinner with habit type names
        for (Habit habit : habitTypes) {
            habitAdapter.add(habit.getHabitName());
        }

        habitSpinner.setAdapter(habitAdapter);

        // Spinner select
        habitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                // Reset comment search when filtering by Habit
                commentFilter.setText("");

                // Refactoring...
//                refreshEvents(); // refreshes through ES re-get
//                ArrayList<HabitEvent> filtList = new ArrayList<HabitEvent>();
//                filtList.clear();
//
//                if (pos == 0) {
//                    filtList.addAll(events);
//                }
//                else {
//                    for (HabitEvent e : events) {
//                        if (e.getHID()==habitTypes.get(pos-1).getHID()) {
//                            filtList.add(e);
//                        }
//                    }
//                }
//                Collections.sort(filtList);
//
//                events.clear();
//                events.addAll(filtList);
//                eventAdapter.notifyDataSetChanged();

                if (pos == 0) {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Highlight events row in drawer
        navigationView.setCheckedItem(R.id.events);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        position = eventAdapter.getPosition();

        switch (item.getItemId()) {
            case 1:
                goToEditActivity(EDIT_EVENT);
                return true;
            case 2:
                deleteEvent();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_action_bar:
                Intent addEventIntent = new Intent(ViewHabitEventActivity.this, AddHabitEventActivity.class);
                addEventIntent.putExtra("profile", 0);
                startActivityForResult(addEventIntent, NEW_EVENT);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToEditActivity(int requestCode) {
        setResult(RESULT_OK);
        Intent editIntent = new Intent(context, EditHabitEventActivity.class);
        int uid = HabitUpApplication.getCurrentUID();
        int hid = eventAdapter.getItem(position).getHID();
        String eid = eventAdapter.getItem(position).getEID();

//        Log.i("HabitUpDEBUG", "ViewHabitEvent eid " + eid);

        editIntent.putExtra(HABIT_EVENT_UID, uid);
        editIntent.putExtra(HABIT_EVENT_HID, hid);
        editIntent.putExtra(HABIT_EVENT_EID, eid);
        editIntent.putExtra(HABIT_EVENT_ACTION, requestCode);
        editIntent.putExtra("profile", 0);
        startActivity(editIntent);
    }

    private void deleteEvent() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ViewHabitEventActivity.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete this habit event?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HabitUpController.deleteHabitEvent(eventAdapter.getItem(position)); // ES delete
                eventAdapter.removeItem(position); // app view delete
                eventListView.setAdapter(eventAdapter);
                eventAdapter.notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    private void refreshEvents() {
        ElasticSearchController.GetHabitEventsByUidTask getHabitEvents = new ElasticSearchController.GetHabitEventsByUidTask();
        getHabitEvents.execute(HabitUpApplication.getCurrentUIDAsString());
        try {
            events.clear();
            events.addAll(getHabitEvents.get());
        } catch (Exception e) {
            Log.i("HabitUpDEBUG", "ViewHabitEvent - Couldn't get HabitEvents");
        }
    }

    EditText.OnKeyListener cFilter = new EditText.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if ((keyEvent.getAction() == KeyEvent.ACTION_UP) && (i == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager methodMan = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                methodMan.toggleSoftInput(0, 0);

                ArrayList<HabitEvent> commentMatches = null;
                String searchText = commentFilter.getText().toString().trim().toLowerCase(Locale.getDefault());

                if (searchText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Error: Please enter a string to search comments", Toast.LENGTH_SHORT);
                } else {
                    ElasticSearchController.GetHabitEventsForCommentMatch commentSearch = new ElasticSearchController.GetHabitEventsForCommentMatch();
                    commentSearch.execute(searchText);
                    try {
                        commentMatches = commentSearch.get();
                    } catch (Exception e) {
                        Log.i("HabitUpDEBUG", "ViewHabitEvents/CommentSearch - failed to get matching HabitEvents");
                    }
                }

                events.clear();
                events.addAll(commentMatches);
                eventAdapter.notifyDataSetChanged();

                // This way lies madness.  Refactoring...
//                refreshEvents(); // refreshes through ES re-get
//                ArrayList<HabitEvent> filtEvents = filterComSort(text,events);
//                events.clear();
//                events.addAll(filtEvents);
//                eventAdapter.notifyDataSetChanged();
            }
            return true;
        }
    };

    EditText.OnClickListener spinCl = new EditText.OnClickListener() {
        @Override
        public void onClick(View view) {
            habitSpinner.setSelection(0);
        }
    };

}

