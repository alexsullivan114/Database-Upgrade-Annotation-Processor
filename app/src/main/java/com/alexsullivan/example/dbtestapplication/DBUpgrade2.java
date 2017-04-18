package com.alexsullivan.example.dbtestapplication;

import com.alexsullivan.DBUpgrade;
import com.alexsullivan.SQLiteUpgrade;

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
