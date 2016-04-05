#import <Mantle.h>

@interface BaseObject : MTLModel<MTLJSONSerializing>
- (NSDictionary*)toDictionary;
@end
