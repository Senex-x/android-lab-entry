# Extra info
* I have started to make android apps about **two years** ago.
Now I have 5 pet projects, three of which deserve a little bit of attention.
* For the first year of study I have average for programming subjects around ~95.
* Also I understand the **core of Java and Kotlin** languages. :+1:

### Interesting apps:
* Almost fully made by me - [App for Ferenetz practice](https://github.com/Senex-x/TextBuilder-app)
* With significant contribution - [App for SimbirSoft's summer practice](https://github.com/Senex-x/tutors_notebook)
* Made by me, like a year ago - [Old chat app](https://github.com/Senex-x/TextMe)

# Android Entry App

## Log in screen
<img src="https://raw.github.com/Senex-x/android-lab-entry/master/assets/t-b4i5lKyug.jpg" width="240">

### Brief description
The first screen for user to see during the first launch.
Allows the user to **log in** if it is a new device or he is logged out of account.
It also makes it possible to **register a new account** if necessary.

### Details
If the user does not have an account, allows him to **go to the registration screen**.
If the user already has an account and wants to log in, after pressing the login button internally
validates email and password using **regex**.
If everything is ok, then **connects with Firebase** database to check if the user exists.
After receiving a response, if the account is found, user is going to a screen with a fragment containing information about the account.
If at least one check is not satisfied, an error message corresponding to the situation is displayed.

## Registration screen
<img src="https://raw.github.com/Senex-x/android-lab-entry/master/assets/Zu5lJCL60HM.jpg" width="240">

### Brief description
If the user is accidentally got here, he can **go back to the login screen**.
Allows the user to **register new account** with all required information.
Profile image is **optional**.

### Details
When you click on the registration button, **all fields are checked for correct filling**.
Email and password fields are validated using **regex**.
If at least one field is filled in incorrectly or the passwords do not match, a corresponding error message is displayed.
If everything is ok, then connects with Firebase to **check if specified email already exists** among created users.
After receiving response, if no duplication is found, the **new user is written to the database**
and the user goes to a screen displaying information about the account. 
**Image is being processed before upload** by resizing, cropping, compressing and rotating if necessary. 

## Account screen
<img src="https://raw.github.com/Senex-x/android-lab-entry/master/assets/8psscVo8R-A.jpg" width="240">

### Brief description
Displays information about the user account. Displays the user's photo, if it present. Allows user **to change the photo**.
Allows user to **log out of account** and go to the account login screen.

### Details
User information is **downloaded from the Firebase** server at startup and **stored locally** on the device.
When the photo is changed, both the remote and local data are updated. 
**Image is being processed before upload** by resizing, cropping, compressing and rotating if necessary. 
