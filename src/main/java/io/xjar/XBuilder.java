package io.xjar;

import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * XJar 构建插件
 *
 * @author Payne 646742615@qq.com
 * 2018/12/4 10:27
 */
public abstract class XBuilder extends AbstractMojo {

    /**
     * 加密算法名称
     */
    @Parameter(property = "xjar.algorithm", required = true, defaultValue = "AES")
    private String algorithm;

    /**
     * 加密密钥长度
     */
    @Parameter(property = "xjar.keySize", required = true, defaultValue = "128")
    private int keySize;

    /**
     * 加密向量长度
     */
    @Parameter(property = "xjar.ivSize", required = true, defaultValue = "128")
    private int ivSize;

    /**
     * 加密密码
     */
    @Parameter(property = "xjar.password", required = true)
    private String password;

    /**
     * 原本JAR所在文件夹
     */
    @Parameter(property = "xjar.originalDir", required = true, defaultValue = "${project.build.directory}")
    private File originalDir;

    /**
     * 原本JAR名称
     */
    @Parameter(property = "xjar.originalJar", required = true, defaultValue = "${project.build.finalName}.jar")
    private String originalJar;

    /**
     * 生成JAR所在文件夹
     */
    @Parameter(property = "xjar.generateDir", required = true, defaultValue = "${project.build.directory}")
    private File generateDir;

    /**
     * 生成JAR名称
     */
    @Parameter(property = "xjar.generateJar", required = true, defaultValue = "${project.build.finalName}.xjar")
    private String generateJar;

    /**
     * 包含资源，避免和excludes配置一起使用，如果混合使用则excludes失效。
     * 使用Ant表达式，例如：
     * io/xjar/**
     * BOOT-INF/classes/**
     * BOOT-INF/lib/*.jar
     */
    @Parameter(property = "xjar.includes")
    private String[] includes;

    /**
     * 排除资源，避免和includes配置一起使用，如果混合使用则excludes失效。
     * 使用Ant表达式，例如：
     * io/xjar/**
     * BOOT-INF/classes/**
     * BOOT-INF/lib/*.jar
     */
    @Parameter(property = "xjar.excludes")
    private String[] excludes;

    @Parameter(readonly = true, defaultValue = "${project.packaging}")
    private String packaging;

    public void execute() throws MojoExecutionException {
        System.out.println(packaging);
        if (!"jar".equalsIgnoreCase(packaging)) {
            return;
        }
        Log log = getLog();
        try {
            if (log.isDebugEnabled()) {
                log.debug("Using algorithm: " + algorithm);
                log.debug("Using key-size: " + keySize);
                log.debug("Using iv-size: " + ivSize);
                log.debug("Using password: " + algorithm);
            }
            File src = new File(originalDir, originalJar);
            File dest = new File(generateDir, generateJar);
            log.info("Building xjar: " + dest + " for jar: " + src);
            XEntryFilter<JarArchiveEntry> filter;
            if (includes != null && includes.length > 0) {
                XAnyEntryFilter<JarArchiveEntry> xIncludesFilter = XEntryFilters.any();
                for (int i = 0; includes != null && i < includes.length; i++) {
                    xIncludesFilter.mix(new XIncludeAntEntryFilter(includes[i]));
                    log.info("Including " + includes[i]);
                }
                filter = xIncludesFilter;
            } else if (excludes != null && excludes.length > 0) {
                XAllEntryFilter<JarArchiveEntry> xExcludesFilter = XEntryFilters.all();
                for (int i = 0; excludes != null && i < excludes.length; i++) {
                    xExcludesFilter.mix(new XExcludeAntEntryFilter(excludes[i]));
                    log.info("Excluding " + excludes[i]);
                }
                filter = xExcludesFilter;
            } else {
                filter = null;
                log.info("Including all resources");
            }
            build(src, dest, password, algorithm, keySize, ivSize, filter);
        } catch (Exception e) {
            throw new MojoExecutionException("could not build xjar", e);
        }
    }

    /**
     * 构建XJar包
     *
     * @param src       原文包
     * @param dest      加密包
     * @param password  密码
     * @param algorithm 加密算法
     * @param keySize   密钥长度
     * @param ivSize    向量长度
     * @param filter    过滤器
     * @throws Exception 加密异常
     */
    protected abstract void build(File src, File dest, String password, String algorithm, int keySize, int ivSize, XEntryFilter<JarArchiveEntry> filter) throws Exception;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public int getIvSize() {
        return ivSize;
    }

    public void setIvSize(int ivSize) {
        this.ivSize = ivSize;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public File getOriginalDir() {
        return originalDir;
    }

    public void setOriginalDir(File originalDir) {
        this.originalDir = originalDir;
    }

    public String getOriginalJar() {
        return originalJar;
    }

    public void setOriginalJar(String originalJar) {
        this.originalJar = originalJar;
    }

    public File getGenerateDir() {
        return generateDir;
    }

    public void setGenerateDir(File generateDir) {
        this.generateDir = generateDir;
    }

    public String getGenerateJar() {
        return generateJar;
    }

    public void setGenerateJar(String generateJar) {
        this.generateJar = generateJar;
    }

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }
}
