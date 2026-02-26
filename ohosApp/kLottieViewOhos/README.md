# kLottieViewOhos

## 简介
kLottieViewOhos is a Ohos native component of kuiklyLottieView

### 集成kLottieViewOhos
1. 你可以通过ohpm将kuikly添加到你的项目中：

  `ohpm install @walkman707/kLottieViewOhos`

2. 或者你可以手动添加到你的项目中：

在应用程序模块的 `oh-package.json5` 中添加kuikly依赖
```json5
  "dependencies": {

    "@walkman707/kLottieViewOhos": 'latest'

  }
```
并执行
`ohpm install`

### 应用程序模块注册
在应用程序的 KuiklyViewDelegate的实现类，注册 kLottieViewOhos Native实现
```ts
...
export class KuiklyViewDelegate extends IKuiklyViewDelegate {
  getCustomRenderViewCreatorRegisterMap(): Map<string, KRRenderViewExportCreator> {
    const map: Map<string, KRRenderViewExportCreator> = new Map();
    map.set(KRMyView.VIEW_NAME, () => new KRMyView());
    map.set(KTLottieViewImpl.VIEW_NAME, () => new KTLottieViewImpl)
    return map;
  }
}
...
```
