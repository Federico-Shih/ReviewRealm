# README - Review Realm

Review Realm fue desarrollada con Spring MVC y Java 8. La aplicación utiliza Maven como herramienta de gestión de dependencias y construcción del proyecto. Además, se utilizan las siguientes versiones de las bibliotecas principales:

    Spring Security 4.2.0
    Hibernate 5.1.0

## Configuración

Para configurar adecuadamente la aplicación, asegúrese de seguir estos pasos:
1. Clonar el repositorio:
```bash
git clone git@bitbucket.org:itba/paw-2023a-04.git
```
2. Importar el proyecto:

3. Configuración de las claves:

La aplicación utiliza claves para el módulo "Remember Me" y para la configuración de envío de correos electrónicos. Asegúrese de ubicar los siguientes archivos en las rutas correspondientes:

    Remember Me key: Copie el archivo rememberme_key.pem en la siguiente ruta: /webapp/src/main/resources/keys/rememberme_key.pem.
    Propiedades de correo: Coloque el archivo de propiedades mailing.properties en la siguiente ruta: /webapp/src/main/resources/mailing/mailing.properties.

Asegúrese de proporcionar los valores adecuados en el archivo mailing.properties para la configuración del servidor de correo electrónico.

4. Configuración de la base de datos:

La aplicación utiliza Hibernate para la capa de persistencia. Asegúrese de configurar correctamente los detalles de la base de datos en el archivo application.properties ubicado en /webapp/src/main/resources/.

```
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
mailing.weburl=
mailing.serverurl=
```
5. Ejecución

Una vez que haya realizado la configuración adecuada, puede ejecutar la aplicación web siguiendo estos pasos:

* Construya el proyecto Maven:

```
mvn clean
mvn build
```

* Ejecutar la aplicacion con Tomcat Server


# Project: Review Realm API Docs
# 📁 Collection: User


## End-point: Get Users
200: OK

204: No Content

400: Error en los QueryParams
### Method: GET
>```
>{{baseUrl}}/users?sort=level&direction=asc&followers=8
>```
### Query Params

|Param|value|
|---|---|
|search|faker|
|sort|level|
|direction|asc|
|username|faker_2|
|id|1|
|pageSize|2|
|page|2|
|email|fedeshih%2B6%40gmail.com|
|preferences|7|
|gamesPlayed|9|
|following|8|
|followers|8|
|samePreferencesAs|8|
|sameGamesPlayedAs|8|


### 🔑 Authentication bearer

|Param|value|Type|
|---|---|---|
|token|eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmZWRlc2hpaCUyQjclNDBnbWFpbC5jb20iLCJleHAiOjE3MDAzNDAxNjIsInVzZXJuYW1lIjoiZmVkZXNoaWg3Iiwicm9sZSI6IltVU0VSXSIsImVtYWlsIjoiZmVkZXNoaWgrN0BnbWFpbC5jb20iLCJpZCI6MjUsImlzcyI6InBhdy0yMDIzYS0wNCIsImlhdCI6MTcwMDI1Mzc2Mn0.f6OGao-av2U-GP4Mu_0KhO5-K6cK7VAXxAPkmMk3_Y8|string|


### Response: undefined
```json
null
```


⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get User By Id
200: OK

404: No existe el usuario
### Method: GET
>```
>{{baseUrl}}/users/3
>```

⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Create User
201: OK

400: mal el body

409: username o email ya existen
### Method: POST
>```
>{{baseUrl}}/users
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.user-form.v1+json|


### Headers

|Content-Type|Value|
|---|---|
|Accept-Language|es|


### Body (**raw**)

```json
{
    "username": "fedeshih",
    "email": "fedeshih@gmail.com",
    "password": "holahola"
}
```

### 🔑 Authentication noauth

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Change Password Request
202: OK

400: Error en el body
### Method: POST
>```
>{{baseUrl}}/users
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.password-reset.v1+json|


### Headers

|Content-Type|Value|
|---|---|
|Accept-Language|en-UK|


### Body (**raw**)

```json
{
    "email": "fedeshih@gmail.com"
}
```

### 🔑 Authentication noauth

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Validate User Request
202: OK

400: Error en el body
### Method: POST
>```
>{{baseUrl}}/users
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.enable-user.v1+json|


### Headers

|Content-Type|Value|
|---|---|
|Accept-Language|en-UK|


### Body (**raw**)

```json
{
    "email": "mmanzur+2@itba.edu.ar"
}
```

### 🔑 Authentication noauth

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Follow User
204: OK

400: error en el body

409: si ya estás siguiendo al usuario

404: si no existe el usuario del path

401/403
### Method: POST
>```
>{{baseUrl}}/users/2/following
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.following-form.v1+json|


### Body (**raw**)

```json
{
    "userId": 16
}
```

### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Unfollow User
204: OK

