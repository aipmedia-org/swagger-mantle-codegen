// This file was generated automatically. Please don't edit it manually.

#import <Foundation/Foundation.h>

@class Error;

@interface ApiRequest : NSObject

@property (nonatomic) NSString* method;
@property (nonatomic) NSString* path;
@property (nonatomic) NSMutableDictionary* pathParams;
@property (nonatomic) NSMutableDictionary* queryParams;
@property (nonatomic, copy) void (^successBlock)(id result, NSHTTPURLResponse* response);
@property (nonatomic, copy) void (^errorBlock)(Error* error, NSHTTPURLResponse* response);

- (void)logURLRequest:(NSURLRequest*)request;

@end
