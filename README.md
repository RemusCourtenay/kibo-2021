# kibo-2021

# How to build
1. Once you've made changes in Android Studio, go to 'Build' in the top menu bar and select 'Rebuild Project'
2. Wait for everything to finish, takes 40-60 secs (you should see 'Build: completed successfully at [DATE, TIME]' in Build Output at the bottom once it's done)
3. The apk will be located in kibo-2021\app\build\outputs\apk

# Don't commit
- .lock, .bin, .iml, .xml, .rawproto files 
- anything from kibo-2021\app\build\intermediates
- any other file where just the file paths have been changed such that the only change is the username e.g. C:/Users/[someone else]/... -> C:/Users/[you]/...
