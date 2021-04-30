import org.example.utils.ImageFilterUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author ly
 * @since 2021/4/22
 */
public class testReplaceWithWhiteColor {
    public static void main(String[] args) throws Exception {

        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\04.png";
        //处理后的图片保存路径
        String processedImg = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "after.png";

        File image = new File(sourceImage);
        BufferedImage bufferedImage = ImageIO.read(image);

        //灰度化
//        BufferedImage grayImage = ImageFilterUtil.gray(bufferedImage);

        BufferedImage paintWhite = ImageFilterUtil.imageRGBDifferenceFilter(bufferedImage, 10);

        //黑白化
        BufferedImage blackWhiteImage = ImageFilterUtil.replaceWithWhiteColor(paintWhite);

        //灰度化
        BufferedImage grayImage = ImageFilterUtil.gray(blackWhiteImage);

        File outFile = new File(processedImg);
        ImageIO.write(grayImage, "png", outFile);

    }
}
