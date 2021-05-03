package org.example.utils;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;

/**
 * @author ly
 * @since 2021/4/21
 */
public class ImageOpencvUtil {
    private static final int BLACK = 0;
    private static final int WHITE = 255;

    private static final Size STANDARDSIZE = new Size(673, 425);

    // 私有化构造函数
    private ImageOpencvUtil() {
    }

    /**
     * 作用：均值滤波
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat blur(Mat src) {
        Mat dst = src.clone();
        Imgproc.blur(src, dst, new Size(9, 9), new Point(-1, -1), Core.BORDER_DEFAULT);
        return dst;
    }

    /**
     * 作用：高斯滤波
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat gaussianBlur(Mat src) {
        Mat dst = src.clone();
        Imgproc.GaussianBlur(src, dst, new Size(9, 9), 0, 0, Core.BORDER_DEFAULT);
        return dst;
    }

    /**
     * 作用：中值滤波
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat medianBlur(Mat src) {
        Mat dst = src.clone();
        Imgproc.medianBlur(src, dst, 7);
        return dst;
    }

    /**
     * 作用：非局部均值去噪
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat pyrMeanShiftFiltering(Mat src) {
        Mat dst = src.clone();
        Imgproc.pyrMeanShiftFiltering(src, dst, 10, 50);
        return dst;
    }

    /**
     * 伽马校正
     * 伽马校正对图像的修正作用就是通过增强低灰度或高灰度的细节实现的
     * 值越小，对图像低灰度部分的扩展作用就越强，值越大，对图像高灰度部分的扩展作用就越强，
     * 通过不同的值，就可以达到增强低灰度或高灰度部分细节的作用。
     * 在对图像进行伽马变换时，如果输入的图像矩阵是CV_8U,在进行幂运算时，大于255的值会自动截断为255；
     * 所以，先将图像的灰度值归一化到【0,1】范围，然后再进行幂运算
     * @param src
     */
    public static Mat imageBrightness(Mat src) {

        //定义2个与输入图像大小类型一致的空对象
        Mat dst = new Mat(src.size(),src.type());
        Mat dst_1 = new Mat(src.size(),src.type());
        /*
         * 缩放并转换到另外一种数据类型：
         * dst：目的矩阵；
         * type：需要的输出矩阵类型，或者更明确的，是输出矩阵的深度，如果是负值（常用-1）则输出矩阵和输入矩阵类型相同；
         * scale:比例因子（输入矩阵参数*比例因子）；
         * shift：将输入数组元素按比例缩放后添加的值（第三个参数处理后+第四个参数）；
         * CV_64F:64 -表示双精度 32-表示单精度 F - 浮点  Cx - 通道数,例如RGB就是三通道
         */
        src.convertTo(dst, CvType.CV_64F, 1.0 / 255, 0);

        /*  将每个数组元素提升为幂：
         *  对于非整数幂指数，将使用输入数组元素的绝对值。 但是，可以使用一些额外的操作获得负值的真实值。
         *  对于某些幂值（例如整数值0.5和-0.5），使用了专用的更快算法。
         *  不处理特殊值（NaN，Inf）。
         *  @param 输入数组。
         *  @param 幂的幂指数。
         *  @param 输出数组，其大小和类型与输入数组相同。
         */
        Core.pow(dst, 0.7, dst_1);
        /* 缩放并转换到另外一种数据类型：
         * CV_8UC1---8位无符号的单通道---灰度图片
         * CV_8UC3---8位无符号的三通道---RGB彩色图像
         * CV_8UC4---8位无符号的四通道---带透明色的RGB图像
         */
        dst_1.convertTo(dst_1, CvType.CV_8U,255,0);

        return dst_1;
    }

