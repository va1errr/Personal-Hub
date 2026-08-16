# Domain

## InboxItem

### Fields

- `id`
- `user_id`
- `content`
- `status`
- `created_at`

### Statuses

- `ACTIVE`
- `PROCESSED`
- `ARCHIVED`

### Business Rules

- a new item is always `ACTIVE`
- an `ACTIVE` item can be converted into `Task` or `Note`
- processing item must preserve original content
- after processing item status becomes `PROCESSED`
- `created_at` must be automatically set to the current timestamp considering timezone

## Task

### Fields

- `id`
- `user_id`
- `inbox_item_id`
- `title`
- `description`
- `priority`
- `status`
- `created_at`
- `due_at`
- `completed_at`

### Statuses

- `OPEN`
- `COMPLETED`

### Priorities

- `LOW`
- `MEDIUM`
- `HIGH`
- `CRITICAL`

### Business Rules

- a new task has always `OPEN` status
- `title` must not be blank
- `description` can be null
- `created_at` must be automatically set to the current timestamp considering timezone
- `due_at` can be null
- `completed_at` must exists only for completed tasks

## Note

### Fields

- `id`
- `user_id`
- `inbox_item_id`
- `content`
- `created_at`

### Business Rules

- `content` must not be blank
- `created_at` must be automatically set to the current timestamp considering timezone

## NoteTag

### Fields

- `id`
- `user_id`
- `tag`

### Business Rules

- `tag` must not be blank

## User

### Fields

- `id`
- `tg_user_id`
- `tg_username`
- `created_at`

### Business Rules

- `username` must no be blank
- `created_at` must be automatically set to the current timestamp considering timezone

## UserSettings

### Fields

- `user_id`
- `timezone`

### Business Rules

- `timezone` must be in the timezone ID format: "Europe/Moscow"
