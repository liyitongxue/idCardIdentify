import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.example.utils.ImageConvert;
import org.example.utils.ImageFilterUtil;
import org.example.utils.ImageOpencvUtil;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * @author ly
 * @since 2021/4/29
 */
public class testOCR {
    private final static int targetDifferenceValue = 15;

    public static void main(String[] args) throws Exception {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\04.png";
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
//        imshow("Zoomed Image", zoomedImg);


        BufferedImage bufferedImage = ImageConvert.Mat2BufImg(zoomedImg, ".png");

        BufferedImage paintWhiteImg = ImageFilterUtil.imageRGBDifferenceFilter(bufferedImage, targetDifferenceValue);
        BufferedImage _grayImg = ImageFilterUtil.gray(paintWhiteImg);

        Mat __grayImg = ImageOpencvUtil.gray(ImageConvert.BufImg2Mat(_grayImg));

        Mat _binaryImg = ImageOpencvUtil.ImgBinarization(__grayImg);
//        Mat _binaryImg = ImageOpencvUtil.whiteBlack(ImageConvert.BufImg2Mat(paintWhiteImg));

//        _binaryImg.removeTileObserver(_binaryImg);
//        Mat temp = ImageOpencvUtil.medianBlur(ImageConvert.BufImg2Mat(_binaryImg));
        imshow("Image", _binaryImg);
        BufferedImage img = ImageConvert.Mat2BufImg(_binaryImg, ".png");


        ITesseract instance = new Tesseract();    //创建ITesseract接口的实现实例对象

        //java.lang.ClassLoader.getSystemResource()方法返回一个URL对象读取资源，如果资源不能被找到则返回null。
        URL tessDataUrl = ClassLoader.getSystemResource("tessdata");    //file:/E:/Desktop/OCRTest/Tess4jOcr/Tess4jOcr/Tess4jOcr/target/classes/tessdata
        String path = tessDataUrl.getPath().substring(1);    //url.getPath()-/E:/Desktop/OCRTest/Tess4jOcr/Tess4jOcr/Tess4jOcr/target/classes/tessdata
        instance.setDatapath(path); //path为tessdata文件夹目录位置
        instance.setLanguage("chi_sim");    //中英文混合识别需用 + 分隔，chi_sim：简体中文，eng：英文
        instance.setTessVariable("user_defined_dpi", "300");    //Warning: Invalid resolution 0 dpi. Using 70 instead.
        String result = null;

        try {
            long startTime = System.currentTimeMillis();    //识别之前的时间
            result = instance.doOCR(_grayImg);    //开始识别
            long endTime = System.currentTimeMillis();    //识别结束的时间
            System.out.println("识别用时：" + (endTime - startTime) + "ms");    //识别图片耗时
        } catch (TesseractException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("识别结果如下：");
        System.out.println(result);    //打印识别结果


        waitKey();
    }
}
