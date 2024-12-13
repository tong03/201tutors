import React, { useState } from "react";
import "./css/SearchPage.css";
import { useNavigate } from "react-router-dom";
const SearchPage = (props) => {
  const navigate = useNavigate();
  const [tutors, setTutors] = useState([]);
  const [sortOrder, setSortOrder] = useState("asc");

  const formatTime = (hour) => {
    const dayType = hour >= 12 ? "PM" : "AM";
    const normHour = hour === 0 ? 12 : hour > 12 ? hour - 12 : hour;
    const fTime = `${normHour} ${dayType}`;
    return fTime;
  };

  const handleChatClick = (event) => {
    event.preventDefault();
    // Check if user is logged in
    if (!props.loggedIn) {
      alert("Please log in or register to chat with tutors");
      navigate("/login");
    } else {
      fetch(`searchServlet/startChat?studentID=${props.userID}&tutorID=${event.target.value}`).then(
        (response) => {
          if (response.ok) {
            navigate("/chat");
          } else {
            alert("You already have a chat with this tutor.");
          }
        })
    }
  };

  const showTutors = (event) => {
    event.preventDefault();
    fetch(`searchServlet/tutors?courseID=${event.target.value}`).then(
      (response) => response.json()).then((data) => {
        setTutors(data)
      })
  };

  const sortCourses = (event) => {
    event.preventDefault();
    setSortOrder(event.target.value);
  };

  const sortFunc = (c1, c2) => {
    if (sortOrder === "asc") {
      return c1.code.localeCompare(c2.code);
    }
    return c2.code.localeCompare(c1.code);
  };

  return (
    <div className="main-content">
      <section className="courses-section">
        <select className="sort-dropdown" onChange={sortCourses}>
          <option value="asc">Sort By: A-Z</option>
          <option value="desc">Sort By: Z-A</option>
        </select>
        <div id="coursesList">
          {props.courses.toSorted(sortFunc).map((course) => (
            <div className="course-item">
              <div className="course-info">
                <h3>CSCI-{course.code}</h3>
                <p>{course.name}</p>
              </div>
              <button
                className="view-tutors-btn"
                onClick={showTutors}
                id={course.code}
                value={course.id}
              >
                View Tutors
              </button>
            </div>
          ))}
        </div>
      </section>

      <section className="tutors-section" id="tutorsSection" style={{display: (tutors.length === 0) ? 'none' : 'block'}}>
        <h2>Available Tutors</h2>
        <div id="tutorsList">
          {tutors.map((tutor) => (
            <div className="tutor-item">
              <div className="tutor-info">
                <h4>{tutor.fname} {tutor.lname}</h4>
                <p>{tutor.standing} in {tutor.major}</p>
                {tutor.availability.map((avail) => (
                  <p>{avail.dayOfWeek} from {formatTime(avail.startHour)} to {formatTime(avail.endHour)}</p>
                ))}
              </div>
              <button className="chat-btn" value={tutor.id} onClick={handleChatClick}>
                Chat Now
              </button>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default SearchPage;