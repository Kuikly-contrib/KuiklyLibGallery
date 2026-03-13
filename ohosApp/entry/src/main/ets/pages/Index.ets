import { ErrorReason, KRRecord, Kuikly, KuiklyRenderBaseView } from '@kuikly-open/render';
import router from '@ohos.router';
import { KuiklyViewDelegate } from '../kuikly/KuiklyViewDelegate';
import globalNativeManager from '../kuikly/MyNativeManager';

@Entry
@Component
struct Index {
  kuiklyViewDelegate = new KuiklyViewDelegate();
  pageName?: string;
  pageData?: KRRecord;
  @State exception?: string = "";

  aboutToAppear() {
    AppStorage.setOrCreate<UIContext>("context", this.getUIContext());

    const params = router.getParams() as Record<string, Object>;
    this.pageName = params?.pageName as string;
    this.pageData = params?.pageData as KRRecord;

    // 将鸿蒙应用沙盒的 databaseDir 注入到 pageData.param 中，
    this.injectDatabaseDir();
    console.log('[Index] pageData after inject = ' + JSON.stringify(this.pageData));
  }

  /**
   * 将鸿蒙沙盒 databaseDir 注入 pageData
   * 在 kuikly 页面中可以获取数据库存储路径用于初始化数据库
   */
  private injectDatabaseDir(): void {
    const ctx = getContext(this);
    if (!ctx || !ctx.databaseDir) {
      return;
    }
    if (!this.pageData) {
      this.pageData = JSON.parse('{}') as KRRecord;
    }
    const pd = this.pageData as Record<string, Object>;
    pd['databaseDir'] = ctx.databaseDir;
  }

  build() {
    Stack() {
      Kuikly({
        pagerName: this.pageName ?? 'router',
        pagerData: this.pageData ?? {},
        delegate: this.kuiklyViewDelegate,
        nativeManager: globalNativeManager,
        onRenderException: (exception: Error, reason: ErrorReason) => {
          this.exception = `${exception.name}:\n${exception.message}`;
        },
      })
      if (this.exception) {
        Text(this.exception)
      }
    }
    .width('100%')
    .height('100%')
    .expandSafeArea([SafeAreaType.KEYBOARD])
  }

  onPageShow(): void {
    this.kuiklyViewDelegate.pageDidAppear();
  }

  onPageHide(): void {
    this.kuiklyViewDelegate.pageDidDisappear();
  }
}