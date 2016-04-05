#import <Foundation/Foundation.h>

@interface NSString (Path)
- (NSString*)pathPattern;
- (NSString*)pathWithParams:(NSDictionary*)params;
@end
