// PATH: app/src/main/java/com/utc2/appreborn/data/local/dao/UserDao.java

package com.utc2.appreborn.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.utc2.appreborn.data.local.entity.StudentProfileEntity;
import com.utc2.appreborn.data.local.entity.UserEntity;

@Dao
public interface UserDao {

    // ─── INSERT ───────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(UserEntity user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStudentProfile(StudentProfileEntity profile);

    // ─── QUERY BY ID ──────────────────────────────────────────────────────────

    @Query("SELECT * FROM user WHERE user_id = :userId LIMIT 1")
    UserEntity getUserById(long userId);

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM student_profile WHERE user_id = :userId LIMIT 1")
    StudentProfileEntity getStudentProfileByUserId(long userId);

    @Query("SELECT * FROM student_profile WHERE student_code = :studentCode LIMIT 1")
    StudentProfileEntity getStudentProfileByCode(String studentCode);

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @Query("DELETE FROM user WHERE user_id = :userId")
    void deleteUserById(long userId);
}