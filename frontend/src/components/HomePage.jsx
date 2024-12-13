import React from "react";
import "./css/HomePage.css";
const HomePage = () => {
  return (
<main className="main">
    <h1 className = "welcome">
      Hi there! Welcome to TechTutors.
    </h1>

    <h2 className="subtitle">
        Are you looking for some extra help with CSCI courses?<br/>
        Or are you skilled enough to tutor others?
    </h2>

    <p className="description">
      TechTutors connects current students taking CSCI classes with experienced 
      peers, taking the stress out of finding help and allowing you to focus more 
      on your assignments!
    </p>

    <p className="cta-text">
      Use the buttons at the top of the page to start learning collaboratively!
    </p>

</main>

  );
};

export default HomePage;
