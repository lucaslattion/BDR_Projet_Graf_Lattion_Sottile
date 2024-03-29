# BDR_Projet_Graf_Lattion_Sottile

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




# API - The routes

We are aware that the token, permission and password management lack of security. If the times permit it, we will add better security.

## Auth routes
### **POST /login**
The request *POST /login* is used to get authenticated by the server.

It provides a token that will be used in future request.

To use it you need to know a user credential or create one: [POST /user](#post-user).

Send a JSON in with a similar example formated values:
```json
{
    "email": "test.user1@email.com",
    "password": "newPassword2"
}
```

The server return a token user with the email address:
```token
user=test.user1@email.com; Path=/;
```

The following *admin* token has all the privileges (some request user based request might not work):
```token
user=admin; Path=/;
```

### **POST /logout**
The request *POST /logout* remove the token user received from the login request.


## Users routes

### **GET /profile**
The request *GET /profile* ask the server the current logged in user profile.

The server reply with a JSON containing the profile information:
```json
{
    "firstName": "Test",
    "lastName": "User1",
    "email": "test.user1@email.com",
    "password": "newPassword2"
}
```

### **POST /user**
The request *POST /user* create a user.

Send a JSON in with a similar example formated values:
```json
{
    "firstName": "Test",
    "lastName": "User1",
    "email": "test.user1@email.com",
    "password": "newPassword2"
}
```
The server answer with status code "201: Created" when the creation is successful.
When the user email address already exist the server return: "409: Conflict".

Once a user is created you can then log in with [POST /login](#post-login) request.


### **PUT /user/{email}**
The request *PUT /user/{email}* update the user with the email address: {email}.

Send the request and a JSON in with a similar example formated values:
```example
PUT /user/test.user1@email.com
```
```json
{
    "firstName": "Test",
    "lastName": "NewLastName",
    "email": "test.user1@email.com",
    "password": "newPassword3"
}
```
The server answer with status code "204: No Content" when the update is successful.

### **DELETE /user/{email}**
The request *DELETE /user/{email}* delete the user with the email address: {email}.

Send a JSON in with a similar example formated values:
```example
DELETE /user/test.user1@email.com
```
The server answer with status code "204: No Content" when the deletion is successful.


## CRUD for table of the database

For the following table of the postgre database, CRUD (Create, Read, Update, Delete) requests are available as described.

The request must be done with a logged in user [POST /login](#post-login).

List of the table-name:
```list of table name
    Aliment
    Aliment_contient_vitamine
    Liste
    Liste_contient_recette
    Recette
    Recette_contient_aliment
    Recette_utilise_ustensil
    Regime
    Sousgroupe
    Type
    Ustensil
    Utilisateur_cache_aliment
    Utilisateur_cache_recette
    Utilisateur_cache_sousgroupe
    Utilisateur_possede_aliment
    Utilisateur_suit_regime
    Vitamine
```

### **GET /table-name**
The request *GET /table-name* (in lowercase) ask the server to get all the entries for the table.

The server reply with a JSON containing the table information (example with aliment):
```json
[
    {
        "anom": "Ananas",
        "kcal": 50,
        "proteines": 0.5,
        "glucides": 13.0,
        "lipides": 0.2,
        "fibres": 1.4,
        "sodium": 1.0,
        "groupe": "Fruits Tropicaux"
    },
    {
        "anom": "Banane",
        "kcal": 105,
        "proteines": 1.3,
        "glucides": 27.0,
        "lipides": 0.3,
        "fibres": 3.1,
        "sodium": 1.0,
        "groupe": "Fruits"
    }
]
```

### **GET /table-name/limit**
The request *GET /table-name/limit* (in lowercase) ask the server to get some entries for the table.

```json
{
    "limit":"0",    // limit the number of element returned, default 0 means all elements.
    "offset":"0",   // skip x number of entries, default 0 means no skipped elements.
    "anom": "TestAliment",       // specific filter to aliment
    "groupe": "Fruits Tropicaux" // specific filter to aliment
}
```

The server reply with a JSON containing the table information (example with aliment):
```json
[
    {
        "anom": "TestAliment",
        "kcal": 50,
        "proteines": 0.5,
        "glucides": 13.0,
        "lipides": 0.2,
        "fibres": 1.4,
        "sodium": 1.0,
        "groupe": "Fruits Tropicaux"
    }
]
```


### **POST /table-name**
The request *POST /table-name* create an entry.

Send a JSON in with the table values, example with aliment formated values:
```json
    {
        "anom": "TestAliment",
        "kcal": 50,
        "proteines": 0.5,
        "glucides": 13.0,
        "lipides": 0.2,
        "fibres": 1.4,
        "sodium": 1.0,
        "groupe": "Fruits Tropicaux"
    }
```
The server answer with status code "201: Created" when the creation is successful.
When the entry already exist the server return: "409: Conflict".

### **PUT /table-name/{identifier}**
The request *PUT /table-name/{email}* update the entry with the identifier: {identifier}.

Send the request and a JSON in with a similar example formated values:
```example
PUT /aliment/TestAliment1
```
```json
{
    "anom": "TestAlimentUpdated",
    "kcal": 50,
    "proteines": 999,
    "glucides": 13.0,
    "lipides": 0.2,
    "fibres": 1.4,
    "sodium": 1.0,
    "groupe": "Fruits Tropicaux"
}
```
The server answer with status code "204: No Content" when the update is successful.

### **DELETE /table-name/{identifier}**
The request *DELETE /table-name/{identifier}* delete the entry with the table identifier: {identifier}.

Send the delete request like:
```example
DELETE /aliment/TestAliment1
```
The server answer with status code "204: No Content" when the deletion is successful.