404: ya se había borrado o no existe el usuario

401/403
### Method: DELETE
>```
>{{baseUrl}}/users/2/following/100
>```
### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Edit User
204: OK

400: error en el body

401/403
### Method: PATCH
>```
>{{baseUrl}}/users/19
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.patch-user-form.v1+json|


### Body (**raw**)

```json
{
    "genres": [1, 2],
    "password": "holahola"
}
```

### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Add Favorite Game
204: OK

409: el juego ya es favorito

400: si está mal el body

401/403
### Method: POST
>```
>{{baseUrl}}/users/2/favoritegames
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.favorite-game-form.v1+json|


### Body (**raw**)

```json
{
    "gameId": 2
}
```

### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Delete Favorite Game
204: OK

404: no existe el game o ya se habia borrado

401/403
### Method: DELETE
>```
>{{baseUrl}}/users/2/favoritegames/9
>```
### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get User Notifications
200: OK

401/403
### Method: GET
>```
>{{baseUrl}}/users/2/notifications
>```
### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Update Notifications
200: OK

400: error en el body

401/403
### Method: PUT
>```
>{{baseUrl}}/users/2/notifications
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.notifications-form.v1+json|


### Body (**raw**)

```json
{
    "userIFollowWritesReview": false,
    "myReviewIsDeleted": false
}
```

### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get User Mission Progresses
200: OK

204: si no tiene progresses

401/403
### Method: GET
>```
>{{baseUrl}}/users/20/mission-progresses
>```
### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃
# 📁 Collection: Enums


## End-point: Get Genres
200: OK

204: No Content

400: exclusive Filters
### Method: GET
>```
>{{baseUrl}}/genres?forGame=7
>```
### Query Params

|Param|value|
|---|---|
|forUser|5|
|forGame|7|


### 🔑 Authentication noauth

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get Genre By Id
200: OK

404: no existe el genre
### Method: GET
>```
>{{baseUrl}}/genres/6
>```
### Headers

|Content-Type|Value|
|---|---|
|If-None-Match|"1795650019"|


### 🔑 Authentication noauth

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get Missions
200: OK
### Method: GET
>```
>{{baseUrl}}/missions
>```
### 🔑 Authentication noauth

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get Mission By Id
200: OK

404: no existe la mission
### Method: GET
>```
>{{baseUrl}}/missions/REPUTATION_GOAL
>```
### 🔑 Authentication noauth

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get Notifications
200: OK
### Method: GET
>```
>{{baseUrl}}/notifications
>```
### 🔑 Authentication noauth

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get notification by id
200: OK

404: no existe la notif
### Method: GET
>```
>{{baseUrl}}/notifications/userIFollowWritesReview
>```

⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃
# 📁 Collection: Games


## End-point: Get Games
200: OK

204: No Content

401: usar el ?suggested= sin iniciar sesion

403: usar el ?suggested= no siendo moderator

400: exclusive filters

400: filtros mal puestos
### Method: GET
>```
>{{baseUrl}}/games?search=a&direction=asc&page=1
>```
### Headers

|Content-Type|Value|
|---|---|
|Accept-Language|es-419|


### Query Params

|Param|value|
|---|---|
|search|a|
|direction|asc|
|excludeNoRating|true|
|suggested|true|
|recommendedFor|1|
|page|1|
|pageSize|10|
|genres|1|
|rating|1.00t10.00|
|favoriteOf|1|
|sort|publishDate|
|notReviewedBy|2|


### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get Game By Id
200: OK

404: si no existe game
### Method: GET
>```
>{{baseUrl}}/games/2
>```

⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Delete Game
204: OK

404: Not Found

401/403
### Method: DELETE
>```
>{{baseUrl}}/games/38
>```
### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Accept/Reject Game
204: OK

401/403

400: si esta mal el body

409: si ya se resolvió
### Method: PATCH
>```
>{{baseUrl}}/games/41
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.game-suggestion-form.v1+json|


### Body (**raw**)

```json
{
    "accept": true
}

```

### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Create Game
pide un form-data como content-type

401: si no estas logueado

201: OK si sos mod

202: OK si no sos mod

400: si hay un argumento mal
### Method: POST
>```
>{{baseUrl}}/games
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|multipart/form-data|


### Body formdata

|Param|value|Type|
|---|---|---|
|image|/C:/Users/matym/OneDrive/Escritorio/foto perfil.jpg|file|
|name|Nombre|text|
|description|Descripción|text|
|publisher|Publisher|text|
|genres|5|text|
|genres|3|text|
|releaseDate|2015-10-15|text|
|developer|Desarrollador|text|
|cualquiera|aaaaaaa|text|


### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: EditGame
200: OK

400: mal argumentos form

404: si no existe el game

401/403
### Method: PATCH
>```
>{{baseUrl}}/games/29
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|multipart/form-data|


### Body formdata

