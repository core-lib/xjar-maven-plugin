package io.xjar;

import io.loadkit.Loaders;
import io.loadkit.Resource;
import io.xjar.boot.XBoot;
import io.xjar.filter.XAllEntryFilter;
import io.xjar.filter.XAnyEntryFilter;
import io.xjar.filter.XMixEntryFilter;
import io.xjar.jar.XJar;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

/**
 * XJar 构建插件
 *
 * @author Payne 646742615@qq.com
 * 2018/12/4 10:27
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.PACKAGE)
public class XBuilder extends AbstractMojo {
    /**
     * 当前Maven工程
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

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
     * 加密模式
     */
    @Parameter(property = "xjar.mode", required = true, defaultValue = "0")
    private int mode;

    /**
     * 加密密码
     */
    @Parameter(property = "xjar.password", required = true)
    private String password;

    /**
     * 原本JAR所在文件夹
     */
    @Parameter(property = "xjar.sourceDir", required = true, defaultValue = "${project.build.directory}")
    private File sourceDir;

    /**
     * 原本JAR名称
     */
    @Parameter(property = "xjar.sourceJar", required = true, defaultValue = "${project.build.finalName}.jar")
    private String sourceJar;

    /**
     * 生成JAR所在文件夹
     */
    @Parameter(property = "xjar.targetDir", required = true, defaultValue = "${project.build.directory}")
    private File targetDir;

    /**
     * 生成JAR名称
     */
    @Parameter(property = "xjar.targetJar", required = true, defaultValue = "${project.build.finalName}.xjar")
    private String targetJar;

    /**
     * 包含资源，避免和excludes配置一起使用，如果混合使用则excludes失效。
     * 使用Ant表达式，例如：
     * io/xjar/**
     * com/company/project/**
     * mapper/*Mapper.xml
     */
    @Parameter(property = "xjar.includes")
    private String[] includes;

    /**
     * 排除资源，避免和includes配置一起使用，如果混合使用则excludes失效。
     * 使用Ant表达式，例如：
     * io/xjar/**
     * static/**
     * META-INF/resources/**
     */
    @Parameter(property = "xjar.excludes")
    private String[] excludes;

    /**
     * 加密完成后需要删除的资源
     * 支持Ant表达式，例如：
     * target/*.jar
     * ../module/target/*.jar
     */
    @Parameter(property = "xjar.deletes")
    private String[] deletes;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        String packaging = project.getPackaging();
        if (!"jar".equalsIgnoreCase(packaging)) {
            log.info("Skip for packaging: " + packaging);
            return;
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("Using algorithm: " + algorithm);
                log.debug("Using key-size: " + keySize);
                log.debug("Using iv-size: " + ivSize);
                log.debug("Using password: " + password);
                log.debug("Using mode: " + mode);
            }
            File src = new File(sourceDir, sourceJar);
            File dest = new File(targetDir, targetJar);
            File folder = dest.getParentFile();
            if (!folder.exists() && !folder.mkdirs() && !folder.exists()) {
                throw new IOException("could not make directory: " + folder);
            }
            log.info("Building xjar: " + dest + " for jar: " + src);

            XMixEntryFilter<JarArchiveEntry> filter;
            if (XArray.isEmpty(includes) && XArray.isEmpty(excludes)) {
                filter = null;
                log.info("Including all resources");
            } else {
                filter = XKit.all();
                if (!XArray.isEmpty(includes)) {
                    XAnyEntryFilter<JarArchiveEntry> including = XKit.any();
                    for (String include : includes) {
                        including.mix(new XIncludeAntEntryFilter(include));
                        log.info("Including " + include);
                    }
                    filter.mix(including);
                }
                if (!XArray.isEmpty(excludes)) {
                    XAllEntryFilter<JarArchiveEntry> excluding = XKit.all();
                    for (String exclude : excludes) {
                        excluding.mix(new XExcludeAntEntryFilter(exclude));
                        log.info("Excluding " + exclude);
                    }
                    filter.mix(excluding);
                }
            }

            Build build = project.getBuild();
            Map<String, Plugin> plugins = build.getPluginsAsMap();
            Plugin plugin = plugins.get("org.springframework.boot:spring-boot-maven-plugin");
            // 非Spring-Boot项目/模块
            if (plugin == null) {
                XJar.encrypt(src, dest, XKit.key(algorithm, keySize, ivSize, password), mode, filter);
            }
            // Spring-Boot项目/模块
            else {
                Object configuration = plugin.getConfiguration();
                // 不允许开启 <executable>true<executable>
                if (configuration instanceof Xpp3Dom) {
                    Xpp3Dom dom = (Xpp3Dom) configuration;
                    {
                        Xpp3Dom child = dom.getChild("executable");
                        String executable = child != null ? child.getValue() : null;
                        if ("true".equalsIgnoreCase(executable)) {
                            String msg = "Unsupported to build an xjar for an <executable>true</executable> spring boot JAR file, ";
                            msg += "maybe you should upgrade xjar-maven-plugin dependency if it have been supported in the later versions,";
                            msg += "if not, delete <executable>true</executable> or set executable as false for the configuration of spring-boot-maven-plugin.";
                            throw new MojoFailureException(msg);
                        }
                    }
                    {
                        Xpp3Dom child = dom.getChild("embeddedLaunchScript");
                        String embeddedLaunchScript = child != null ? child.getValue() : null;
                        if (embeddedLaunchScript != null) {
                            String msg = "Unsupported to build an xjar for an <embeddedLaunchScript>...</embeddedLaunchScript> spring boot JAR file, ";
                            msg += "maybe you should upgrade xjar-maven-plugin dependency if it have been supported in the later versions,";
                            msg += "if not, delete <embeddedLaunchScript>...</embeddedLaunchScript> for the configuration of spring-boot-maven-plugin.";
                            throw new MojoFailureException(msg);
                        }
                    }
                }
                XBoot.encrypt(src, dest, XKit.key(algorithm, keySize, ivSize, password), mode, filter);
            }

            File root = project.getFile().getParentFile();
            for (int i = 0; deletes != null && i < deletes.length; i++) {
                String delete = deletes[i];
                File dir = root;
                while (delete.startsWith("../")) {
                    dir = dir.getParentFile() != null ? dir.getParentFile() : dir;
                    delete = delete.substring("../".length());
                }

                log.info("Deleting file(s) matching pattern: " + dir.getCanonicalPath().replace('\\', '/') + "/" + delete);

                Enumeration<Resource> resources = Loaders.ant(Loaders.file(dir)).load(delete);
                while (resources.hasMoreElements()) {
                    Resource resource = resources.nextElement();
                    URL url = resource.getUrl();
                    String path = url.getPath();
                    File file = new File(path);
                    log.debug("Deleting file: " + file.getCanonicalPath());
                    if (!file.delete()) {
                        log.warn("Could not delete file: " + file);
                    }
                }
            }

        } catch (MojoExecutionException | MojoFailureException e) {
            throw e;
        } catch (Exception e) {
            throw new MojoExecutionException("could not build xjar", e);
        }
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public File getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(File sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getSourceJar() {
        return sourceJar;
    }

    public void setSourceJar(String sourceJar) {
        this.sourceJar = sourceJar;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }

    public String getTargetJar() {
        return targetJar;
    }

    public void setTargetJar(String targetJar) {
        this.targetJar = targetJar;
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

    public String[] getDeletes() {
        return deletes;
    }

    public void setDeletes(String[] deletes) {
        this.deletes = deletes;
    }
}
