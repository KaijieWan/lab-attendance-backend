package com.example.lab_attendance_app.util;

import com.example.lab_attendance_app.models.entities.Lab;
import com.example.lab_attendance_app.models.entities.LabSession;
import com.example.lab_attendance_app.models.entities.embedded.LabId;
import com.example.lab_attendance_app.models.repositories.AttendanceRepository;
import com.example.lab_attendance_app.models.repositories.LabRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Utility {

    public static class Interval {
        LocalTime startTime;
        LocalTime endTime;
        int studentCount;
        boolean isNewSession;

        public Interval(LocalTime startTime, LocalTime endTime, int studentCount, boolean isNewSession) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.studentCount = studentCount;
            this.isNewSession = isNewSession;
        }
    }

    public static class TimePoint {
        LocalTime time;
        int studentCountChange;
        boolean isNewSession;

        public TimePoint(LocalTime time, int studentCountChange, boolean isNewSession) {
            this.time = time;
            this.studentCountChange = studentCountChange;
            this.isNewSession = isNewSession;
        }
    }

    public static Map<String, Object> isLabRoomFull(String labName, int room, LocalDate date, LocalTime startTime, LocalTime endTime, int noOfStudentsToBeAdded, LabRepository labRepository, AttendanceRepository attendanceRepository) {
        // Retrieve the lab entity to get the room capacity
        LabId labId = new LabId(labName, room);
        Optional<Lab> labOptional = labRepository.findById(labId);
        if (labOptional.isEmpty()) {
            return Map.of("message", "Lab not found");
        }
        Lab lab = labOptional.get();
        int roomCapacity = lab.getCapacity();
        int maximumRemainingCapacity = roomCapacity;

        // Fetch overlapping lab sessions and their student counts
        List<Object[]> overlappingSessions = attendanceRepository.findOverlappingLabSessionsWithStudentCounts(labName, room, date, startTime, endTime);

        // Build intervals with start time, end time, and student counts
        List<Interval> intervals = new ArrayList<>();
        for (Object[] obj : overlappingSessions) {
            LabSession ls = (LabSession) obj[0];
            Long studentCount = (Long) obj[1];
            intervals.add(new Interval(ls.getStartTime(), ls.getEndTime(), studentCount.intValue(), false));
        }

        // Add the new session
        intervals.add(new Interval(startTime, endTime, noOfStudentsToBeAdded, true));

        // Build a timeline of events
        List<TimePoint> timePoints = new ArrayList<>();
        for (Interval interval : intervals) {
            timePoints.add(new TimePoint(interval.startTime, interval.studentCount, interval.isNewSession));
            timePoints.add(new TimePoint(interval.endTime, -interval.studentCount, interval.isNewSession));
        }

        // Sort timePoints by time
        timePoints.sort(Comparator.comparing(tp -> tp.time));

        // Calculate the number of students at each interval
        int currentStudentsIncludingNewSession = 0;
        int currentStudentsExcludingNewSession = 0;
        boolean isFull = false;
        for (TimePoint tp : timePoints) {
            currentStudentsIncludingNewSession += tp.studentCountChange;

            if (!tp.isNewSession) {
                currentStudentsExcludingNewSession += tp.studentCountChange;
            }

            int remainingCapacityExcludingNewSession = roomCapacity - currentStudentsExcludingNewSession;
            maximumRemainingCapacity = Math.min(maximumRemainingCapacity, remainingCapacityExcludingNewSession);

            if (currentStudentsIncludingNewSession > roomCapacity) {
                isFull = true;
                break;
            }
        }

        // Return the result
        return Map.of("isFull", isFull, "roomCapacity", roomCapacity, "remainingCapacity", maximumRemainingCapacity);
    }

}