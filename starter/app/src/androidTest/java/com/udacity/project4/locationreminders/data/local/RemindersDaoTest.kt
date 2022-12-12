package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder_getReminderId() = runBlockingTest {
        val reminder = ReminderDTO("title", "description", "location", 9.82723, 5.24340)
        database.reminderDao().saveReminder(reminder)

        val result = database.reminderDao().getReminderById(reminder.id)

        assertThat(result as ReminderDTO, notNullValue())
        assertThat(result.id, `is`(reminder.id))
        assertThat(result.title, `is`(reminder.title))
        assertThat(result.description, `is`(reminder.description))
        assertThat(result.location, `is`(reminder.location))
        assertThat(result.latitude, `is`(reminder.latitude))
        assertThat(result.longitude, `is`(reminder.longitude))
    }

    @Test
    fun saveReminders_deleteReminders() = runBlockingTest {
        val reminder1 = ReminderDTO("title1", "description1", "location1", 9.82723, 5.24340)
        val reminder2 = ReminderDTO("title2", "description2", "location2", 9.82723, 5.24340)
        val reminder3 = ReminderDTO("title3", "description3", "location3", 9.82723, 5.24340)

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)
        database.reminderDao().deleteAllReminders()

        val remindersList = database.reminderDao().getReminders()
        assertThat(remindersList, `is`(emptyList()))
    }

    @Test
    fun getRemindersFromDatabase() = runBlockingTest {
        val reminder1 = ReminderDTO("title1", "description1", "location1", 9.82723, 5.24340)
        val reminder2 = ReminderDTO("title2", "description2", "location2", 9.82723, 5.24340)
        val reminder3 = ReminderDTO("title3", "description3", "location3", 9.82723, 5.24340)

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        val remindersList = database.reminderDao().getReminders()

        assertThat(remindersList, `is`(notNullValue()))
    }
}
