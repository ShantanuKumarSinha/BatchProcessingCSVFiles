# Application Running Steps

## Prerequisites
- Java 21
- PostgreSQL running locally
- Gradle wrapper (`./gradlew`) from this repository

## 1) Configure Database (Default Profile: `postgres`)
This project uses `postgres` profile by default (`spring.profiles.active=postgres`).

Update PostgreSQL connection values if needed in:
`src/main/resources/application-postgres.yml`

Example values:
- URL: `jdbc:postgresql://localhost:5432/your_database`
- Username: `your_username`
- Password: `your_password`

## 2) Build the Project
From repository root:

```bash
./gradlew clean build
```

## 3) Run the Application

```bash
./gradlew bootRun
```

## 4) Run Specific Batch Jobs
The application accepts `--job` argument:

- Run all startup jobs (default):
```bash
./gradlew bootRun --args='--job=all'
```

- Run only user import:
```bash
./gradlew bootRun --args='--job=user'
```

- Run only product import:
```bash
./gradlew bootRun --args='--job=product'
```

## 5) Scheduled Movie Rating Import (Along with User and Product Imports)
`importMovieRatingJob` is scheduled and runs based on the cron in:
`src/main/java/com/shann/springbatch/ScheduledJobRunner.java`

Current cron expression:
- `0 20 16 * * *`

## Notes
- CSV files are read from `src/main/resources/csvFiles/`.
- Spring Batch tables are auto-initialized (`spring.batch.jdbc.initialize-schema=always`).
