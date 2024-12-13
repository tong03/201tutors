import React, { useState } from "react";
import "./css/LoginPage.css";
import { useNavigate } from "react-router-dom";

const LoginPage = (props) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [freshEmail, setFreshEmail] = useState("");
  const [freshPassword, setFreshPassword] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [year, setYear] = useState("");
  const [major, setMajor] = useState("");
  const navigate = useNavigate();
  const register = (event) => {
    event.preventDefault();
    if (!freshEmail.endsWith("@usc.edu")) {
      alert("Please enter a USC email address.");
      return;
    }

    fetch("RegisterServlet", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: freshEmail,
        password: freshPassword,
        fname: firstName,
        lname: lastName,
        standing: year,
        major: major,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        const id = parseInt(data);
        if (id === -1) {
          alert("This email already has an account associated with it.");
        } else {
          props.loginCallback(id);
          navigate("/");
        }
      });
  };

  const login = (event) => {
    event.preventDefault();
    fetch("LoginServlet", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: email,
        password: password,
      }),
    })
      .then((response) => response.json())
      .then((data) => {
        const id = parseInt(data);
        if (id === -1) {
          alert(
            "Incorrect login information; try a different email or password."
          );
        } else {
          props.loginCallback(id);
          navigate("/");
        }
      });
  };

  return (
    <>
      <div className="login-page-container">
        <div className="login-page">
          <div className="login-left">
            <form className="login-form">
              <label className="login-form-title">Login</label>
              <div className="inputDiv">
                <label htmlFor="email">Email</label>
                <input
                  id="email"
                  type="email"
                  placeholder="Email"
				  pattern="[a-z0-9._%+\-]+@[usc]+\.[edu]{2,}$"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>

              <div className="inputDiv">
                <label className="password">Password</label>
                <input
                  id="password"
                  type="password"
                  placeholder="Password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
              </div>

              <div className="login-control">
                <button className="sign-in-btn" onClick={(e) => login(e)}>
                  Sign In
                </button>
              </div>
            </form>
          </div>

          <div className="login-right">
            <label className="register-form-title">Create Account</label>
            <form className="register-form">
              <div className="inputDiv">
                <label htmlFor="freshEmail">Email</label>
                <input
                  id="freshEmail"
                  type="email"
				  pattern="[a-z0-9._%+\-]+@[usc]+\.[edu]{2,}$"
                  placeholder="Email"
                  value={freshEmail}
                  onChange={(e) => setFreshEmail(e.target.value)}
                />
              </div>

              <div className="inputDiv">
                <label htmlFor="freshPassword">Password</label>
                <input
                  id="freshPassword"
                  type="password"
                  placeholder="Password"
                  value={freshPassword}
                  onChange={(e) => setFreshPassword(e.target.value)}
                />
              </div>

              <div className="inputDiv">
                <label htmlFor="firstName">First Name</label>
                <input
                  id="firstName"
                  type="text"
                  placeholder="First Name"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                />
              </div>

              <div className="inputDiv">
                <label htmlFor="lastName">Last Name</label>
                <input
                  id="lastName"
                  type="text"
                  placeholder="Last Name"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                />
              </div>

              <div className="inputDiv">
                <label htmlFor="year">Standing / Year</label>
                <input
                  id="year"
                  type="text"
                  placeholder="Standing / Year"
                  value={year}
                  onChange={(e) => setYear(e.target.value)}
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
              <div className="register-control">
                <button className="register-btn" onClick={(e) => register(e)}>
                  Create Account
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  );
};

export default LoginPage;
