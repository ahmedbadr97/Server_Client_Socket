# Server_Client_Socket
## Fatma and Call of Duty <br>
Instructions
After the recent publishing of the game Call of Duty: Modern Warfare 3, a first-person shooting game, Fatma became jealous of the developers who created this game and decided to create her own version of this game Call of Duty: Fatma Warfare. Unfortunately, Fatma doesn’t have knowledge of game engines, so she decided to make the game Command-Line Interface (CLI) using Java, the only programming language she knows.

The game she wants to create is simple:

You sign up with your username on the main server using the login <username> command, e.g.
login khaled

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

|khaled | alive | 5 |

|zeinab | dead| 7 |

|fatma | alive | -1 |

---------------------------



The game ends when all players but one are dead.

When a player dies, all players are informed by the server.
