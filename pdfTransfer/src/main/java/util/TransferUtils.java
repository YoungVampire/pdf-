package util;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import com.lowagie.text.pdf.PdfReader;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class TransferUtils {


    /**
     * 提取图片保存
     */
    public static void selectImageFromPdf() throws Exception{
        Integer count = 0;
        File file = new File("D:\\工作\\杨校禹\\杨校禹调档函.pdf");
        FileInputStream fis = new FileInputStream(file);
        PDDocument document = PDDocument.load(fis);
        int allPages = document.getNumberOfPages();
        for (int i = 0; i < allPages; i++) {
            PDPage page = document.getPage(i);
            PDResources resources = page.getResources();
            Iterable<COSName> xObjectNames = resources.getXObjectNames();
            if (xObjectNames != null) {
                Iterator<COSName> iterator = xObjectNames.iterator();
                while (iterator.hasNext()) {
                    COSName key = iterator.next();
                    if (resources.isImageXObject(key)) {
                        PDImageXObject image = (PDImageXObject) resources.getXObject(key);
                        BufferedImage bImage = image.getImage();
                        ImageIO.write(bImage, "JPG", new File("D:\\工作\\杨校禹\\" + "image_" + (i + 1) + "页" + count + ".jpg"));
                        count++;
                    }
                }
            }
        }
        document.close();
    }


    /**
     * 使用pdfbox将整个pdf转换成图片
     *
     * @param fileAddress 文件地址 如:C:\\Users\\user\\Desktop\\test
     * @param filename    PDF文件名不带后缀名
     * @param type        图片类型 png 和jpg
     */
    public static void pdf2jpg(String fileAddress, String filename, String type) {
        long startTime = System.currentTimeMillis();
        // 将文件地址和文件名拼接成路径 注意：线上环境不能使用\\拼接
        File file = new File(fileAddress + "\\" + filename + ".pdf");
        try {
            // 写入文件
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                // dpi为144，越高越清晰，转换越慢
                BufferedImage image = renderer.renderImageWithDPI(i, 400); // Windows native DPI
                // 将图片写出到该路径下
                ImageIO.write(image, type, new File(fileAddress + "\\" + filename + "_" + (i + 1) + "." + type));
            }
            long endTime = System.currentTimeMillis();
            System.out.println("共耗时：" + ((endTime - startTime) / 1000.0) + "秒");  //转化用时
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * PDF文件转PNG图片，全部页数
     * @param pdfFilePath pdf完整路径:C:\\Users\\user\\Desktop\\test\\1234.pdf
     * @param dpi dpi越大转换后越清晰，相对转换速度越慢
     */
    public static void pdf2Image(String pdfFilePath, int dpi) {
        long startTime = System.currentTimeMillis();
        File file = new File(pdfFilePath);
        PDDocument pdDocument;
        try {
            String imgPdfPath = file.getParent();
            int dot = file.getName().lastIndexOf('.');
            // 获取图片文件名
            String imagePdfName = file.getName().substring(0, dot);
            pdDocument = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            /* dpi越大转换后越清晰，相对转换速度越慢 */
            PdfReader reader = new PdfReader(pdfFilePath);
            int pages = reader.getNumberOfPages();
            StringBuffer imgFilePath;
            for (int i = 0; i < pages; i++) {
                String imgFilePathPrefix = imgPdfPath + File.separator + imagePdfName;
                imgFilePath = new StringBuffer();
                imgFilePath.append(imgFilePathPrefix);
                imgFilePath.append("_");
                imgFilePath.append((i + 1));
                imgFilePath.append(".png");
                File dstFile = new File(imgFilePath.toString());
                BufferedImage image = renderer.renderImageWithDPI(i, dpi);
                ImageIO.write(image, "png", dstFile);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("共耗时：" + ((endTime - startTime) / 1000.0) + "秒");  //转化用时
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自由确定起始页和终止页
     * @param fileAddress 文件地址 如:C:\\Users\\user\\Desktop\\test
     * @param filename    PDF文件名不带后缀名
     * @param indexOfStart 开始页  开始转换的页码，从0开始
     * @param indexOfEnd 结束页  停止转换的页码，-1为全部
     * @param type        图片类型 png 和jpg
     */
    public static void pdf2pngByPageNum(String fileAddress,String filename,int indexOfStart,int indexOfEnd,String type) {
        long startTime = System.currentTimeMillis();
        // 将文件地址和文件名拼接成路径 注意：线上环境不能使用\\拼接
        File file = new File(fileAddress+"\\"+filename+".pdf");
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = indexOfStart; i < indexOfEnd; i++) {
                // dpi为144，越高越清晰，转换越慢
                BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
                // 将图片写出到该路径下
                ImageIO.write(image, type, new File(fileAddress+"\\"+filename+"_"+(i+1)+"."+type));
            }
            long endTime = System.currentTimeMillis();
            System.out.println("共耗时：" + ((endTime - startTime) / 1000.0) + "秒"); // 转换用时
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用文件流整个pdf转换成图片
     * @param fileAddress 文件地址 如:C:\\Users\\user\\Desktop\\test
     * @param filename    PDF文件名不带后缀名
     * @param type        图片类型 png 、jpg
     */
    public static List<Map<String, String>> pdfToImage(String fileAddress, String filename, String type) {
        long startTime = System.currentTimeMillis();
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> resultMap = null;
        PDDocument pdDocument = null;
        String fileName = null;
        String imgPath = null;
        try {
            // 将文件地址和文件名拼接成路径 注意：线上环境不能使用\\拼接
            File FilePath = new File(fileAddress + "\\" + filename + ".pdf");
            // 文件流
            FileInputStream inputStream = new FileInputStream(FilePath);
            int dpi = 296;
            pdDocument = PDDocument.load(inputStream);
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            int pageCount = pdDocument.getNumberOfPages();
            /* dpi越大转换后越清晰，相对转换速度越慢 */
            for (int i = 0; i < pageCount; i++) {
                resultMap = new HashMap<>();
                fileName = filename + "_" + (i + 1) + "." + type;
                imgPath = fileAddress + "\\" + fileName;
                BufferedImage image = renderer.renderImageWithDPI(i, dpi);
                ImageIO.write(image, type, new File(imgPath));
                resultMap.put("fileName", fileName);
                resultMap.put("filePath", imgPath); // 图片路径
                list.add(resultMap);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("共耗时：" + ((endTime - startTime) / 1000.0) + "秒");  //转化用时
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 这里需要关闭PDDocument，不然如果想要删除pdf文件时会提示文件正在使用，无法删除的情况
                pdDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private static String[] getPdfs(String fileAddress) throws IOException {
        File file = new File(fileAddress);
        String[] pdfs;
        if (file.isDirectory()) {
            pdfs = file.list();
            return pdfs;
        } else {
            throw new IOException("输入的路径有问题");
        }
    }


    /**
     * 合并文件夹中pdf
     * @param fileAddress 文件夹地址 如:C:\\Users\\user\\Desktop\\test
     * @param fileName 输出文件名称 文件名不带后缀名
     * @throws IOException
     */
    public static void mergePdfs(String fileAddress,String fileName) throws IOException {
        PDFMergerUtility mergePdf = new PDFMergerUtility();
        System.out.println("你输入的路径是:" + fileAddress);
        String destinationFileName = fileName + ".pdf";
        String[] pdfs = getPdfs(fileAddress);
        for (int i = 0; i < pdfs.length; i++)
            mergePdf.addSource(fileAddress + File.separator + pdfs[i]);
        mergePdf.setDestinationFileName(destinationFileName);
        System.out.println("合并比较费时间,请等待个几分钟吧!");
        mergePdf.mergeDocuments();
        System.out.print("合并完成");

    }

    /**
     * 拆分pdf
     * @param fileAddress 文件地址 如:C:\\Users\\user\\Desktop\\test
     * @param fileName 文件名称 文件名不带后缀名
     * @param splitName 拆分后名称
     * @throws IOException
     */
    public static void splitPdf(String fileAddress,String fileName,String splitName) throws IOException {
        File file = new File(fileAddress + "\\" + fileName + ".pdf");
        PDDocument document = PDDocument.load(file);
        //创建一个拆分器对象
        Splitter splitter = new Splitter();
        //list中存放好被拆分的pdf对象 其中内容是pdf的每一页
        List<PDDocument>Pages = splitter.split(document);
        //创建迭代器对象
        Iterator<PDDocument>iterator = Pages.listIterator();
        //saving splits as individual PDF document
        int i = 1;
        while(iterator.hasNext()) {
            PDDocument pd = iterator.next();
            pd.save(fileAddress + "\\" +  splitName + ".pdf");
        }
        System.out.println("pdf拆分成功");
        document.close();

    }



    /**
     * 多图片合成pdf的限制后缀
     */
    private static final List IMAGE_SUFFIX = Arrays.asList("jpg", "png", "jpeg");
    /**
     * 多个图片合成一个pdf
     *
     * @param imgFolder 多图片的文件夹路径  例如:"D:\\image\\"
     * @param target    合并的图片路径          "D:\\image\\merge.pdf"
     * @throws IOException
     */
    public static void manyImageToOnePdf(String imgFolder, String target) throws IOException {
        PDDocument doc = new PDDocument();
        //创建一个空的pdf文件
        doc.save(target);
        PDPage page;
        PDImageXObject pdImage;
        PDPageContentStream contents;
        BufferedImage bufferedImage;
        String fileName;
        float w, h;
        String suffix;
        File tempFile;
        int index;
        File folder = new File(imgFolder);
        for (int i = 0; i < folder.listFiles().length; i++) {
            tempFile = folder.listFiles()[i];
            if (!tempFile.isFile()) {
                continue;
            }
            fileName = tempFile.getName();
            index = fileName.lastIndexOf(".");
            if (index == -1) {
                continue;
            }
            //获取文件的后缀
            suffix = fileName.substring(index + 1);
            //如果文件后缀不是图片格式,跳过当前循环
            if (!IMAGE_SUFFIX.contains(suffix)) {
                continue;
            }
            bufferedImage = ImageIO.read(folder.listFiles()[i]);
            //Retrieving the page
            pdImage = LosslessFactory.createFromImage(doc, bufferedImage);
            w = pdImage.getWidth();
            h = pdImage.getHeight();
            page = new PDPage(new PDRectangle(w, h));
            contents = new PDPageContentStream(doc, page);
            contents.drawImage(pdImage, 0, 0, w, h);
            System.out.println("Image inserted");
            contents.close();
            doc.addPage(page);
        }
        //保存pdf
        doc.save(target);
        //关闭pdf
        doc.close();
    }


    /**
     * 将pdf转为一张长图
     * @param pdfPath pdf路径（包含文件名） 如:C:\\Users\\user\\Desktop\\test\\***.pdf
     * @param outputPath 输出路径（包含文件名，不包含后缀）如:C:\\Users\\user\\Desktop\\test\\***
     * @param dpi 像素，越高越清晰
     * @param type 图片格式，可选png、jpg、jpeg
     */
    public static void pdf2OneImage(String pdfPath,String outputPath,int dpi,String type){


            try {
            // 1. 加载PDF文档
            PDDocument document = PDDocument.load(new File(pdfPath));
            PDFRenderer renderer = new PDFRenderer(document);

            // 2. 计算合并后的图片总高度
            int totalHeight = 0;
            int maxWidth = 0;
            BufferedImage[] pageImages = new BufferedImage[document.getNumberOfPages()];

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, dpi);
                pageImages[i] = image;
                totalHeight += image.getHeight();
                maxWidth = Math.max(maxWidth, image.getWidth());
            }

            // 3. 创建合并后的长图
            BufferedImage combined = new BufferedImage(
                    maxWidth,
                    totalHeight,
                    BufferedImage.TYPE_INT_RGB
            );

            // 4. 合并所有页面图片
            int y = 0;
            for (BufferedImage image : pageImages) {
                combined.createGraphics().drawImage(image, 0, y, null);
                y += image.getHeight();
            }

            // 5. 保存结果
            ImageIOUtil.writeImage(combined, outputPath + "." + type, dpi);
            document.close();

            System.out.println("转换完成，保存至: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
