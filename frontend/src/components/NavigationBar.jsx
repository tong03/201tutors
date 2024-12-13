import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "./css/NavigationBar.css";

// Props has booleans loggedIn and isTutor
const NavigationBar = (props) => {
  const navigate = useNavigate();
  const tryLogout = (event) => {
    if (props.loggedIn) {
      event.preventDefault();
      props.loginCallback(-1);
      navigate("/");
    }
  }

  const tryRedirect = (event) => {
    if (!props.loggedIn) {
      event.preventDefault();
      alert("Please login to use this feature.");
      navigate("/login");
    }
  }
  return (
    <div className="navbar">
      <nav>
        <ul style={{ listStyleType: "none" }}>
          <div className="left" id="homelink">
            <li>
              <Link to="/">Tech Tutors</Link>
            </li>
          </div>
          <div className="right">
            <li>
              <Link to="/search">
                <button>Find a Tutor</button>
              </Link>
            </li>
            <li>
              <Link to="/profile">
                <button onClick={tryRedirect}>
                  {props.isTutor && props.loggedIn
                    ? "Manage Profile"
                    : "Become a Tutor"}
                </button>
              </Link>
            </li>
            <li>
              <Link to="/chat">
                <button onClick={tryRedirect}>Chat</button>
              </Link>
            </li>
            <li>
              <Link to="/login">
                <button onClick={tryLogout}>{props.loggedIn ? "Logout" : "Login"}</button>
              </Link>
            </li>
          </div>
        </ul>
      </nav>
    </div>
  );
};

export default NavigationBar;
