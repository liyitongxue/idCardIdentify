package org.example.utils;

import net.sourceforge.tess4j.util.ImageHelper;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author ly
 * @since 2021/4/21
 */
public class ImageFilterUtil {

    //比较三个数的大小并取最大数
    public static int getBiggest(int x, int y, int z) {
        if (x >= y && x >= z) {
            return x;
        } else if (y >= x && y >= z) {
            return y;
        } else if (z >= x && z >= y) {
            return z;
        } else {
            return 0;
        }
    }

    //比较三个数的大小并取最小数
    public static int getSmallest(int x, int y, int z) {
        if (x <= y && x <= z) {
            return x;
        } else if (y <= x && y <= z) {
            return y;
        } else if (z <= x && z <= y) {
            return z;
        } else {
            return 0;
        }
    }

    //计算并返回三个数的平均值
    public static int getAvg(int x, int y, int z) {
        int avg = (x + y + z) / 3;
        return avg;
    }

    /**
     * 灰度化
     *
     * @param image 灰度化处理的图片
     * @return
     */
    public static BufferedImage gray(BufferedImage image) {
        int[] rgb = new int[3];
        int width = image.getWidth();
        int height = image.getHeight();
        int minx = image.getMinX();
        int miny = image.getMinY();
        BufferedImage grayImage = new BufferedImage(width, height, image.getType());
        for (int i = minx; i < width - 1; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = image.getRGB(i, j);
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);

                int gray = getBiggest(rgb[0], rgb[1], rgb[2]);//最大值法灰度化
//                int gray = getSmallest(rgb[0], rgb[1], rgb[2]);//最小值法灰度化
//                int gray = getAvg(rgb[0], rgb[1], rgb[2]);//均值法灰度化
//                int gray = (int) (0.3 * rgb[0] + 0.59 * rgb[1] + 0.11 * rgb[2]);//加权法灰度化

                Color newColor = new Color(gray, gray, gray);
                int newRgb = newColor.getRGB();
                grayImage.setRGB(i, j, newRgb);
            }
        }
        return grayImage;
    }

    /**
     * 把图像处理成黑白照片
     *
     * @param image 黑白处理的图片
     * @return
     */
    public static BufferedImage replaceWithWhiteColor(BufferedImage image) {
        int[] rgb = new int[3];

        int width = image.getWidth();
        int height = image.getHeight();
        int minx = image.getMinX();
        int miny = image.getMinY();
        //遍历图片的像素，为处理图片上的杂色，所以要把指定像素上的颜色换成目标白色 用二层循环遍历长和宽上的每个像素
        int hitCount = 0;
        for (int i = minx; i < width - 1; i++) {
            for (int j = miny; j < height; j++) {
                //得到指定像素（i,j)上的RGB值，
                int pixel = image.getRGB(i, j);
                //分别进行位操作得到 r g b上的值
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);

                /**
                 *
                 * 进行换色操作，我这里是要换成白底，那么就判断图片中rgb值是否在范围内的像素
                 *
                 */
                // 经过不断尝试，RGB数值相互间相差15以内的都基本上是灰色，
                // 对以身份证来说特别是介于73到78之间，还有大于100的部分RGB值都是干扰色，将它们一次性转变成白色
                if ((Math.abs(rgb[0] - rgb[1]) < 30)
                        && (Math.abs(rgb[0] - rgb[2]) < 30)
                        && (Math.abs(rgb[1] - rgb[2]) < 30)
                        && (((rgb[0] > 73) && (rgb[0] < 78)) || (rgb[0] > 100))) {
                    // 进行换色操作,0xffffff是白色
                    image.setRGB(i, j, 0xffffff);
                }
            }
        }
        return image;
    }

    /**
     * 图片像素RGB差值滤镜--将彩色的地方涂白
     *
     * @param image
     * @param differenceValue 最大允许差值
     * @return
     */
    public static BufferedImage imageRGBDifferenceFilter(BufferedImage image, int differenceValue) {
        int[] rgb = new int[3];
        int width = image.getWidth();
        int height = image.getHeight();
        int minx = image.getMinX();
        int miny = image.getMinY();
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                Object data = image.getRaster().getDataElements(i, j, null);
                int r = image.getColorModel().getRed(data);
                int g = image.getColorModel().getGreen(data);
                int b = image.getColorModel().getBlue(data);

                if (differenceValue <= Math.abs(r - b)
                        && differenceValue <= Math.abs(r - g)
                        && differenceValue <= Math.abs(b - g)
                        && b - g > 0) {
                    //把超过最大差值的像素涂白
                    image.setRGB(i, j, Color.white.getRGB());
                }
            }
        }
        return image;
    }

    /**
     * 作用：图片缩放
     *
     * @param image  需要缩放的图片
     * @param width  需要缩放宽度
     * @param height 需要缩放高度
     * @return
     */
    public static BufferedImage testZoom(BufferedImage image, int width, int height) {
        return ImageHelper.getScaledInstance(image, width, height);
    }

    /**
     * 作用：图片缩放
     *
     * @param image 需要缩放的图片
     * @return
     */
    public static BufferedImage zoom(BufferedImage image) {
        return ImageHelper.getScaledInstance(image, 673, 425);
    }

    public static BufferedImage convertToBinary(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] rgb = new int[3];

        int black = new Color(0, 0, 0).getRed();
        int white = new Color(255, 255, 255).getRGB();
        BufferedImage binaryImage = new BufferedImage(width, height, image.getType());

        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height; j++) {
                //得到指定像素（i,j)上的RGB值，
                int pixel = image.getRGB(i, j);
                //分别进行位操作得到 r g b上的值
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);
                if (((rgb[0] + rgb[1] + rgb[2]) / 3) < 192) {
                    image.setRGB(i, j, black);
                } else {
                    image.setRGB(i, j, white);
                }
            }
        }

        return binaryImage;
    }


    public static void removeBrinaryImageNoisePoint(BufferedImage image) {
        int[][] grayMatrix = new int[image.getWidth()][image.getHeight()];
//      找到灰度差异扩大点
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Object data = image.getRaster().getDataElements(x, y, null);
                int red = image.getColorModel().getRed(data);
                grayMatrix[x][y] = red;
            }
        }
        for (int x = 0; x < grayMatrix.length; x++) {
            for (int y = 0; y < grayMatrix[x].length; y++) {
                if (grayMatrix[x][y] < 255) {
                    //判断四周有没有存在像素
                    boolean isNoisepoint = true;
                    if (x - 1 >= 0) {
                        isNoisepoint = isNoisepoint && grayMatrix[x - 1][y] != 0;
                    }
                    if (y - 1 >= 0) {
                        isNoisepoint = isNoisepoint && grayMatrix[x][y - 1] != 0;
                    }
                    if (x + 1 < grayMatrix.length) {
                        isNoisepoint = isNoisepoint && grayMatrix[x + 1][y] != 0;
                    }
                    if (y + 1 < grayMatrix[x].length) {
                        isNoisepoint = isNoisepoint && grayMatrix[x][y + 1] != 0;
                    }

                    if (isNoisepoint)
                        image.setRGB(x, y, Color.white.getRGB());
                }
            }
        }
    }
}
