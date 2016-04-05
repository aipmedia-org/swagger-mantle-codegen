#import "BaseApi.h"
#import "NSString+Path.h"

@interface OVCHTTPSessionManager (Private)
- (NSURLSessionDataTask *)_dataTaskWithHTTPMethod:(NSString *)method
                                        URLString:(NSString *)URLString
                                       parameters:(id)parameters
                                       completion:(void (^)(OVCResponse *, NSError *))completion;
@end

@implementation BaseApi

+ (instancetype)sharedInstance
{
    static id sharedInstance = nil;
    static dispatch_once_t once = 0;
    dispatch_once(&once, ^{ sharedInstance = [[self alloc] initWithBaseURL:[NSURL URLWithString:BASE_URL]]; });
    return sharedInstance;
}

+ (NSDictionary*)modelClassesByResourcePath
{
    static id modelClassesByResourcePath = nil;
    static dispatch_once_t once = 0;
    dispatch_once(&once, ^{ modelClassesByResourcePath = [NSMutableDictionary dictionary]; });
    return modelClassesByResourcePath;
}

+ (void)sendRequest:(ApiRequest*)request
{
    NSURLSessionDataTask* task = [[self sharedInstance] _dataTaskWithHTTPMethod:request.method URLString:[request.path pathWithParams:request.pathParams] parameters:request.queryParams completion:^(OVCResponse* response, NSError* error) {
#if ENABLE_LOGS
        NSLog(@"%@: %@ [%ld]", error != nil ? @"NAK" : @"ACK", task.originalRequest.URL.absoluteString, response.HTTPResponse.statusCode);
#endif
        if (error != nil)
            request.errorBlock(error);
        else
            request.successBlock(response.result);
    }];
#if ENABLE_LOGS
    NSLog(@"REQ: %@", task.originalRequest.URL.absoluteString);
#endif
#if ENABLE_CURL_LOGS
    [request logURLRequest:task.originalRequest];
#endif
    [task resume];
}

@end
