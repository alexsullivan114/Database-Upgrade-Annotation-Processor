package com.alexsullivan.example.dbtestapplication;

import com.alexsullivan.DBUpgradeContainer;

/**
 * Created by Alexs on 5/5/2017.
 */

@DBUpgradeContainer
public class DBUpgradeContainerExample {

//    @DBUpgrade(version = 5)
    public String getDbUpgrade() {
        return "";
    }
}
