import org.example.utils.ImageFilterUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author ly
 * @since 2021/4/27
 */
public class testFilterZoom {
    public static void main(String[] args) throws Exception {
        //原图路径
        String sourceImage = "E:\\Desktop\\OCRTest\\image\\04.png";
        //处理后的图片保存路径
        String processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "afterFilterZoom.png";
        String _processedImage = sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "afterFilter_Zoom.png";

        File image = new File(sourceImage);
        BufferedImage bufferedImage = ImageIO.read(image);

        //Filter的缩放testZoom()方法
        BufferedImage zoomedImg = ImageFilterUtil.testZoom(bufferedImage, 673, 425);
        File outFile = new File(processedImage);
        //将缩放后的图片保存到outFile对应文件位置处
        ImageIO.write(zoomedImg, "png", outFile);

        //Filter的缩放testZoom()方法
        BufferedImage _zoomedImg = ImageFilterUtil.testZoom(zoomedImg, 453, 282);
        File _outFile = new File(_processedImage);
        //将缩放后的图片保存到_outFile对应文件位置处
        ImageIO.write(_zoomedImg, "png", _outFile);
    }
}
