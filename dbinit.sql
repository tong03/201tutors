DROP SCHEMA IF EXISTS 201FP;
CREATE SCHEMA 201FP;
USE 201FP;
-- Main user table
CREATE TABLE Users (
	userID INT NOT NULL AUTO_INCREMENT,
	email VARCHAR(64) NOT NULL,
	pw VARCHAR(64) NOT NULL,
    fname VARCHAR(64) NOT NULL,
    lname VARCHAR(64) NOT NULL,
    standing VARCHAR(64) NOT NULL,
    major VARCHAR(64) NOT NULL,
	PRIMARY KEY (userID)
);
-- List of CSCI courses
CREATE TABLE Courses (
	courseID INT NOT NULL AUTO_INCREMENT,
    courseCode VARCHAR(5) NOT NULL,
    courseName VARCHAR(64) NOT NULL,
    PRIMARY KEY (courseID)
);
-- Table of users who can tutor in a given course
CREATE TABLE CourseTutors (
	userID INT NOT NULL,
    courseID INT NOT NULL,
    FOREIGN KEY (userID) REFERENCES Users(userID),
    FOREIGN KEY (courseID) REFERENCES Courses(courseID)
);
-- Table of availabilities for tutors
CREATE TABLE Availability (
    userID INT NOT NULL,
    dayOfWeek VARCHAR(9) NOT NULL,
    startHour INT NOT NULL,
    endHour INT NOT NULL,
    FOREIGN KEY (userID) REFERENCES Users(userID)
);
CREATE TABLE Conversations (
	chatID INT NOT NULL AUTO_INCREMENT,
    tutorID INT NOT NULL,
    studentID INT NOT NULL,
    PRIMARY KEY (chatID),
    FOREIGN KEY (tutorID) REFERENCES Users(userID),
    FOREIGN KEY (studentID) REFERENCES Users(userID)
);
-- Timestamp format (in UTC): yyyy-mm-dd hh:mm:ss
-- Sender is 0 if tutor, 1 if student
CREATE TABLE Messages (
	messageID INT NOT NULL AUTO_INCREMENT,
	chatID INT NOT NULL,
    sender INT NOT NULL,
    msgTimestamp VARCHAR(19) NOT NULL,
    content VARCHAR(512) NOT NULL,
    PRIMARY KEY (messageID),
    FOREIGN KEY (chatID) REFERENCES Conversations(chatID)
);

