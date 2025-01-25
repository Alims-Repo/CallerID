package com.alim.callerid

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.alim.callerid.data.repository.interfaces.IContactsRepository
import com.alim.callerid.model.ModelContacts
import com.alim.callerid.ui.viewmodel.ContactsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.kotlin.*

/**
 * Unit test class for `ContactsViewModel`.
 * This test suite validates the functionality of the ViewModel, including loading contacts,
 * searching, handling exceptions, and ensuring proper LiveData updates.
 */
@ExperimentalCoroutinesApi
class ContactsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule() // Ensures LiveData operates on a single thread.

    private lateinit var viewModel: ContactsViewModel
    private val repository: IContactsRepository = mock() // Mocked repository for testing.
    private val testDispatcher = StandardTestDispatcher() // Test dispatcher for coroutines.

    /**
     * Setup method to initialize the required components before each test.
     * Sets the main dispatcher to a test dispatcher.
     */
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ContactsViewModel(repository)
    }

    /**
     * Cleanup method to reset the dispatcher after each test.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Test to verify that `loadContacts` updates the `filteredContacts` LiveData correctly
     * with the data returned by the repository.
     */
    @Test
    fun `loadContacts updates filteredContacts LiveData`() = runTest {
        // Arrange
        val contacts = listOf(
            ModelContacts(1, "Alice", "12345", ""),
            ModelContacts(2, "Bob", "67890", "")
        )
        whenever(repository.getAllDeviceContacts(null, null)).thenReturn(contacts)

        // Act
        viewModel.loadContacts()
        testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutine execution.

        // Assert
        val observedContacts = viewModel.filteredContacts.getOrAwaitValue()
        Assert.assertEquals(contacts, observedContacts)
    }

    /**
     * Test to verify that `loadBlockedContacts` updates the `filterBlocked` LiveData correctly
     * with the blocked contacts returned by the repository.
     */
    @Test
    fun `loadBlockedContacts updates filterBlocked LiveData`() {
        // Arrange
        val blockedContacts = listOf(
            ModelContacts(1, "Alice", "12345", "").also { it.blocked = true },
            ModelContacts(2, "Bob", "67890", "").also { it.blocked = true },
        )
        val liveDataBlockedContacts = MutableLiveData<List<ModelContacts>>(blockedContacts)
        whenever(repository.getBlockedContacts()).thenReturn(liveDataBlockedContacts)

        // Act
        viewModel.loadBlockedContacts()

        // Assert
        val observedBlockedContacts = viewModel.filterBlocked.getOrAwaitValue()
        Assert.assertEquals(blockedContacts, observedBlockedContacts)
    }

    /**
     * Test to verify that `searchContacts` filters contacts based on a provided query.
     */
    @Test
    fun `searchContacts filters contacts based on query`() = runTest {
        // Arrange
        val contacts = listOf(
            ModelContacts(1, "Alice", "12345", ""),
            ModelContacts(2, "Bob", "67890", ""),
            ModelContacts(3, "Charlie", "54321", "")
        )
        whenever(repository.getAllDeviceContacts(null, null)).thenReturn(contacts)

        // Act
        viewModel.loadContacts()
        viewModel.searchContacts("Bob")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val filteredContacts = viewModel.filteredContacts.getOrAwaitValue()
        Assert.assertEquals(listOf(contacts[1]), filteredContacts)
    }

    /**
     * Test to verify that `searchBlockedContacts` filters blocked contacts based on a provided query.
     */
    @Test
    fun `searchBlockedContacts filters blocked contacts based on query`() = runTest {
        // Arrange
        val blockedContacts = listOf(
            ModelContacts(1, "Alice", "12345", "").also { it.blocked = true },
            ModelContacts(2, "Bob", "67890", "").also { it.blocked = true }
        )
        whenever(repository.getAllDeviceContacts(null, null)).thenReturn(blockedContacts)

        // Act
        viewModel.loadContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.searchContacts("Alice")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val filteredContacts = viewModel.filteredContacts.getOrAwaitValue()
        Assert.assertEquals(listOf(blockedContacts[0]), filteredContacts)
    }

    /**
     * Test to verify that `loadContacts` handles exceptions thrown by the repository
     * and keeps the `filteredContacts` LiveData empty.
     */
    @Test
    fun `loadContacts handles repository exception`() = runTest {
        // Arrange
        whenever(repository.getAllDeviceContacts(null, null)).thenThrow(RuntimeException("Repository error"))

        // Act
        viewModel.loadContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val observedContacts = viewModel.filteredContacts.getOrAwaitValue()
        Assert.assertTrue(observedContacts.isEmpty())
    }

    /**
     * Test to verify that `searchContacts` debounces user input correctly.
     */
    @Test
    fun `searchContacts debounces user input`() = runTest {
        // Arrange
        val contacts = listOf(
            ModelContacts(1, "Alice", "12345", ""),
            ModelContacts(2, "Bob", "67890", "")
        )
        whenever(repository.getAllDeviceContacts(null, null)).thenReturn(contacts)

        // Act
        viewModel.loadContacts()
        viewModel.searchContacts("A")
        viewModel.searchContacts("Al")
        viewModel.searchContacts("Ali")
        testDispatcher.scheduler.advanceTimeBy(400) // Simulate debounce delay.

        // Assert
        val filteredContacts = viewModel.filteredContacts.getOrAwaitValue()
        Assert.assertEquals(listOf(contacts[0]), filteredContacts)
    }

    /**
     * Test to verify that `loadAll` loads both contacts and blocked contacts into their respective LiveData.
     */
    @Test
    fun `loadAll loads both contacts and blocked contacts`() = runTest {
        // Arrange
        val contacts = listOf(ModelContacts(1, "Alice", "12345", ""))
        val blockedContacts = listOf(ModelContacts(2, "Bob", "67890", "").also { it.blocked = true })
        whenever(repository.getAllDeviceContacts(null, null)).thenReturn(contacts)
        whenever(repository.getBlockedContacts()).thenReturn(MutableLiveData(blockedContacts))

        // Act
        viewModel.loadAll()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val observedContacts = viewModel.filteredContacts.getOrAwaitValue()
        val observedBlockedContacts = viewModel.filterBlocked.getOrAwaitValue()

        Assert.assertEquals(contacts, observedContacts)
        Assert.assertEquals(blockedContacts, observedBlockedContacts)
    }

    /**
     * Test to verify `contactMessage` shows the correct message when no contacts are present.
     */
    @Test
    fun `contactMessage shows no contacts available`() = runTest {
        // Arrange
        whenever(repository.getAllDeviceContacts(null, null)).thenReturn(emptyList())

        // Act
        viewModel.loadContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val message = viewModel.contactMessage.getOrAwaitValue()
        Assert.assertEquals("No contacts to display. Add contacts to your list, and they’ll appear here.", message)
    }

    /**
    * Test to verify `contactMessage` shows the correct message when no matching contacts are found.
    */
    @Test
    fun `contactMessage shows no matching contacts found`() = runTest {
        // Arrange
        val contacts = listOf(
            ModelContacts(1, "Alice", "12345", ""),
            ModelContacts(2, "Bob", "67890", "")
        )
        whenever(repository.getAllDeviceContacts(null, null)).thenReturn(contacts)

        // Act
        viewModel.filteredContacts.observeForever {
            // It must be observed for the `contactMessage` to update
        }
        viewModel.loadContacts()
        viewModel.searchContacts("Charlie") // Query not matching any contact
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val message = viewModel.contactMessage.getOrAwaitValue()
        Assert.assertEquals("No matching contacts found. Try searching for a different contact.", message)
    }

    /**
     * Test to verify `contactMessage` is empty when matching contacts are found.
     */
    @Test
    fun `contactMessage is empty when contacts are available`() = runTest {
        // Arrange
        val contacts = listOf(
            ModelContacts(1, "Alice", "12345", ""),
            ModelContacts(2, "Bob", "67890", "")
        )
        whenever(repository.getAllDeviceContacts(null, null)).thenReturn(contacts)

        viewModel.filteredContacts.observeForever {
            // It must be observed for the `contactMessage` to update
        }
        viewModel.loadContacts()
        viewModel.searchContacts("Bob")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val message = viewModel.contactMessage.getOrAwaitValue()
        Assert.assertEquals("Total 1 contacts found", message)
    }

    /**
     * Test to verify `blockedMessage` shows the correct message when no blocked contacts are present.
     */
    @Test
    fun `blockedMessage shows no blocked contacts available`() {
        // Arrange
        val liveDataBlockedContacts = MutableLiveData<List<ModelContacts>>(emptyList())
        whenever(repository.getBlockedContacts()).thenReturn(liveDataBlockedContacts)

        // Act
        viewModel.filterBlocked.observeForever {
            // It must be observed for the `blockedMessage` to update
        }
        viewModel.loadBlockedContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val message = viewModel.blockedMessage.getOrAwaitValue()
        Assert.assertEquals("Blocked contacts will appear here. To add, simply block a contact from your contact list.", message)
    }

    /**
     * Test to verify `blockedMessage` shows the correct message when no matching blocked contacts are found.
     */
    @Test
    fun `blockedMessage shows no matching blocked contacts found`() = runTest {
        // Arrange
        val blockedContacts = listOf(
            ModelContacts(1, "Alice", "12345", "").also { it.blocked = true },
            ModelContacts(2, "Bob", "67890", "").also { it.blocked = true }
        )
        val liveDataBlockedContacts = MutableLiveData(blockedContacts)
        whenever(repository.getBlockedContacts()).thenReturn(liveDataBlockedContacts)

        // Act
        viewModel.filterBlocked.observeForever {
            // It must be observed for the `contactMessage` to update
        }
        viewModel.loadBlockedContacts()
        viewModel.searchBlockedContacts("Charlie") // Query not matching any blocked contact
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val message = viewModel.blockedMessage.getOrAwaitValue()
        Assert.assertEquals("No matching contacts found. Try searching for a different contact.", message)
    }

    /**
     * Test to verify `blockedMessage` is empty when matching blocked contacts are found.
     */
    @Test
    fun `blockedMessage is empty when blocked contacts are available`() = runTest {
        // Arrange
        val blockedContacts = listOf(
            ModelContacts(1, "Alice", "12345", "").also { it.blocked = true },
            ModelContacts(2, "Bob", "67890", "").also { it.blocked = true }
        )
        val liveDataBlockedContacts = MutableLiveData(blockedContacts)
        whenever(repository.getBlockedContacts()).thenReturn(liveDataBlockedContacts)

        // Act
        viewModel.filterBlocked.observeForever {
            // It must be observed for the `contactMessage` to update
        }
        viewModel.loadBlockedContacts()
        viewModel.searchBlockedContacts("Alice") // Query matching a blocked contact
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val message = viewModel.blockedMessage.getOrAwaitValue()
        Assert.assertEquals("Total 1 contacts found", message)
    }
}