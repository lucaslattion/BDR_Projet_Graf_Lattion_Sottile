# BDR_Projet_Graf_Lattion_Sottile

# TODO
Complete the routes - Backend
Complete the html pages - Frontend
Update security change, password hash and cookie with additional word and time for validity also hashed.



# Start the project
## Setup the database

1. Docker compose up
2. Open the project folder "BDR_DataGrip_Proj" with DataGrip application
3. Launch the 00_creation_table_project.sql, use username=bdr and password=bdr
4. Select the schema bdr.bdrProject to launch the other sql queries.

## Setup the Web application developpement

1. Open the project folder "BDR_API_Web_Proj" with Intellij
2. Run "Package as Jar file"
3. Run the application: "java -jar .\target\BDR_API_Web_Proj-1.0-SNAPSHOT.jar"
4. Access http://localhost:8080/




# API

## The routes

// Done: Lucas
### Auth routes
app.post("/login", authController::login);
app.post("/logout", authController::logout);
app.get("/profile", authController::profile);


// Done: Lucas
### Users routes
app.post("/user", userController::create);
app.get("/user", userController::getMany);
app.get("/user/{email}", userController::getOne);
app.put("/user/{email}", userController::update);
app.delete("/user/{email}", userController::delete);


### Aliment routes
app.get("/aliment", alimentController::getMany);
app.post("/aliment", alimentController::create);
app.put("/aliment", alimentController::update);
app.delete("/aliment", alimentController::delete);

### Vitamine routes
app.get("/vitamine", vitamineController::getMany);
app.post("/vitamine", vitamineController::create);
app.put("/vitamine", vitamineController::update);
app.delete("/vitamine", vitamineController::delete);


### Recette routes
app.get("/recette", alimentController::getMany);
app.post("/recette", alimentController::create);
app.put("/recette", alimentController::update);
app.delete("/recette", alimentController::delete);



### Fonctionalités

app.post("/utilisateur_possede_aliment", UtilisateurPossedeAlimentController::create);
app.put("/utilisateur_possede_aliment", UtilisateurPossedeAlimentController::update);
app.delete("/utilisateur_possede_aliment", UtilisateurPossedeAlimentController::delete);

app.post("/utilisateur_cache_aliment", UtilisateurCacheAlimentController::create);
app.put("/utilisateur_cache_aliment", UtilisateurCacheAlimentController::update);
app.delete("/utilisateur_cache_aliment", UtilisateurCacheAlimentController::delete);


