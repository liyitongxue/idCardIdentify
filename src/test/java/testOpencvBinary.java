import org.example.utils.ImageConvert;
import org.example.utils.ImageOpencvUtil;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;


/**
 * @author ly
 * @since 2021/4/22
 */
//测试opencv二值化binaryzation()方法——√
public class testOpencvBinary {
    private final static int targetDifferenceValue = 10;

    public static void main(String[] args) throws Exception {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\01.png";
        //处理后的图片保存路径--在原来的图片主名后加上afterProcessed
        String result_image = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "afterProcessed.png";

        BufferedImage originalImage = ImageIO.read(new File(sourceImage));

        Mat image =ImageConvert.BufImg2Mat(originalImage);

        imshow("Original Image", image);

        //opencv灰度化
        Mat grayImage = ImageOpencvUtil.gray(image);
        imshow("Grayed Image", grayImage);

        //opencv二值化
        Mat binaryImage = ImageOpencvUtil.binaryzation(grayImage);
        imshow("Binary Image", binaryImage);

        waitKey();
    }
}
