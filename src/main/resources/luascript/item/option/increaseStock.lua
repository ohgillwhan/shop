local result = 'success';

local stock = redis.call('GET', KEYS[1]);

if ( stock == nil or (type(stock) == "boolean" and not stock) or stock < ARGV[1])  then
    redis.call('SET', KEYS[1], ARGV[1]);
else
    redis.call('INCRBY', KEYS[1], ARGV[1]);
end

return result;