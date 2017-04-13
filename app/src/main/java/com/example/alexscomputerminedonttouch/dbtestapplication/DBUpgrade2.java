package com.example.alexscomputerminedonttouch.dbtestapplication;

import com.example.DBUpgrade;
import com.example.SQLiteUpgrade;

/**
 * Created by Alexs on 4/3/2017.
 */

@DBUpgrade(version = 2)
public class DBUpgrade2 implements SQLiteUpgrade{
    @Override
    public String upgradeScript() {
        return "ALTER TABLE CUSTOMERS ADD COLUMN AGITATION_LEVEL DECIMAL(2,0)";
    }
}
