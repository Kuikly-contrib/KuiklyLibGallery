#import "DemoPluginNative.h"
#import <UIKit/UIKit.h>
#import <sys/utsname.h>

/**
 * iOS 原生侧 Demo 插件（独立实现，不依赖 KuiklyxBridgeNative）
 * 插件名: "demo"
 * 方法路由:
 *   demo.showToast → showToast:callback:
 *   demo.getDeviceInfo → getDeviceInfo:callback:
 *   demo.getTimestamp → getTimestamp:callback:
 *   demo.openUrl → openUrl:callback:
 */
@implementation DemoPluginNative

#pragma mark - showToast

- (id _Nullable)showToast:(id _Nullable)params callback:(KuiklyRenderCallback _Nullable)callback {
    NSString *message = @"Hello";
    if ([params isKindOfClass:[NSString class]]) {
        message = (NSString *)params;
    } else if ([params isKindOfClass:[NSDictionary class]]) {
        message = [(NSDictionary *)params objectForKey:@"single_param"] ?: @"Hello";
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        UIViewController *topVC = [self topViewController];
        if (topVC) {
            UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil
                                                                          message:message
                                                                   preferredStyle:UIAlertControllerStyleAlert];
            [topVC presentViewController:alert animated:YES completion:nil];
            // 1.5 秒后自动关闭
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [alert dismissViewControllerAnimated:YES completion:nil];
            });
        }
    });
    
    if (callback) {
        callback(@{@"code": @(0), @"msg": @"success"});
    }
    return nil;
}

#pragma mark - getDeviceInfo

- (id _Nullable)getDeviceInfo:(id _Nullable)params callback:(KuiklyRenderCallback _Nullable)callback {
    struct utsname systemInfo;
    uname(&systemInfo);
    NSString *deviceModel = [NSString stringWithCString:systemInfo.machine encoding:NSUTF8StringEncoding];
    
    NSDictionary *deviceInfo = @{
        @"brand": @"Apple",
        @"model": deviceModel ?: @"Unknown",
        @"systemVersion": [[UIDevice currentDevice] systemVersion] ?: @"Unknown",
        @"systemName": [[UIDevice currentDevice] systemName] ?: @"Unknown",
        @"platform": @"iOS"
    };
    
    if (callback) {
        callback(@{@"code": @(0), @"msg": @"success", @"data": deviceInfo});
    }
    return nil;
}

#pragma mark - getTimestamp

- (id _Nullable)getTimestamp:(id _Nullable)params callback:(KuiklyRenderCallback _Nullable)callback {
    NSTimeInterval timestamp = [[NSDate date] timeIntervalSince1970] * 1000;
    return [NSString stringWithFormat:@"%.0f", timestamp];
}

#pragma mark - openUrl

- (id _Nullable)openUrl:(id _Nullable)params callback:(KuiklyRenderCallback _Nullable)callback {
    NSString *urlString = @"";
    if ([params isKindOfClass:[NSString class]]) {
        urlString = (NSString *)params;
    } else if ([params isKindOfClass:[NSDictionary class]]) {
        urlString = [(NSDictionary *)params objectForKey:@"single_param"] ?: @"";
    }
    
    if (urlString.length == 0) {
        if (callback) {
            callback(@{@"code": @(-1), @"msg": @"URL为空"});
        }
        return nil;
    }
    
    NSURL *url = [NSURL URLWithString:urlString];
    if (url && [[UIApplication sharedApplication] canOpenURL:url]) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIApplication sharedApplication] openURL:url options:@{} completionHandler:^(BOOL success) {
                if (callback) {
                    if (success) {
                        callback(@{@"code": @(0), @"msg": @"success"});
                    } else {
                        callback(@{@"code": @(-1), @"msg": @"打开URL失败"});
                    }
                }
            }];
        });
    } else {
        if (callback) {
            callback(@{@"code": @(-1), @"msg": @"无效的URL"});
        }
    }
    return nil;
}

#pragma mark - Helper

- (UIViewController *)topViewController {
    UIWindowScene *scene = nil;
    for (UIScene *s in [UIApplication sharedApplication].connectedScenes) {
        if ([s isKindOfClass:[UIWindowScene class]] && s.activationState == UISceneActivationStateForegroundActive) {
            scene = (UIWindowScene *)s;
            break;
        }
    }
    UIWindow *keyWindow = scene.windows.firstObject;
    UIViewController *vc = keyWindow.rootViewController;
    while (vc.presentedViewController) {
        vc = vc.presentedViewController;
    }
    if ([vc isKindOfClass:[UINavigationController class]]) {
        vc = ((UINavigationController *)vc).topViewController;
    }
    return vc;
}

@end