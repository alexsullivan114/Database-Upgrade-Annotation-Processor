# Database-Upgrade-Annotation-Processor

Annotation processor and sample app for doing easy database upgrades.

* Set the version of your database using normal ```SQLiteOpenHelper``` methods
* Create custom ```SQLiteUpgrade``` classes for each upgrade
* annotate each custom class with a ```DBUpgrade``` annotation and supply the version number
* extend the (generated) ```SQLiteUpgradeHelper``` class
* Done!

## TODO ##

* Create DBUpgradeContainer annotation to facilitate the user of a single class containing multiple DB upgrades.
