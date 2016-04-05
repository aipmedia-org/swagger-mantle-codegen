// This file was generated automatically. Please don't edit it manually.

#import "NSString+Path.h"

@implementation NSString (Path)

- (NSString*)pathPattern
{
    NSString* pathPattern = self;
    NSRegularExpression* regexp = [NSRegularExpression regularExpressionWithPattern:@"{.*}" options:kNilOptions error:nil];
    NSRange range = NSMakeRange(0, 0);
    while ((range = [regexp firstMatchInString:pathPattern options:NSMatchingReportProgress range:NSMakeRange(0, self.length)].range).length > 0)
        pathPattern = [pathPattern stringByReplacingCharactersInRange:range withString:@"*"];
    return pathPattern;
}

- (NSString*)pathWithParams:(NSDictionary*)params
{
    NSString* path = self;
    for (NSString* key in params)
        path = [path stringByReplacingOccurrencesOfString:[NSString stringWithFormat:@"{%@}", key] withString:[NSString stringWithFormat:@"%@", params[key]]];
    return path;
}

@end