-- Course list from the Spring 2025 Course Catalogue
INSERT INTO Courses (courseCode, courseName) VALUES ("102L", "Fundamentals of Computation");
INSERT INTO Courses (courseCode, courseName) VALUES ("103L", "Introduction to Programming");
INSERT INTO Courses (courseCode, courseName) VALUES ("104L", "Data Structures and Object Oriented Design");
INSERT INTO Courses (courseCode, courseName) VALUES ("170", "Discrete Methods in Computer Science");
INSERT INTO Courses (courseCode, courseName) VALUES ("201", "Principles of Software Development");
INSERT INTO Courses (courseCode, courseName) VALUES ("270", "Introduction to Algorithms and Theory of Computing");
INSERT INTO Courses (courseCode, courseName) VALUES ("310", "Software Engineering");
INSERT INTO Courses (courseCode, courseName) VALUES ("350", "Introduction to Operating Systems");
INSERT INTO Courses (courseCode, courseName) VALUES ("353", "Introduction to Internetworking");
INSERT INTO Courses (courseCode, courseName) VALUES ("356", "Introduction to Computer Systems");
INSERT INTO Courses (courseCode, courseName) VALUES ("360", "Introduction to Artificial Intelligence");
INSERT INTO Courses (courseCode, courseName) VALUES ("368", "Cross-Platform App Development");
INSERT INTO Courses (courseCode, courseName) VALUES ("402", "Operating Systems");
INSERT INTO Courses (courseCode, courseName) VALUES ("420", "Computer Graphics");
INSERT INTO Courses (courseCode, courseName) VALUES ("426", "Game Prototyping");
INSERT INTO Courses (courseCode, courseName) VALUES ("430", "Introduction to Computer and Network Security");
INSERT INTO Courses (courseCode, courseName) VALUES ("435", "Professional C++");
INSERT INTO Courses (courseCode, courseName) VALUES ("450", "Introduction to Computer Networks");
INSERT INTO Courses (courseCode, courseName) VALUES ("455x", "Introduction to Programming Systems Design");
INSERT INTO Courses (courseCode, courseName) VALUES ("457", "Computer Systems Organization");
INSERT INTO Courses (courseCode, courseName) VALUES ("458", "Numerical Methods");
INSERT INTO Courses (courseCode, courseName) VALUES ("467", "Introduction to Machine Learning");
INSERT INTO Courses (courseCode, courseName) VALUES ("475", "Theory of Computation");
INSERT INTO Courses (courseCode, courseName) VALUES ("487", "Programming Game Engines");
INSERT INTO Courses (courseCode, courseName) VALUES ("501", "Numerical Analysis and Computation");
INSERT INTO Courses (courseCode, courseName) VALUES ("502b", "Numerical Analysis");
INSERT INTO Courses (courseCode, courseName) VALUES ("505b", "Applied Probability");
INSERT INTO Courses (courseCode, courseName) VALUES ("517", "Research Methods and Analysis for User Studies");
INSERT INTO Courses (courseCode, courseName) VALUES ("520", "Computer Animation and Simulation");
INSERT INTO Courses (courseCode, courseName) VALUES ("522", "Game Engine Development");
INSERT INTO Courses (courseCode, courseName) VALUES ("526", "Advanced Mobile Devices and Game Consoles");
INSERT INTO Courses (courseCode, courseName) VALUES ("531", "Applied Cryptography");
INSERT INTO Courses (courseCode, courseName) VALUES ("532", "Innovation for Defense Applications");
INSERT INTO Courses (courseCode, courseName) VALUES ("533", "Algebraic Combinatorics");
INSERT INTO Courses (courseCode, courseName) VALUES ("534", "Affective Computing");
INSERT INTO Courses (courseCode, courseName) VALUES ("535", "Multimodal Probabilistic Learning of Human Communcation");
INSERT INTO Courses (courseCode, courseName) VALUES ("544", "Applied Natural Language Processing");
INSERT INTO Courses (courseCode, courseName) VALUES ("550", "Advanced Data Stores");
INSERT INTO Courses (courseCode, courseName) VALUES ("555L", "Advanced Operating Systems");
INSERT INTO Courses (courseCode, courseName) VALUES ("557", "Computer Systems Architecture");
INSERT INTO Courses (courseCode, courseName) VALUES ("559", "Machine Learning I: Supervised Methods");
INSERT INTO Courses (courseCode, courseName) VALUES ("561", "Foundations of Artificial Intelligence");
INSERT INTO Courses (courseCode, courseName) VALUES ("563", "Building Knowledge Graphs");
INSERT INTO Courses (courseCode, courseName) VALUES ("566", "Deep Learning and Its Applications");
INSERT INTO Courses (courseCode, courseName) VALUES ("567", "Machine Learning");
INSERT INTO Courses (courseCode, courseName) VALUES ("568", "Requirements Engineering");
INSERT INTO Courses (courseCode, courseName) VALUES ("570", "Analysis of Algorithms");
INSERT INTO Courses (courseCode, courseName) VALUES ("571", "Web Technologies");
INSERT INTO Courses (courseCode, courseName) VALUES ("572", "Information Retrieval and Web Search Engines");
INSERT INTO Courses (courseCode, courseName) VALUES ("576", "Multimedia Systems Design");
INSERT INTO Courses (courseCode, courseName) VALUES ("577a", "Software Engineering");
INSERT INTO Courses (courseCode, courseName) VALUES ("580", "3-D Graphics and Rendering");
INSERT INTO Courses (courseCode, courseName) VALUES ("585", "Database Systems");
INSERT INTO Courses (courseCode, courseName) VALUES ("587", "Geospatial Information Management");
INSERT INTO Courses (courseCode, courseName) VALUES ("625", "Program Synthesis and Computer-Aided Verification");
INSERT INTO Courses (courseCode, courseName) VALUES ("644", "Natural Language Dialogue Systems");
INSERT INTO Courses (courseCode, courseName) VALUES ("649", "Haptic Interfaces and Virtual Environments");
INSERT INTO Courses (courseCode, courseName) VALUES ("655", "Advanced Topics in Operating Systems");
INSERT INTO Courses (courseCode, courseName) VALUES ("658", "Diagnosis and Design of Reliable Digital Systems");
INSERT INTO Courses (courseCode, courseName) VALUES ("670", "Advanced Analysis of Algorithms");
INSERT INTO Courses (courseCode, courseName) VALUES ("673", "Structure and Dynamics of Networked Information");
INSERT INTO Courses (courseCode, courseName) VALUES ("677", "Advanced Computer Vision");


-- TESTING: Insert dummy data into Users to prepare for CourseTutors table
INSERT INTO Users (email, pw, fname, lname, standing, major) VALUES ("jmixon@gmail.com", "1234", "Joe", "Mixon", "senior", "econ");
