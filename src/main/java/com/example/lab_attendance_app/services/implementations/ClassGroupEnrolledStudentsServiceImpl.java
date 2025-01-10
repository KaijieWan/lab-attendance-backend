package com.example.lab_attendance_app.services.implementations;

import com.example.lab_attendance_app.enums.ExecutionStatus;
import com.example.lab_attendance_app.models.entities.ClassGroup;
import com.example.lab_attendance_app.models.entities.Student;
import com.example.lab_attendance_app.models.entities.Student_Enrolled_ClassGroup;
import com.example.lab_attendance_app.models.entities.embedded.ClassGroupId;
import com.example.lab_attendance_app.models.repositories.ClassGroupEnrolledStudentsRepository;
import com.example.lab_attendance_app.models.repositories.ClassGroupRepository;
import com.example.lab_attendance_app.models.repositories.StudentRepository;
import com.example.lab_attendance_app.services.ClassGroupEnrolledStudentsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClassGroupEnrolledStudentsServiceImpl implements ClassGroupEnrolledStudentsService {
    private static final Logger logger = LogManager.getLogger(ClassGroupEnrolledStudentsServiceImpl.class);
    private final StudentRepository studentRepository;
    private final ClassGroupEnrolledStudentsRepository classGroupEnrolledStudentsRepository;
    private final ClassGroupRepository classGroupRepository;

    public ClassGroupEnrolledStudentsServiceImpl(StudentRepository studentRepository, ClassGroupEnrolledStudentsRepository classGroupEnrolledStudentsRepository, ClassGroupRepository classGroupRepository) {
        this.studentRepository = studentRepository;
        this.classGroupEnrolledStudentsRepository = classGroupEnrolledStudentsRepository;
        this.classGroupRepository = classGroupRepository;
    }

    public ExecutionStatus createNewClassGroupEnrolledStudents(Student_Enrolled_ClassGroup classGroupEnrolledStudents){
        Optional<Student_Enrolled_ClassGroup> existingClassGroupEnrolledStudents = classGroupEnrolledStudentsRepository.findById(classGroupEnrolledStudents.getId());

        if (existingClassGroupEnrolledStudents.isPresent()) {
            return ExecutionStatus.VALIDATION_ERROR;
        }

        // Retrieve and set the Student entity
        String studentId = classGroupEnrolledStudents.getId().getStudentId();
        logger.info(classGroupEnrolledStudents);

        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isEmpty()) {
            return ExecutionStatus.NOT_FOUND;
        }
        classGroupEnrolledStudents.setStudent(studentOptional.get());

        // Retrieve and set the ClassGroup entity
        String moduleCode = classGroupEnrolledStudents.getId().getModuleCode();
        String classGroupId = classGroupEnrolledStudents.getId().getClassGroupId();
        String semesterId = classGroupEnrolledStudents.getId().getSemesterID();
        //Find based on classGroupId that is made up of the three attributes as seen
        Optional<ClassGroup> classGroupOptional = classGroupRepository.findById(new ClassGroupId(classGroupId, moduleCode, semesterId));
        if (classGroupOptional.isEmpty()) {
            return ExecutionStatus.INVALID;

        }
        classGroupEnrolledStudents.setClassGroup(classGroupOptional.get());

        // Save the new Student_Enrolled_ClassGroup
        classGroupEnrolledStudentsRepository.save(classGroupEnrolledStudents);
        return ExecutionStatus.SUCCESS;
    }

    public List<Student_Enrolled_ClassGroup> getAllClassGroupEnrolledStudents(){
        return classGroupEnrolledStudentsRepository.findAll();
    }

    public Map<String, List<Student>> getStudentsByModuleAndSemester(String moduleCode, String semesterId){
        // Retrieve all class groups for the given moduleCode and semesterId
        List<ClassGroup> classGroups = classGroupRepository.findByModuleCodeAndSemesterId(moduleCode, semesterId);

        // Create a mapping of classGroupId to the list of students
        Map<String, List<Student>> classGroupToStudentsMap = new HashMap<>();

        for (ClassGroup classGroup : classGroups) {
            String classGroupId = classGroup.getClassGroupId().getClassGroupID();
            List<Student> students = classGroupEnrolledStudentsRepository.findStudentsByClassGroup(
                    classGroupId, moduleCode, semesterId);
            classGroupToStudentsMap.put(classGroupId, students);
        }

        return classGroupToStudentsMap;
    }
}
