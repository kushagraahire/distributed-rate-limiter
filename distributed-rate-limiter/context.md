# Copilot Context File for Distributed Rate Limiter (GCRA)

project:
  name: Distributed Rate Limiter
  algorithm: GCRA (Generic Cell Rate Algorithm)
  description: >
    A distributed rate limiter designed to control request flow across multiple
    services using Redis as a centralized store and Lua scripts for atomic
    operations. Implements GCRA algorithm to enforce fairness and prevent
    overload.

demo:
  name: API Playground Dashboard
  description: Single-page demo showcasing rate limiting in action
  scenarios:
    - name: Login Brute-Force Protection
      endpoint: POST /api/demo/login
      limit: 5 requests per minute
      use_case: Prevents password guessing attacks
    
    - name: OTP/SMS Abuse Prevention
      endpoint: POST /api/demo/otp
      limit: 3 requests per 5 minutes
      use_case: Prevents SMS bombing and cost abuse
    
    - name: API Rate Limiting
      endpoint: POST /api/demo/chat
      limit: 10 requests per minute
      use_case: Fair usage enforcement for API consumers

  features:
    - Tab-based scenario selection
    - Real-time status visualization (remaining requests)
    - Attack simulation button (burst requests)
    - Request log with timestamps and latency
    - Retry countdown timer
    - Visual progress bar

components:
  - name: Redis Config
    purpose: Centralized data store for TAT (Theoretical Arrival Time)
    details:
      - Holds TAT per client/endpoint
      - Configured for high availability
      - Optimized with TTL for auto-expiring keys

  - name: Lua Script (gcra_tat_update.lua)
    purpose: Atomic rate limiting logic
    details:
      - Executes GCRA algorithm in Redis
      - Ensures race-free updates across distributed nodes
      - Returns allow (1) or reject (wait time in ms)

  - name: GcraRateLimiter Service
    purpose: Core rate limiting logic
    details:
      - Written in Java/Spring Boot
      - Executes Lua script via RedisTemplate
      - Configurable limits and burst allowance

  - name: REST Controller
    purpose: Exposes demo endpoints
    details:
      - /api/demo/login - Login attempt simulation
      - /api/demo/otp - OTP request simulation
      - /api/demo/chat - API call simulation
      - /api/demo/status - Check remaining quota

  - name: Frontend Dashboard
    purpose: Interactive demo UI
    details:
      - Static HTML + JavaScript
      - No framework required
      - Visual feedback for allow/reject decisions

  - name: Docker Compose
    purpose: One-click demo environment
    details:
      - Redis container
      - Spring Boot application
      - Easy setup for recruiters

workflow:
  - User opens dashboard in browser
  - Selects scenario (Login/OTP/API)
  - Clicks "Send Request" or "Simulate Attack"
  - Frontend calls REST endpoint
  - Controller invokes GcraRateLimiter
  - Lua script executes atomically in Redis
  - Response returned with allow/reject + metadata
  - UI updates with visual feedback

tech_stack:
  - Java 17
  - Spring Boot 3.x
  - Redis
  - Lua scripting
  - HTML/CSS/JavaScript
  - Docker & Docker Compose

future_work:
  - name: AI Chat Assistant
    description: LLM-powered assistant to explain rate limiting status
    details:
      - Answers "Why was my request blocked?"
      - Shows usage stats in natural language
      - Integrates with OpenAI/Gemini API
      - Context-aware responses based on user's rate limit state