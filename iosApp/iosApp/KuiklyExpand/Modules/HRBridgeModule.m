#import "HRBridgeModule.h"
#import "DemoPluginNative.h"

#import "KuiklyRenderViewController.h"
#import <OpenKuiklyIOSRender/NSObject+KR.h>

#define REQ_PARAM_KEY @"reqParam"
#define CMD_KEY @"cmd"
#define FROM_HIPPY_RENDER @"from_hippy_render"
// 扩展桥接接口
/*
 * @brief Native暴露接口到kotlin侧，提供kotlin侧调用native能力
 */

@implementation HRBridgeModule {
    DemoPluginNative *_demoPlugin;
}

@synthesize hr_rootView;

#pragma mark - KuiklyRenderModuleExportProtocol

/**
 * 拦截所有 Kuikly 侧的 callNative 调用
 * 对于 "pluginName.methodName" 格式的方法名，路由到对应的插件处理
 * 注意: KuiklyxBridgeNative Pod 尚未发布，因此在此手动实现路由
 */
- (id _Nullable)hrv_callWithMethod:(NSString * _Nonnull)method
                            params:(id _Nullable)params
                          callback:(KuiklyRenderCallback _Nullable)callback {
    // 检查是否为 kuiklyx-bridge 插件路由格式（包含点号）
    NSRange dotRange = [method rangeOfString:@"."];
    if (dotRange.location != NSNotFound) {
        NSString *pluginName = [method substringToIndex:dotRange.location];
        NSString *methodName = [method substringFromIndex:dotRange.location + 1];
        
        if ([pluginName isEqualToString:@"demo"]) {
            return [self handleDemoPluginWithMethod:methodName params:params callback:callback];
        }
        // 未知插件
        if (callback) {
            callback(@{@"code": @(-1), @"msg": [NSString stringWithFormat:@"未知插件: %@", pluginName]});
        }
        return nil;
    }
    // 非插件路由，返回 nil 让基类继续处理
    return nil;
}

#pragma mark - Demo 插件路由

- (DemoPluginNative *)demoPlugin {
    if (!_demoPlugin) {
        _demoPlugin = [[DemoPluginNative alloc] init];
    }
    return _demoPlugin;
}

- (id _Nullable)handleDemoPluginWithMethod:(NSString *)method
                                    params:(id _Nullable)params
                                  callback:(KuiklyRenderCallback _Nullable)callback {
    DemoPluginNative *plugin = [self demoPlugin];
    
    if ([method isEqualToString:@"showToast"]) {
        return [plugin showToast:params callback:callback];
    } else if ([method isEqualToString:@"getDeviceInfo"]) {
        return [plugin getDeviceInfo:params callback:callback];
    } else if ([method isEqualToString:@"getTimestamp"]) {
        return [plugin getTimestamp:params callback:callback];
    } else if ([method isEqualToString:@"openUrl"]) {
        return [plugin openUrl:params callback:callback];
    }
    
    // 未知方法
    if (callback) {
        callback(@{@"code": @(-1), @"msg": [NSString stringWithFormat:@"未知方法: demo.%@", method]});
    }
    return nil;
}

#pragma mark - 原有 Bridge 方法

- (void)copyToPasteboard:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    NSString *content = params[@"content"];
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    pasteboard.string = content;
}

- (void)log:(NSDictionary *)args {
    NSDictionary *params = [args[KR_PARAM_KEY] hr_stringToDictionary];
    NSString *content = params[@"content"];
    NSLog(@"KuiklyRender:%@", content);
}

@end