import org.example.utils.ImageFilterUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author ly
 * @since 2021/4/21
 */
//测试ImageFilterUtil.gray()灰度化方法
public class testFilterGray {
    public static void main(String[] args) throws Exception {
        //原图数组路径
        String[] sourceImage = new String[]{"E:\\Desktop\\OCRTest\\image\\01.png",
                "E:\\Desktop\\OCRTest\\image\\02.png",
                "E:\\Desktop\\OCRTest\\image\\03.png",
                "E:\\Desktop\\OCRTest\\image\\04.png",
                "E:\\Desktop\\OCRTest\\image\\05.png",
                "E:\\Desktop\\OCRTest\\image\\06.png",
                "E:\\Desktop\\OCRTest\\image\\07.png"
        };

        //灰度化后的图片保存路径--在原来的图片主名后加上afterGray 灰度化方法
        String[] processedImage = new String[sourceImage.length];
        for (int i = 0; i < sourceImage.length; i++) {
            processedImage[i] = sourceImage[i].substring(0, sourceImage[i].lastIndexOf(".")) + "afterGray最大值法.png";

            File image = new File(sourceImage[i]);
            BufferedImage bufferedImage = ImageIO.read(image);

            //灰度化处理
            BufferedImage grayImage = ImageFilterUtil.gray(bufferedImage);

            File outFile = new File(processedImage[i]);
            //将灰度化后的图片保存到outFile对应文件位置处
            ImageIO.write(grayImage, "png", outFile);
        }
    }
}
