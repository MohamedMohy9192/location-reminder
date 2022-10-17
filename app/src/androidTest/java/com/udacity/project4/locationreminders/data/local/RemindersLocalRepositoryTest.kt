package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //    TODO: Add testing implementation to the RemindersLocalRepository.kt
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Class under test
    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun createRepository() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        // Get a reference to the class under test
        remindersLocalRepository = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_getReminderById() = runBlocking {
        // GIVEN - Insert a Reminder.
        val reminder = ReminderDTO(
            title = "Title",
            description = "Description",
            location = "location",
            latitude = 11.22222,
            longitude = 512.484848
        )

        remindersLocalRepository.saveReminder(reminder)
        // WHEN - Get the Reminder by id from the Repository.
        val result = remindersLocalRepository.getReminder(reminder.id)
        result as Result.Success
        // THEN - Same Reminder is returned
        assertThat(result, notNullValue())
        assertThat(result.data.title, `is`("Title"))
        assertThat(result.data.description, `is`("Description"))
        assertThat(result.data.location, `is`("location"))
    }

    @Test
    fun saveReminders_ReturnRemindersList_notNull() = runBlocking {
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
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)
        remindersLocalRepository.saveReminder(reminder3)

        // WHEN - Get the Reminders from the database.
        val loadedList = remindersLocalRepository.getReminders()
        loadedList as Result.Success
        // Then
        assertThat(loadedList.data, notNullValue())
        assertThat(loadedList.data.size, `is`(3))
    }

    @Test
    fun deleteAllReminders_returnEmptyList() = runBlocking {
        // GIVEN - Insert a Reminder.
        val reminder1 = ReminderDTO(
            title = "Title",
            description = "Description",
            location = "location",
            latitude = 11.22222,
            longitude = 512.484848
        )
        remindersLocalRepository.saveReminder(reminder1)
        // WHEN - delete the Reminders from the database.
        remindersLocalRepository.deleteAllReminders()
        val loadedList = remindersLocalRepository.getReminders()
        loadedList as Result.Success
        // Then
        assertThat(loadedList.data, `is`(emptyList()))
        assertThat(loadedList.data.size, `is`(0))
    }

    @Test
    fun getReminderByID_reminderNotFound() = runBlocking{
        // arbitrary id
        val id = "5"
        val result = remindersLocalRepository.getReminder(id)
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }
}