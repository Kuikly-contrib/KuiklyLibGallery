import { common } from '@kit.AbilityKit';
import { BusinessError } from '@kit.BasicServicesKit';
import { KRAny } from '@kuikly-open/render';
import { KuiklyRenderBaseModule } from '@kuikly-open/render';
import { KuiklyRenderCallback } from '@kuikly-open/render';
import util from '@ohos.util';
import fs from '@ohos.file.fs';
import { fileUri } from '@kit.CoreFileKit';
import { promptAction } from '@kit.ArkUI';
import { deviceInfo } from '@kit.BasicServicesKit';

export class KRBridgeModule extends KuiklyRenderBaseModule {
  static readonly MODULE_NAME = "HRBridgeModule";
  static readonly SHOW_ALERT = "showAlert";
  static readonly CLOSE_PAGE = "closePage";
  static readonly OPEN_PAGE = "openPage";
  static readonly COPY_TO_PASTE_BOARD = "copyToPasteboard";
  static readonly TOAST = "toast";
  static readonly LOG = "log";
  static readonly REPORT_DT = "reportDT";
  static readonly REPORT_REAL_TIME = "reportRealtime";
  static readonly LOCAL_SERVE_TIME = "localServeTime";
  static readonly CURRENT_TIME_STAMP = "currentTimestamp";
  static readonly DATE_FORMATTER = "dateFormatter";
  static readonly READ_ASSET_FILE = "readAssetFile";

  syncMode(): boolean {
    return false;
  }

  call(method: string, params: KRAny, callback: KuiklyRenderCallback): KRAny {
    switch (method) {
      case KRBridgeModule.SHOW_ALERT: {
        this.showAlert(params, callback);
        break;
      }
      case KRBridgeModule.CLOSE_PAGE: {
        this.closePage();
        break;
      }
      case KRBridgeModule.OPEN_PAGE: {
        this.openPage();
        break;
      }
      case KRBridgeModule.COPY_TO_PASTE_BOARD: {
        this.copyToPasteboard();
        break;
      }
      case KRBridgeModule.TOAST: {
        this.toast();
        break;
      }
      case KRBridgeModule.LOG: {
        this.log(params);
        break;
      }
      case KRBridgeModule.REPORT_DT: {
        this.reportDT(params);
        break;
      }
      case KRBridgeModule.REPORT_REAL_TIME: {
        this.reportRealtime(params);
        break;
      }
      case KRBridgeModule.LOCAL_SERVE_TIME: {
        this.localServeTime(params, callback);
        break;
      }
      case KRBridgeModule.CURRENT_TIME_STAMP: {
        return this.currentTimestamp();
      }
      case KRBridgeModule.DATE_FORMATTER: {
        return this.dateFormatter(params);
      }
      case KRBridgeModule.READ_ASSET_FILE: {
        this.readAssetFile(params, callback);
        break;
      }
      default:
        // kuiklyx-bridge 插件路由: 处理 "pluginName.methodName" 格式
        return this.handleBridgePlugin(method, params, callback);
    }
    return null;
  }

  /**
   * 处理 kuiklyx-bridge 插件路由
   * 方法格式: "pluginName.methodName"，如 "demo.showToast"
   */
  private handleBridgePlugin(method: string, params: KRAny, callback: KuiklyRenderCallback): KRAny {
    const dotIndex = method.indexOf('.');
    if (dotIndex < 0) {
      callback?.({ "code": -1, "msg": `方法不存在: ${method}` });
      return null;
    }
    const pluginName = method.substring(0, dotIndex);
    const methodName = method.substring(dotIndex + 1);

    switch (pluginName) {
      case 'demo':
        return this.handleDemoPlugin(methodName, params, callback);
      default:
        callback?.({ "code": -1, "msg": `未知插件: ${pluginName}` });
        return null;
    }
  }

