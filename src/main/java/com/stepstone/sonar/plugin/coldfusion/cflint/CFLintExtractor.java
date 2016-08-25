/*
Copyright 2016 StepStone GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

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
