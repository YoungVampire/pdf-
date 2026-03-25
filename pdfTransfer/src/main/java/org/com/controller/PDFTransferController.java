package org.com.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.TransferUtils;

@RestController
@RequestMapping("/pdfTransfer")
public class PDFTransferController {



    @RequestMapping("/pdf2Image")
    public void pdf2Image(String filePath,int dpi){
        TransferUtils.pdf2Image(filePath,dpi);
    }
}
