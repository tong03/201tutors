import React, { useState, useEffect, useRef } from "react";
import "./css/ChatPage.css";
import './css/ChatPageList.css';

const ChatPage = (props) => {
  const [messages, setMessages] = useState({});
  const [newMessage, setNewMessage] = useState("");
  const [tutorChats, setTutorChats] = useState([]);
  const [tuteeChats, setTuteeChats] = useState([]);
  const [currentChat, setCurrentChat] = useState(-1);
  const [viewAsTutor, setViewAsTutor] = useState(false);
  const socket = useRef(null);
  const [receiverName, setReceiverName] = useState("Select a user to chat with.");

  useEffect(() => {
    const getChatHistory = async () => {
      if (props.userID !== -1) {
        const data = await fetch(`ChatServlet?id=${props.userID}`).then((response) => response.json());
        setMessages(data.messages);
        setTutorChats(data.tutorChats);
        setTuteeChats(data.tuteeChats);
      }
    }

    getChatHistory();
  }, [props.userID]);

  useEffect(() => {
    socket.current = new WebSocket("ChatServer");
    socket.current.onopen = (event) => {
      console.log("Connected to the server");
    };
    socket.current.onmessage = (event) => {
      const msgData = JSON.parse(event.data);
      handleMessage(msgData);
    };
  }, []);

  const handleMessage = (message) => {
    setMessages((prevMessages) => {
      const newMessages = { ...prevMessages };
      // Only record this message if it's one this user currently has a chat for
      if (message.chatID in newMessages) {
        newMessages[message.chatID] = [...newMessages[message.chatID], message];
      }
      return newMessages;
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (newMessage.trim() && (currentChat !== -1)) {
      socket.current.send(
        JSON.stringify({
          chatID: currentChat,
          sentByTutor: viewAsTutor,
          content: newMessage,
        })
      );
      setNewMessage("");
    }
  };

  const userIsTutorInConvo = (chatID) => {
    for (const chat of tuteeChats) {
      if (chat.id === chatID) {
        return true;
      }
    }

    for (const chat of tutorChats) {
      if (chat.id === chatID) {
        return false;
      }
    }

    return false;
  }

  //format time
  const formatDisplayTime = (utcTime) => {
    const date = new Date(utcTime + " UTC");
    return date.toLocaleString("en-US", {
      hour: "numeric",
      minute: "2-digit",
      hour12: true,
    });
  };

  return (
    <span className="overall">
        <div className="chatPageList-page-container">
        <div className="chatPageList-page">
          <div className="chatPageList-left">
            <div className="button-container">
              <button
                className={viewAsTutor ? 'activeBtn' : ''}
                onClick={() => setViewAsTutor(true)}
              >
                Tutees
              </button>
              <button
                className={!viewAsTutor ? 'activeBtn' : ''}
                onClick={() => setViewAsTutor(false)}
              >
                Tutors
              </button>
            </div>
              <ul>
                {/* Render the right list based on if the user is chatting with tutees or tutors */}
                {(viewAsTutor ? tuteeChats : tutorChats).map((chat, index) => (
                  <li
                    key={index}
                    onClick={() => {
                      setCurrentChat(chat.id);
                      setReceiverName(chat.otherFname + " " + chat.otherLname); 
                    }}
                    className={currentChat === index + 1 ? "active-chat" : ""}
                  >
                    <span className="name">{chat.otherFname + " " + chat.otherLname}</span><br />
                    {messages[chat.id].length > 0 &&
                      <span className="small-text">{messages[chat.id].at(-1).content}</span>
                    }
                  </li>
                ))}
              </ul>
          </div>
        </div>
      </div>
      <div className="chatWindow">
        <div className="chatHeader">
          <div className="receiverName">{receiverName}</div>
        </div>
        <div className="chats">
          {(currentChat in messages) && messages[currentChat].map((message, index) => (
            <div key={index} className={(message.sentByTutor === userIsTutorInConvo(currentChat)) ? "mine" : "other"}>
              {message.content}
              <div className="timestamp">
                {formatDisplayTime(message.timestamp)}
              </div>
            </div>
          ))}
        </div>
        <div className="chatBox">
          <form onSubmit={handleSubmit}>
            <input
              type="text"
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              placeholder="Enter Message"
            />
            <button className="send" type="submit" disabled={currentChat === -1}>
              Send
            </button>
          </form>
        </div>
      </div>
    </span>
  );
};

export default ChatPage;
