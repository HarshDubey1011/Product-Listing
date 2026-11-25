-- KEYS[1] = bucket key
-- ARGV[1] = bucket capacity
-- ARGV[2] = leak rate (requests per second)
-- ARGV[3] = current time in milliseconds
-- ARGV[4] = TTL in seconds

local capacity = tonumber(ARGV[1])
local leak_rate = tonumber(ARGV[2])
local now_ms = tonumber(ARGV[3])
local ttl = tonumber(ARGV[4])

-- Read the existing bucket state.
local level = tonumber(
    redis.call('HGET', KEYS[1], 'level') or '0'
)

local last_updated = tonumber(
    redis.call('HGET', KEYS[1], 'lastUpdated') or tostring(now_ms)
)

-- Step 1: Calculate how much time has passed.
local elapsed = math.max(
    0,
    (now_ms - last_updated) / 1000
)

-- Step 2: Let the bucket leak.
level = math.max(
    0,
    level - (elapsed * leak_rate)
)

-- Step 3: Check whether the new request fits.
local allowed = 0

if level + 1 <= capacity then
    level = level + 1
    allowed = 1
end

-- Step 4: Save the updated state.
redis.call(
    'HSET', KEYS[1],
    'level', level,
    'lastUpdated', now_ms
)

-- Step 5: Expire idle bucket state.
redis.call('EXPIRE', KEYS[1], ttl)

return allowed