import org.example.utils.ImageFilterUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author ly
 * @since 2021/5/3
 */
public class testFilterBrightness {
    public static void main(String[] args) throws IOException {
        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\03.png";
        //处理后的图片保存路径
        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "afterFilterBrightness.png";

        File image = new File(sourceImage);
        BufferedImage bufferedImage = ImageIO.read(image);

        int brightness = ImageFilterUtil.imageBrightness(bufferedImage);
        System.out.println("brightness = " + brightness);
        int fixedBrightness = brightness - 80;
        System.out.println("fixedBrightness = " + fixedBrightness);

        BufferedImage brightnessImg = bufferedImage;
        if (brightness > 180) {
            //Filter的调节亮度imageBrightness()方法
            brightnessImg = ImageFilterUtil.imageBrightness(bufferedImage, -60);
        }


        File outFile = new File(processedImage);
        //将缩放后的图片保存到outFile对应文件位置处
        ImageIO.write(brightnessImg, "png", outFile);
    }
}
