package io.xjar;

import io.xjar.jar.XJar;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

/**
 * XJar - 原生 JAR 加密插件
 *
 * @author Payne 646742615@qq.com
 * 2018/12/4 14:02
 */
@Mojo(name = "jar", defaultPhase = LifecyclePhase.PACKAGE)
public class XJarBuilder extends XBuilder {

    @Override
    protected void build(File src, File dest, String password, String algorithm, int keySize, int ivSize, XEntryFilter<JarArchiveEntry> filter) throws Exception {
        XJar.encrypt(src, dest, password, algorithm, keySize, ivSize, filter);
    }

}
