package com.udacity.project4.locationreminders.reminderslist

import org.koin.core.context.stopKoin
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    private lateinit var reminderViewModel: RemindersListViewModel
    private lateinit var remindersRepository: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Before
    fun setupViewModel() {
        remindersRepository = FakeDataSource()
        reminderViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadReminders_loading() {
        mainCoroutineRule.pauseDispatcher()

        reminderViewModel.loadReminders()

        assertThat(reminderViewModel.showLoading.getOrAwaitValue()).isTrue()

        mainCoroutineRule.resumeDispatcher()

        assertThat(reminderViewModel.showLoading.getOrAwaitValue()).isFalse()

    }

    @Test
    fun loadReminders_remainderLisIstNotEmpty() = mainCoroutineRule.runBlockingTest  {
        val reminder = ReminderDTO("My Store", "Pick Stuff", "Abuja", 6.454202, 7.599545)

        remindersRepository.saveReminder(reminder)
        reminderViewModel.loadReminders()

        assertThat(reminderViewModel.remindersList.getOrAwaitValue()).isNotEmpty()
    }

    @Test
    fun loadReminders_snackBarUpdated() {
        mainCoroutineRule.pauseDispatcher()

        remindersRepository.setReturnError(true)

        reminderViewModel.loadReminders()

        mainCoroutineRule.resumeDispatcher()

        assertThat(reminderViewModel.showSnackBar.getOrAwaitValue()).isEqualTo("Error getting reminders")
    }
}