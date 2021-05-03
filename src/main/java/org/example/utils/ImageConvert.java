package org.example.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ly
 * @since 2021/4/22
 */
public class ImageConvert {

    /**
     * 8-bit RGBA convert 8-bit RGB
     *
     * @param original 原来的BufferedImage图片
     * @param type     BufferedImage图片类型
     * @return
     */
    private static BufferedImage toBufferedImageOfType(BufferedImage original, int type) {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }
        // Don't convert if it already has correct type
        if (original.getType() == type) {
            return original;
        }
        // Create a buffered image
        BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), type);
        // Draw the image onto the new buffer
        Graphics2D g = image.createGraphics();
        try {
            g.setComposite(AlphaComposite.Src);
            g.drawImage(original, 0, 0, null);
        } finally {
            g.dispose();
        }
        return image;
    }

    /**
     * BufferedImage转换成Mat
     *
     * @param originalImage 要转换的BufferedImage
     */
    public static Mat BufImg2Mat(BufferedImage originalImage) throws IOException {
        // Convert INT to BYTE
        originalImage = toBufferedImageOfType(originalImage, BufferedImage.TYPE_3BYTE_BGR);
        // Convert bufferedimage to byte array
        byte[] pixels = ((DataBufferByte) originalImage.getRaster().getDataBuffer()).getData();
        // Create a Matrix the same size of image
        Mat image = new Mat(originalImage.getHeight(), originalImage.getWidth(), 16);
        // Fill Matrix with image values
        image.put(0, 0, pixels);
        return image;
    }

    /**
     * Mat转换成BufferedImage
     *
     * @param src           要转换的Mat
     * @param fileExtension 格式为 ".jpg", ".png", etc
     * @return
     */
    public static BufferedImage Mat2BufImg(Mat src, String fileExtension) {
        //编码图像
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(fileExtension, src, matOfByte);
        //将编码的Mat存储在字节数组中
        byte[] byteArray = matOfByte.toArray();

        BufferedImage bufImage = null;
        //准备缓冲图像
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufImage;
    }
}
