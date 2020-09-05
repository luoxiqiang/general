package per.qiang.common.fdfs.service;

public class FastdfsPath {

    public static FastdfsPath StorePathConversion(String url, String group, String path) {
        FastdfsPath fastdfsPath = new FastdfsPath();
        fastdfsPath.setAbsolutePath(url+"/"+group+"/"+path);
        fastdfsPath.setRelativePath("/"+group+"/"+path);
        fastdfsPath.setStoreGroup(group);
        fastdfsPath.setStorePath(path);
        return fastdfsPath;
    }

    /**
     * 相对路径
     */
    private String relativePath;

    /**
     * 绝对路径
     */
    private String absolutePath;

    /**
     * 存储组名
     */
    private String StoreGroup;

    /**
     * 存储路径
     */
    private String StorePath;

    /**
     * 缩略图or封面
     */
    private String thumbnailPath;

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getStoreGroup() {
        return StoreGroup;
    }

    public void setStoreGroup(String storeGroup) {
        StoreGroup = storeGroup;
    }

    public String getStorePath() {
        return StorePath;
    }

    public void setStorePath(String storePath) {
        StorePath = storePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public FastdfsPath() {

    }
}
