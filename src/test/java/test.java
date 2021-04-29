import org.example.utils.ImageConvert;
import org.example.utils.ImageFilterUtil;
import org.example.utils.ImageOpencvUtil;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;

/**
 * @author ly
 * @since 2021/4/22
 */

/**
 * 灰度化
 * 二值化
 * 膨胀腐蚀
 * 倾斜校正--摆正
 * <p>
 * 图片裁剪身份证
 * <p>
 * 标准化--统一大小
 * <p>
 * 根据位置裁剪识别
 */

public class test {
    private final static int targetDifferenceValue = 15;

    public static void main(String[] args) throws Exception {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\10.png";
        //处理后的图片保存路径
        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "after.png";

        //读取图像
        Mat image = imread(sourceImage);
        if (image.empty()) {
            throw new Exception("image is empty");
        }
        imshow("Original Image", image);

        //opencv灰度化
        Mat grayImage = ImageOpencvUtil.gray(image);

        //二值化
        Mat binaryImg = ImageOpencvUtil.binaryzation(grayImage);

        //膨胀与腐蚀
        Mat corrodedImg = ImageOpencvUtil.corrosion(binaryImg);

        //文字区域
        List<RotatedRect> rects = ImageOpencvUtil.findTextRegion(corrodedImg);

        //倾斜矫正
        Mat correctedImg = ImageOpencvUtil.correction(rects, image);

        //倾斜校正后裁剪
        Mat cuttedImg = ImageOpencvUtil.cutRect(correctedImg);

        //裁剪后缩放标准化
        Mat zoomedImg = ImageOpencvUtil.zoom(cuttedImg);
        imshow("Zoomed Image", zoomedImg);



        BufferedImage bufferedImage = ImageConvert.Mat2BufImg(zoomedImg, ".png");

        BufferedImage paintWhiteImg = ImageFilterUtil.imageRGBDifferenceFilter(bufferedImage, targetDifferenceValue);
        BufferedImage _grayImg=ImageFilterUtil.gray(paintWhiteImg);
        Mat _binaryImg = ImageOpencvUtil.binaryzation(ImageConvert.BufImg2Mat(_grayImg));


//        Mat _grayImg = ImageOpencvUtil.gray(zoomedImg);

//        Mat _binaryImg = ImageOpencvUtil.binaryzation(_grayImg);

//        Mat _corrodedImg = ImageOpencvUtil.corrosion(_binaryImg);


//        List<Mat> lst = ImageOpencvUtil.cut(_binaryImg);
//        for (int i = 0; i < lst.size(); i++) {
//            String win_name = "roi" + i;
//            imshow(win_name, lst.get(i));
//        }

//        _binaryImg.removeTileObserver(_binaryImg);
//        Mat temp = ImageOpencvUtil.medianBlur(ImageConvert.BufImg2Mat(_binaryImg));
        imshow("Image", _binaryImg);
        waitKey();
    }
}
