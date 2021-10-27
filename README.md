# The Video Game Repository API

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
