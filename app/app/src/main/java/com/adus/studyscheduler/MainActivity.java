package com.adus.studyscheduler;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.adus.contentscheduler.dao.entity.Calendar;
import com.adus.contentscheduler.dao.entity.Content;
import com.adus.contentscheduler.dao.entity.ScheduledContent;
import com.adus.contentscheduler.dao.repository.spi.CalendarRepository;
import com.adus.contentscheduler.dao.repository.spi.ContentRepository;
import com.adus.studyscheduler.crud.StudySchedulerRoomDatabase;
import com.adus.studyscheduler.crud.repository.RepositoryFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ContentRepository contentRepository = RepositoryFactory.contentRepository(getApplication());
        CalendarRepository calendarRepository = RepositoryFactory.calendarRepository(getApplication());

        // hook for testing if the crud operations work in a live app
        // todo: remove mock-data(and this testing code), once a steel-thread UI-flow is established
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            MockData.setupSeedDataInDatabase(getApplicationContext());
            StudySchedulerRoomDatabase.databaseWriteExecutor.execute(() -> {
                Calendar calendar = calendarRepository.getCalendarSlice(new Date(), new Date());
                Content programme = contentRepository.findProgramme();
                ScheduledContent con = new ScheduledContent();
                con.setContent(programme);
                con.setDuration(10.0f);
                List<ScheduledContent> sch = Arrays.asList(con);
                calendar.getDaySchedules().get(0).setScheduledContents(sch);
                calendarRepository.saveCalendar(calendar);
                System.out.println(calendar);
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
