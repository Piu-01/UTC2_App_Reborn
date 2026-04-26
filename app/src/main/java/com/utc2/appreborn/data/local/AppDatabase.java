package com.utc2.appreborn.data.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.utc2.appreborn.data.local.dao.StudentDao;
import com.utc2.appreborn.data.local.dao.UserDao;
import com.utc2.appreborn.data.local.entity.StudentEntity;
import com.utc2.appreborn.data.local.entity.UserEntity;

/**
 * AppDatabase
 * ──────────────────────────────────────────────────────────────
 * Room Database singleton cho toàn bộ app.
 *
 * Thêm dependency vào build.gradle (nếu chưa có):
 *   implementation 'androidx.room:room-runtime:2.6.1'
 *   annotationProcessor 'androidx.room:room-compiler:2.6.1'
 *
 * Package: com.utc2.appreborn.data.local
 *
 * ─── Cách dùng khi sẵn sàng ───────────────────────────────────
 * // Trong Repository hoặc ViewModel:
 * AppDatabase db = AppDatabase.getInstance(context);
 * UserDao    userDao    = db.userDao();
 * StudentDao studentDao = db.studentDao();
 */
@Database(
        entities = { UserEntity.class, StudentEntity.class },
        version  = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "utc2_app.db";

    // ── Singleton ─────────────────────────────────────────────
    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME)
                            .fallbackToDestructiveMigration() // đơn giản hoá trong dev
                            .build();
                }
            }
        }
        return instance;
    }

    // ── DAOs ──────────────────────────────────────────────────
    public abstract UserDao    userDao();
    public abstract StudentDao studentDao();
}