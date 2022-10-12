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

    //    TODO: Add testing implementation to the RemindersDao.kt
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminder_GetById() = runBlockingTest {
        // GIVEN - Insert a Reminder.
        val reminder = ReminderDTO(
            title = "Title",
            description = "Description",
            location = "location",
            latitude = 11.22222,
            longitude = 512.484848
        )
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the Reminder by id from the database.
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - The loaded data contains the expected values.
        assertThat(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    @Test
    fun insertReminders_getReminders_areNotNull() = runBlockingTest {
        // GIVEN - Insert a Reminder.
        val reminder1 = ReminderDTO(
            title = "Title",
            description = "Description",
            location = "location",
            latitude = 11.22222,
            longitude = 512.484848
        )
        val reminder2 = ReminderDTO(
            title = "Title2",
            description = "Description2",
            location = "location2",
            latitude = 11.22222,
            longitude = 512.484848
        )
        val reminder3 = ReminderDTO(
            title = "Title3",
            description = "Description3",
            location = "location3",
            latitude = 11.22222,
            longitude = 512.484848
        )

        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // WHEN - Get the Reminders from the database.
        val loadedList = database.reminderDao().getReminders()
        assertThat(loadedList, notNullValue())
        assertThat(loadedList.size, `is`(3))
    }

    @Test
    fun insertReminders_getReminders_areEmpty() = runBlockingTest {
        // GIVEN - Insert a Reminder.
        val reminder1 = ReminderDTO(
            title = "Title",
            description = "Description",
            location = "location",
            latitude = 11.22222,
            longitude = 512.484848
        )
        database.reminderDao().saveReminder(reminder1)
        // WHEN - delete the Reminders from the database.
        database.reminderDao().deleteAllReminders()
        val loadedList = database.reminderDao().getReminders()
        assertThat(loadedList, `is`(emptyList()))
        assertThat(loadedList.size, `is`(0))
    }
}