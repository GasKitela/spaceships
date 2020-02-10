# XL-Spaceship


Spaceship game in Scala

#Requirements


* Scala 2.12.2
* sbt 0.13.17

#Usage


* Method 1) 'run.sh' script (may need execute permissions first). That will compile and run the application. You can provide an specific port, i.e. "./run.sh 8080".
* Method 2) 'sbt run' (or sbt "run <port>") in XLSpaceShipsPlay directory.

#Gameplay


* Once per instance, call **POST /user/new** with body _{"user_id": String, "full_name": String, "host": String, "port": Int }_
 Where host and port must be the ones where your instance is running.

* Next up, if you want to challenge another player, you must **POST /xl-spaceship/user/game/new** with body:

Where hostname and port, in this case, belong to the player you are challenging.

* If the challenge is successful, battle simulation starts and you will be redirected to status screen (gameId also printed in console).

#Routes

####Protocol API

* POST    /xl-spaceship/protocol/game/new
````
{
    "user_id": "gki10",
    "full_name": "Gaston Kitela",
    "spaceship_protocol": {
        "hostname": "127.0.0.1",
        "port": 8080
    }
}

Request for new simulation sent by gki10 to player in 127.0.0.1:8080.
````
* PUT     /xl-spaceship/protocol/game/:id
````
{
    "salvo": ["0x0", "8x4", "DxA", "AxA", "7xF"]
}

Salvo attack received from opponent.

NOTE: salvo shot are read like positions in a matrix. 
For example, "8x4" means row 8 (top to bottom), column 4 (left to right).
````

####User API

* POST    /user/new
```
{
  	"user_id": "gki10",
  	"full_name": "Gaston Kitela",
  	"host": "127.0.0.1",
    "port": 8080
}
  
First step after instantiating application. Mandatory to start playing. 
```

* POST    /xl-spaceship/user/game/new
```
{
    "spaceship_protocol": {
        "hostname": "10.0.0.2",
        "port": 9000
    }
}

Challenge another player. The opponent wil receive this via protocol API.
```
* GET     /xl-spaceship/user/game/:id
```
Displays vital information about the game:
* Your board status
* Opponent board status
* who goes next
* winner
```

* PUT     /xl-spaceship/user/game/:id/fire
````
{
    "salvo": ["0x0", "8x4", "DxA", "AxA", "7xF"]
}

Salvo attack to send to opponent. The opponent wil receive this via protocol API
````
