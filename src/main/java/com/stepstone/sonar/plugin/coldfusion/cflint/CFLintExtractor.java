package com.stepstone.sonar.plugin.coldfusion.cflint;

import org.sonar.api.batch.InstantiationStrategy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.google.common.base.Preconditions.checkNotNull;

@InstantiationStrategy(InstantiationStrategy.PER_BATCH)
public class CFLintExtractor {

    private static final String CFLINT_RESOURCE = "/META-INF/runner/cflint.jar";
    private static final String CFLINT = "runner/cflint.jar";

    private final File workDir;

    public CFLintExtractor(File workDir) {
        checkNotNull(workDir);

        this.workDir = workDir;
    }

    public File extract() throws IOException {
        File cflintJar = getFile();

        try (InputStream input = getClass().getResourceAsStream(CFLINT_RESOURCE)) {
            Files.copy(input, cflintJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        return cflintJar;
    }

    private File getFile() throws IOException {
        final File cflintJar = new File(workDir, CFLINT);

        mkdirs(cflintJar.getParentFile());

        return cflintJar;
    }

    private void mkdirs(File directory) throws IOException {
        if (!directory.exists()) {
            final boolean mkdirs = directory.mkdirs();

            if (!mkdirs) {
                throw new IOException("Cannot create directory: " + directory);
            }
        }
    }

}
