# secondBot — Telegram-бот с микросервисной архитектурой

Telegram-бот (webhook), который принимает от пользователей текст, документы и фотографии,
сохраняет файлы и выдаёт ссылки для скачивания. Регистрация пользователя подтверждается
через письмо на email.

## Стек технологий

- Java 11
- Spring Boot 2.5.2
- Telegram Bots (telegrambots-starter 6.1.0)
- RabbitMQ — обмен сообщениями между сервисами
- PostgreSQL — хранение пользователей и файлов
- Lombok, Log4j, Maven (мульти-module проект)

## Модули

| Модуль | Назначение |
|---|---|
| `dispatcher` | Точка входа. Принимает обновления от Telegram по webhook, публикует их в RabbitMQ и отправляет ответы пользователю |
| `node` | Основная бизнес-логика: обработка команд, регистрация, загрузка файлов, работа с БД |
| `rest-service` | REST API: скачивание файлов по ссылке и активация учётной записи |
| `mail-service` | Отправка писем для подтверждения регистрации (SMTP) |
| `common-rabbitmq` | Константы имён очередей |
| `common-jpa` | JPA-сущности и DAO |
| `common-utils` | Общие утилиты и DTO (`CryptoTool`, `MailParams`) |

## Как это работает

```
Telegram ──webhook──> dispatcher ──RabbitMQ──> node ──> PostgreSQL
                        ^                       │
                        │                       ├──> mail-service ──SMTP──> почта
                        └────RabbitMQ───────────┘
                                                  
Пользователь ──ссылка──> rest-service ──> файл из PostgreSQL
```

1. `dispatcher` получает update от Telegram и кладёт его в очередь RabbitMQ
   (`text_message_update`, `doc_message_update`, `photo_message_update`).
2. `node` забирает update из очереди, обрабатывает и сохраняет данные в PostgreSQL.
3. Ответы возвращаются в очередь `answer_message`, `dispatcher` отправляет их в Telegram.
4. При регистрации `node` отправляет запрос в `mail-service`, тот шлёт письмо со ссылкой
   активации. Ссылка ведёт на `rest-service`, который активирует пользователя.
5. Загруженные документы и фото отдаются через `rest-service` по зашифрованной ссылке.

## Команды бота

| Команда | Описание |
|---|---|
| `/start` | Приветствие |
| `/help` | Список доступных команд |
| `/registration` | Регистрация: бот просит email и отправляет письмо для подтверждения |
| `/cancel` | Отмена текущей команды |

Зарегистрированные и активированные пользователи могут отправлять боту документы и фото,
в ответ он возвращает ссылку для скачивания.

## Порты сервисов

| Сервис | Порт |
|---|---|
| `dispatcher` | 8084 |
| `node` | 8085 |
| `rest-service` | 8086 |
| `mail-service` | 8087 |
| RabbitMQ | 5672 |
| PostgreSQL | 5432 |

## Требования

- JDK 11+
- Maven 3.6+
- Запущенные RabbitMQ и PostgreSQL

## Сборка и запуск

Сборка всех модулей:

```bash
mvn clean install
```

Запуск сервисов (в отдельных терминалах, в порядке зависимостей):

```bash
mvn spring-boot:run -pl rest-service
mvn spring-boot:run -pl mail-service
mvn spring-boot:run -pl node
mvn spring-boot:run -pl dispatcher
```

## Конфигурация

Настройки находятся в `application.properties` каждого модуля:

- **dispatcher** — токен и имя бота, адрес webhook (ngrok для локальной разработки), подключение к RabbitMQ;
- **node** — подключение к RabbitMQ и PostgreSQL, URI mail-service, адрес `rest-service`, `salt` для шифрования ссылок;
- **rest-service** — подключение к PostgreSQL, `salt` для дешифрования ссылок;
- **mail-service** — SMTP-настройки (хост, порт, логин, пароль), URI активации в `rest-service`.

> ⚠️ Токен бота, пароли от БД, SMTP и RabbitMQ сейчас хранятся в репозитории.
> Для продакшена вынесите их в переменные окружения и не коммитьте секреты.