  /**
   * Demo 插件方法分发
   */
  private handleDemoPlugin(method: string, params: KRAny, callback: KuiklyRenderCallback): KRAny {
    switch (method) {
      case 'showToast': {
        const message = (params as string) || 'Hello';
        promptAction.showToast({
          message: message,
          duration: 2000
        });
        callback?.({ "code": 0, "msg": "success" });
        return null;
      }
      case 'getDeviceInfo': {
        const info: Record<string, string> = {
          "brand": deviceInfo.brand || "Unknown",
          "model": deviceInfo.productModel || "Unknown",
          "osVersion": deviceInfo.osFullName || "Unknown",
          "platform": "HarmonyOS"
        };
        callback?.({
          "code": 0,
          "msg": "success",
          "data": info
        });
        return null;
      }
      case 'getTimestamp': {
        return Date.now().toString();
      }
      case 'openUrl': {
        const url = (params as string) || '';
        if (url.length > 0) {
          try {
            const context = getContext(this) as common.UIAbilityContext;
            const want: Record<string, string> = {
              "uri": url,
              "action": "ohos.want.action.viewData"
            };
            // 尝试打开浏览器
            context.startAbility({ uri: url, action: 'ohos.want.action.viewData' });
            callback?.({ "code": 0, "msg": "success" });
          } catch (e) {
            callback?.({ "code": -1, "msg": `打开URL失败: ${(e as Error).message}` });
          }
        } else {
          callback?.({ "code": -1, "msg": "URL为空" });
        }
        return null;
      }
      default:
        callback?.({ "code": -1, "msg": `未知方法: demo.${method}` });
        return null;
    }
  }

  private showAlert(params: KRAny, callback: KuiklyRenderCallback) {
  }

  // TODO close page
  private closePage() {
  }

  // TODO open new page
  private openPage() {
  }

  // TODO copy to paste board
  private copyToPasteboard() {
  }

  // TODO open toast
  private toast() {
  }

  private log(params: KRAny) {
    if (params == null) {
      return
    }
    console.log(`KuiklyRender ${JSON.stringify(params)}`,)
  }

  private reportDT(params: KRAny) {
  }

  private reportRealtime(params: KRAny) {
  }

  private localServeTime(params: KRAny, callback: KuiklyRenderCallback) {
    callback({ "time": Date.now() / 1000.0 });
  }

  private currentTimestamp(): string {
    return Date.now().toString()
  }

  // TODO format date
  private dateFormatter(params: KRAny): string {
    console.log(`========== ${params}`)
    return "";
  }

  private readAssetFile(params: KRAny, callback: KuiklyRenderCallback) {
    // let startTimeMills = Date.now();
    let records = JSON.parse(params as string) as Record<string, string>;
    let assetPath = records["assetPath"];

    let context = getContext(this) as common.UIAbilityContext;
    /*
    context.resourceManager.getRawFileContent(assetPath, (error: BusinessError, value: Uint8Array) => {
      let textDecoder: util.TextDecoder = util.TextDecoder.create();
      let res: string = textDecoder.decodeWithStream(value);
      callback({
        "result": JSON.parse(res)
      });
    });
    */
    let resDir = context.resourceDir
    let path = resDir + '/' + assetPath
    let assetURI = fileUri.getUriFromPath(path)
    const file = fs.openSync(assetURI, fs.OpenMode.READ_ONLY);
    const stat = fs.statSync(file.fd);
    const arrayBuffer = new ArrayBuffer(stat.size);
    fs.read(file.fd, arrayBuffer, (err: BusinessError, readLen: number) => {
      if (err) {
        console.error("read failed with error message: " + err.message + ", error code: " + err.code);
      } else {
        let buf = new Uint8Array(arrayBuffer, 0, readLen);
        const textDecoder = new util.TextDecoder('utf-8');
        const res = textDecoder.decodeWithStream(new Uint8Array(buf));
        callback({
          'result': JSON.parse(res)
        });
      }
      fs.closeSync(file);
    });
  }
}