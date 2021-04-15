# Forum REST Service

## Description
This is a service that allows you to add threads, comment on them and like threads and comments. The forum is divided into sub-forums where threads are placed. Threads can be blocked. The number of sub-forum nests and the number of threads in the forum is unlimited. The authors of threads and comments can modify or delete them. Global chat is available on the home page with the option of sending messages to a specific user.

Authentication is implemented on the forum. Users who are not logged in can only view the forum. A set of roles has been prepared for the management of forums. The admin and head_moderator roles cover all forums, while the moderator is assigned to specific forum and its subforums. For users with one of these roles, a user management panel is available with the option of assigning a role to other users.

## Running project locally

Clone code from main branch. To run this project MySQL server is required. On database server you need account with permissions to do every action on schema. Username, password and URL needs to be specified in application.property file inside main directory. In order to run tests the same action has to be done inside test directory. Schema can be empty because in this project liquibase is implemented, it creates tables and inserts some default values specified in resources/db/changelog/data.sql.

To see request results proper tool is required(e.g. postman)

## Project on host
  
This project is hosted on azure platform with front-end application made by friend of mine in angular. CI/CD is applied on main branch. 
Check here: https://pkaminski-forum.azurewebsites.net

## Additional info

Users pre-created both in main branch to run application locally and on hosting site have following accounts:

user1, user2, user3<br>
admin1, admin2<br>
headModerator1<br>
moderator1, moderator2, moderator3

Each account has role corresponding to login.

For all these accounts password is login+"pass" (e.g login: user1 password: user1pass)
