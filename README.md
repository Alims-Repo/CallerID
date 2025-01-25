# Caller ID App

The Caller ID App is designed to provide advanced call screening, contact management, and real-time information display with a modern UI and smooth animations. It utilizes the latest Android development practices, including MVVM architecture, ViewBinding, DataBinding, Room database, and modern coroutine-based asynchronous operations.

## Features

### Call Screening
- Automatically screens incoming calls using the CallScreeningService API.
- Blocks calls based on user-selected contacts from the app’s contact list.
- Skips call logs and notifications for blocked calls.

### Contact Management
- Displays contacts fetched from the user’s device.
- Allows blocking/unblocking contacts directly from the app.
- Prevents duplicate contact entries using normalized phone numbers.
- Real-time search with debounced input handling.

### Real-Time Call Information
- Displays a movable overlay with caller details for incoming calls.
- Uses the user’s input to block contacts dynamically, rather than relying on predefined numbers.

### Permissions Handling
- Ensures that all necessary permissions are granted, including:
   - READ_CONTACTS
   - ANSWER_PHONE_CALLS
   - READ_PHONE_STATE
   - READ_CALL_LOG
- Dynamically requests permissions at runtime.

### Custom UI and Animations
- Smooth animations for RecyclerView.
- Modern Material Design 3 theme with enhanced UI components.
- Overlay window with draggable capability for better user interaction.

## Testing
- Includes comprehensive unit tests for ViewModel logic.
- Verifies LiveData updates, contact management, and search functionality.

## Running Instructions

### Prerequisites
- Android Studio: Ladybug 24.2.2 or later
- Gradle Version: 8.10.2
- Android Gradle Plugin (AGP): 8.8.0
- Permissions: Ensure runtime permissions are granted for app functionality.

### Steps to Run the App
1. Clone the Repository
```
git clone https://github.com/Alims-Repo/CallerID.git
cd CallerID
```
2. Open in Android Studio
   - Launch Android Studio.
   - Navigate to File > Open and select the CallerID project folder.
3. Sync Gradle
   - Gradle sync will start automatically. Ensure it completes successfully.
4. Build and Run the Project
   - Build the app using Ctrl+F9 or Build > Make Project.
   - Run the app using Shift+F10 or the Run button.
5. Grant Runtime Permissions
   - On the first launch, the app will request permissions for contacts, call logs, and phone state. Grant all permissions for full functionality.

## Implemented Technologies

### 1. ViewBinding and DataBinding
The app leverages both ViewBinding and DataBinding for efficient UI management:
- **ViewBinding**: Removes boilerplate code for UI component initialization.
- **DataBinding**: Allows dynamic updates of UI components using LiveData.

### Custom BindingAdapters
- **RecyclerView Animations**:
```
@BindingAdapter("itemAnimation")
fun setRecyclerViewAnimation(recyclerView: RecyclerView, enable: Boolean) {
    if (enable) {
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_in_bottom)
        recyclerView.layoutAnimation = controller
        recyclerView.scheduleLayoutAnimation()
    }
}
```
- **Animated Visibility**:
```
@BindingAdapter("animateVisibility")
fun setAnimatedVisibility(view: View, isVisible: Boolean) {
    if (isVisible) {
        view.visibility = View.VISIBLE
        view.alpha = 0f
        view.animate().alpha(1f).setDuration(300).start()
    } else {
        view.visibility = View.GONE
    }
}
```
- **Dynamic Image Loading**:
```
@BindingAdapter("imageUrl", "fallbackName", requireAll = false)
fun loadImage(view: ImageView, imageUrl: String?, fallbackName: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context).load(imageUrl).circleCrop().into(view)
    } else if (!fallbackName.isNullOrEmpty()) {
        view.setImageDrawable(generateLetterDrawable(view, fallbackName))
    } else {
        view.setImageResource(R.color.darker_gray)
    }
}
```

### 2. Call Screening
- Utilizes the CallScreeningService API to intercept and handle incoming calls.
- Responds to calls dynamically, blocking unwanted numbers while showing overlays for allowed calls.

### 3. Overlay Service
- **Movable Overlay**:
   - Displays caller details in an interactive overlay.
   - Users can drag the overlay window anywhere on the screen.

## Testing Frameworks
- **JUnit**: Unit testing framework.
- **Mockito**: Mocking library for repository testing.
- **Coroutines Test**: For testing coroutine-based ViewModel logic.

## Key Test Cases

The test cases focus on validating the ViewModel logic, ensuring proper handling of LiveData updates, contact management, and error scenarios.

### Sample Test Code
```
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
```

### Additional Test Cases
1. Filter Matching Contacts:
   - Ensures filtered results are displayed for valid queries.
2. Dynamic Message Updates:
   - Verifies filter message updates based on LiveData changes.