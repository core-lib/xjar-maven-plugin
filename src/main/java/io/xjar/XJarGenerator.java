package io.xjar;

import io.xjar.boot.XBoot;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;

/**
 * Spring-Boot XJar 生成 Maven 插件
 *
 * @author Payne 646742615@qq.com
 * 2018/12/4 14:02
 */
@Mojo(name = "generate-jar")
public class XJarGenerator extends XGenerator {

    @Override
    protected void generate(File src, File dest, String password, String algorithm, int keySize, int ivSize, XEntryFilter<JarArchiveEntry> filter) throws Exception {
        XBoot.encrypt(src, dest, password, algorithm, keySize, ivSize, filter);
    }

}
