package com.example.it22063androidprojectsept2025;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.Executors;

@Database(entities = {Drug.class, TimeTerm.class}, version = 4)
@TypeConverters({Converters.class})
public abstract class DrugDatabase extends RoomDatabase {

    // Abstract DAOs that Room will generate implementations for
    public abstract DrugDao drugDao();
    public abstract timeTermDao timeTermDao();

    // Singleton instance of the database
    private static volatile DrugDatabase INSTANCE;

    /**
     * Returns the singleton instance of the database.
     * Creates it if it doesn't already exist.
     */
    public static DrugDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DrugDatabase.class) {
                if (INSTANCE == null) {
                    // Build the database with fallback migration and initialization logic
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DrugDatabase.class, "drug_database")
                            // If there's a schema mismatch (version change), recreate the DB
                            .fallbackToDestructiveMigration()
                            // Callback for when the DB is first created
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);

                                    // Insert default TimeTerm values on DB creation in a background thread
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        getDatabase(context).timeTermDao().InsertAll(
                                                new TimeTerm(1, "before-breakfast"),
                                                new TimeTerm(2, "at-breakfast"),
                                                new TimeTerm(3, "after-breakfast"),
                                                new TimeTerm(4, "before-lunch"),
                                                new TimeTerm(5, "at-lunch"),
                                                new TimeTerm(6, "after-lunch"),
                                                new TimeTerm(7, "before-dinner"),
                                                new TimeTerm(8, "at-dinner"),
                                                new TimeTerm(9, "after-dinner")
                                        );
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
