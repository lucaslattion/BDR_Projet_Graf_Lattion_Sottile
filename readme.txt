# BDR_Projet_Graf_Lattion_Sottile

# Start the project
## Setup and start the database

0. Clone the Github project: https://github.com/lucaslattion/BDR_Projet_Graf_Lattion_Sottile
1. Docker compose up
2. Open the project folder "BDR_DataGrip_Proj" with DataGrip application
3. Launch the 00_creation_table_project.sql, use username=bdr and password=bdr
4. Select the schema bdr.bdrProject to launch the other sql queries.

## Setup and start the Web application

1. Open the project folder "BDR_API_Web_Proj" with Intellij
2. Run "Package as Jar file"
3. Run the application: "java -jar .\target\BDR_API_Web_Proj-1.0-SNAPSHOT.jar"
4. Access http://localhost/

