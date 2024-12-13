import { Routes, Route, HashRouter } from "react-router-dom";
import ChatPage from "./components/ChatPage";
import HomePage from "./components/HomePage";
import LoginPage from "./components/LoginPage";
import NavigationBar from "./components/NavigationBar";
import ProfilePage from "./components/ProfilePage";
import SearchPage from "./components/SearchPage";
import React, { useState, useEffect } from "react";
import { useCookies } from "react-cookie";
import "./App.css";

function App() {
  const [courses, setCourses] = useState([]);
  const [cookies, setCookies] = useCookies(['userID']);
  const [isTutor, setIsTutor] = useState(true);
  useEffect(() => {
    fetch("searchServlet").then((response) => response.json().then((data) => {
      setCourses(data);
    }));
    if (!cookies.userID) {
      setCookies('userID', -1);
    }
    if (cookies.userID !== -1) {
      checkIfTutor(cookies.userID);
    }
  }, []);

  const login = (id) => {
    setCookies('userID', id);
    if (id !== -1) {
      checkIfTutor(id);
    }
  }

  const checkIfTutor = (id) => {
    fetch(`ProfilePage?userID=${id}`).then((response) => response.json().then((data) => {
      setIsTutor(data.courses.length > 0);
    }));
  }

  return (
    <HashRouter>
      <div className="app">
        <NavigationBar loggedIn={cookies.userID !== -1} isTutor={isTutor} loginCallback={login}></NavigationBar>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route
            path="/search"
            element={<SearchPage loggedIn={cookies.userID !== -1} userID={cookies.userID} courses={courses} />}
          />
          <Route path="/profile" element={<ProfilePage userID={cookies.userID} courseList={courses} setIsTutor={setIsTutor}/>} />
          <Route path="/chat" element={<ChatPage userID={cookies.userID}/>} />
          <Route path="/login" element={<LoginPage loginCallback={login}/>} />
        </Routes>
      </div>
    </HashRouter>
  );
}

export default App;
