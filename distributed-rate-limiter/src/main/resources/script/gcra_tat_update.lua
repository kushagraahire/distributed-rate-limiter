-- KEYS[1]: The key used to store the TAT (e.g., 'rate_limit:user:123')
-- ARGV[1]: The current time in milliseconds (now)
-- ARGV[2]: The calculated request interval in milliseconds (P = window / limit)
-- ARGV[3]: The calculated burst allowance in milliseconds (Burst = burstRequests * P)

local key = KEYS[1]
local now = tonumber(ARGV[1])
local interval = tonumber(ARGV[2])
local burst = tonumber(ARGV[3])

local tat = tonumber(redis.call('GET', key))
if tat == nil then
    tat = 0
end

local earliest_time = tat - burst

if now >= earliest_time then
    local new_tat = math.max(tat, now) + interval
    local ttl_ms = new_tat - now + burst
    redis.call('SET', key, new_tat, 'PX', ttl_ms)
    return 1
else
    return earliest_time - now
end