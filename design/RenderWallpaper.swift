import Foundation
import CoreGraphics
import ImageIO
import UniformTypeIdentifiers

// Original procedural artwork. Static at runtime; no full-screen animation or image fetches.
let width = 1920, height = 1080
var pixels = [UInt8](repeating: 255, count: width * height * 4)
func gaussian(_ x: Float, _ y: Float, _ cx: Float, _ cy: Float, _ sx: Float, _ sy: Float) -> Float {
    let a = (x-cx)/sx, b = (y-cy)/sy
    return expf(-(a*a+b*b)*1.7)
}
func blend(_ rgb: inout SIMD3<Float>, _ color: SIMD3<Float>, _ alpha: Float) {
    let t = min(1, max(0, alpha))
    rgb = rgb * (1-t) + color*t
}
for py in 0..<height {
    let y = Float(py)/Float(height)
    for px in 0..<width {
        let x = Float(px)/Float(width)
        var rgb = SIMD3<Float>(0.018,0.033,0.078)
        blend(&rgb, SIMD3<Float>(0.23,0.16,0.44), gaussian(x,y,0.24,0.30,0.68,0.62)*0.58)
        blend(&rgb, SIMD3<Float>(0.05,0.42,0.48), gaussian(x,y,0.91,0.22,0.65,0.60)*0.62)
        let ridge = 0.71 - 0.58*x + 0.16*sinf(x*4.2 - 0.85)
        let d = y-ridge
        let spectrum = SIMD3<Float>(0.28 + 0.13*(1-x),0.21+0.39*x,0.68+0.08*x)
        blend(&rgb,spectrum,expf(-powf((d+0.040)/0.14,2))*0.56)
        blend(&rgb,SIMD3<Float>(0.25,0.55,0.76),expf(-powf(d/0.016,2))*0.17)
        blend(&rgb,SIMD3<Float>(0.018,0.025,0.075),expf(-powf((d-0.045)/0.045,2))*0.57)
        let second = 0.84 - 0.26*x - 0.20*sinf(x*3.1 + 0.3)
        let d2 = y-second
        blend(&rgb,SIMD3<Float>(0.25,0.22,0.57),expf(-powf((d2+0.025)/0.085,2))*0.30)
        blend(&rgb,SIMD3<Float>(0.07,0.11,0.25),expf(-powf((d2-0.045)/0.05,2))*0.40)
        // Quiet dark regions preserve contrast behind the time and the five app cards.
        blend(&rgb,SIMD3<Float>(0.012,0.021,0.052),gaussian(x,y,0.07,0.04,0.46,0.35)*0.72)
        blend(&rgb,SIMD3<Float>(0.015,0.025,0.055),max(0,(y-0.69)/0.31)*0.76)
        let vignette = max(0,1-powf((x-0.5)*1.2,2)-powf((y-0.5)*0.8,2)*0.5)
        var seed = UInt32(truncatingIfNeeded: px &* 374761393 &+ py &* 668265263)
        seed = (seed ^ (seed >> 13)) &* 1274126177
        let grain = (Float(seed & 255)/255-0.5)*0.0045
        let i = (py*width+px)*4
        for c in 0..<3 { pixels[i+c] = UInt8(max(0,min(255,(rgb[c]*(0.78+0.22*vignette)+grain)*255))) }
    }
}
let space = CGColorSpace(name: CGColorSpace.sRGB)!
let data = Data(pixels) as CFData
let image = CGImage(width: width,height: height,bitsPerComponent: 8,bitsPerPixel: 32,bytesPerRow: width*4,space: space,bitmapInfo: CGBitmapInfo(rawValue: CGImageAlphaInfo.noneSkipLast.rawValue),provider: CGDataProvider(data: data)!,decode: nil,shouldInterpolate: true,intent: .defaultIntent)!
let output = URL(fileURLWithPath: CommandLine.arguments[1])
let dest = CGImageDestinationCreateWithURL(output as CFURL,UTType.jpeg.identifier as CFString,1,nil)!
CGImageDestinationAddImage(dest,image,[kCGImageDestinationLossyCompressionQuality:0.93] as CFDictionary)
if !CGImageDestinationFinalize(dest) { fatalError("Could not save wallpaper") }
print("Rendered \(width)×\(height) wallpaper")
