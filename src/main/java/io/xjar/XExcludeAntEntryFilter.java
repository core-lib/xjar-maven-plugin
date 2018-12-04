package io.xjar;

import org.apache.commons.compress.archivers.jar.JarArchiveEntry;

/**
 * 排除过滤器
 *
 * @author Payne 646742615@qq.com
 * 2018/12/4 14:10
 */
public class XExcludeAntEntryFilter extends XAntEntryFilter implements XEntryFilter<JarArchiveEntry> {

    public XExcludeAntEntryFilter(String ant) {
        super(ant);
    }

    @Override
    public boolean filtrate(JarArchiveEntry entry) {
        return !matches(entry.getName());
    }
}
