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
        src = dst;
        return src;
    }

    /**
     * 作用：高斯滤波
     *
     * @param src Mat矩阵图像
     * @return
     */
    public static Mat GaussianBlur(Mat src) {
        Mat dst = src.clone();
        Imgproc.GaussianBlur(src, dst, new Size(9, 9), 0, 0, Core.BORDER_DEFAULT);
        src = dst;
        return src;
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
        src = dst;
        return src;
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
        Imgproc.threshold(grayImage, threshImage, 100, 255, Imgproc.THRESH_BINARY);
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
        if (src.channels() != 1) {
            throw new RuntimeException("不是单通道图，需要先灰度化！！！");
        }
        int nWhiteSum = 0, nBlackSum = 0;
        int i, j;
        int width = src.cols();
        int height = src.rows();
        int value;

        int threshold = getAdapThreshold(src);

        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                value = (int) src.get(j, i)[0];
                if (value > threshold) {
                    src.put(j, i, WHITE);
                    nWhiteSum++;
                } else {
                    src.put(j, i, BLACK);
                    nBlackSum++;
                }
            }
        }
        if (true) {
            // 白底黑字
            if (nBlackSum > nWhiteSum) {
                src = turnPixel(src);
            }
        } else {
            // 黑底白字
            if (nWhiteSum > nBlackSum) {
                src = turnPixel(src);
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
        //怎么判断如果一个举行最大边没有被完全检测，即检测的不是一个闭合的矩形，但是仍应该保留这个矩形
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
     * 返回边缘检测之后的最大矩形轮廓,并返回
     *
     * @param cannyMat Canny之后的mat矩阵
     * @return
     */
//    public static RotatedRect findMaxRect(Mat cannyMat) {
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Mat hierarchy = new Mat();
//
//        // 寻找轮廓
//        Imgproc.findContours(cannyMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE, new Point(0, 0));
//
//        // 找出匹配到的最大轮廓
//        double area = Imgproc.boundingRect(contours.get(0)).area();
//        int index = 0;
//
//        // 找出匹配到的最大轮廓
//        for (int i = 0; i < contours.size(); i++) {
//            double tempArea = Imgproc.boundingRect(contours.get(i)).area();
//            if (tempArea > area) {
//                area = tempArea;
//                index = i;
//            }
//        }
//
//        MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contours.get(index).toArray());
//
//        RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);
//
//        return rect;
//    }

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
     * 矫正图像
     *
     * @param src
     * @return
     */
//    public static Mat correct(Mat src) {
//        // Canny
//        Mat cannyMat = canny(src);
//
//        // 获取最大矩形
//        RotatedRect rect = findMaxRect(cannyMat);
//
//        // 旋转矩形
//        Mat CorrectImg = rotation(cannyMat, rect);
//        Mat NativeCorrectImg = rotation(src, rect);
//
//        //裁剪矩形
////        correctMat = cutRect(CorrectImg, NativeCorrectImg);
////        return correctMat;
//        cutRect(CorrectImg, NativeCorrectImg);
//        return CorrectImg;
//    }


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

    /**
     * 水平投影法切割，适用于类似表格的图像(默认白底黑字) 改进
     *
     * @param src Mat矩阵对象
     * @return
     */
    public static List<Mat> _cutImgX(Mat src) {
        int i, j;
        int width = src.cols(), height = src.rows();
        int[] xNum, cNum;
        int average = 0;//记录黑色像素和的平均值

        int zipLine = 3;
        // 压缩像素值数量；即统计三行像素值的数量为一行// 统计出每行黑色像素点的个数
        xNum = zipLinePixel(countPixel(src, height, width, true), zipLine);

        // 排序
        cNum = Arrays.copyOf(xNum, xNum.length);
        Arrays.sort(cNum);

        for (i = 31 * cNum.length / 32; i < cNum.length; i++) {
            average += cNum[i];
        }
        average /= (height / 32);

        // System.out.println(average);

        // 把需要切割的y轴点存到cutY中
        List<Integer> cutY = new ArrayList<Integer>();
        for (i = 0; i < xNum.length; i++) {
            if (xNum[i] > average) {
                cutY.add(i * zipLine + 1);
            }
        }

        // 优化cutY,把距离相差在30以内的都清除掉
        if (cutY.size() != 0) {
            int temp = cutY.get(cutY.size() - 1);
            // 因为线条有粗细，优化cutY
            for (i = cutY.size() - 2; i >= 0; i--) {
                int k = temp - cutY.get(i);
                if (k <= 30) {
                    cutY.remove(i + 1);
                } else {
                    temp = cutY.get(i);
                }
            }
            temp = cutY.get(cutY.size() - 1);
            // 因为线条有粗细，优化cutY
            for (i = cutY.size() - 2; i >= 0; i--) {
                int k = temp - cutY.get(i);
                if (k <= 30) {
                    cutY.remove(i + 1);
                } else {
                    temp = cutY.get(i);
                }
            }
        }

        //把切割的图片保存到YMat中
        List<Mat> YMat = new ArrayList<Mat>();
        for (i = 1; i < cutY.size(); i++) {
            // 设置感兴趣区域
            int startY = cutY.get(i - 1);
            int h = cutY.get(i) - startY;
            // System.out.println(startY);
            // System.out.println(h);
            Mat temp = new Mat(src, new Rect(0, startY, width, h));
            Mat t = new Mat();
            temp.copyTo(t);
            YMat.add(t);
        }
        return YMat;
    }

    /**
     * 切割 因为是表格图像，采用新的切割思路，中和水平切割和垂直切割一次性切割出所有的小格子
     *
     * @param src
     * @return
     */
    public static List<Mat> cut(Mat src) {
        if (src.channels() == 3) {
            // TODO
        }
        int i, j, k;
        int width = src.cols(), height = src.rows();
        int[] xNum = new int[height], copy_xNum;
        int x_average = 0;
        int value = -1;
        // 统计每行每列的黑色像素值
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                value = (int) src.get(j, i)[0];
                if (value == BLACK) {
                    xNum[j]++;
                }
            }
        }

        int zipXLine = 3;
        xNum = zipLinePixel(xNum, zipXLine);

        // 排序 ............求水平切割点
        copy_xNum = Arrays.copyOf(xNum, xNum.length);
        Arrays.sort(copy_xNum);

        for (i = 31 * copy_xNum.length / 32; i < copy_xNum.length; i++) {
            x_average += copy_xNum[i];
        }
        x_average /= (height / 32);

        // System.out.println("x_average: " + x_average);

        // 把需要切割的y轴点存到cutY中
        List<Integer> cutY = new ArrayList<Integer>();
        for (i = 0; i < xNum.length; i++) {
            if (xNum[i] > x_average) {
                cutY.add(i * zipXLine + zipXLine / 2);
            }
        }

        // 优化cutY,把距离相差在30以内的都清除掉
        if (cutY.size() != 0) {
            int temp = cutY.get(cutY.size() - 1);
            // 因为线条有粗细，优化cutY
            for (i = cutY.size() - 2; i >= 0; i--) {
                k = temp - cutY.get(i);
                if (k <= 10 * zipXLine) {
                    cutY.remove(i + 1);
                } else {
                    temp = cutY.get(i);
                }
            }
            temp = cutY.get(cutY.size() - 1);
            // 因为线条有粗细，优化cutY
            for (i = cutY.size() - 2; i >= 0; i--) {
                k = temp - cutY.get(i);
                if (k <= 10 * zipXLine) {
                    cutY.remove(i + 1);
                } else {
                    temp = cutY.get(i);
                }
            }
        }

        // 把需要切割的x轴的点存到cutX中
        /**
         * 新思路，因为不是很畸变的图像，y轴的割点还是比较好确定的 随机的挑选一个y轴割点，用一个滑动窗口去遍历选中点所在直线，确定x轴割点
         */
        List<Integer> cutX = new ArrayList<Integer>();
        int choiceY = cutY.size() > 1 ? cutY.get(1) : (cutY.size() > 0 ? cutY.get(0) : -1);
        if (choiceY == -1) {
            throw new RuntimeException("切割失败，没有找到水平切割点");
        }

        int winH = 5;
        List<Integer> LH1 = new ArrayList<Integer>();
        List<Integer> LH2 = new ArrayList<Integer>();
        if (choiceY - winH >= 0 && choiceY + winH <= height) {
            // 上下
            for (i = 0; i < width; i++) {
                value = (int) src.get(choiceY - winH, i)[0];
                if (value == BLACK) {
                    LH1.add(i);
                }
                value = (int) src.get(choiceY + winH, i)[0];
                if (value == BLACK) {
                    LH2.add(i);
                }
            }
        } else if (choiceY + winH <= height && choiceY + 2 * winH <= height) {
            // 下
            for (i = 0; i < width; i++) {
                value = (int) src.get(choiceY + 2 * winH, i)[0];
                if (value == BLACK) {
                    LH1.add(i);
                }
                value = (int) src.get(choiceY + winH, i)[0];
                if (value == BLACK) {
                    LH2.add(i);
                }
            }
        } else if (choiceY - winH >= 0 && choiceY - 2 * winH >= 0) {
            // 上
            for (i = 0; i < width; i++) {
                value = (int) src.get(choiceY - winH, i)[0];
                if (value == BLACK) {
                    LH1.add(i);
                }
                value = (int) src.get(choiceY - 2 * winH, i)[0];
                if (value == BLACK) {
                    LH2.add(i);
                }
            }
        } else {
            throw new RuntimeException("切割失败，图像异常");
        }

        // 优化LH1、LH2,把距离相差在30以内的都清除掉
        if (LH1.size() != 0) {
            int temp = LH1.get(LH1.size() - 1);
            // 因为线条有粗细，优化cutY
            for (i = LH1.size() - 2; i >= 0; i--) {
                k = temp - LH1.get(i);
                if (k <= 50) {
                    LH1.remove(i + 1);
                } else {
                    temp = LH1.get(i);
                }
            }
            temp = LH1.get(LH1.size() - 1);
            // 因为线条有粗细，优化cutY
            for (i = LH1.size() - 2; i >= 0; i--) {
                k = temp - LH1.get(i);
                if (k <= 50) {
                    LH1.remove(i + 1);
                } else {
                    temp = LH1.get(i);
                }
            }
        }
        if (LH2.size() != 0) {
            int temp = LH2.get(LH2.size() - 1);
            // 因为线条有粗细，优化cutY
            for (i = LH2.size() - 2; i >= 0; i--) {
                k = temp - LH2.get(i);
                if (k <= 50) {
                    LH2.remove(i + 1);
                } else {
                    temp = LH2.get(i);
                }
            }
            temp = LH2.get(LH2.size() - 1);
            // 因为线条有粗细，优化cutY
            for (i = LH2.size() - 2; i >= 0; i--) {
                k = temp - LH2.get(i);
                if (k <= 50) {
                    LH2.remove(i + 1);
                } else {
                    temp = LH2.get(i);
                }
            }
        }

        if (LH1.size() < LH2.size()) {
            // 进一步优化LH1
            int avg = 0;
            for (k = 1; k < LH1.size() - 2; k++) {
                avg += LH1.get(k + 1) - LH1.get(k);
            }
            avg /= (LH1.size() - 2);

            int temp = LH1.get(LH1.size() - 1);
            for (i = LH1.size() - 2; i >= 0; i--) {
                k = temp - LH1.get(i);
                if (k <= avg) {
                    LH1.remove(i + 1);
                } else {
                    temp = LH1.get(i);
                }
            }
            cutX = LH1;
        } else {
            // 进一步优化LH2
            int avg = 0;
            for (k = 1; k < LH2.size() - 2; k++) {
                avg += LH2.get(k + 1) - LH2.get(k);
            }
            avg /= (LH2.size() - 2);

            int temp = LH2.get(LH2.size() - 1);
            for (i = LH2.size() - 2; i >= 0; i--) {
                k = temp - LH2.get(i);
                if (k <= avg) {
                    LH2.remove(i + 1);
                } else {
                    temp = LH2.get(i);
                }
            }
            cutX = LH2;
        }

        List<Mat> destMat = new ArrayList<Mat>();
        for (i = 1; i < cutY.size(); i++) {
            for (j = 1; j < cutX.size(); j++) {
                // 设置感兴趣的区域
                int startX = cutX.get(j - 1);
                int w = cutX.get(j) - startX;
                int startY = cutY.get(i - 1);
                int h = cutY.get(i) - startY;
                Mat temp = new Mat(src, new Rect(startX + 2, startY + 2, w - 2, h - 2));
                Mat t = new Mat();
                temp.copyTo(t);
                destMat.add(t);
            }
        }

        return destMat;
    }


}
