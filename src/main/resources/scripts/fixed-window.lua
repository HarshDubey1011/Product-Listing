
-- KEYS[1] = counter key for one IP and one window
-- ARGV[1] = request limit
-- ARGV[2] = seconds remaining in this window

local count = redis.call('INCR', KEYS[1])

-- Set expiry only when the counter is first created.
if count == 1 then
    redis.call('EXPIRE', KEYS[1], tonumber(ARGV[2]))
end

local limit = tonumber(ARGV[1])

if count <= limit then
    return 1
else
    return 0
end
