-- KEYS[1] = current window counter key
-- KEYS[2] = previous window counter key
--
-- ARGV[1] = request limit
-- ARGV[2] = window duration in seconds
-- ARGV[3] = elapsed seconds in current window
-- ARGV[4] = TTL for a newly created counter

local current_count =
    tonumber(redis.call('GET', KEYS[1]) or '0')

local previous_count =
    tonumber(redis.call('GET', KEYS[2]) or '0')

local limit = tonumber(ARGV[1])
local window_seconds = tonumber(ARGV[2])
local elapsed = tonumber(ARGV[3])
local ttl = tonumber(ARGV[4])

-- The formula we are using is C + P(1-E/W);
-- Estimate requests in the trailing window.
local previous_weight = 1 - (elapsed / window_seconds)

local estimated_count =
    current_count + (previous_count * previous_weight)

-- Count this attempt, including rejected attempts.
local count = redis.call('INCR', KEYS[1])

-- Keep the current counter through the following window,
-- so it can be used as the previous counter.
if count == 1 then
    redis.call('EXPIRE', KEYS[1], ttl)
end

-- Evaluate the estimate from BEFORE this request was counted.
if estimated_count < limit then
    return 1
else
    return 0
end