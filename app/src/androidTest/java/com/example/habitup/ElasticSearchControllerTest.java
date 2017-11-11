package com.example.habitup;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.habitup.Controller.ElasticSearchController;
import com.example.habitup.Model.Habit;
import com.example.habitup.Model.HabitEvent;
import com.example.habitup.Model.UserAccount;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;


/**
 * Created by Ty on 2017-11-08.
 */
@RunWith(AndroidJUnit4.class)
public class ElasticSearchControllerTest {

    @Test
    public void testAddUsersTask() {
        UserAccount u1 = new UserAccount("theise", "Tyler Heise", null);
        UserAccount u2 = new UserAccount("ezakirova", "E Z", null);

        UserAccount u3 = new UserAccount("wtf", "E Z", null);

        //private ArrayList<UserAccount> userList = new ArrayList<UserAccount>();
        String x = u1.getUsername();
        String y = u2.getUsername();

        ElasticSearchController.AddUsersTask addUsersTask = new ElasticSearchController.AddUsersTask();

        addUsersTask.execute(u3);

        Log.i("DeBug", "u3id is: " + u3.getESID());
    }

    @Test
    public void testGetUsersTask() {


        UserAccount u3 = new UserAccount("please work", "E Z", null);
        ElasticSearchController.AddUsersTask addUsersTask = new ElasticSearchController.AddUsersTask();

        addUsersTask.execute(u3);

        ElasticSearchController.GetUser getUserTask = new ElasticSearchController.GetUser();
        ArrayList<UserAccount> users = new ArrayList<>();

        getUserTask.execute(u3.getUsername());

        try {
            users = getUserTask.get();
        } catch (Exception e) {
            Log.i("Error", "Failed to get the User from the async object");
        }

        Log.i("Debug", "The length of the resulting list is: " + Integer.toString(users.size()));
        Log.i("Debug", "The username of the founduser is: " + users.get(0).getUsername());
        assertTrue (users.get(0).getUsername().equals(u3.getUsername()));

    }

    @Test
    public void testAddHabitTask() {
        Boolean[] schedule = {true};
        Habit h1 = new Habit(1, "testHabit1", "to test", "Physical", null, schedule);
        h1.setHID(0);
        Habit h2 = new Habit(2, "testHabit2", "to test", "Physical", null, schedule);
        h2.setHID(1);

        ElasticSearchController.AddHabitsTask addHabitsTask = new ElasticSearchController.AddHabitsTask();

        addHabitsTask.execute(h1);

        ElasticSearchController.AddHabitsTask addHabitsTask2 = new ElasticSearchController.AddHabitsTask();

        addHabitsTask2.execute(h2);

    }

    @Test
    public void testGetHabitTask() {
        ElasticSearchController.GetHabitsTask getHabitsTask = new ElasticSearchController.GetHabitsTask();
        getHabitsTask.execute("1");

        ArrayList<Habit> habits = new ArrayList<>();

        try {
            habits = getHabitsTask.get();
        } catch (Exception e) {
            Log.i("Error", "Failed to get the User from the async object");
        }

        assertTrue(habits.get(0).getHID() == 1);

    }


    @Test
    public void testAddHabitEventTask() {

        HabitEvent he1 = new HabitEvent(3, 3);
        HabitEvent he2 = new HabitEvent(3, 4);

        ElasticSearchController.AddHabitEventsTask addHabitEventsTask = new ElasticSearchController.AddHabitEventsTask();

        addHabitEventsTask.execute(he1);

        ElasticSearchController.AddHabitEventsTask addHabitEventsTask2 = new ElasticSearchController.AddHabitEventsTask();

        addHabitEventsTask2.execute(he2);

    }


    @Test
    public void testGetHabitEventsUidTask() {
        ElasticSearchController.GetHabitEventsByUidTask getHabitEventsTask = new ElasticSearchController.GetHabitEventsByUidTask();

        getHabitEventsTask.execute("3");

        ArrayList<HabitEvent> habitEvents = new ArrayList<HabitEvent>();

        try {
            habitEvents = getHabitEventsTask.get();

        } catch (Exception e) {
            Log.i("Error", "Failed to get the Habits from the async object");
        }

        String Alen = Integer.toString(habitEvents.size());
        Log.i("DeBug", "length of returned list is: " + Alen);

        for (HabitEvent habitEvent: habitEvents){
            Log.i("DeBug", "the uid is: " +Integer.toString(habitEvent.getUID()));
            Log.i("Debug", "the hid is: " + Integer.toString(habitEvent.getHID()));
        }

        //assertTrue (habitEvents.get(0).getComment().equals("Notnice"));
        //assertTrue (habitEvents.get(1).getComment().equals("nice"));
    }

    @Test
    public void testGetMaxUid() {
        ElasticSearchController.GetMaxUidTask getMaxUidTask = new ElasticSearchController.GetMaxUidTask();

        getMaxUidTask.execute();

        Integer MaxUID = -1;

        try {
            MaxUID = getMaxUidTask.get();
        } catch (Exception e) {
            Log.i("Error", "Failed to get the maxUid from the async object");
        }

        Log.i("Debug", "maxUid returns: " + Integer.toString(MaxUID));

        assertTrue (MaxUID != -1);
        assertTrue (MaxUID == 2);
    }

    @Test
    public void testGetMaxHid() {
        ElasticSearchController.GetMaxHidTask getMaxHidTask = new ElasticSearchController.GetMaxHidTask();

        getMaxHidTask.execute();

        Integer MaxHID = -1;

        try {
            MaxHID = getMaxHidTask.get();
        } catch (Exception e) {
            Log.i("Error", "Failed to get the maxUid from the async object");
        }

        assertTrue (MaxHID != -1);
        assertTrue (MaxHID == 2);
    }

}
