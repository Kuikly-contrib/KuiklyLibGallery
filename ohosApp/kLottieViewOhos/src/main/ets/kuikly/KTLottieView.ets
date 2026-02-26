import { KRAny, KuiklyRenderBaseView, KuiklyRenderCallback } from '@kuikly-open/render';
import lottie, { AnimationItem } from '@ohos/lottie';
import { BMEnterFrameEvent } from '@ohos/lottie/src/main/js/utils/common';
import common from '@ohos.app.ability.common';
import { JSON, util } from '@kit.ArkTS';
import hilog from '@ohos.hilog';
import { ComponentContent } from '@kit.ArkUI';

const PROP_SRC = 'src';
const PROP_IMAGE_PATH = "imagePath";
const PROP_AUTO_PLAY = 'autoPlay';
const PROP_LOOP = 'loop';
const EVENT_ON_ANIMATION_UPDATE = 'onAnimationUpdate'
const EVENT_ON_ANIMATION_REPEAT = 'onAnimationRepeat'
const EVENT_ON_ANIMATION_COMPLETE = 'onAnimationComplete'
const EVENT_ON_ANIMATION_LOADED = 'onAnimationLoaded'
const METHOD_PLAY = "play"
const METHOD_PAUSE = "pause"
const METHOD_RESUME = "resume"
const METHOD_PROGRESS = "progress"

@Observed
export class KTLottieViewImpl extends KuiklyRenderBaseView {
  static readonly VIEW_NAME = 'KTLottieView'
  curAnim?: AnimationItem
  rawFilePath: string = ''
  imagePath?: string = undefined
  autoPlay: boolean = true
  loop: boolean = true
  outOnAnimationUpdate?: KuiklyRenderCallback
  outOnAnimationRepeat?: KuiklyRenderCallback
  outOnAnimationComplete?: KuiklyRenderCallback
  outOnAnimationLoaded?: KuiklyRenderCallback
  outOnAnimationLoadFailed?: KuiklyRenderCallback
  private renderContext?: CanvasRenderingContext2D
  private scheduleId?: number

  onCanvasReady(context: CanvasRenderingContext2D) {
    this.renderContext = context
    this.schedule()
  }

  setProp(propKey: string, propValue: KRAny | KuiklyRenderCallback): boolean {
    hilog.info(0x0000, `LottieView`, `setProp ${propKey} ${propValue}`)
    switch (propKey) {
      case PROP_SRC:
        this.rawFilePath = propValue as string;
        this.schedule()
        return true;
      case PROP_IMAGE_PATH:
        this.imagePath = propValue as string;
        return true;
      case PROP_AUTO_PLAY:
        this.autoPlay = propValue as boolean;
        return true;
      case PROP_LOOP:
        this.loop = propValue as boolean;
        return true;
      case EVENT_ON_ANIMATION_UPDATE:
        this.outOnAnimationUpdate = propValue as KuiklyRenderCallback
        return true;
      case EVENT_ON_ANIMATION_REPEAT:
        this.outOnAnimationRepeat = propValue as KuiklyRenderCallback
        return true;
      case EVENT_ON_ANIMATION_COMPLETE:
        this.outOnAnimationComplete = propValue as KuiklyRenderCallback
        return true;
      case EVENT_ON_ANIMATION_LOADED:
        this.outOnAnimationLoaded = propValue as KuiklyRenderCallback
        return true;
      default:
        return super.setProp(propKey, propValue);
    }
  }

  call(method: string, param: KRAny, callback: KuiklyRenderCallback): void {
    hilog.info(0x0000, `LottieView`, `call ${method} ${param}`)
    switch (method) {
      case METHOD_PLAY:
        this.curAnim?.goToAndPlay(0)
        break
      case METHOD_PAUSE:
        this.curAnim?.pause()
        break
      case METHOD_RESUME:
        this.curAnim?.play()
        break
      case METHOD_PROGRESS:
        const ratio = param as number
        const total = this.curAnim?.totalFrames ?? 0
        const actual = ratio * total
        this.curAnim?.goToAndStop(actual, true)
        break
    }
  }

  schedule() {
    if (!this.renderContext) return
    const lastId = this.scheduleId
    if (lastId) {
      clearTimeout(lastId)
    }
    this.scheduleId = setTimeout(() => {
      this.startAnim()
    }, 0)
  }

