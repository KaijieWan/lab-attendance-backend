package com.example.lab_attendance_app.services.implementations;

import com.example.lab_attendance_app.models.entities.Absent_Details;
import com.example.lab_attendance_app.models.entities.Absent_Logs;
import com.example.lab_attendance_app.models.entities.Attendance;
import com.example.lab_attendance_app.models.repositories.AbsentDetailsRepository;
import com.example.lab_attendance_app.models.repositories.AbsentLogsRepository;
import com.example.lab_attendance_app.models.repositories.AttendanceRepository;
import com.example.lab_attendance_app.services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AbsentServiceImpl {

    private final AttendanceRepository attendanceRepository;
    private final AbsentDetailsRepository absentDetailsRepository;
    private final AbsentLogsRepository absentLogsRepository;
    private final EmailService emailService;

    private static String frontend_ip;

    /*@Value("${FRONTEND_IP}")
    public void setFrontend_ip(String frontend_ip) {
        AbsentService.frontend_ip = frontend_ip;
    }*/

    public AbsentServiceImpl(AttendanceRepository attendanceRepository,
                             AbsentDetailsRepository absentDetailsRepository, AbsentLogsRepository absentLogsRepository,
                             EmailService emailService) {
        this.attendanceRepository = attendanceRepository;
        this.absentDetailsRepository = absentDetailsRepository;
        this.absentLogsRepository = absentLogsRepository;
        this.emailService = emailService;
    }

    @Async
    @Transactional
    public void processAbsences() throws MessagingException, InterruptedException {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        List<Attendance> absences = attendanceRepository.findPendingAbsences(currentDate, currentTime);

        for (Attendance attendance : absences) {
            attendance.setStatus("Absent");
            System.out.println("Mark student as absent: " + attendance.getStudent().getStudent_ID());

            // If It's true, means we already created a make-up session for this student before, and the student did not attend the make up session
            // Then we need to mark the absent created previously as "Absent During Make-up"
            // If its false, we just create an absent for the student and send email to request for reason of absence
            if (attendance.getIsMakeUpSession()) {
                System.out.println("Student is absent for make up lesson");

                // Retrieve absent created previously
                Optional<Absent_Details> absentDetailsOptional = absentDetailsRepository.getAbsentDetailsByMakeUpAttendanceID(attendance.getAttendance_ID());

                if (absentDetailsOptional.isPresent()) {
                    Absent_Details absentDetails = absentDetailsOptional.get();
                    absentDetails.setStatus("Absent During Make Up");
                    absentDetailsRepository.save(absentDetails);

                    // Add log for marking absent during make-up
                    Absent_Logs absentLog = new Absent_Logs(absentDetails.getAbsent_ID(), "System", "Student did not attend make up lesson session. \n\nStatus updated to [Absent During Make-Up]", "System Generated");
                    absentLogsRepository.save(absentLog);

                } else {
                    System.out.println("Absent details not found for make up session");
                }

            } else {
                System.out.println("Student is absent for normal lab lesson");
                // Create new absent for the student
                Absent_Details absentDetails = new Absent_Details();
                absentDetails.setReason("");
                absentDetails.setImageLink("");
                absentDetails.setStatus("Pending");
                absentDetailsRepository.save(absentDetails);

                attendance.setAbsent_ID(absentDetails.getAbsent_ID());
                attendanceRepository.save(attendance);

                // Add log for marking absent
                Absent_Logs absentLog = new Absent_Logs(absentDetails.getAbsent_ID(), "System", "Student was absent for lab session. \n\nStatus updated to [Pending]", "System Generated");
                absentLogsRepository.save(absentLog);

                // TODO: uncomment this code to send to REAL STUDENT EMAIL instead of mock data.
                // String oneTimeLink = generateOneTimeLink(attendance.getStudent().getStudent_ID());
                // emailService.sendAbsenceEmail(attendance.getStudent().getStudent_ID(), oneTimeLink);

                // TODO: comment this code which sends to myself for testing purposes only.
                //String oneTimeLink = generateOneTimeLink(absentDetails.getAbsent_ID());
                //System.out.println("One-time link: " + oneTimeLink);
                //emailService.sendAbsenceEmail("zsoh007", oneTimeLink, java.sql.Date.valueOf(attendance.getLabsession().getDate()), attendance.getLabsession().getStartTime(), attendance.getLabsession().getEndTime(), attendance.getStudent().getFullName(), attendance.getLabsession().getClassGroup().getClassGroupId().getModuleCode(), attendance.getLabsession().getClassGroupID().getClassGroupID(), attendance.getLabsession().getLabID().getLabName());

                //Absent_Logs absentLog2 = new Absent_Logs(absentDetails.getAbsent_ID(), "System", "Automated email is sent to student with one time link to submit absence justification. \n\n<b>Link:</b> " + oneTimeLink, "System Generated");
                //absentLogsRepository.save(absentLog2);

                // Add a delay between email sends
                Thread.sleep(1000);  // 1 seconds delay between emails
            }
        }


        // Testing generating oneTimeLink and sending it to my own email at zsoh007
//        String oneTimeLink = generateOneTimeLink(UUID.fromString("6907b84c-fd67-4ea5-8fe8-70b1cb0089e7"));
//        System.out.println("One-time link: " + oneTimeLink);
//        emailService.sendAbsenceEmail("zsoh007", oneTimeLink, new Date(), LocalTime.of(9, 30), LocalTime.of(11, 20), "Soh Zu Wei", "SC2006", "MACS", "SWLAB3");

//        Absent_Logs absentLog = new Absent_Logs(UUID.fromString("6907b84c-fd67-4ea5-8fe8-70b1cb0089e7"), "System", "Automated email is sent to student with one time link to submit absence justification. \n\n<b>Link:</b> " + oneTimeLink, "System Generated");
//        absentLogsRepository.save(absentLog);
    }

    /*@Async
    @Transactional
    public void processAbsencesForDemoPurposes() throws MessagingException, InterruptedException {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        List<Attendance> absences = attendanceRepository.findPendingAbsencesForToday(currentDate, currentTime);

        for (Attendance attendance : absences) {
            attendance.setStatus("Absent");
            System.out.println("Mark student as absent: " + attendance.getStudent().getStudent_ID());

            // If It's true, means we already created a make-up session for this student before, and the student did not attend the make up session
            // Then we need to mark the absent created previously as "Absent During Make-up"
            // If its false, we just create an absent for the student and send email to request for reason of absence
            if (attendance.getIsMakeUpSession()) {
                System.out.println("Student is absent for make up lesson");

                // Retrieve absent created previously
                Optional<Absent_Details> absentDetailsOptional = absentDetailsRepository.getAbsentDetailsByMakeUpAttendanceID(attendance.getAttendance_ID());

                if (absentDetailsOptional.isPresent()) {
                    Absent_Details absentDetails = absentDetailsOptional.get();
                    absentDetails.setStatus("Absent During Make Up");
                    absentDetailsRepository.save(absentDetails);

                    // Add log for marking absent during make-up
                    Absent_Logs absentLog = new Absent_Logs(absentDetails.getAbsent_ID(), "System", "Student did not attend make up lesson session. \n\nStatus updated to [Absent During Make-Up]", "System Generated");
                    absentLogsRepository.save(absentLog);

                } else {
                    System.out.println("Absent details not found for make up session");
                }

            } else {
                System.out.println("Student is absent for normal lab lesson");
                // Create new absent for the student
                Absent_Details absentDetails = new Absent_Details();
                absentDetails.setReason("");
                absentDetails.setImageLink("");
                absentDetails.setStatus("Pending");
                absentDetailsRepository.save(absentDetails);

                attendance.setAbsent_ID(absentDetails.getAbsent_ID());
                attendanceRepository.save(attendance);

                // Add log for marking absent
                Absent_Logs absentLog = new Absent_Logs(absentDetails.getAbsent_ID(), "System", "Student was absent for lab session. \n\nStatus updated to [Pending]", "System Generated");
                absentLogsRepository.save(absentLog);

                // TODO: uncomment this code to send to REAL STUDENT EMAIL instead of mock data.
                // String oneTimeLink = generateOneTimeLink(attendance.getStudent().getStudent_ID());
                // emailService.sendAbsenceEmail(attendance.getStudent().getStudent_ID(), oneTimeLink);

                // TODO: comment this code which sends to myself for testing purposes only.
                String oneTimeLink = generateOneTimeLink(absentDetails.getAbsent_ID());
                System.out.println("One-time link: " + oneTimeLink);
                emailService.sendAbsenceEmail("zsoh007", oneTimeLink, java.sql.Date.valueOf(attendance.getLabsession().getDate()), attendance.getLabsession().getStartTime(), attendance.getLabsession().getEndTime(), attendance.getStudent().getFullName(), attendance.getLabsession().getClassGroup().getClassGroupId().getModuleCode(), attendance.getLabsession().getClassGroupID().getClassGroupID(), attendance.getLabsession().getLabID().getLabName());

                Absent_Logs absentLog2 = new Absent_Logs(absentDetails.getAbsent_ID(), "System", "Automated email is sent to student with one time link to submit absence justification. \n\n<b>Link:</b> " + oneTimeLink, "System Generated");
                absentLogsRepository.save(absentLog2);

                // Add a delay between email sends
                Thread.sleep(1000);  // 1 seconds delay between emails
            }
        }*/


        // Testing generating oneTimeLink and sending it to my own email at zsoh007
//        String oneTimeLink = generateOneTimeLink(UUID.fromString("6907b84c-fd67-4ea5-8fe8-70b1cb0089e7"));
//        System.out.println("One-time link: " + oneTimeLink);
//        emailService.sendAbsenceEmail("zsoh007", oneTimeLink, new Date(), LocalTime.of(9, 30), LocalTime.of(11, 20), "Soh Zu Wei", "SC2006", "MACS", "SWLAB3");

//        Absent_Logs absentLog = new Absent_Logs(UUID.fromString("6907b84c-fd67-4ea5-8fe8-70b1cb0089e7"), "System", "Automated email is sent to student with one time link to submit absence justification. \n\n<b>Link:</b> " + oneTimeLink, "System Generated");
//        absentLogsRepository.save(absentLog);
//    }

    /*public static String generateOneTimeLink(UUID absentID) {
        // Generate and return the one-time link
        return frontend_ip + "/absences/view/" + absentID.toString();
    }*/
}
