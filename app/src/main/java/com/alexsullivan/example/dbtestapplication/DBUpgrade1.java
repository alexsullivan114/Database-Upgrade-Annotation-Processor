package com.alexsullivan.example.dbtestapplication;

import com.alexsullivan.DBUpgrade;
import com.alexsullivan.SQLiteUpgrade;

@DBUpgrade(version = 1)
public class DBUpgrade1 implements SQLiteUpgrade{
    @Override
    public String upgradeScript() {
        return "CREATE TABLE CUSTOMERS(\n" +
                "   ID   INT              NOT NULL,\n" +
                "   NAME VARCHAR (20)     NOT NULL,\n" +
                "   AGE  INT              NOT NULL,\n" +
                "   ADDRESS  CHAR (25) ,\n" +
                "   SALARY   DECIMAL (18, 2),       \n" +
                "   PRIMARY KEY (ID)\n" +
                ");";
    }
}
