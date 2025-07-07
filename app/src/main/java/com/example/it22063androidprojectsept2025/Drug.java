package com.example.it22063androidprojectsept2025;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        foreignKeys = @ForeignKey(
                entity = TimeTerm.class,
                parentColumns = "id",
                childColumns = "time_term_id",
                onDelete = ForeignKey.NO_ACTION
        )
)
public class Drug {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "short_name")
    public String shortName;

    @ColumnInfo(name = "brief_desc")
    public String briefDesc;

    @ColumnInfo(name = "time_term_id", index = true)
    public int timeTermId;

    @ColumnInfo(name = "start_date")
    public Date startDate;

    @ColumnInfo(name = "end_date")
    public Date endDate;

    @ColumnInfo(name = "doc_name")
    public String docName;

    @ColumnInfo(name = "doc_location")
    public String docLocation;

    @ColumnInfo(name="is_active")
    public boolean isActive;
    @ColumnInfo(name="last_date_received")
    public Date lastDateReceived;

    @ColumnInfo(name="has_received_today")
    public boolean hasReceivedToday;

    @Ignore // Room will ignore this column for storage
    public String timeTermName;


    public Drug (String shortName, String briefDesc, int timeTermId, Date startDate, Date endDate, String docName, String docLocation, Date  lastDateReceived, Boolean hasReceivedToday){
        this.shortName = shortName;
        this.briefDesc = briefDesc;
        this.timeTermId = timeTermId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.docName = docName;
        this.docLocation = docLocation;
        Date today = new Date();
        this.isActive = today.before(endDate) && today.after(startDate);
        this.lastDateReceived = lastDateReceived;
        this.hasReceivedToday = hasReceivedToday;

    }

}
