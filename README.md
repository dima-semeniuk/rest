# RestfullApi example App

<hr>

## Technologies and tools used

* Java (SOLID, Stream Api, Collections)
* Spring (Boot, Web MVC)
* Test: JUnit, Mockito,
* Version Control: Git
* Maven


<hr>

## Functionalities of controllers

#### User controller

>  POST method: /api/users/registration

This endpoint is for registration new user. Example of request body:

```json
{
  "email":"some.email@ukr.net",
  "firstName":"Dmytro",
  "lastName":"Semeniuk",
  "birthDate": "2006-04-04",
  "address":"Saharova, 110",
  "phoneNumber": null
}
```

When registration will be successful, you will get response body and HttpStatus '201 Created':

```json
{
  "id": 1,
  "email": "some.email@ukr.net",
  "firstName": "Dmytro",
  "lastName": "Sememiuk",
  "birthDate": "2006-04-04",
  "address": "Saharova, 110",
  "phoneNumber": null
}
```

<br>

>  PUT method: /api/users/{id}

This endpoint is for updating existing user by id. Request body is the same with previous endpoint. 
When updating will be successful, you will get response body and HttpStatus '200 Ok'.

<br>

>  PATCH method: /api/users/{id}

This endpoint is for partial updating existing user by id. Request body example:

```json
{
  "email":"another.email@ukr.net",
  "address":"Skovorody, 110"
}
```

When updating will be successful, you will get response body and HttpStatus '200 Ok'.

<br>

>  DELETE method: /api/users/{id}

This endpoint is for deleting existing user by id. When deleting will be successful, 
you will get HttpStatus '204 No Content'.

<br>

>  GET method: /api/books/searchByBirthDateRange

This endpoint shows all users, filtered by birthdate range.

<br>