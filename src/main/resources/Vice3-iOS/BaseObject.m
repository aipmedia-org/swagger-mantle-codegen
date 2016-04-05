#import "BaseObject.h"

@implementation BaseObject

- (NSDictionary*)toDictionary
{
    return [MTLJSONAdapter JSONDictionaryFromModel:self error:nil];
}

- (NSString*)description
{
    return [[self toDictionary] description];
}

#pragma mark -
#pragma mark *** MTLJSONSerializing ***
#pragma mark -

+ (NSDictionary*)JSONKeyPathsByPropertyKey
{
    return nil;
}

@end
