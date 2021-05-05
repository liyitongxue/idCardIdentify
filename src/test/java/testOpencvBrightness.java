import org.example.utils.ImageOpencvUtil;
import org.opencv.core.Mat;

import java.net.URL;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * @author ly
 * @since 2021/5/3
 */
public class testOpencvBrightness {
    public static void main(String[] args) throws Exception {
        //加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\03.png";
        //处理后的图片保存路径--在原来的图片主名后加上afterBrightnessProcessed
        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "afterBrightnessProcessed.png";

        //读取图像
        Mat image = imread(sourceImage);
        if (image.empty()) {
            throw new Exception("image is empty");
        }
        //展示原图
        imshow("Original Image", image);

        //调用ImageOpencvUtil的亮度调节imageBrightness方法
        Mat brightnessImg = ImageOpencvUtil.imageBrightness(image);

        //展示灰度化后处理后图像
        imshow("Processed Image", brightnessImg);

        //保存到字符串processedImage对应位置
//        imwrite(processedImage, grayImage);
        waitKey();
    }
}
