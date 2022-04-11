Getting Started With Art-Ping
==================================

## Prerequisites

1. Create your Google account and your project in [Firebase console](https://console.firebase.google.com)
2. The same Gmail account is used for sending e-mails containing links for changing the password. Therefore, it is necessary to grant your account to work with smtp. In case not https protocol is used, it is required to switch on the accessibility of «insecure apps» into your account settings
3. [Min.io](https://min.io) is used for containing gamification content

## Main steps for Art-Ping installation:

### Step 1: Database setup

Use [Postgres](https://www.postgresql.org) min version 13.4. Use [configuration file](/docker/postgres/postgres.conf) for database configuration.

1. To start work, create a scheme `art_ping_db` and a User `art` who is going to work with the scheme.
User’s name can be different but, in this case, reconfigure the application access to the Database, see below.
User’s login and password are fixed in [`application.properties`](/backend/src/main/resources/application.properties)

2. In an initial state create just a User, Database and the User’s access to the empty database
For creating and updating the structure of the database use [liquibase](https://www.liquibase.org). For scripts executions change in  \backend\src\main\resources\application.properties the following properties:
   ```properties 
   spring.datasource.url=jdbc:postgresql://host:5432/art_ping_db
   spring.datasource.username=art
   spring.datasource.password=art
   ```
   When spring-boot application is started, liqui-scripts of creating tables in database will be executed.

3. The Administrator’s account will be created (`Super User`) with the following login and password
   ```
   admin/root
   ```
   If you want to change the Admin’s password, you need to code it via third-part services, 
   which are able to generate `bcrypt` passwords. Then update admin's account in table`art_ping.user` 

### Step 2: Google Firebase
`Google Firebase` is used for creating subscriptions on users’ devices and sending notifications
to these gadgets.

1. Having created your application into the [console](https://console.firebase.google.com), save 
its name into [file](backend/src/main/resources/firebaseServiceAccountKey.json) or use any 
other name but save it in [resources directory](backend/src/main/resources) and put this name
into [`application.properties`](/backend/src/main/resources/application.properties) in ``push-service.serviceAccountPK``

2. In case you need to restore your password, add login/password to the e-mail account which 
is used for sending e-mails into [`application.properties`](/backend/src/main/resources/application.properties) 
in `# spring mail properties` section 
    ```properties 
    spring.mail.username=your email
    spring.mail.password=your password
    art-ping.pwdRecoveryURL=https://your host/recovery
    ```

### Step 3: Min.io
`Min.io` used for keeping images.

Min.io was added in order to make the routine of checks process friendlier and funnier for 
employees. Set the section `# minio client properties` in [`application.properties`](/backend/src/main/resources/application.properties)
```properties
minio-client.endpoint=http://your minio host
minio-client.port=9000
minio-client.access-key=your minio user
minio-client.secret-key=your minio password
minio-client.bucket-name=your bucket with images
```


## Build and setup

[Apache Maven](https://maven.apache.org) minimum version 3.6.3  
[JDK](https://www.oracle.com/java/) version 11
For build application use:
``` bash 
$ mvn clean package 
```
Create a project using the [console](https://console.firebase.google.com) and add to the 
[file](frontend/public/firebase-messaging-sw.js) all necessary constants such as `API_KEY`,
 `VAPIDKEY`, `WEBAPP`, `APP_ID`, `PROJECT_ID` and so on. 

Repeat the same steps with the [file](frontend/src/constants/firebase_config.ts)
Add the backend link in target section of the [file](frontend/src/setupProxy.js) 


## Usage
To work with the application on a regular base as a user you need Firefox или Chrome. 
Save the link send by the Administrator in your bookmarks or favorites and allow 
notifications and geolocation.


