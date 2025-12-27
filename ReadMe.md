Prerequisites

Java 21
Maven 3.8+

ToCheck version: 
java -version
mvn -version

Build the Application

mvn clean install

Run the Application

mvn spring-boot:run

## Application will start at:  http://localhost:8080

## Swagger UI URL: http://localhost:8080/swagger-ui/index.html

## Swagger OpenApi DOCs: http://localhost:8080/v3/api-docs

Sample API Request:
## CreateCustomer: http://localhost:8080/Customer/api/customers
Request Body:
{
"name": "sonu",
"email": "hhh@gmail.com",
"annualSpend": 1000,
"lastPurchaseDate": "2025-10-12"
}

Response:
{
"data": {
"id": "3b1cf408-10f9-4ef5-9b77-ffd686bea454",
"name": "sonu",
"email": "hhh@gmail.com",
"annualSpend": 1000,
"lastPurchaseDate": "2025-10-12",
"tier": "GOLD"
},
"message": "Success",
"status": 201
}

## GetCustomerById: http://localhost:8080/Customer/api/customers/3b1cf408-10f9-4ef5-9b77-ffd686bea454
Response:
{
"data": {
"id": "3b1cf408-10f9-4ef5-9b77-ffd686bea454",
"name": "sonu",
"email": "hhh@gmail.com",
"annualSpend": 1000.00,
"lastPurchaseDate": "2025-10-12",
"tier": "GOLD"
},
"message": "Success",
"status": 200
}

## GetCustomerByName: http://localhost:8080/Customer/api/customers?name=golu
Response:

{
"data": [
{
"id": "dfd33466-d5a0-4e89-9619-51ea3c6a7c14",
"name": "golu",
"email": "golu@gmail.com",
"annualSpend": 1000.00,
"lastPurchaseDate": "2025-10-12",
"tier": "GOLD"
}
],
"message": "Success",
"status": 200
}

## getCustomerByEmail: http://localhost:8080/Customer/api/customers/byEmail?email=golu@gmail.com

{
"data": {
"id": "dfd33466-d5a0-4e89-9619-51ea3c6a7c14",
"name": "golu",
"email": "golu@gmail.com",
"annualSpend": 1000.00,
"lastPurchaseDate": "2025-10-12",
"tier": "GOLD"
},
"message": "Success",
"status": 200
}

## UpdateCustomer byID: http://localhost:8080/Customer/api/customers/dfd33466-d5a0-4e89-9619-51ea3c6a7c14

RequestBody:
{
"name":"sonu",
"email":"sk@gmail.com",
"annualSpend": 2501.75,
"lastPurchaseDate": "2025-10-12"
}

Response: 

{
"data": {
"id": "dfd33466-d5a0-4e89-9619-51ea3c6a7c14",
"name": "sonu",
"email": "sk@gmail.com",
"annualSpend": 2501.75,
"lastPurchaseDate": "2025-10-12",
"tier": "GOLD"
},
"message": "Success",
"status": 200
}

## Delete Customer By Id: http://localhost:8080/Customer/api/customers/dfd33466-d5a0-4e89-9619-51ea3c6a7c14
Response: 204 no content


How to Access DB:

## URl: http://localhost:8080/h2-console
UserName: jigyasu
password: jigyasu


How to Run Unit Test:

mvm test



