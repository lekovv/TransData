# TransData

_TransData_ is a web service that stores user transactions in a DB,
collects metrics using Apache Spark, saves them in the DB once a day
and provides an API for receiving these metrics.

### The project implements:
* Authorization (Jwt)
* ZIO-HTTP Endpoints
* Work with PostgreSQL, Quill
* Liquibase for migrations
* Logging (ZIO-logging, SLF4J2)
* Scheduler
* Service Pattern and DI
* Error handling logic
* Apache Spark metrics such as:
  * Total transaction amount
  * Average transaction amount
  * Top users by total transaction amount
  * Rating of countries from which transactions were made

### Authorization:
When migrations are applied, an administrator will be created in the DB.
To log in, use the `POST /login` endpoint with the request body:
```json
{
"username": "admin",
"password": "admin"
}
```
Use the received token in other methods, adding `Bearer Token`

### Description of endpoints:
1) `POST /api/create/users` - method for creating users (uuid is generated automatically).
   Request body:
```json
[
 {
 "email": "user1@example.com",
 "firstName": "John",
 "lastName": "Doe",
 "country": "USA"
 },
 {
 "email": "user2@example.com",
 "firstName": "Jane",
 "lastName": "Smith",
 "country": "Canada"
 }
]
```
The output will be a list of their identifiers:
```json
[
 "021ebe62-eba9-4e03-807a-1f00b76e4dda",
 "abae737e-01de-42e2-b2b2-04382a65a965"
]
```
2) `POST /api/create/transactions` - method for creating user transactions (uuid is generated automatically).
   Request body:
```json
[
{
"userId": "021ebe62-eba9-4e03-807a-1f00b76e4dda",
"amount": 150.75,
"transactionType": "credit",
"description": "Payment for services"
},
{
"userId": "abae737e-01de-42e2-b2b2-04382a65a965",
"amount": 75.50,
"transactionType": "debit",
"description": "Withdrawal from account"
}
]
```
3) `GET /api/spark/amount` - method for getting metrics _total and average transaction amount_
4) `GET /api/spark/top-users` - method for getting metrics _top users by total transaction amount_
5) `GET /api/spark/country-stats` - method for getting metrics _rating of countries from which transactions were made_