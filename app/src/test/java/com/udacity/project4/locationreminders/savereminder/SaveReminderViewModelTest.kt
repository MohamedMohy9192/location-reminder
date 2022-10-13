package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataSource

    //Subject under test
    private lateinit var viewModel: SaveReminderViewModel

    //TODO: provide testing to the SaveReminderView and its live data objects

    @Before
    fun setupViewModel() {
        stopKoin()
        dataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @Test
    fun loadReminders_loading() = mainCoroutineRule.runBlockingTest {
        val reminder = ReminderDataItem(
            title = "Title",
            description = "Description",
            location = "location",
            latitude = 11.22222,
            longitude = 512.484848
        )
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder(reminder)
        // Then progress indicator is shown
        MatcherAssert.assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()
        // Then progress indicator is hidden
        MatcherAssert.assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun saveReminder_noTitleError() = mainCoroutineRule.runBlockingTest {

        val reminder = ReminderDataItem(
            title = "",
            description = "Description",
            location = "location",
            latitude = 11.22222,
            longitude = 512.484848
        )

        viewModel.validateAndSaveReminder(reminder)
        assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            `is`(R.string.err_enter_title)
        )
    }

    @Test
    fun saveReminder() {
        val reminder = ReminderDataItem(
            title = "Title",
            description = "Description",
            location = "location",
            latitude = 11.22222,
            longitude = 512.484848
        )
        viewModel.saveReminder(reminder)
        assertThat(
            viewModel.showToast.getOrAwaitValue(), `is`(
                ApplicationProvider.getApplicationContext<Context>()
                    .getString(R.string.reminder_saved)
            )
        )
    }

}