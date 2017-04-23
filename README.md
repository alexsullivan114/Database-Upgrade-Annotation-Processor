# Database-Upgrade-Annotation-Processor

Annotation processor and sample app for doing easy database upgrades.

* Set the version of your database using normal ```SQLiteOpenHelper``` methods
* Create custom ```SQLiteUpgrade``` classes for each upgrade
* annotate each custom class with a ```DBUpgrade``` annotation and supply the version number
* extend the (generated) ```SQLiteUpgradeHelper``` class
* Done!

## TODO ##

* Create DBUpgradeContainer annotation to facilitate the user of a single class containing multiple DB upgrades.

# DOWNLOAD #

Add ```jcenter()``` to your repositories:
```
allprojects {
    repositories {
        jcenter()
    }
}
```

Then add both the compiler and the api depedencies:
```
compile 'com.alexsullivan:sqlite-upgrade-helper-api:0.1'
annotationProcessor 'com.alexsullivan:sqlite-upgrade-helper-processor:0.1'
```
