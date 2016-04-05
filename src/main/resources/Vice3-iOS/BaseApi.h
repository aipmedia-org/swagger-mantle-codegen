// This file was generated automatically. Please don't edit it manually.

#import <Overcoat/Overcoat.h>
#import "ApiRequest.h"

#if DEBUG
#define ENABLE_LOGS 1
#define ENABLE_CURL_LOGS 0
#endif

@interface BaseApi : OVCHTTPSessionManager
+ (BaseApi*)sharedInstance;
+ (void)setAuthToken:(NSString*)token;
+ (void)sendRequest:(ApiRequest*)request;
@end
