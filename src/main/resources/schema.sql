CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS system_admin
(
    id           BIGSERIAL PRIMARY KEY,
    emailAddress VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    fullName     VARCHAR(100) NOT NULL,
);

CREATE TABLE IF NOT EXISTS users
(
    user_id      BIGSERIAL PRIMARY KEY, -- Remember to change once integrating with SSO
    emailAddress VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    domain       VARCHAR(255) NOT NULL,
    fullName     VARCHAR(100) NOT NULL,
);

CREATE TABLE IF NOT EXISTS professors
(
    prof_id    BIGINT NOT NULL,
    PRIMARY KEY prof_id,
    college    VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL,
    title      VARCHAR(255) NOT NULL,
    CONSTRAINT fk_prof FOREIGN KEY (prof_id) REFERENCES users (user_id) ON DELETE CASCADE,
);

CREATE TABLE IF NOT EXISTS lab_executives
(
    lab_exec_id        BIGINT NOT NULL,
    PRIMARY KEY lab_exec_id,
    college            VARCHAR(255) NOT NULL,
    lab_specialization VARCHAR(255) NOT NULL,
    title              VARCHAR(255) NOT NULL,
    CONSTRAINT fk_lab_exec FOREIGN KEY (lab_exec_id) REFERENCES users (user_id) ON DELETE CASCADE,
);

CREATE TABLE IF NOT EXISTS school_admins
(
    sch_admin_id      BIGINT NOT NULL,
    PRIMARY KEY sch_admin_id,
    title             VARCHAR(255) NOT NULL,
    role              VARCHAR(255) NOT NULL,
    permission        VARCHAR(255) NOT NULL,
    assigned_faculty  VARCHAR(255) NOT NULL,
    CONSTRAINT fk_sch_admin FOREIGN KEY (sch_admin_id) REFERENCES users (user_id) ON DELETE CASCADE,
);

CREATE TABLE IF NOT EXISTS students
(
    matric_no      VARCHAR(255) NOT NULL,
    PRIMARY KEY matric_no,
    fullName       VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL,
);

CREATE TABLE IF NOT EXISTS lab_session
(
    lab_sess_id  UUID DEFAULT uuid_generate_v4(),
    PRIMARY KEY lab_sess_id,
    courseName   VARCHAR(255) NOT NULL,
    courseCode   VARCHAR(255) NOT NULL,
    schoolName   VARCHAR(255) NOT NULL,
    prof_id      VARCHAR(255) NOT NULL,
    lab_exec_id  VARCHAR(255) NOT NULL,
    datetime     TIMESTAMP NOT NULL,
    CONSTRAINT fk_prof_id FOREIGN KEY (prof_id) REFERENCES professors (prof_id) ON DELETE CASCADE,
    CONSTRAINT fk_lab_exec_id FOREIGN KEY (lab_exec_id) REFERENCES lab_executives (lab_exec_id) ON DELETE CASCADE,
);

CREATE TABLE IF NOT EXISTS lab_session_student
(
    lab_sess_id UUID,
    student_id  VARCHAR(255),
    PRIMARY KEY (lab_sess_id, student_id),
    CONSTRAINT fk_lab_sess_id FOREIGN KEY (lab_sess_id) REFERENCES lab_session (lab_sess_id) ON DELETE CASCADE,
    CONSTRAINT fk_student_id FOREIGN KEY (student_id) REFERENCES students (matric_no) ON DELETE CASCADE,
);

CREATE TABLE IF NOT EXISTS notifications
(
    notification_id UUID DEFAULT uuid_generate_v4(),
    domain          VARCHAR(255) NOT NULL,
    user_id         BIGINT NOT NULL,
    PRIMARY KEY notification_id,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
);