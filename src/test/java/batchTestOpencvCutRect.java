import org.example.utils.ImageOpencvUtil;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import java.net.URL;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * @author ly
 * @since 2021/4/28
 */
//批量测试ImageOpencvUtil的CutRect()方法
public class batchTestOpencvCutRect {
    public static void main(String[] args) throws Exception {
        // 加载动态库
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java452.dll");
        System.load(url.getPath());

        //原图路径
        String[] sourceImage = new String[]{"E:\\Desktop\\OCRTest\\image\\01.png",
                "E:\\Desktop\\OCRTest\\image\\02.png",
                "E:\\Desktop\\OCRTest\\image\\03.png",
                "E:\\Desktop\\OCRTest\\image\\04.png",
                "E:\\Desktop\\OCRTest\\image\\05.png",
                "E:\\Desktop\\OCRTest\\image\\06.png",
                "E:\\Desktop\\OCRTest\\image\\07.png",
                "E:\\Desktop\\OCRTest\\image\\08.png",
                "E:\\Desktop\\OCRTest\\image\\09.png",
                "E:\\Desktop\\OCRTest\\image\\10.png",
                "E:\\Desktop\\OCRTest\\image\\11.png"
        };
        String[] processedImage = new String[sourceImage.length];
        for (int i = 0; i < sourceImage.length; i++) {
            //处理后的图片保存路径
            processedImage[i] = sourceImage[i].substring(0, sourceImage[i].lastIndexOf(".")) + "afterCutted.png";

            //读取图像
            Mat image = imread(sourceImage[i]);
            if (image.empty()) {
                throw new Exception("image is empty");
            }

            //opencv灰度化
            Mat grayImg = ImageOpencvUtil.gray(image);

            //二值化
            Mat binaryImg = ImageOpencvUtil.binaryzation(grayImg);

            //膨胀与腐蚀
            Mat corrodedImg = ImageOpencvUtil.corrosion(binaryImg);

            //文字区域
            List<RotatedRect> rects = ImageOpencvUtil.findTextRegion(corrodedImg);

            //倾斜矫正
            Mat correctedImg = ImageOpencvUtil.correction(rects, image);

            //倾斜校正后裁剪
            Mat cuttedImg = ImageOpencvUtil.cutRect(correctedImg);

            //保存倾斜校正并裁剪后的图片
            imwrite(processedImage[i], cuttedImg);
        }
    }
}
