package per.qiang.common.fdfs.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.DefaultAppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileService {

    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    /**
     * 支持的图片类型
     */
    private static final String[] SUPPORT_IMAGE_TYPE = {"JPG", "JPEG", "PNG", "GIF", "BMP", "WBMP"};
    private static final List<String> SUPPORT_IMAGE_LIST = Arrays.asList(SUPPORT_IMAGE_TYPE);
    /**
     * 支持的视频文件类型
     */
    private static final String[] SUPPORT_VIDEO_TYPE = {"AVI", "FLV", "MP4", "WMV", "WEBM"};
    private static final List<String> SUPPORT_VIDEO_LIST = Arrays.asList(SUPPORT_VIDEO_TYPE);
    /**
     * Office文档支持Wordd/Excel/Ppt
     */
    private static final String[] SUPPORT_OFFICE_TYPE = {"XLS", "XLSX", "DOC", "DOCX", "PPT", "PPTX"};
    private static final List<String> SUPPORT_OFFICE_LIST = Arrays.asList(SUPPORT_OFFICE_TYPE);

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    private DefaultAppendFileStorageClient dafStorageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    /**
     * uploadFile:(上传文件  file).
     */
    public FastdfsPath uploadFile(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
        return FastdfsPath.StorePathConversion(fdfsWebServer.getWebServerUrl(), storePath.getGroup(), storePath.getPath());
    }

    /**
     * uploadFile:(上传文件  file).
     */
    public FastdfsPath uploadFile(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        StorePath storePath = storageClient.uploadFile(input, file.length(), suffix, null);
        return FastdfsPath.StorePathConversion(fdfsWebServer.getWebServerUrl(), storePath.getGroup(), storePath.getPath());
    }

    /**
     * uploadImageAndCrtThumbImage:(上传图片保存原始图片并且生成缩略图).
     */
    public FastdfsPath uploadImageAndCrtThumbImage(MultipartFile file) throws IOException {
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
        FastdfsPath path = FastdfsPath.StorePathConversion(fdfsWebServer.getWebServerUrl(), storePath.getGroup(), storePath.getPath());
        path.setThumbnailPath(getThumbImagesUrl(storePath));
        return path;
    }

    /**
     * uploadImageAndCrtThumbImage:(上传图片保存原始图片并且生成缩略图).
     */
    public FastdfsPath uploadImageAndCrtThumbImage(File file) throws IOException {
        FileInputStream input = new FileInputStream(file);
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(input, file.length(), FilenameUtils.getExtension(file.getName()), null);
        FastdfsPath path = FastdfsPath.StorePathConversion(fdfsWebServer.getWebServerUrl(), storePath.getGroup(), storePath.getPath());
        path.setThumbnailPath(getThumbImagesUrl(storePath));
        return path;
    }

    /**
     * uploadThumbImage:(上传图片仅保存生成的缩略图).
     */
    public FastdfsPath uploadThumbImage(MultipartFile file) throws IOException {
        //上传原始图片并生成缩略图
        StorePath storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
        //删除原始图片
        deleteFile(storePath.getFullPath());
        //组装缩略图路径
        StringBuffer sbpath = new StringBuffer();
        sbpath.append(storePath.getPath()).insert(storePath.getPath().lastIndexOf("."), "_" + thumbImageConfig.getHeight() + "x" + thumbImageConfig.getWidth());
        storePath.setPath(sbpath.toString());
        FastdfsPath path = FastdfsPath.StorePathConversion(fdfsWebServer.getWebServerUrl(), storePath.getGroup(), storePath.getPath());
        path.setThumbnailPath(path.getRelativePath());
        return path;
    }

    /**
     * uploadFile:(将一段字符串生成一个文件上传).
     */
    public FastdfsPath uploadFile(String content, String fileExtension) {
        byte[] buff = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream stream = new ByteArrayInputStream(buff);
        StorePath storePath = storageClient.uploadFile(stream, buff.length, fileExtension, null);
        return FastdfsPath.StorePathConversion(fdfsWebServer.getWebServerUrl(), storePath.getGroup(), storePath.getPath());
    }

    /**
     * getThumbImagesUrl:(封装缩略图图片完整存储地址).
     */
    private String getThumbImagesUrl(StorePath storePath) {
        StringBuffer path = new StringBuffer();
        path.append(storePath.getFullPath()).insert(storePath.getFullPath().lastIndexOf("."), "_" + thumbImageConfig.getHeight() + "x" + thumbImageConfig.getWidth());
        return path.toString();
    }

    /**
     * uploadContinueFile:(断点续传).
     */
    public FastdfsPath uploadContinueFile(MultipartFile file) throws IOException {
        StorePath storePath = dafStorageClient.uploadAppenderFile("group1", file.getInputStream(), file.getSize(), FilenameUtils.getExtension(file.getOriginalFilename()));
        return FastdfsPath.StorePathConversion(fdfsWebServer.getWebServerUrl(), storePath.getGroup(), storePath.getPath());
    }

    /**
     * downloadFile:(完整文件下载).
     */
    public byte[] downloadFile(String group, String fileurl) throws IOException {
        return storageClient.downloadFile(group, fileurl, new DownloadByteArray());
    }

    /**
     * isSupportImage:(是否是支持的图片类型).
     */
    public boolean isSupportImage(String fileExtName) {
        return SUPPORT_IMAGE_LIST.contains(fileExtName.toUpperCase());
    }

    /**
     * isSupportImage:(是否是支持的视频类型).
     */
    public boolean isSupportVideo(String fileExtName) {
        return SUPPORT_VIDEO_LIST.contains(fileExtName.toUpperCase());
    }


    /**
     * isSupportOffice:(判断是否为office文档,文档格式支持Excel/Word/Ppt).
     */
    public boolean isSupportOffice(String fileExtName) {
        return SUPPORT_OFFICE_LIST.contains(fileExtName.toUpperCase());
    }

    /**
     * 文件上传
     *
     * @param bytes     文件字节
     * @param fileSize  文件大小
     * @param extension 文件扩展名
     */
    public FastdfsPath uploadFile(byte[] bytes, long fileSize, String extension) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        // 元数据
        Set<MetaData> metaDataSet = new HashSet<>();
        metaDataSet.add(new MetaData("dateTime", LocalDateTime.now().toString()));
        StorePath storePath = storageClient.uploadFile(bais, fileSize, extension, metaDataSet);
        return FastdfsPath.StorePathConversion(fdfsWebServer.getWebServerUrl(), storePath.getGroup(), storePath.getPath());
    }

    /**
     * 下载文件
     * @param filePath 文件路径
     */
    public byte[] downloadFile(String filePath) throws IOException {
        byte[] bytes = null;
        if (StringUtils.isNotBlank(filePath)) {
            String group = filePath.substring(0, filePath.indexOf("/"));
            String path = filePath.substring(filePath.indexOf("/") + 1);
            DownloadByteArray byteArray = new DownloadByteArray();
            bytes = storageClient.downloadFile(group, path, byteArray);
        }
        return bytes;
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) return;
        try {
            StorePath storePath = StorePath.parseFromUrl(fileUrl);
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (FdfsUnsupportStorePathException e) {
            logger.warn("文件删除异常 {}", e.getMessage());
        }
    }
}
