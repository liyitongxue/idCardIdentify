import org.example.utils.ImageOpencvUtil;
import org.opencv.core.Mat;

import java.net.URL;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * @author ly
 * @since 2021/4/28
 */
public class binaryzation {
    public static void main(String[] args) throws Exception {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\01.jpg";
        //处理后的图片保存路径--在原来的图片主名后加上after
        String gray_result_image = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "after1.png";

        // 读取图像
        Mat image = imread(sourceImage);
        if (image.empty()) {
            throw new Exception("image is empty");
        }
        imshow("Original Image", image);

        Mat grayImage=ImageOpencvUtil.gray(image);

        Mat binaryImg = ImageOpencvUtil.binaryzation(grayImage);
        imshow("binary Image", binaryImg);
        waitKey();
    }
}
