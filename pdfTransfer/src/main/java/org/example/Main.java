package org.example;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import util.TransferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) throws Exception {
        TransferUtils.pdf2OneImage("D:\\工作\\杨校禹\\毕业证.pdf","D:\\工作\\杨校禹\\毕业证",100,"jpg");
    }
}