|Param|value|Type|
|---|---|---|
|image||file|
|name|Nombre|text|
|description|Descripcion|text|
|publisher|Publisher|text|
|genres|3|text|
|genres|4|text|
|releaseDate|2015-10-15|text|
|developer|Developer|text|
|accepted|false|text|


### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃
# 📁 Collection: Reports


## End-point: Get Reports
200: OK

204: No Content

400: si esta mal los queryParams

401/403
### Method: GET
>```
>{{baseUrl}}/reports?reporterId=2&reason=spam
>```
### Body (**raw**)

```json

```

### Query Params

|Param|value|
|---|---|
|reviewId|36|
|reporterId|2|
|moderatorId|2|
|reportedUserId|8|
|page|1|
|pageSize|10|
|reason|spam|


### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get Report by Id
200: OK

404: no existe el report

401/403
### Method: GET
>```
>{{baseUrl}}/reports/1
>```
### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Create Report
201: OK

404: si no existe el reviewId

400: si esta mal la reason

401
### Method: POST
>```
>{{baseUrl}}/reports
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.report-form.v1+json|


### Body (**raw**)

```json
{
    "reviewId": 10,
    "reason": "spam"
}
```

### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Accept/Reject Report
200: OK

400: si está mal el body

404: no existe el report

401/403

409: si ya se resolvió
### Method: PATCH
>```
>{{baseUrl}}/reports/27
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.report-handle-form.v1+json|


### Body (**raw**)

```json
{
    "state": "rejected"
}
```

### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|


### Response: undefined
```json
null
```


⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃
# 📁 Collection: Reviews


## End-point: Get Reviews
200: OK

400: Exclusive Filters

400: error en los queryparams

204: No Content
### Method: GET
>```
>{{baseUrl}}/reviews
>```
### Query Params

|Param|value|
|---|---|
|recommendedFor|2|
|gameId|2|
|sort|rating|
|direction|asc|
|gameGenres|3|
|excludeAuthors|2|
|authors|2|
|timeplayed|5|
|authorPreferences|5|
|platforms|PC|
|difficulty|EASY|
|completed|true|
|replayable|false|
|search|jueg|
|newForUser|7|


### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get Review By Id
200: OK

404: no existe
### Method: GET
>```
>{{baseUrl}}/reviews/25
>```

⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Create Review
201: OK

400: mal el form

409: review repetida

401
### Method: POST
>```
>{{baseUrl}}/reviews
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.review-form.v1+json|


### Body (**raw**)

```json
{
    "reviewTitle": "titulo de la reseña",
    "reviewContent": "contenido",
    "reviewRating": 8,
    "replayability": "True",
    "gameId": 15,
    "platform": "pc",
    "difficulty": "hard",
    "completed": "True",
    "gameLength": 1000,
    "unit": "minutes"
}
```

### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Delete review by id
204: OK

404: no existe

401/403
### Method: DELETE
>```
>{{baseUrl}}/reviews/38
>```
### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Edit Review
204: OK

404: no existe la reseña

400: error en el body

401/403
### Method: PUT
>```
>{{baseUrl}}/reviews/39
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.review-update.v1+json|


### Body (**raw**)

```json
{
    "reviewTitle": "título de la reseña",
    "reviewContent": "contenido",
    "reviewRating": 8,
    "replayability": true,
    "platform": "xbox",
    "difficulty": "hard",
    "completed": true,
    "gameLength": 78,
    "unit": "hours"
}
```

### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Get Review Feedback From User
200: OK

404: no existe la review o el feedback de ese usuario (o el usuario)
### Method: GET
>```
>{{baseUrl}}/reviews/36/feedback/70
>```
### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Update Review Feedback
201: si no había feedback

200: OK

404: no existe la reseña o el usuario

400: error en el body

401/403
### Method: PUT
>```
>{{baseUrl}}/reviews/29/feedback/8
>```
### Headers

|Content-Type|Value|
|---|---|
|Content-Type|application/vnd.review-feedback-form.v1+json|


### Body (**raw**)

```json
{
    "feedbackType": "LIKE"
}
```

### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|


### Response: undefined
```json
null
```


⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃

## End-point: Delete Review Feedback
200: OK

404: No existe el feedback

401/403
### Method: DELETE
>```
>{{baseUrl}}/reviews/29/feedback/8
>```
### 🔑 Authentication basic

|Param|value|Type|
|---|---|---|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃
# 📁 Collection: Images


## End-point: Get Image
200: OK

404: si no existe
### Method: GET
>```
>{{baseUrl}}/images/WOTBNztTdhyY7gc7?width=150&height=100
>```
### Query Params

|Param|value|
|---|---|
|width|150|
|height|100|



⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃ ⁃
_________________________________________________
Powered By: [postman-to-markdown](https://github.com/bautistaj/postman-to-markdown/)
