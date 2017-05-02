Please edit the config.properties file to include your own
database, username, and password. The "database" field 
will include the hostname, port, and name of the database, 
e.g. localhost:3306/databasename. Therefore, the connection 
string is:
jdbc:mysql://<hostname>:<port>/<dbname>?useSSL=false<user>
<password>

The overall connection string may be something like:
jdbc:mysql://localhost:3306/testdb?useSSL=falserootpassword

The project to be imported sits within the smartstreamdb folder

Note: the program was written in the Eclipse IDE
