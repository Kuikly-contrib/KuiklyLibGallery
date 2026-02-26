import SwiftUI
import MMKV

@main
struct iOSApp: App {
	
	init() {
		// 初始化 MMKV
		initializeMMKV()
	}
	
	private func initializeMMKV() {
		// 设置 MMKV 根目录
		let rootDir = MMKV.initialize(rootDir: nil)
		print("MMKV root directory: \(rootDir)")
		
		// 获取默认 MMKV 实例
		guard let mmkv = MMKV.default() else {
			print("Failed to initialize MMKV")
			return
		}
		
		// 写入测试数据
		mmkv.set("abcdef", forKey: "test")
		
		print("MMKV initialized successfully")
	}
	
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}