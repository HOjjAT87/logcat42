# Logcat 42

This is the sample code used for the 42n'd Logcat session talk on Gradle.
The Code is meant to be minimal but demonstrate how a sample Gradle plugin might work.
The solution provided here is just for learning purposes and is not the best way such a plugin should be implemented.

The plugin generates a BuildConfig file similar to what the Android Gradle plugin does, but generates a Kotlin file instead 
of a Java one and gives the ability to declare static variables instead of writing the declarations using strings 
in order to reduce errors. It can also generate a changle log if the Versioning Interface 
is implemented(i.e using conventional commits) but right now uses a mock for demo.

The talk is in Persian and could be seen [here](https://www.youtube.com/watch?v=CdvDTB8slKc).
