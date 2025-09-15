# Test Tak for Krainet Company

## Start
1. Clone the git repository
```git
git clone https://github.com/Cueerno/UnmatchedTracker.git
```

2. Set up environment <br>
Find the file .env.example and rename it to .env and fill it with your data (database password, etc.)
.env.example -> .env
```dotenv
# PostgreSQL
POSTGRES_DB={your_database_name}
POSTGRES_USER={your_database_username}
POSTGRES_PASSWORD={your_database_password}

# RabbitMQ  default credentials - guest guest
RABBIT_USER={your_rabbit_username}
RABBIT_PASS={your_rabbir_password}
```

3. Set up Spring <br>
Find the file application-docker.properties.example in two microservices and rename it to application-docker.properties
application-docker.properties.example -> application-docker.properties

4. Set up Docker Compose
```docker
docker-compose up --build
```

5. Go to your browser and follow these two addresses:
```
http://localhost:15672/#/ - rabbit gui
http://localhost:8025/#   - mailhog gui
```

## API Endpoints

### AuthController (`/api/v1/auth`)

| Method | URL                     | Description                        | Access |
|--------|--------------------------|------------------------------------|--------|
| POST   | `/api/v1/auth/register` | Register a new user                | Public |
| POST   | `/api/v1/auth/login`    | Authenticate and get JWT token     | Public |

---

### UserController (`/api/v1/users`)

| Method | URL                   | Description                             | Access                 |
|--------|------------------------|-----------------------------------------|------------------------|
| GET    | `/api/v1/users/me`    | Get the current authenticated user info | Authenticated (USER/ADMIN) |
| PATCH  | `/api/v1/users/me`    | Update own profile                      | Authenticated (USER/ADMIN) |
| DELETE | `/api/v1/users/me`    | Delete own account                      | Authenticated (USER/ADMIN) |

---

### AdminController (`/api/v1/admin/users`)

| Method | URL                          | Description                        | Access |
|--------|-------------------------------|------------------------------------|--------|
| GET    | `/api/v1/admin/users/{id}`   | Get user by id                     | ADMIN  |
| PATCH  | `/api/v1/admin/users/{id}`   | Update user by id                  | ADMIN  |
| DELETE | `/api/v1/admin/users/{id}`   | Delete user by id                  | ADMIN  |