    /**
     * 作用：灰度化
     *
     * @param src 需灰度化处理的Mat矩阵图像
     * @return
     */
    public static Mat gray(Mat src) {
        Mat grayImage = new Mat();
        try {
            grayImage = new Mat(src.height(), src.width(), CvType.CV_8UC1);
            Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY);
        } catch (Exception e) {
            grayImage = src.clone();
            grayImage.convertTo(grayImage, CvType.CV_8UC1);
            System.out.println("The Image File Is Not The RGB File!已处理...");
        }
        return grayImage;
    }

    /**
     * 作用：二值化
     *
     * @param grayImage 需二值化处理的灰度化后的Mat矩阵图像
     * @return
     */
    public static Mat ImgBinarization(Mat grayImage) {
        Mat threshImage = new Mat(grayImage.height(), grayImage.width(), CvType.CV_8UC1);
//        Imgproc.threshold(grayImage, threshImage, 100, 255, Imgproc.THRESH_BINARY);//效果不好
//        Imgproc.threshold(grayImage, threshImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
//        Imgproc.threshold(grayImage, threshImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_TRIANGLE);//白黑不行
        Imgproc.threshold(grayImage, threshImage, 127, 255, Imgproc.THRESH_TRUNC);//还行
//        Imgproc.threshold(grayImage, threshImage, 127, 255, Imgproc.THRESH_TOZERO);//不行
        return threshImage;
    }

    /**
     * 作用：自适应选取阀值
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static int getAdapThreshold(Mat src) {
        int threshold = 0, thresholdNew = 127;
        int nWhiteCount, nBlackCount;
        int nWhiteSum, nBlackSum;
        int value, i, j;
        int width = src.cols();
        int height = src.rows();

        while (threshold != thresholdNew) {
            nWhiteSum = nBlackSum = 0;
            nWhiteCount = nBlackCount = 0;
            for (j = 0; j < height; j++) {
                for (i = 0; i < width; i++) {
                    value = (int) src.get(j, i)[0];
                    if (value > thresholdNew) {
                        nWhiteCount++;
                        nWhiteSum += value;
                    } else {
                        nBlackCount++;
                        nBlackSum += value;
                    }
                }
            }
            threshold = thresholdNew;
            thresholdNew = (nWhiteSum / nWhiteCount + nBlackSum / nBlackCount) / 2;
        }
        return threshold;
    }

    /**
     * 作用：翻转图像像素
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat turnPixel(Mat src) {
        if (src.channels() != 1) {
            throw new RuntimeException("不是单通道图，需要先灰度化！！！");
        }
        int j, i, value;
        int width = src.cols();
        int height = src.rows();
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                value = (int) src.get(j, i)[0];
                if (value == 0) {
                    src.put(j, i, WHITE);
                } else {
                    src.put(j, i, BLACK);
                }
            }
        }
        return src;
    }

    /**
     * 图像二值化 阀值自适应确定
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat binaryzation(Mat src) {
        Mat dst = src.clone();
        if (dst.channels() != 1) {
            throw new RuntimeException("不是单通道图，需要先灰度化！！！");
        }
        int nWhiteSum = 0, nBlackSum = 0;
        int i, j;
        int width = dst.cols();
        int height = dst.rows();
        int value;

        int threshold = getAdapThreshold(dst);

        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                value = (int) dst.get(j, i)[0];
                if (value > threshold) {
                    dst.put(j, i, WHITE);
                    nWhiteSum++;
                } else {
                    dst.put(j, i, BLACK);
                    nBlackSum++;
                }
            }
        }
        if (true) {
            // 白底黑字
            if (nBlackSum > nWhiteSum) {
                dst = turnPixel(dst);
            }
        } else {
            // 黑底白字
            if (nWhiteSum > nBlackSum) {
                dst = turnPixel(dst);
            }
        }
        return dst;
    }

    public static Mat whiteBlack(Mat src) {
        int width = src.cols();
        int height = src.rows();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < height; j++) {
                if ((int) src.get(j, i)[0] != BLACK) {
                    src.put(j, i, WHITE);
                }
            }
        }
        return src;
    }

    /**
     * 根据二值化图片进行膨胀与腐蚀
     *
     * @param binaryImage 需膨胀腐蚀处理的二值化后的Mat矩阵图像
     * @return
     */
    public static Mat corrosion(Mat binaryImage) {
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
        Mat dilate1 = new Mat();
//        Imgproc.dilate(binaryImage, dilate1, element2);
        Imgproc.dilate(binaryImage, dilate1, element2, new Point(-1, -1), 1, 1, new Scalar(1));

        Mat erode1 = new Mat();
        Imgproc.erode(dilate1, erode1, element1);
        Mat dilate2 = new Mat();
        Imgproc.dilate(erode1, dilate2, element2);
        return dilate2;
    }

    /**
     * 作用：获取文字区域
     *
     * @param img 膨胀与腐蚀后的Mat矩阵图像
     * @return
     */
    public static List<RotatedRect> findTextRegion(Mat img) {
        List<RotatedRect> rects = new ArrayList<RotatedRect>();
        //1.查找轮廓
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(img, contours, hierarchy, RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, new Point(-1, -1));//11.png不能被倾斜校正
//        Imgproc.findContours(img, contours, hierarchy, Imgproc.RETR_CCOMP, CHAIN_APPROX_SIMPLE, new Point(-1, -1));//11.png可以被倾斜校正
//        Imgproc.findContours(img, contours, hierarchy, RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, new Point(0, 0));//11.png不能被倾斜校正

        int img_width = img.width();
        int img_height = img.height();
        int size = contours.size();

        //2.筛选那些面积小的
        for (int i = 0; i < size; i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area < 400)//原来是1000
                continue;
            //轮廓近似，作用较小，approxPolyDP函数有待研究
            double epsilon = 0.001 * Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true);
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approxCurve, epsilon, true);

            //找到最小矩形，该矩形可能有方向
            RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
            //计算高和宽
            int m_width = rect.boundingRect().width;
            int m_height = rect.boundingRect().height;

            //筛选那些太细的矩形，留下扁的
            if (m_width < m_height)
                continue;
            if (img_width == rect.boundingRect().br().x)
                continue;
            if (img_height == rect.boundingRect().br().y)
                continue;
            //符合条件的rect添加到rects集合中
            rects.add(rect);
        }
        return rects;
    }


    /**
     * 倾斜矫正
     *
     * @param rects    文字区域
     * @param srcImage 原Mat矩阵图像
     * @return
     */
    public static Mat correction(List<RotatedRect> rects, Mat srcImage) {
        double degree = 0;
        double degreeCount = 0;
        for (int i = 0; i < rects.size(); i++) {
            if (rects.get(i).angle >= -90 && rects.get(i).angle < -45) {
                degree = rects.get(i).angle;
                if (rects.get(i).angle != 0) {
                    degree += 90;
                }
            }
            if (rects.get(i).angle > -45 && rects.get(i).angle <= 0) {
                degree = rects.get(i).angle;
            }
            if (rects.get(i).angle <= 90 && rects.get(i).angle > 45) {
                degree = rects.get(i).angle;
                if (rects.get(i).angle != 0) {
                    degree -= 90;
                }
            }
            if (rects.get(i).angle < 45 && rects.get(i).angle >= 0) {
                degree = rects.get(i).angle;
            }
            if (degree > -5 && degree < 5) {
                degreeCount += degree;
            }

        }
        if (degreeCount != 0) {
            // 获取平均水平度数
            degree = degreeCount / rects.size();
        }
        Point center = new Point(srcImage.cols() / 2, srcImage.rows() / 2);
        Mat rotm = Imgproc.getRotationMatrix2D(center, degree, 1.0);    //获取仿射变换矩阵
        Mat dst = new Mat();
        Imgproc.warpAffine(srcImage, dst, rotm, srcImage.size(), Imgproc.INTER_LINEAR, 0, new Scalar(255, 255, 255));    // 进行图像旋转操作
        return dst;
    }

    public static Mat imgCorrection(Mat src) {
        //灰度化
        Mat grayImage = gray(src);
        //二值化
        Mat binaryImage = binaryzation(grayImage);
        //膨胀与腐蚀
        Mat preprocess = corrosion(binaryImage);
        //查找和筛选文字区域
        List<RotatedRect> rects = findTextRegion(preprocess);
        //校正
        Mat correctedImage = correction(rects, src);
        return correctedImage;
    }

    /**
     * canny算法，边缘检测
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat canny(Mat src) {
        Mat dst = src.clone();
        src = gray(src);
//        src = binaryzation(grayImage);
//        src = ImgBinarization(src);
        //Canny边缘检测
        Imgproc.Canny(src, dst, 20, 60, 3, false);
        //膨胀，连接边缘
        Imgproc.dilate(dst, dst, new Mat(), new Point(-1, -1), 1, 1, new Scalar(0.5));
        return dst;
    }

    /**
     * 返回边缘检测之后的最大矩形轮廓,并返回
     *
     * @param cannyMat Canny之后的mat矩阵
     * @return
     */
    public static RotatedRect findMaxRect(Mat cannyMat) {
        //边缘检测
        cannyMat = canny(cannyMat);
//        Imgproc.dilate(cannyMat, cannyMat, new Mat(), new Point(-1, -1), 1, 1, new Scalar(0.5));
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        //轮廓提取
        Imgproc.findContours(cannyMat, contours, hierarchy, RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, new Point(0, 0));
//        Imgproc.findContours(cannyMat, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

        // 找出匹配到的最大轮廓
        double area = Imgproc.boundingRect(contours.get(0)).area();
        int index = 0;

        // 找出匹配到的最大轮廓
        for (int i = 0; i < contours.size(); i++) {
            double tempArea = Imgproc.boundingRect(contours.get(i)).area();
            if (tempArea > area) {
                area = tempArea;
                index = i;
            }
        }

        MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contours.get(index).toArray());

        RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);

        return rect;
    }

    /**
     * 作用：把矫正后的图像切割出来
     *
     * @param correctMat 图像矫正后的Mat矩阵
     */
    public static Mat cutRect(Mat correctMat) {
        // 获取最大矩形
        RotatedRect rect = findMaxRect(correctMat);

        Point[] rectPoint = new Point[4];
        rect.points(rectPoint);

        int startLeft = (int) Math.abs(rectPoint[0].x);
        int startUp = (int) Math.abs(Math.abs(rectPoint[0].y) < Math.abs(rectPoint[1].y) ? rectPoint[0].y : rectPoint[1].y);
        int width = (int) Math.abs(rectPoint[2].x - rectPoint[0].x);
//        int height = (int) Math.abs(rectPoint[1].y - rectPoint[0].y);
        int height = (int) Math.abs(rectPoint[3].y - rectPoint[1].y);

        System.out.println("startLeft = " + startLeft);
        System.out.println("startUp = " + startUp);
        System.out.println("width = " + width);
        System.out.println("height = " + height);

        //检测的高度过低，则说明拍照时身份证边框没拍全，直接返回correctMat，如检测的不是身份证则不需要这个if()判断
        //怎么判断如果一个矩形最大边没有被完全检测，即检测的不是一个闭合的矩形，但是仍应该保留这个矩形
//        if (height < 0.3 * width)
//            return correctMat;

        for (Point p : rectPoint) {
            System.out.println(p.x + " , " + p.y);
        }

        if (startLeft + width > correctMat.width()) {
            width = correctMat.width() - startLeft;
        }
        if (startUp + height > correctMat.height()) {
            height = correctMat.height() - startUp;
        }

        Mat temp = new Mat(correctMat, new Rect(startLeft, startUp, width, height));
//        try {
//            temp = new Mat(correctMat, new Rect(startLeft, startUp, width, height));
//        } catch (Exception e) {
//            System.out.println(e);
//        }

        return temp;
    }


    //test
    public static Mat getMax(Mat img) {
        // MeanShift滤波，降噪（速度太慢！）
        //Imgproc.pyrMeanShiftFiltering(img, img, 30, 10);

        // 彩色转灰度
        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);

        // 高斯滤波，降噪
        Imgproc.GaussianBlur(img, img, new Size(3, 3), 2, 2);

        // Canny边缘检测
        Imgproc.Canny(img, img, 20, 60, 3, false);

        // 膨胀，连接边缘
        Imgproc.dilate(img, img, new Mat(), new Point(-1, -1), 3, 1, new Scalar(1));

        //轮廓提取
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(img, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

        // 找出轮廓对应凸包的四边形拟合
        List<MatOfPoint> squares = new ArrayList<>();
        List<MatOfPoint> hulls = new ArrayList<>();
        MatOfInt hull = new MatOfInt();
        MatOfPoint2f approx = new MatOfPoint2f();
        approx.convertTo(approx, CvType.CV_32F);

        for (MatOfPoint contour : contours) {
            // 边框的凸包
            Imgproc.convexHull(contour, hull);

            // 用凸包计算出新的轮廓点
            Point[] contourPoints = contour.toArray();
            int[] indices = hull.toArray();
            List<Point> newPoints = new ArrayList<>();
            for (int index : indices) {
                newPoints.add(contourPoints[index]);
            }
            MatOfPoint2f contourHull = new MatOfPoint2f();
            contourHull.fromList(newPoints);

            // 多边形拟合凸包边框(此时的拟合的精度较低)
            Imgproc.approxPolyDP(contourHull, approx, Imgproc.arcLength(contourHull, true) * 0.02, true);

            // 筛选出面积大于某一阈值的，且四边形的各个角度都接近直角的凸四边形
            MatOfPoint approxf1 = new MatOfPoint();
            approx.convertTo(approxf1, CvType.CV_32S);
            if (approx.rows() == 4 && Math.abs(Imgproc.contourArea(approx)) > 40000 &&
                    Imgproc.isContourConvex(approxf1)) {
                double maxCosine = 0;
                for (int j = 2; j < 5; j++) {
                    double cosine = Math.abs(getAngle(approxf1.toArray()[j % 4], approxf1.toArray()[j - 2], approxf1.toArray()[j - 1]));
                    maxCosine = Math.max(maxCosine, cosine);
                }
                // 角度大概72度
                if (maxCosine < 0.3) {
                    MatOfPoint tmp = new MatOfPoint();
                    contourHull.convertTo(tmp, CvType.CV_32S);
                    squares.add(approxf1);
                    hulls.add(tmp);
                }
            }
        }
        return hierarchy;
    }

    // 根据三个点计算中间那个点的夹角   pt1 pt0 pt2
    private static double getAngle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    // 找到最大的正方形轮廓
    private static int findLargestSquare(List<MatOfPoint> squares) {
        if (squares.size() == 0)
            return -1;
        int max_width = 0;
        int max_height = 0;
        int max_square_idx = 0;
        int currentIndex = 0;
        for (MatOfPoint square : squares) {
            Rect rectangle = Imgproc.boundingRect(square);
            if (rectangle.width >= max_width && rectangle.height >= max_height) {
                max_width = rectangle.width;
                max_height = rectangle.height;
                max_square_idx = currentIndex;
            }
            currentIndex++;
        }
        return max_square_idx;
    }


    /**
     * 根据二值化图片进行膨胀与腐蚀
     *
     * @param img 需膨胀腐蚀处理的灰度化后的Mat矩阵图像
     * @return
     */
    public static Mat preprocess(Mat img) {
        //1.Sobel算子，x方向求梯度
        Mat sobel = new Mat();
        Imgproc.Sobel(img, sobel, 0, 1, 0, 3);

        //2.二值化
        Mat binaryImage = new Mat();
        Imgproc.threshold(sobel, binaryImage, 0, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);

        //3.膨胀和腐蚀操作核设定
        Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 9));
        //控制高度设置可以控制上下行的膨胀程度，例如3比4的区分能力更强,但也会造成漏检
        Mat element2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(30, 12));

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

        return dilate2;
    }

    /**
     * 作用：获取文字区域
     *
     * @param img 膨胀与腐蚀后的Mat矩阵图像
     * @return
     */
    public static List<RotatedRect> findTextRegion1(Mat img) {
        List<RotatedRect> rects = new ArrayList<RotatedRect>();
        //1.查找轮廓
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
//        Imgproc.findContours(img, contours, hierarchy, RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, new Point(-1, -1));//11.png不能被倾斜校正
        Imgproc.findContours(img, contours, hierarchy, Imgproc.RETR_CCOMP, CHAIN_APPROX_SIMPLE, new Point(-1, -1));//11.png可以被倾斜校正
//        Imgproc.findContours(img, contours, hierarchy, RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, new Point(0, 0));//11.png不能被倾斜校正

        int img_width = img.width();
        int img_height = img.height();
        int size = contours.size();

//        RotatedRect idRect, nameRect = null, sexRect = null;
//        //身份证宽度
//        int idWidth = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(0).toArray())).boundingRect().width;
//        int index = 0;
//
//        for (int i = 0; i < size; i++) {
//            double area = Imgproc.contourArea(contours.get(i));
//            if (area < 1000)//原来是1000
//                continue;
//
//            //找到身份证号码矩形框
//            RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
//            int tempIdWidth = rect.boundingRect().width;
//            if (tempIdWidth > idWidth) {
//                idWidth = tempIdWidth;
//                index = i;
//            }
//        }
//        idRect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(index).toArray()));
//
//        rects.add(idRect);
//
//        //8个区域：姓名，性别，名族，出生年份，出生月份，出生日，住址，身份证号码
//        nameRect = new RotatedRect(new Point(idRect.center.x - 245, idRect.center.y - 290), new Size(110, 60), 0);
//        sexRect = new RotatedRect(new Point(idRect.center.x - 275, idRect.center.y - 238), new Size(45, 40), 0);
//        rects.add(nameRect);
//        rects.add(sexRect);

//        for (int i = 0; i < size; i++) {
//
//        }


        //2.筛选那些面积小的
        for (int i = 0; i < size; i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area < 1000)//原来是1000
                continue;

            //轮廓近似，作用较小，approxPolyDP函数有待研究
            double epsilon = 0.001 * Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true);
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approxCurve, epsilon, true);

            //找到最小矩形，该矩形可能有方向
            RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
            //计算高和宽
            int m_width = rect.boundingRect().width;
            int m_height = rect.boundingRect().height;

            System.out.println(m_width);

