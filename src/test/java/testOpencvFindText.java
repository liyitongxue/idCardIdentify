import org.example.utils.ImageConvert;
import org.example.utils.ImageFilterUtil;
import org.example.utils.ImageOpencvUtil;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * @author ly
 * @since 2021/4/30
 */
public class testOpencvFindText {
    private final static int targetDifferenceValue = 10;

    public static void main(String[] args) throws Exception {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\02.png";
        //处理后的图片保存路径
        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "after.png";

        //读取图像
        Mat image = imread(sourceImage);
        if (image.empty()) {
            throw new Exception("image is empty");
        }
//        imshow("Original Image", image);

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


        Mat img = zoomedImg.clone();
        BufferedImage bufferedImage = ImageConvert.Mat2BufImg(img, ".png");
        //涂白
        BufferedImage paintWhiteImg = ImageFilterUtil.imageRGBDifferenceFilter(bufferedImage, targetDifferenceValue);
        //黑白化
        BufferedImage blackWhiteImage = ImageFilterUtil.replaceWithWhiteColor(paintWhiteImg);
        //灰度化
        BufferedImage _grayImg = ImageFilterUtil.gray(blackWhiteImage);

        Mat temp = ImageConvert.BufImg2Mat(_grayImg);
        //opencv灰度化
        Mat __grayImg = new Mat();
        __grayImg = ImageOpencvUtil.pyrMeanShiftFiltering(temp);
        imshow("__grayImg", __grayImg);
        __grayImg = ImageOpencvUtil.gray((__grayImg));

        //2.二值化
        Mat binaryImage = new Mat();
//        binaryImage = ImageOpencvUtil.binaryzation(__grayImg);
//        binaryImage = ImageOpencvUtil.ImgBinarization(__grayImg);//有用

        //1.Sobel算子，x方向求梯度
        Mat sobel = new Mat();
        Imgproc.Sobel(__grayImg, sobel, 0, 1, 0, 3);
        Imgproc.threshold(sobel, binaryImage, 0, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);


        imshow("binaryImage", binaryImage);

        //3.膨胀和腐蚀操作核设定
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 9));
        //控制高度设置可以控制上下行的膨胀程度，例如3比4的区分能力更强,但也会造成漏检
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(26, 9));

        //4.膨胀一次，让轮廓突出
        Mat dilate1 = new Mat();
//        Imgproc.dilate(binaryImage, dilate1, element2);
        Imgproc.dilate(binaryImage, dilate1, element2, new Point(-1, -1), 1, 1, new Scalar(1));

        //5.腐蚀一次，去掉细节，表格线等。这里去掉的是竖直的线
        Mat erode1 = new Mat();
        Imgproc.erode(dilate1, erode1, element1);

        //6.再次膨胀，让轮廓明显一些
        Mat dilate2 = new Mat();
        Imgproc.dilate(erode1, dilate2, element2);


        Mat dilation = dilate2;
        imshow("膨胀", dilate2);
        //3.查找和筛选文字区域
        List<RotatedRect> rects1 = ImageOpencvUtil.findTextRegion1(dilation);

        //4.用线画出这些找到的轮廓
        for (RotatedRect rotatedRect : rects1) {
            Point[] rectPoint = new Point[4];
            rotatedRect.points(rectPoint);
            for (int j = 0; j <= 3; j++) {
                Imgproc.line(img, rectPoint[j], rectPoint[(j + 1) % 4], new Scalar(0, 0, 255), 2);
            }
        }

        //5.显示带轮廓的图像
        imshow("img", img);

        waitKey();

    }
}
