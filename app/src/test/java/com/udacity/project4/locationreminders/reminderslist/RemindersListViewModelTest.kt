package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.base.CharMatcher.isNot
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    // Executes each task synchronously using Architecture Components.
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var viewModel: RemindersListViewModel

    // Use a fake dataSource to be injected into the view model
    private lateinit var dataSource: FakeDataSource

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        stopKoin()
        dataSource = FakeDataSource()
        viewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource
        )
    }

    @Test
    fun check_loading() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()
        // Load the Reminders in the view model
        viewModel.loadReminders()
        // Then progress indicator is shown
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()
        // Then progress indicator is hidden
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun shouldReturnError() = mainCoroutineRule.runBlockingTest {
        dataSource.setReturnError(true)
        viewModel.loadReminders()
        assertThat(viewModel.showSnackBar.getOrAwaitValue(), `is`("Test Exception"))
    }

    @Test
    fun insertReminders_remindersAreNotEmpty() = mainCoroutineRule.runBlockingTest {
        val reminder = ReminderDTO(
            title = "Title",
            description = "Description",
            location = "location",
            latitude = 11.22222,
            longitude = 512.484848
        )
        dataSource.saveReminder(reminder)
        viewModel.loadReminders()
        val result = viewModel.remindersList.getOrAwaitValue()
        assertThat(result, `is`(not(emptyList())))
    }
}