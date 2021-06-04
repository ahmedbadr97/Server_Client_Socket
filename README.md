# Server_Client_Socket
## Fatma and Call of Duty <br>
Instructions
After the recent publishing of the game Call of Duty: Modern Warfare 3, a first-person shooting game, Fatma became jealous of the developers who created this game and decided to create her own version of this game Call of Duty: Fatma Warfare. Unfortunately, Fatma doesn’t have knowledge of game engines, so she decided to make the game Command-Line Interface (CLI) using Java, the only programming language she knows.

The game she wants to create is simple:

You sign up with your username on the main server using the login <username> command, e.g.
login ahmed

If the username is not taken, you enter the game

You start shooting people using their username and the shoot <username> command, e.g.,
shoot NoobMaster69

If the username is alive, when you shoot him/her, he dies and a point is added to your score.

If the username is already dead, a point is deducted from your score.

You can get all available usernames and their status using the players command, e.g.,
players

---------------------------
|username|status|score|

---------------------------

|ahmed | alive | 5 |

|zeinab | dead| 7 |

|fatma | alive | -1 |

---------------------------



The game ends when all players but one are dead.

When a player dies, all players are informed by the server.  
There are two CLient Versions  
  1. Cli version
  2. Gui javafx8 version
 -------------------------------------------------
 ### Server side  
 start the server to responde to client requests  
  1. user login  
  . check if the username already exsits it will send a string message to the client and end the connection with the client  
  . else it add the username to the server datastructure with score 0 and send to the client message "you have successfully logged in" and prints the     score board to the client
  2. user starts the game
  . user will have two commands to send to the server 
    1.shoot <username> --> shoot username already exists and username != his name 
      .if the <username> alive Server will send a message to all active users that <username> is dead and it will decrease <username> score by 1 and        increase client username by one
      . if <username> is already dead it will decrease the sending user score by one
  3. serve will continoue running till there is only one player alive which is the winner at this time the server will send to all active client that   the game is finished and it will send the score board to them and close the connection with all clients and shutdown the server 
  ### Client side
  1. Cli versrion
    after the client  logged in it will have a listener for the server messsages to print it to the console and listener to input from console to         send it to the client  
    client program will shutdown when the server sends to it a string contains 'quit' or connection is lost
    2.players 
  2. Gui version
    it will have a textArea to recive server messages in it and a textfield to send a message to the server and send button after the game is             finished you wont be able to send a message to the server as the textfield will be disabled and the button you will have access to the textarea       that have the server messages to check your score 
  
