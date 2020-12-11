# forum_backend
This project is forum. Users can create their own account, they are authenticated by JWT. Unauthenticated users can see topics and comments. To add new topic or comment user has to be authenticated. Author of topic or comment can delete or modify his entry.

To run this project MySQL server is required. On database server you need account with permissions to do every action on schema. If username is different than "admin" and password is different than "admin123" it has to be specified in application.properties (spring.datasource.username and spring.datasource.password) and in liquibase.properties (username and password). Database url also has to be specified in those property files if it is different than default. Schema can be empty because in this project liquibase is implemented, it also starts proper schema with default values.

There is no front-end application for this project so to see results of request proper tool has to be used (e.g. postman).
