# API Contract - FindIt@TLU

## Base URL
- Development: `http://10.0.2.2:8000/api/` (Android Emulator)
- Development: `http://192.168.x.x:8000/api/` (Physical Device)
- Production: `https://findit-tlu.com/api/`

## Authentication
Tất cả các request (trừ login/register) cần có header:
```
Authorization: Bearer {token}
```

## Endpoints

### 1. Authentication

#### Login
```
POST /auth/login
Content-Type: application/x-www-form-urlencoded

Body:
- email: string (required, email TLU)
- password: string (required)

Response 200:
{
    "token": "string",
    "user": {
        "id": 1,
        "name": "string",
        "email": "string",
        "phone_number": "string",
        "role_id": 1,
        "is_active": true
    }
}
```

#### Register
```
POST /auth/register
Content-Type: application/x-www-form-urlencoded

Body:
- name: string (required)
- email: string (required, email TLU)
- password: string (required, min:8)
- password_confirmation: string (required, same as password)
- phone_number: string (required)

Response 201:
{
    "token": "string",
    "user": {...}
}
```

#### Logout
```
POST /auth/logout
Authorization: Bearer {token}

Response 200:
{
    "message": "Logged out successfully"
}
```

### 2. User Profile

#### Get Profile
```
GET /user/profile
Authorization: Bearer {token}

Response 200:
{
    "id": 1,
    "name": "string",
    "email": "string",
    "phone_number": "string",
    "photo_url": "string|null",
    "role": {
        "id": 1,
        "name": "User"
    }
}
```

#### Update Profile
```
PUT /user/profile
Authorization: Bearer {token}
Content-Type: application/json

Body:
{
    "name": "string",
    "phone_number": "string"
}

Response 200:
{
    "user": {...}
}
```

### 3. Posts/Items

#### Get All Posts (Public)
```
GET /items?page=1&per_page=20&item_type=lost&category_id=1&search=keyword
Authorization: Bearer {token}

Query Parameters:
- page: int (default: 1)
- per_page: int (default: 20)
- item_type: string (lost/found)
- category_id: int
- search: string

Response 200:
{
    "data": [
        {
            "id": 1,
            "user_id": 1,
            "category_id": 1,
            "title": "string",
            "description": "string",
            "location_type": "exact|area",
            "location_description": "string",
            "item_type": "lost|found",
            "status": "searching|holding|found|completed",
            "date_lost_or_found": "2025-01-01",
            "expiration_date": "2025-01-15",
            "is_contact_info_public": true,
            "created_at": "2025-01-01T00:00:00Z",
            "user": {
                "id": 1,
                "name": "string",
                "phone_number": "string|null"
            },
            "category": {
                "id": 1,
                "name": "string"
            },
            "images": [
                {
                    "id": 1,
                    "image_url": "string",
                    "caption": "string"
                }
            ]
        }
    ],
    "meta": {
        "current_page": 1,
        "last_page": 5,
        "per_page": 20,
        "total": 100
    }
}
```

#### Get My Posts
```
GET /user/items?page=1&per_page=20&status=searching
Authorization: Bearer {token}

Query Parameters:
- page: int
- per_page: int
- status: string (searching|holding|found|completed)

Response 200: Same format as Get All Posts
```

#### Create Post
```
POST /items
Authorization: Bearer {token}
Content-Type: application/json

Body:
{
    "category_id": 1,
    "title": "string",
    "description": "string",
    "location_type": "exact",
    "location_description": "string",
    "item_type": "lost",
    "date_lost_or_found": "2025-01-01",
    "is_contact_info_public": true
}

Response 201:
{
    "post": {...}
}
```

#### Update Post
```
PUT /items/{id}
Authorization: Bearer {token}
Content-Type: application/json

Body: Same as Create Post

Response 200:
{
    "post": {...}
}
```

#### Delete Post
```
DELETE /items/{id}
Authorization: Bearer {token}

Response 204: No Content
```

#### Mark as Completed
```
PUT /items/{id}/complete
Authorization: Bearer {token}

Response 200:
{
    "post": {...}
}
```

### 4. Categories

#### Get All Categories
```
GET /categories

Response 200:
[
    {
        "id": 1,
        "name": "Điện tử",
        "description": "string"
    }
]
```

### 5. Images

#### Upload Item Image
```
POST /items/{id}/images
Authorization: Bearer {token}
Content-Type: multipart/form-data

Body:
- image: file (required, max:5MB, types:jpg,png)
- caption: string

Response 201:
{
    "id": 1,
    "item_id": 1,
    "image_url": "string",
    "caption": "string"
}
```

### 6. Notifications

#### Get Notifications
```
GET /notifications?page=1&per_page=20
Authorization: Bearer {token}

Response 200:
{
    "data": [
        {
            "id": "uuid",
            "type": "string",
            "data": {
                "message": "string",
                "item_id": 1
            },
            "read_at": null,
            "created_at": "2025-01-01T00:00:00Z",
            "item": {...}
        }
    ],
    "meta": {...}
}
```

#### Mark as Read
```
PUT /notifications/{id}/read
Authorization: Bearer {token}

Response 200:
{
    "message": "Marked as read"
}
```

## Error Responses

### 401 Unauthorized
```json
{
    "message": "Unauthenticated"
}
```

### 422 Validation Error
```json
{
    "message": "The given data was invalid",
    "errors": {
        "email": ["Email is required"],
        "password": ["Password must be at least 8 characters"]
    }
}
```

### 404 Not Found
```json
{
    "message": "Resource not found"
}
```

### 500 Server Error
```json
{
    "message": "Server error"
}
``` 