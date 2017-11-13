package com.example.habitup.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitup.Controller.ElasticSearchController;
import com.example.habitup.Controller.HabitUpApplication;
import com.example.habitup.Controller.HabitUpController;
import com.example.habitup.Model.Attributes;
import com.example.habitup.Model.Habit;
import com.example.habitup.Model.UserAccount;
import com.example.habitup.R;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {

    private ArrayList<Habit> habitsArrayList;
    private ListView habitListView;
    private ProfileHabitsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DEBUG
//        UserAccount testUser = null;
//        try {
//            testUser = HabitUpApplication.getUserAccount("gojeffcho");
//        } catch (Exception e) {
//            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//
//        if (testUser != null) {
//            HabitUpApplication.setCurrentUser(testUser);
//        } else {
//            Log.i("HabitUpDEBUG", "Couldn't get test user.");
//        }

//        UserAccount testUser = new UserAccount("gojeffcho", "Jeef Chee", null);
//        HabitUpApplication.addUserAccount(testUser);
//        HabitUpApplication.setCurrentUser(testUser);
        // DEBUG



        // Initialize list view for today's habits
        habitListView = (ListView) findViewById(R.id.habit_listview);
        LayoutInflater inflater = this.getLayoutInflater();
        View profileView = inflater.inflate(R.layout.profile_banner, habitListView, false);
        habitListView.addHeaderView(profileView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Highlight profile in drawer
        navigationView.setCheckedItem(R.id.profile);

        // Get the user
        UserAccount currentUser = HabitUpApplication.getCurrentUser();

        // Set user's photo
        Bitmap photo = currentUser.getPhoto();

        if (photo != null) {
            CircleImageView profilePic = (CircleImageView) findViewById(R.id.drawer_pic);
            profilePic.setImageBitmap(photo);
        }

        // Set user's display name
        TextView nameField = (TextView) findViewById(R.id.username);

        nameField.setText(currentUser.getRealname());

        // Set user's level
        TextView levelField = (TextView) findViewById(R.id.user_level);
        levelField.setText("Level " + String.valueOf(currentUser.getLevel()));

        // Set user's level up in
        TextView levelUpField = (TextView) findViewById(R.id.level_title);
        levelUpField.setText("Level up in " + String.valueOf(currentUser.getXPtoNext() - currentUser.getXP()) + " XP");

        // Set progress bar
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setProgress(currentUser.getXP());
        progressBar.setMax(currentUser.getXPtoNext());

        // Get user attributes
        Attributes userAttrs = HabitUpApplication.getCurrentAttrs();

        // Set user's Mental value
        TextView attr2Field = (TextView) findViewById(R.id.attribute2_value);
        attr2Field.setText(String.valueOf(userAttrs.getValue("Mental")));

        // Set user's Physical value
        TextView attr1Field = (TextView) findViewById(R.id.attribute1_value);
        attr1Field.setText(String.valueOf(userAttrs.getValue("Physical")));

        // Set user's Discipline value
        TextView attr3Field = (TextView) findViewById(R.id.attribute3_value);
        attr3Field.setText(String.valueOf(userAttrs.getValue("Social")));

        // Set user's Social value
        TextView attr4Field = (TextView) findViewById(R.id.attribute4_value);
        attr4Field.setText(String.valueOf(userAttrs.getValue("Discipline")));

        // Set up the array and adapter
        habitsArrayList = HabitUpController.getTodaysHabits();
        Collections.sort(habitsArrayList);
        adapter = new ProfileHabitsAdapter(this, R.layout.todays_habits, habitsArrayList);
        habitListView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        if (habitsArrayList.size() == 0) {
            TextView subHeading = (TextView) findViewById(R.id.today_subheading);
            subHeading.setText(getString(R.string.no_habits));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                Intent editIntent = new Intent(this, EditProfileActivity.class);
                startActivity(editIntent);
                return true;
            case R.id.stats_profile:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            int habit_pos = data.getExtras().getInt("habit_pos");
            if (resultCode == RESULT_CANCELED && habit_pos >= 0) {
                View view = habitListView.getChildAt(habit_pos + 1);
                CheckBox lastChecked = view.findViewById(R.id.today_habit_checkbox);
                lastChecked.setChecked(false);
                lastChecked.setClickable(false);
            }
    }

}
