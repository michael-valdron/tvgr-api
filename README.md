# The Video Game Repository API

Demo Scala Play web backend for making / view records of a 
video game titles and their information.

## Requirements

- Java 1.8+
- Scala 2.13.6+
- sbt 1.5.2+

## Setup

Before running the application or using the test cases, there
is a few things that need to be setup.

### Environment Variables

There are two ways to setup the necessary environment variables

1. Manual Exports - Recommended for production setups
2. `.env` File - Using `sbt` allows for the automatic 
exporting of environment variable declarations in `.env`
thanks to the
[sbt-dotenv](https://github.com/mefellows/sbt-dotenv) plugin.

#### Environment Variable Definitions
|           Name           |           Description                       |
|--------------------------|---------------------------------------------|
| POSTGRES_URL             | The JDBC address to the database deployment. Example: `jdbc:postgresql://localhost:5432/mydb`|
| POSTGRES_USER            | The user for accessing the deployed database. |
| POSTGRES_PASSWORD        | The password for the user defined above.      |
| POSTGRES_THREADS         | Number of threads for database actions. Example: `10` |
| POSTGRES_MAX_CONNECTIONS | Max number of connection to database at one given time. **Note**: This must be at most as much as `POSTGRES_THREADS` or there can be problems. 

### Database

This project uses the 
[PostgreSQL](https://www.postgresql.org/) DBMS as the backend
database system.

For deploying a local instance of PostgreSQL, one can either
install it to their system or using `docker-compose` in
the project folder as such:

```shell
docker-compose up postgres
```

One can start PostgreSQL as a background process by doing
the following:

```shell
docker-compose up -d postgres
```

Clean up can be done using:

```shell
docker-compose down
```

**Note**: Base directory `docker-compose.yml` is used for
local deployments NOT production. If you plan to run this
in a live environment, please perform the proper database
setup for doing for. For instance, setup system environment
variables in the live environment rather than use a `.env`
file.

## Usage
To run the application, use the follow form in the shell:

```shell
sbt run
# or 
docker up -d postgres && sbt run # if database is not deployed yet
```

### Testing

For testing, one can run the testsuites by doing the following:

```shell
sbt test
# or 
docker up -d postgres && sbt test # if database is not deployed yet
```

## Routing
### APIs
| Method |    Route             | Parameter(s) |
|--------|----------------------|--------------|
|  GET   |     `/v1/games`      |              |
|  GET   |  `/v1/game/:entryId` |   `entryId`  |
|  PUT   |  `/v1/add`           |              |
|  POST  |  `/v1/edit/:entryId` |   `entryId`  |
| DELETE | `/v1/remove/:entryId`|   `entryId`  |

### Authentication

| Method |    Route             | Parameter(s) |
|--------|----------------------|--------------|
|  POST  |     `/register`      |              |
|  POST  |   `/login`           |              |

## References

### Authentication

Due to the continuous learning process of learning 
the Play framework the following was used as references
for a good portion of the authentication process:
- Dmitriy Chuprina & Oleg Yermolaiev's [GitHub example](https://github.com/sysgears/auth-with-play-silhouette-example)
- Dmitriy Chuprina's [blog](https://sysgears.com/articles/how-to-create-restful-api-with-scala-play-silhouette-and-slick/) 
on setting up for JWT authentication using Play

### Others
- Play Framework's Slick [samples](https://github.com/playframework/play-slick/tree/master/samples)
- Slick's [samples](https://github.com/slick/slick/tree/main/samples)
