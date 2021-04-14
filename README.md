# Forum REST Service

## Description
This is a service that allows you to add threads, comment on them and like threads and comments. The forum is divided into sub-forums where threads are placed. Threads can be blocked. The number of sub-forum nests and the number of threads in the forum is unlimited. The authors of threads and comments can modify or delete them. Global chat is available on the home page with the option of sending messages to a specific user.

Authentication is implemented on the forum. Users who are not logged in can only view the forum. A set of roles has been prepared for the management of forums. The admin and head_moderator roles cover all forums, while the moderator is assigned to specific forum and its subforums. For users with one of these roles, a user management panel is available with the option of assigning a role to other users.
  
## Running project locally

Clone code from main branch. To run this project MySQL server is required. On database server you need account with permissions to do every action on schema. If username is different than "admin" and password is different than "admin123" it has to be specified in application.properties (spring.datasource.username and spring.datasource.password) and in liquibase.properties (username and password). Database url also has to be specified in those property files if it is different than default. Schema can be empty because in this project liquibase is implemented, it creates tables and inserts some default values specified in resources/db/changelogdata.sql.

To see request results proper tool is required(e.g. postman)

## Project on host
  
 This project is hosted on azure platform with front-end application made by friend of mine in angular. CI/CD is applied on "azure" branch. 
 Check here: https://pkaminski-forum.azurewebsites.net
