Drug Reminder Android App

Drug Reminder Android App is a user-friendly Android application designed to help users—especially elderly individuals—manage their prescription medications. The app allows users to track, organize, and review their active drugs using a clean interface and reliable local data storage. It also provides daily tracking and export functionality for better healthcare management.
🧩 Features

    Add New Drugs: Easily add prescription medications using a form with text inputs, dropdowns for time intervals, and date pickers.

    Input Validation: Ensures required fields are filled correctly; prevents invalid date ranges.

    View Active Drugs: Display all active prescriptions on the main screen, sorted by time term.

    Detailed Drug Info: Tap any drug to view full details including name, description, doctor, time term, and last taken date.

    Mark as Taken: Users can mark a drug as taken for the day with a checkbox. This updates the internal database.

    Daily Status Reset: On every app launch, the app checks and updates drug status based on the current date.

    Delete Drugs: Safely remove drugs with confirmation dialogs.

    Export to HTML: One-tap export of all active prescriptions to an HTML file stored in the device’s Downloads folder.

📱 Screens and UI

    MainActivity: Displays a list of active prescription drugs and provides buttons for adding or exporting drugs.

    AddDrugsActivity: Allows entry of drug data using EditTexts, Spinners, and DatePickers.

    MoreInfoActivity: Shows all drug data with delete and mark-as-received controls.

    Elder-Friendly Design: Simple layouts and large inputs ensure accessibility.

🛠 Tech Stack

    Language: Java

    Architecture: MVVM-ish with Room persistence

    Local Database: Room (SQLite)

    UI Components: RecyclerView, EditText, Spinner, DatePicker

    Storage: Internal app database and HTML file export

    Android Frameworks: LiveData, Toasts, Dialogs

🚀 Getting Started

To run the app locally:

    Clone the repo:

    git clone https://github.com/your-username/drug-reminder-android-app.git

    Open the project in Android Studio

    Build and run on an emulator or Android device (API 26+ recommended)

    No additional dependencies or API keys required.

📁 Project Structure

    MainActivity.java – Displays active drugs and handles HTML export

    AddDrugsActivity.java – UI to enter a new prescription

    MoreInfoActivity.java – Full drug details and update/delete options

    Drug.java – Entity model

    DrugDao.java – Data access operations

    AppDatabase.java – Room DB initialization

    TimeTerm.java – Time intervals used to organize drugs

🔒 Limitations / Future Improvements

    No support for syncing or cloud backup

    No ContentProvider integration for inter-app sharing

    Notifications/reminders not yet implemented

    Could benefit from color coding, accessibility improvements, or multilingual support

📤 HTML Export Example

An HTML file (my_active_drugs.html) is saved to the device’s Downloads folder and contains all active prescription data, formatted for easy sharing or printing.
👤 Author

Christos Lizos
Undergraduate at Harokopio University
Expected Graduation: 2026
