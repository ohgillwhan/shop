local result = 'success';

local stock = redis.call('GET', KEYS[1]);

if ( stock == nil or (type(stock) == "boolean" and not stock) or stock < ARGV[1])  then
    result = 'fail';
else
    redis.call('DECRBY', KEYS[1], ARGV[1]);
end

return result;