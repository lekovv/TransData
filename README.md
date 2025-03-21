# TransData

[English version](README.en.md)

_TransData_ - это веб-сервис, который хранит транзакции пользователей в БД,
собирает метрики с помощью Apache Spark, сохраняет их в БД один раз в сутки
и предоставляет АПИ для получения этих метрик.

### В проекте реализовано:
* Авторизация (Jwt)
* ZIO-HTTP Endpoints
* Работа с PostgreSQL, Quill
* Liquibase для миграций
* Логирование (ZIO-logging, SLF4J2)
* Scheduler
* Service Pattern и DI
* Логика обработки ошбибок
* Метрики Apache Spark такие как:
    * Общая сумма транзакций
    * Средняя сумма транзакций
    * Топ пользователей по итоговой сумме транзакций
    * Рейтинг стран из которых были совершены транзакции

### Авторизация:
При выполнении миграций, в БД создается администратор.
Чтобы авторизоваться, используйте эндпойнт `POST /login` с телом запроса:
```json
{
  "username": "admin",
  "password": "admin"
}
```
Полученный токен используйте в остальных методах, добавив `Bearer Token`

### Описание эндпойнтов:
1) `POST /api/create/users` - метод для создания пользователей (uuid генерируется автоматически).
Тело запроса:
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
На выходе получите список их идентификаторов:
```json
[
  "021ebe62-eba9-4e03-807a-1f00b76e4dda", 
  "abae737e-01de-42e2-b2b2-04382a65a965"
]
```
2) `POST /api/create/transactions` - метод для создания транзакций пользователей (uuid генерируется автоматически).
Тело запроса:
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
3) `GET /api/spark/amount` - метод для получения метрик _общая и средняя сумма транзакций_
4) `GET /api/spark/top-users` - метод для получения метрики _топ пользователей по итоговой сумме транзакций_
5) `GET /api/spark/country-stats` - метод для получения метрики _рейтинг стран из которых были совершены транзакции_


