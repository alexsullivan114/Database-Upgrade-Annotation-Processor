package com.example.alexscomputerminedonttouch.dbtestapplication;

import com.example.DBUpgrade;
import com.example.SQLiteUpgrade;

@DBUpgrade(version = 1)
public class TestClass implements SQLiteUpgrade{
    @Override
    public String upgradeScript() {
        return null;
    }
}
