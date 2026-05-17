# BatchProcessingCSVFiles

## Application Overview
BatchProcessingCSVFiles is a Spring Boot + Spring Batch application that reads CSV files and stores the data in database tables.

It currently supports:
- **User import job** (`importUserJob`) from `src/main/resources/csvFiles/users.csv`
- **Product import job** (`importProductJob`) from `src/main/resources/csvFiles/products.csv`
- **Movie rating import job** (`importMovieRatingJob`) from `src/main/resources/csvFiles/ratings.csv` (scheduled job)

Data is written to:
- `batch_user`
- `batch_product`
- `batch_movie_rating`

## Tech Stack
- Java 21
- Spring Boot
- Spring Batch
- Spring Data JPA
- Gradle
- PostgreSQL (active profile by default)

## Prerequisites
- Java 21 installed
- PostgreSQL running locally (or update profile/config for another DB)
- A database created for this app

## Configuration
1. Open `src/main/resources/application.yml` and confirm active profile:
   - `spring.profiles.active: postgres`
2. Open `src/main/resources/application-postgres.yml` and set:
   - `spring.datasource.url`
   - `spring.datasource.username`
   - `spring.datasource.password`
3. (Optional) H2/MySQL profile templates are available in:
   - `application-h2.yml`
   - `application-mysql.yml`

## Build and Test
From the repository root:

```bash
./gradlew clean test
```

> Note: Tests require a reachable configured database.

## Run the Application
From the repository root, run one of the following commands:

### Run all supported command-line jobs
```bash
./gradlew bootRun
```
(Default behavior runs both user and product jobs.)

### Run only user import
```bash
./gradlew bootRun --args='--job=user'
```

### Run only product import
```bash
./gradlew bootRun --args='--job=product'
```

### Run both user and product imports explicitly
```bash
./gradlew bootRun --args='--job=all'
```

## Scheduled Processing
`importMovieRatingJob` is executed by a scheduled runner (`ScheduledJobRunner`) using the configured cron expression in code.

## Input CSV Files
Place/update files under:
- `src/main/resources/csvFiles/users.csv`
- `src/main/resources/csvFiles/products.csv`
- `src/main/resources/csvFiles/ratings.csv`
