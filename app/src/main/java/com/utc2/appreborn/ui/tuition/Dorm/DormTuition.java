package com.utc2.appreborn.ui.tuition.Dorm;
import com.utc2.appreborn.ui.tuition.Tuition;

public class DormTuition extends Tuition {

    public DormTuition(String roomName, String details, long amount, int status) {
        // roomName của Dorm sẽ được gán vào trường 'name' của lớp cha
        super(roomName, details, amount, status);
    }

    @Override
    public String getIdentifier() {
        // Dorm có thể không cần ID tăng dần mà dùng chính tên phòng làm khóa
        return "DORM-" + name;
    }
}