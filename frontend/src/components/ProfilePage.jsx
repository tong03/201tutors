import React, { useState, useEffect } from "react";
import "./css/ProfilePage.css";
import { useNavigate } from "react-router-dom";

const ProfilePage = (props) => {
  const [loading, setLoading] = useState(false);
  const [courses, setCourses] = useState([]);
  const [schedule, setSchedule] = useState([]);

  const [selectedCourse, setSelectedCourse] = useState("");
  const [selectedDay, setSelectedDay] = useState("");
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [major, setMajor] = useState("");
  const [standing, setStanding] = useState("");

  const navigate = useNavigate();

  const addTimeDisabled = !selectedDay || !startTime || !endTime;
  const addCourseDisabled = !selectedCourse;

  const parseTimeInput = (timeStr) => {
    if (timeStr.includes(":")) {
      return parseInt(timeStr.split(":")[0], 10);
    } else {
      return parseInt(timeStr, 10);
    }
  } 

  const formatTime = (hour) => {
    const dayType = hour >= 12 ? "PM" : "AM";
    const normHour = hour === 0 ? 12 : hour > 12 ? hour - 12 : hour;
    const fTime = `${normHour}:00 ${dayType}`;
    return fTime;
  };

  useEffect(() => {
    const getProfile = async () => {
      try {
        const res = await fetch(`ProfilePage?userID=${props.userID}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        });

        if (res.status == 200) {
          const data = await res.json();
          if (data) {
            setFirstName(data.fname || "");
            setLastName(data.lname || "");
            setMajor(data.major || "");
            setStanding(data.standing || "");
            setCourses(data.courses || []);
            setSchedule(data.availability || []);
          }

          console.log("Data retrieved");
        } else if (res.status == 400) {
          console.log("No profile found, starting blank...");
        }
      } catch (err) {
        console.error("Error getting profile data:", err);
      } finally {
        setLoading(false);
      }
    };
    getProfile();
  }, [props.userID]);

  const handleConfirm = async (e) => {
    e.preventDefault();
    const formData = {
      id: props.userID,
      fname: firstName,
      lname: lastName,
      major: major,
      standing: standing,
      courses: courses,
      availability: schedule,
    };

    try {
      const res = await fetch(`ProfilePage`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (res.status == 200) {
        console.log("Form data successfully saved in DB!");
        props.setIsTutor(courses.length > 0);
        navigate("/");
      } else {
        console.log("Error saving form data into DB", res.status);
      }
    } catch (err) {
      console.error("Error sending profile data to DB:", err);
    }
  };

  const handleCancel = (e) => {
    e.preventDefault();
    navigate("/");
  };

  const handleScheduleAdd = (e) => {
    e.preventDefault();

    const startHour = parseTimeInput(startTime);
    const endHour = parseTimeInput(endTime);

    if (startHour >= endHour) {
      alert("Please input an end time that is after the start time.");
      return;
    }

    setSchedule([...schedule, { dayOfWeek: selectedDay, startHour: startHour, endHour: endHour }]);
    setSelectedDay("");
    setStartTime("");
    setEndTime("");
  };

  const handleScheduleRemove = (targetSlot) => {
    setSchedule((allSlots) => allSlots.filter((slot) => slot !== targetSlot));
  };

  const handleCourseAdd = (e) => {
    e.preventDefault();
    const course = props.courseList[selectedCourse - 1];
    if (course && !courses.includes(course)) {
      setCourses([...courses, course]);
      setSelectedCourse("");
    }
  };

  const handleCourseRemove = (targetCourse) => {
    setCourses((allCourses) =>
      allCourses.filter((course) => course.id !== targetCourse)
    );
  };

  if (loading) {
    return <div>Loading Profile Page....</div>;
  }

  return (
    <>
      <div className="profile-page-container">
        <div className="profile-page">
          <div className="profile-right">
            <form className="profile-form">
              <div className="inputDiv">
                <label htmlFor="fName">First Name</label>
                <input
                  id="fName"
                  type="text"
                  placeholder="First Name"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                />
              </div>

              <div className="inputDiv">
                <label htmlFor="lName">Last Name</label>
                <input
                  id="lName"
                  type="text"
                  placeholder="Last Name"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                />
              </div>

              <div className="inputDiv">
                <label htmlFor="major">Major</label>
                <input
                  id="major"
                  type="text"
                  placeholder="Major"
                  value={major}
                  onChange={(e) => setMajor(e.target.value)}
                />
              </div>

              <div className="inputDiv">
                <label>Year Standing</label>
                <input
                  type="text"
                  placeholder="Year Standing"
                  value={standing}
                  onChange={(e) => setStanding(e.target.value)}
                />
              </div>

              <div className="courseInputDiv">
                <div className="input-headers">
                  <label htmlFor="courseSelect">Courses</label>
                  <button
                    className="addCourseBtn"
                    onClick={(e) => handleCourseAdd(e)}
                    disabled={addCourseDisabled}
                  >
                    Add Course
                  </button>
                </div>
                <select
                  className="courseSelect"
                  id="courseSelect"
                  name="courses"
                  value={selectedCourse}
                  onChange={(e) => setSelectedCourse(e.target.value)}
                >
                  <option value="" disabled>
                    Select a course
                  </option>
                  {props.courseList.map((course) => 
                    !(courses.some((c) => c.id === course.id)) && <option key={course.id} value={course.id}>CSCI-{course.code} - {course.name}</option>
                  )}
                </select>
              </div>

              <div className="timeInputDiv">
                <label htmlFor="weekdaySelect">Time Available</label>
                <div className="input-headers">
                  <select
                    id="weekdaySelect"
                    className="weekdays"
                    value={selectedDay}
                    onChange={(e) => setSelectedDay(e.target.value)}
                  >
                    <option value="" disabled>
                      Day of Week
                    </option>
                    <option value="Monday">Monday</option>
                    <option value="Tuesday">Tuesday</option>
                    <option value="Wednesday">Wednesday</option>
                    <option value="Thursday">Thursday</option>
                    <option value="Friday">Friday</option>
                    <option value="Saturday">Saturday</option>
                    <option value="Sunday">Sunday</option>
                  </select>
                  <button
                    className="addTimeBtn"
                    disabled={addTimeDisabled}
                    onClick={(e) => handleScheduleAdd(e)}
                  >
                    Add Time
                  </button>
                </div>
                <div className="timeInput">
                  <input
                    type="text"
                    placeholder="Start - 13:00"
                    value={startTime}
                    onChange={(e) => setStartTime(e.target.value)}
                  />
                  <div></div>
                  <input
                    type="text"
                    placeholder="End - 16:00"
                    value={endTime}
                    onChange={(e) => setEndTime(e.target.value)}
                  />
                </div>
              </div>
            </form>

            <div className="display-container">
              <div className="course-display">
                <ul>
                  {courses.map((course, idx) => (
                    <li key={idx} className="course-item">
                      <span className="course-name">CSCI-{course.code}</span>
                      <span
                        className="course-remove"
                        onClick={() => handleCourseRemove(course.id)}
                      >
                        X
                      </span>
                    </li>
                  ))}
                </ul>
              </div>
              <div className="time-display">
                <table className="time-table">
                  <thead>
                    <tr>
                      <th>Day</th>
                      <th>Start Time</th>
                      <th>End Time</th>
                    </tr>
                  </thead>
                  <tbody>
                    {schedule.map((slot, idx) => (
                      <tr key={idx}>
                        <td>{slot.dayOfWeek}</td>
                        <td>{formatTime(slot.startHour)}</td>
                        <td className="endTimeControls">
                          <span className="endTime">{formatTime(slot.endHour)}</span>
                          <span
                            onClick={() => handleScheduleRemove(slot)}
                            className="endTimeRemove"
                          >
                            X
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <div className="profile-controls">
          <button className="cancel-btn" onClick={(e) => handleCancel(e)}>
            Cancel
          </button>
          <button className="confirm-btn" onClick={(e) => handleConfirm(e)}>
            Confirm
          </button>
        </div>
      </div>
    </>
  );
};

export default ProfilePage;
