import org.example.utils.ImageOpencvUtil;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import java.net.URL;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * @author ly
 * @since 2021/4/23
 */
//测试Opencv的图片倾斜校正correction()方法
public class testOpencvCorrection {
    public static void main(String[] args) throws Exception {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\05.png";
        //处理后的图片保存路径
        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "after.png";

        // 读取图像
        Mat image = imread(sourceImage);
        if (image.empty()) {
            throw new Exception("image is empty");
        }
        imshow("Original Image", image);

        //opencv灰度化
        Mat grayImg = ImageOpencvUtil.gray(image);

        //二值化
        Mat binaryImg = ImageOpencvUtil.binaryzation(grayImg);

        //膨胀与腐蚀
        Mat corrodedImg = ImageOpencvUtil.corrosion(binaryImg);

        //文字区域
        List<RotatedRect> rects = ImageOpencvUtil.findTextRegion(corrodedImg);

        //倾斜矫正
        Mat correctedImg = ImageOpencvUtil.correction(rects, image);

        imshow("Corrected Image", correctedImg);
        waitKey();
    }
}
