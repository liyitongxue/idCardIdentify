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
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\07.jpg";
        //处理后的图片保存路径
        String gray_result_image = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "after.png";

        File image = new File(sourceImage);
        BufferedImage bufferedImage = ImageIO.read(image);

        //灰度化
        BufferedImage grayImage = ImageFilterUtil.gray(bufferedImage);

        //黑白化
        BufferedImage blackWhiteImage = ImageFilterUtil.replaceWithWhiteColor(bufferedImage);

        File outFile = new File(gray_result_image);
        ImageIO.write(blackWhiteImage, "png", outFile);

    }
}