//            if (m_width < 80)
//                continue;
//            if (m_width < m_height * 2)
//                continue;
            if (80 < rect.center.y && rect.center.y < 200)
                continue;

            //筛选那些太细的矩形，留下扁的/**/
            if (m_width * 1.2 < m_height)
                continue;
            if (img_width == rect.boundingRect().br().x)
                continue;
            if (img_height == rect.boundingRect().br().y)
                continue;

            //符合条件的rect添加到rects集合中
            rects.add(rect);
        }

        System.out.println("中心坐标x：");
        for (int i = 0; i < rects.size(); i++) {
            System.out.println(rects.get(i).center.x);
        }
        System.out.println("中心坐标y：");
        for (int i = 0; i < rects.size(); i++) {

            System.out.println(rects.get(i).center.y);
        }

        return rects;
    }

    /**
     * 旋转矩形
     *
     * @param cannyMat Canny之后的mat矩阵
     * @param rect     矩形
     * @return
     */
    public static Mat rotation(Mat cannyMat, RotatedRect rect) {
        // 获取矩形的四个顶点
        Point[] rectPoint = new Point[4];
        rect.points(rectPoint);

        double angle = rect.angle + 90;

        Point center = rect.center;

        Mat correctMat = new Mat(cannyMat.size(), cannyMat.type());

        cannyMat.copyTo(correctMat);

        // 得到旋转矩阵算子
        Mat matrix = Imgproc.getRotationMatrix2D(center, angle, 0.8);

        Imgproc.warpAffine(correctMat, correctMat, matrix, correctMat.size(), 1, 0, new Scalar(0, 0, 0));

        return correctMat;
    }


    /**
     * 作用：缩放图片
     *
     * @param src 需要缩放的Mat矩阵图像
     * @return
     */
    public static Mat zoom(Mat src) {
        Mat dst = new Mat();
        //区域插值(INTER_AREA):图像放大时类似于线性插值，图像缩小时可以避免波纹出现
        Imgproc.resize(src, dst, STANDARDSIZE, 0, 0, Imgproc.INTER_AREA);
        return dst;
    }

    /**
     * 统计图像每行/每列黑色像素点的个数
     * (n1,n2)=>(height,width),b=true;统计每行
     * (n1,n2)=>(width,height),b=false;统计每列
     *
     * @param src Mat矩阵对象
     * @param n1
     * @param n2
     * @param b   true表示统计每行;false表示统计每列
     * @return
     */
    public static int[] countPixel(Mat src, int n1, int n2, boolean b) {
        int[] numPixel = new int[n1];
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                if (b) {
                    if ((int) src.get(i, j)[0] == BLACK) {
                        numPixel[i]++;
                    }
                } else {
                    if ((int) src.get(j, i)[0] == BLACK) {
                        numPixel[i]++;
                    }
                }
            }
        }
        return numPixel;
    }

    /**
     * 压缩像素值数量；即统计zipLine行像素值的数量为一行
     *
     * @param num
     * @param zipLine
     */
    public static int[] zipLinePixel(int[] num, int zipLine) {
        int len = num.length / zipLine;
        int[] result = new int[len];
        int sum;
        for (int i = 0, j = 0; i < num.length && i + zipLine < num.length; i += zipLine) {
            sum = 0;
            for (int k = 0; k < zipLine; k++) {
                sum += num[i + k];
            }
            result[j++] = sum;
        }
        return result;
    }


    private org.opencv.core.Mat projectionVerticality(org.opencv.core.Mat mat) {
        org.opencv.core.Mat projectionMat = mat.clone();//曲线救国，获取同样一个mat
        projectionMat.setTo(new org.opencv.core.Scalar(255));//然后再把颜色换成白色
        Double[] dotList = new Double[mat.cols()];//创建一个list用于存储每一列的黑点数量
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println(mat.dump());
        int col = mat.cols();
        int row = mat.rows();
        for (int x = 0; x < col; x++) {
            dotList[x] = 0.0;
            for (int y = 0; y < row; y++) {
                double binData = mat.get(y, x)[0];
                if (binData == 0) {//黑色

                    dotList[x]++;
                }
            }
        }
        //然后生成投影图
        for (int x = 0; x < mat.cols(); x++) {
            for (int y = 0; y < mat.rows(); y++) {
                if (x == 147) {
                    System.out.println("下一行将会出现错误");
                }
                if (y < dotList[x]) {
                    projectionMat.put(y, x, 0);
                    System.out.println(x + "列" + y + "行");
                }
            }
        }
        return projectionMat;
    }


}
