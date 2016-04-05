#import "ApiRequest.h"
#import "NSString+Path.h"

@implementation ApiRequest
{
    NSMutableDictionary* _pathParams;
    NSMutableDictionary* _queryParams;
}

- (NSMutableDictionary*)pathParams
{
    if (_pathParams == nil)
        _pathParams = [NSMutableDictionary dictionary];
    return _pathParams;
}

- (NSMutableDictionary*)queryParams
{
    if (_queryParams == nil)
        _queryParams = [NSMutableDictionary dictionary];
    return _queryParams;
}

- (void)logURLRequest:(NSURLRequest*)request
{
    __block NSMutableString* displayString = [NSMutableString stringWithFormat:@"curl -X %@", request.HTTPMethod];

    [displayString appendFormat:@" \'%@\'",  request.URL.absoluteString];

    [request.allHTTPHeaderFields enumerateKeysAndObjectsUsingBlock:^(id key, id val, BOOL *stop) {
        [displayString appendFormat:@" -H \'%@: %@\'", key, val];
    }];

    if ([request.HTTPMethod isEqualToString:@"POST"] || [request.HTTPMethod isEqualToString:@"PUT"] || [request.HTTPMethod isEqualToString:@"PATCH"])
    {
        NSString* option = _queryParams.count == 0 ? @"-d" : @"-F";
        [_queryParams enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL *stop) {
            [displayString appendFormat:@" %@ \'%@=%@\'", option, key, obj];
        }];
    }

    NSLog(@"%@", displayString);
}

@end
