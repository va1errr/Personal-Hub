# Personal Hub

## Goal

A personal Telegram-based system for quick capturing and organizing information.

## Core principles

- Telegram is the primary UI
- Business logic does not depend on Telegram

## V1 features

- Quick capture
- Inbox processing
- Ordinary tasks and priorities
- Ordinary notes and tags
- Daily briefing
- Settings
- Health check

## Out of scope for V1

- Links
- Places
- Reminders
- Statistics
- China
- AI

## Core User Scenarios

### Capture information

**Given** a user sends a text message to the bot  
**When** the message is not a reserved command  
**Then** the message is saved as a new `InboxItem`  
**And** the bot confirms saving

### Process `InboxItem` as `Task`

**Given** there is an unprocessed record  
**When** a user selects 'convert to `Task`'  
**And** a user selects a priority  
**Then** a new `Task` is created  
**And** an appropriate `InboxItem` changes its status to `PROCESSED`

### Process `InboxItem` as `Note`

**Given** there is an unprocessed record  
**When** a user selects 'convert to `Note`'  
**Then** a new `Note` is created  
**And** an appropriate `InboxItem` changes its status to `PROCESSED`

### Get daily briefing

**Given** a user want to see today's information  
**When** a user asks for that information  
**Then** the bot sends today's tasks + extra information

### Change settings

**Given** a user wants to change settings  
**When** they go to the settings  
**And** they set timezone  
**Then** timezone is changed in the entire system  
**And** all time-sensitive features now depend on that timezone
