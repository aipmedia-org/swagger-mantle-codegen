#import <Overcoat.h>
#import "ApiRequest.h"

#if DEBUG
#define ENABLE_LOGS 1
#define ENABLE_CURL_LOGS 0
#endif

@interface BaseApi : OVCHTTPSessionManager
+ (instancetype)sharedInstance;
+ (void)sendRequest:(ApiRequest*)request;
@end
