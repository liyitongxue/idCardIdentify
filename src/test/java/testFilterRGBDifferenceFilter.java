import org.example.utils.ImageFilterUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author ly
 * @since 2021/4/23
 */
//测试ImageFilterUtil.imageRGBDifferenceFilter()涂白方法
public class testFilterRGBDifferenceFilter {
    private final static int targetDifferenceValue = 15;

    public static void main(String[] args) throws IOException {
        //原图数组路径
        String[] sourceImage = new String[]{"E:\\Desktop\\OCRTest\\image\\01.png",
                "E:\\Desktop\\OCRTest\\image\\02.png",
                "E:\\Desktop\\OCRTest\\image\\03.png",
                "E:\\Desktop\\OCRTest\\image\\04.png",
                "E:\\Desktop\\OCRTest\\image\\05.png",
                "E:\\Desktop\\OCRTest\\image\\06.png",
                "E:\\Desktop\\OCRTest\\image\\07.png"
        };

        //处理后的图片保存路径
        String[] processedImage = new String[sourceImage.length];
        for (int i = 0; i < sourceImage.length; i++) {
            processedImage[i] = sourceImage[i].substring(0, sourceImage[i].lastIndexOf(".")) + "afterPaintWhite.png";

            File image = new File(sourceImage[i]);
            BufferedImage bufferedImage = ImageIO.read(image);

            //涂白操作
            BufferedImage paintWhite = ImageFilterUtil.imageRGBDifferenceFilter(bufferedImage, targetDifferenceValue);

            File outFile = new File(processedImage[i]);
            //将涂白后的图片保存到outFile对应文件位置处
            ImageIO.write(paintWhite, "png", outFile);
        }
    }
}
