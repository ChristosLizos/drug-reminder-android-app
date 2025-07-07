package com.example.it22063androidprojectsept2025;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TimeTerm {
    @PrimaryKey(autoGenerate = false)
    public int id;

    @ColumnInfo(name = "label")
    public String label;

    public TimeTerm(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public static int getId(String label){
        if (label.equals("before-breakfast")){
            return 1;
        } else if (label.equals("at-breakfast")) {
            return 2;
        } else if (label.equals("after-breakfast")) {
            return 3;
        }else if (label.equals("before-lunch")) {
            return 4;
        }else if (label.equals("at-lunch")) {
            return 5;
        }else if (label.equals("after-lunch")) {
            return 6;
        }else if (label.equals("before-dinner")) {
            return 7;
        }else if (label.equals("at-dinner")) {
            return 8;
        }else if (label.equals("after-dinner")) {
            return 9;
        }
        return 0;

    }

}
