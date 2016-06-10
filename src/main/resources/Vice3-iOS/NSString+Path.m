// This file was generated automatically. Please don't edit it manually.

#import "NSString+Path.h"

@implementation NSString (Path)

- (NSString*)pathPattern
{
    NSString* pathPattern = self;
    NSRegularExpression* regexp = [NSRegularExpression regularExpressionWithPattern:@"\\{.*\\}" options:kNilOptions error:nil];
    return [regexp stringByReplacingMatchesInString:pathPattern options:0 range:NSMakeRange(0, [pathPattern length]) withTemplate:@"*"];
}

- (NSString*)pathWithParams:(NSDictionary*)params
{
    NSString* path = self;
    for (NSString* key in params)
        path = [path stringByReplacingOccurrencesOfString:[NSString stringWithFormat:@"{%@}", key] withString:[NSString stringWithFormat:@"%@", params[key]]];
    return path;
}

@end
