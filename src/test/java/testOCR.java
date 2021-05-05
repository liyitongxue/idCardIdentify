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
import java.util.ArrayList;
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

        long startTime = System.currentTimeMillis();    //识别之前的时间

        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\01.png";
        //处理后的图片保存路径
        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "after.png";

        //读取图像
        Mat image = imread(sourceImage);
        if (image.empty()) {
            throw new Exception("image is empty");
        }
//        imshow("Original Image", image);

        //倾斜校正
        Mat correctedImg = ImageOpencvUtil.imgCorrection(image);

        //倾斜校正后裁剪
        Mat cuttedImg = ImageOpencvUtil.cutRect(correctedImg);
        //裁剪后缩放标准化
        Mat zoomedImg = ImageOpencvUtil.zoom(cuttedImg);

        imshow("Zoomed Image", zoomedImg);

        Mat img = zoomedImg.clone();
        BufferedImage bufferedImage = ImageConvert.Mat2BufImg(img, ".png");

        //ImageFilterUtil调节亮度
        int brightness = ImageFilterUtil.imageBrightness(bufferedImage);
        System.out.println("brightness = " + brightness);
        BufferedImage brightnessImg = bufferedImage;
        if (brightness > 180) {
            brightnessImg = ImageFilterUtil.imageBrightness(bufferedImage, -60);
        }

        //涂白
//        BufferedImage paintWhiteImg = ImageFilterUtil.imageRGBDifferenceFilter(bufferedImage, targetDifferenceValue);
        //黑白化
//        BufferedImage blackWhiteImage = ImageFilterUtil.replaceWithWhiteColor(paintWhiteImg);

        //ImageFilterUtil灰度化
        BufferedImage grayImage = ImageFilterUtil.gray(brightnessImg);
        //将ImageFilterUtil灰度化后的图片转换为Mat矩阵图像
        Mat matImg = ImageConvert.BufImg2Mat(grayImage);

        //opencv非局部均值去噪（需要三通道的Mat图像）
        Mat denoiseImg = ImageOpencvUtil.pyrMeanShiftFiltering(matImg);
//        grayImg = ImageOpencvUtil.pyrMeanShiftFiltering(grayImg);

        //opencv灰度化--转为单通道
        Mat grayImg = ImageOpencvUtil.gray(denoiseImg);

        imshow("grayImg", grayImg);

        //膨胀与腐蚀后的Mat图像
        Mat dilationImg = ImageOpencvUtil.preprocess(grayImg);
        imshow("dilation", dilationImg);

        //查找和筛选文字区域
        List<RotatedRect> rects = ImageOpencvUtil.findTextRegionRect(dilationImg);
        if (rects.size() != 5)
            System.out.println("身份证信息文本框获取错误！！！");

        //用红线画出找到的轮廓
        for (RotatedRect rotatedRect : rects) {
            Point[] rectPoint = new Point[4];
            rotatedRect.points(rectPoint);
            for (int j = 0; j <= 3; j++) {
                Imgproc.line(img, rectPoint[j], rectPoint[(j + 1) % 4], new Scalar(0, 0, 255), 2);
            }
        }
        //显示带轮廓的图像
        imshow("Contour Image", img);

        //截取并显示轮廓图片
        Mat dst;
        System.out.println("rects.size:" + rects.size());
        List<Mat> lstMat = new ArrayList<>();

        for (int i = 0; i < rects.size(); i++) {
//            dst = new Mat(img, rects.get(i).boundingRect());
            dst = ImageOpencvUtil.cropImage(matImg, rects.get(i).boundingRect());
            lstMat.add(dst);
            imshow("croppedImg" + i, dst);
        }

        List<BufferedImage> lstBufferedImg = new ArrayList<>();

        for (int i = lstMat.size() - 1; i >= 0; i--) {
            BufferedImage tempBufferedImg = ImageConvert.Mat2BufImg(lstMat.get(i), ".png");
            lstBufferedImg.add(tempBufferedImg);
        }

        ITesseract instance = new Tesseract();    //创建ITesseract接口的实现实例对象

        //java.lang.ClassLoader.getSystemResource()方法返回一个URL对象读取资源，如果资源不能被找到则返回null。
        URL tessDataUrl = ClassLoader.getSystemResource("tessdata");    //file:/E:/Desktop/OCRTest/Tess4jOcr/Tess4jOcr/Tess4jOcr/target/classes/tessdata
        String path = tessDataUrl.getPath().substring(1);    //url.getPath()-/E:/Desktop/OCRTest/Tess4jOcr/Tess4jOcr/Tess4jOcr/target/classes/tessdata
        instance.setDatapath(path); //path为tessdata文件夹目录位置
        instance.setLanguage("chi_sim");    //中英文混合识别需用 + 分隔，chi_sim：简体中文，eng：英文
        instance.setTessVariable("user_defined_dpi", "300");    //Warning: Invalid resolution 0 dpi. Using 70 instead.
        String result = null;

        System.out.println("识别结果如下：");
        try {
            String name = "";
            instance.setLanguage("chi_sim");    //中英文混合识别需用 + 分隔，chi_sim：简体中文，eng：英文
            name = instance.doOCR(lstBufferedImg.get(0)).replaceAll("[^\\u4e00-\\u9fa5]", "").trim();
            System.out.println("姓名：" + name);

            String idNumber = "";
            instance.setLanguage("eng");    //中英文混合识别需用 + 分隔，chi_sim：简体中文，eng：英文
            idNumber = instance.doOCR(lstBufferedImg.get(lstBufferedImg.size() - 1)).replaceAll("[^0-9xX]", "");

            char sex;
            if (Integer.parseInt(idNumber.substring(16, 17)) % 2 == 0) {
                sex = '女';
            } else {
                sex = '男';
            }
            System.out.println("性别：" + sex);

            String nation = "";
            instance.setLanguage("chi_sim");
            nation = instance.doOCR(lstBufferedImg.get(1)).trim();
            System.out.println("名族：" + nation);

            int year = Integer.parseInt(idNumber.substring(6, 10));
            int month = Integer.parseInt(idNumber.substring(10, 12));
            int day = Integer.parseInt(idNumber.substring(12, 14));
            System.out.println("出生：" + year + "年" + month + "月" + day + "日");

            String address = "";
            instance.setLanguage("chi_sim");    //中英文混合识别需用 + 分隔，chi_sim：简体中文，eng：英文
            for (int i = 2; i < lstBufferedImg.size() - 1; i++) {
                address += instance.doOCR(lstBufferedImg.get(i)).trim();
                //            replaceAll("[^\\s\\u4e00-\\u9fa5\\-0-9]+", "").replaceAll("\\n", "").trim()
            }
            address = address.replaceAll("[^\\s\\u4e00-\\u9fa5\\-0-9]+", "").replaceAll(" +", "").trim();
//            address = address.replaceAll(" ", "");
            System.out.println("地址：" + address);

//            String test = instance.doOCR(lstBufferedImg.get(1)).trim();
//            System.out.println(test);

//            String idNumber = "";
//            idNumber = instance.doOCR(lstBufferedImg.get(lstBufferedImg.size() - 1)).replaceAll("[^0-9xX]", "");
            System.out.println("身份证号：" + idNumber);

            long endTime = System.currentTimeMillis();    //识别结束的时间
            System.out.println("识别用时：" + (endTime - startTime) + "ms");    //识别图片耗时
        } catch (TesseractException e) {
            System.out.println(e.getMessage());
        }

        waitKey();
    }
}
