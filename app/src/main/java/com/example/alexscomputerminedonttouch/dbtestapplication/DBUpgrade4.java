package com.example.alexscomputerminedonttouch.dbtestapplication;

import com.example.DBUpgrade;
import com.example.SQLiteUpgrade;

/**
 * Created by Alexs on 4/13/2017.
 */

@DBUpgrade(version = 4)
public class DBUpgrade4 implements SQLiteUpgrade {
    @Override
    public String upgradeScript() {
        return "INSERT INTO CUSTOMERS (ID, NAME, AGE, ADDRESS, SALARY) VALUES (11, John, \"35 Fake Street, Boston MA\", 200)";
    }

}
