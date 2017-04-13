package com.example.alexscomputerminedonttouch.dbtestapplication;

import com.example.DBUpgrade;
import com.example.SQLiteUpgrade;

/**
 * Created by Alexs on 4/6/2017.
 */

@DBUpgrade(version = 3)
public class DBUpgrade3 implements SQLiteUpgrade{
    @Override
    public String upgradeScript() {
        return "INSERT INTO CUSTOMERS (ID, NAME, AGE, ADDRESS, SALARY) VALUES (10, Alex, \"30 Fake Street, Boston MA\", 00)";
    }
}
