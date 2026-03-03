#import <Foundation/Foundation.h>
#import <OpenKuiklyIOSRender/KuiklyRenderModuleExportProtocol.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * iOS 原生侧 Demo 插件
 * 插件名: "demo"
 * 对应 Kuikly 侧的 DemoPlugin
 *
 * 注意: KuiklyxBridgeNative Pod 尚未发布，因此不继承 KKBridgePluginHelper，
 * 而是作为独立的工具类，由 HRBridgeModule 直接调用。
 */
@interface DemoPluginNative : NSObject

- (id _Nullable)showToast:(id _Nullable)params callback:(KuiklyRenderCallback _Nullable)callback;
- (id _Nullable)getDeviceInfo:(id _Nullable)params callback:(KuiklyRenderCallback _Nullable)callback;
- (id _Nullable)getTimestamp:(id _Nullable)params callback:(KuiklyRenderCallback _Nullable)callback;
- (id _Nullable)openUrl:(id _Nullable)params callback:(KuiklyRenderCallback _Nullable)callback;

@end

NS_ASSUME_NONNULL_END