  startAnim() {
    hilog.info(0x0000, `LottieView`, `startAnim`)
    this.curAnim?.destroy()
    if (!this.rawFilePath) return
    
    // 通过 UIContext 获取主应用的 context，而不是库模块的 context
    let context = this.getUIContext()?.getHostContext() as common.UIAbilityContext;
    if (!context) {
      hilog.error(0x0000, `LottieView`, `Failed to get host context`)
      this.outOnAnimationLoadFailed && this.outOnAnimationLoadFailed({})
      return
    }
    
    hilog.info(0x0000, `LottieView`, `load rawfile: ${this.rawFilePath}`)
    
    // 使用 resourceManager.getRawFileContent() 读取 rawfile 资源
    context.resourceManager.getRawFileContent(this.rawFilePath).then((data: Uint8Array) => {
      hilog.info(0x0000, `LottieView`, `rawfile loaded, size: ${data.byteLength}`)
      let decoder = util.TextDecoder.create('utf-8', {ignoreBOM: true});
      const jsonStr = decoder.decodeWithStream(data);
      const tmpAnim = lottie.loadAnimation({
        container: this.renderContext,
        renderer: 'canvas',
        loop: this.loop,
        autoplay: this.autoPlay,
        imagePath: this.imagePath,
        animationData: JSON.parse(jsonStr),
      })
      this.curAnim = tmpAnim
      tmpAnim.addEventListener<BMEnterFrameEvent>("enterFrame", (e) => {
        const fraction = e.currentTime / e.totalTime
        hilog.info(0x0000, `LottieView`, `enterFrame fraction=${fraction}`)
        this.outOnAnimationUpdate && this.outOnAnimationUpdate({
          'value': fraction
        })
      })
      tmpAnim.addEventListener<object>("loopComplete", (e) => {
        hilog.info(0x0000, `LottieView`, `loopComplete`)
        this.outOnAnimationRepeat && this.outOnAnimationRepeat({})
      })
      tmpAnim.addEventListener<object>("complete", (e) => {
        hilog.info(0x0000, `LottieView`, `complete`)
        this.outOnAnimationUpdate && this.outOnAnimationUpdate({
          'value': 1
        })
        this.outOnAnimationComplete && this.outOnAnimationComplete({})
      })
      tmpAnim.addEventListener<object>("DOMLoaded", (e) => {
        hilog.info(0x0000, `LottieView`, `DOMLoaded`)
        this.outOnAnimationLoaded && this.outOnAnimationLoaded({})
      })
    }).catch((err: Error) => {
      hilog.error(0x0000, `LottieView`, `rawfile load failed: ${err.message}`)
      this.outOnAnimationLoadFailed && this.outOnAnimationLoadFailed({})
    })
  }


  createArkUIView(): ComponentContent<KuiklyRenderBaseView> {
    const uiContext = this.getUIContext() as UIContext
    return new ComponentContent<KuiklyRenderBaseView>(uiContext, wrapBuilder<[KuiklyRenderBaseView]>(createMyView), this)
  }
}



@Component
export struct KTLottieView {
  @ObjectLink renderView: KTLottieViewImpl
  private renderContext: CanvasRenderingContext2D = new CanvasRenderingContext2D(new RenderingContextSettings(true))

  build() {
    Canvas(this.renderContext)
      .onReady(() => {
        hilog.info(0x0000, `LottieView`, `onReady`)
        //抗锯齿的设置
        this.renderContext.imageSmoothingEnabled = true;
        this.renderContext.imageSmoothingQuality = 'medium'
        this.renderView.onCanvasReady(this.renderContext)
      })
      // .applyRenderViewBaseAttr(this.renderView)
  }
}

@Builder
function createMyView(view: KuiklyRenderBaseView) {
  KTLottieView({ renderView: view as KTLottieViewImpl })
}

// 应用基础属性
// TODO: 用attributeModifier替换
// @Extend(Canvas)
// function applyRenderViewBaseAttr(renderView: KuiklyRenderBaseView) {
//   .backgroundColor(renderView.cssBackgroundColor)
//   .linearGradient(renderView.cssLinearGradient)
//   .position({ x: renderView.cssPositionX, y: renderView.cssPositionY })
//   .size({ width: renderView.cssWidth, height: renderView.cssHeight })
//   .opacity(renderView.cssOpacity)
//   .clip(renderView.cssOverflow || renderView.cssBorderRadius !== null)
//   .visibility((renderView as KuiklyRenderBaseView).cssVisibility) // preview编译有坑，得as 一下
//   .touchable(renderView.cssTouchEnable) // 后面适配最新api，暂时用旧的
//   .zIndex(renderView.cssZIndex)
//   .border(renderView.cssBorder)
//   .rotate(renderView.cssRotate)
//   .scale(renderView.cssScale)
//   .translate(renderView.cssTranslate)
//   .onClick(renderView.eventClick)
// } // end of applyRenderViewBaseAttr()