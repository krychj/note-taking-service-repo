# Note Taking Service

A Spring Boot REST API for creating, searching, updating, and deleting notes, backed by MongoDB.

## Prerequisites

- Docker Runtime with Docker Compose v2
- Git, to check out the repository
- Ports `8080` (the API) and `27017` (MongoDB) must be free on your machine. If another process is already using either port, stop it first or the containers will fail to start.

## Getting Started

To run the service locally:

1. Check out the `develop` branch from the repository [https://github.com/krychj/note-taking-service-repo.git](https://github.com/krychj/note-taking-service-repo.git)
2. From the service's root directory, run:

   docker-compose up -d

   This will download the required dependencies (including the MongoDB image), build the `note-taking-service` image, and start both containers. The first run may take around 2 minutes while dependencies are downloaded and the image is built.

3. Once running, verify the API is up by opening the interactive Swagger documentation:

   [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

   Swagger UI lets you browse every endpoint and execute live requests directly against the running service: no separate API client (Postman, curl) required to get started.

## Stopping the Service

To stop the running containers:

```bash
docker-compose down
```

This stops and removes the `app` and `mongo` containers, but keeps the MongoDB data volume so your notes persist across restarts.

To also remove the MongoDB data volume and start completely fresh next time:

```bash
docker-compose down -v
```

## Design Choices

The following three design decisions received the most time during development:

### 1. Pagination on  unbounded collection of notes

Endpoints that can return a large or unbounded number of notes (listing all notes, and filtering by date range) are paginated by default rather than returning the full result set. Without this, a collection that grows over time could produce very large responses: increasing memory pressure on the service, slower response times, and unnecessary load on MongoDB for requests that only need a subset of the data.

Pagination is using a configurable default page size of 20 and a maximum page size of 100 (`spring.data.web.pageable.default-page-size`, `spring.data.web.pageable.max-page-size`), so a client cannot request an arbitrarily large page and force the service to load an excessive number of documents at once. Paginated responses expose page metadata (current page, total elements, total pages) so clients can navigate results without needing to guess when they've reached the end.

### 2. Keeping API requests (DTO) separate from stored data (entity), and protecting system-generated fields

The `Note` entity (persisted to MongoDB) and the DTO used at the API boundary are kept intentionally separate, rather than exposing the entity directly through the controller. This was a deliberate choice so that:

- The database schema and the public API contract can evolve independently of each other.
- System-generated fields, `id`, `createdAt`, and `lastUpdatedAt`, cannot be set or overwritten by a client, even if a request body includes them. 

### 3. Centralized, consistent error handling with GlobalExceptionHandler

Every exception raised across the API, for example a note that cannot be found on `GET /{id}` or `PUT /{id}`, is funneled through a single global exception handler rather than handled locally in each controller method. 

## What I'd Change, Add, or Stop Doing With More Time

- **Add support for querying by `lastUpdatedAt` field, in addition to `createdAt`.** This would let clients find recently modified notes, not just recently created ones.
- **Add more unit tests and integration tests**, for example using TestContainers for MongoDB, to exercise the full stack rather than relying on the service layer being mocked.
- **Add tracking of note modification history.** I would consider a pub/sub approach with Apache Kafka, where each change to a note is retained in a topic log and can be replayed if needed. Kafka consumers could subscribe to a specific topic and listen for notes with specific attributes (e.g. via message headers).
- **Improve `GlobalExceptionHandler`** and add additional custom exceptions to cover a wider range of edge cases.
- **Secure the service** with proper authentication and authorization safeguards utilizing JWT.

## Source Control | Git Flow

The public GitHub repository is available at: [https://github.com/krychj/note-taking-service-repo](https://github.com/krychj/note-taking-service-repo)

The repository follows a Git Flow-based process: a `develop` branch plus short-lived feature branches for individual pieces of work.
Changes are committed and pushed to a feature branch first, then a pull request is opened, reviewed, and approved before being merged into `develop`.
This keeps feature branches short-lived and reduces the chances of merge conflicts compared to multiple people working directly against a single long-running branch.
