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

package com.stepstone.sonar.plugin.coldfusion;

import com.stepstone.sonar.plugin.coldfusion.cflint.CFLintAnalysisResultImporter;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFLintAnalyzer;
import com.stepstone.sonar.plugin.coldfusion.cflint.CFLintConfigExporter;

import com.google.common.base.Preconditions;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLStreamException;

public class ColdFusionSensor implements Sensor {

    private final FileSystem fs;
    private final ActiveRules ruleProfile;
    private final Logger LOGGER = Loggers.get(ColdFusionSensor.class);

    public ColdFusionSensor(FileSystem fs, ActiveRules ruleProfile) {
        Preconditions.checkNotNull(fs);
        Preconditions.checkNotNull(ruleProfile);

        this.fs = fs;
        this.ruleProfile = ruleProfile;
    }


    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.onlyOnLanguage(ColdFusionPlugin.LANGUAGE_KEY);
        descriptor.createIssuesForRuleRepository(ColdFusionPlugin.REPOSITORY_KEY);
    }

    @Override
    public void execute(SensorContext context) {
        try {
            analyze(context);
            importResults(context);
            measureProcessor(context);
        } catch (IOException | XMLStreamException e) {
            LOGGER.error("",e);
        }
    }

    private void analyze(SensorContext context) throws IOException, XMLStreamException {
        File configFile = generateCflintConfig();
        try {
            new CFLintAnalyzer(context).analyze(configFile);
        } finally {
            //when analysis is done we delete the created file
            deleteFile(configFile);
        }
    }

    private File generateCflintConfig() throws IOException, XMLStreamException {
        final File configFile = new File(fs.workDir(), "cflint-config.xml");
        new CFLintConfigExporter(ruleProfile.findByRepository(ColdFusionPlugin.REPOSITORY_KEY)).save(configFile);
        return configFile;
    }

    private void deleteFile(File configFile) throws IOException {
        if(configFile!= null){
           Files.deleteIfExists(configFile.toPath());
        }
    }

    private void importResults(SensorContext sensorContext) throws IOException {
        try {
            new CFLintAnalysisResultImporter(fs, sensorContext).parse(new File(fs.workDir(), "cflint-result.xml"));
        } catch (XMLStreamException e) {
            LOGGER.error(",e");
        } finally {
            deleteFile(new File(fs.workDir(), "cflint-result.xml"));
        }
    }

    private void measureProcessor(SensorContext context) {
        LOGGER.info("Starting measure processor");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<Callable<Integer>> callableTasks = new ArrayList<>();

        for (InputFile inputFile : fs.inputFiles(fs.predicates().hasLanguage(ColdFusionPlugin.LANGUAGE_KEY))) {
            Callable<Integer> callableTask = () -> {
                try {
                    metricsLinesCounter(inputFile, context);
                    return 1;
                } catch (IOException e) {
                    return 0;
                }
            };
            callableTasks.add(callableTask);
        }

        try {
            executorService.invokeAll(callableTasks);
            executorService.shutdown();
            executorService.awaitTermination(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            LOGGER.error("",e);
        }

        LOGGER.info("Measure processor done");
    }

    //Very basic and naive line of code counter for Coldfusion
    //Might count a line of code as comment
    private void metricsLinesCounter(InputFile inputFile, SensorContext context) throws IOException {
        String currentLine;
        int commentLines = 0;
        int blankLines = 0;
        int lines = 0;
        int complexity = 1;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputFile.inputStream()))) {
            if (inputFile.inputStream() != null) {
                while ((currentLine = reader.readLine()) != null) {
                    lines++;
                    if (currentLine.contains("<!--")) {
                        commentLines++;
                        if (currentLine.contains("-->")) {
                            continue;
                        }
                        commentLines++;
                        lines++;
                        while (!(reader.readLine()).contains("-->")) {
                            lines++;
                            commentLines++;
                        }
                    } else if (currentLine.trim().isEmpty()) {
                        blankLines++;
                    }

                    complexity = getLineComplexity(currentLine, complexity);
                }
            }
        }
        int linesOfCode = lines-blankLines-commentLines;
        // every 100 lines of code add 1 to the content's complexity
        complexity = complexity + (linesOfCode / 100);


        context.<Integer>newMeasure().forMetric(CoreMetrics.COMMENT_LINES).on(inputFile).withValue(commentLines).save();
        context.<Integer>newMeasure().forMetric(CoreMetrics.NCLOC).on(inputFile).withValue(linesOfCode).save();
        context.<Integer>newMeasure().forMetric(CoreMetrics.LINES).on(inputFile).withValue(lines).save();
        context.<Integer>newMeasure().forMetric(CoreMetrics.COMPLEXITY).on(inputFile).withValue(complexity).save();
    }

    private int getLineComplexity(String currentLine, int complexity) {
        int mcCabeComplexity =0;
        int lineByLineComplexity = 0;
        int lineByLineComplexityIncrement = 4;
        int thisLineComplexityAdd = 0;
        int thisLineComplexitySubtract = 0;

        // SCORE INCREMENTS
        mcCabeComplexity += countRegexOccurrences(currentLine, "(<cfif\\s|<cfelseif\\s|<cfcase\\s|<cfloop\\s|<cfoutput\\s*query|iif\\s*\\()");
        mcCabeComplexity += countRegexOccurrences(currentLine, "(\\b(if|for|while|do|foreach)\\s*\\(|\\scase\\s+[\\w\"\"\\s]+:)");

        thisLineComplexityAdd = countRegexOccurrences(currentLine, "(<cfif\\s|<cfelseif\\s|<cfcase\\s|<cfloop\\s|<cfoutput\\s*query|\\biif\\s*\\()") * lineByLineComplexityIncrement;
        // TODO: account for script {braces} as well as non-braced if(?)do;else do;
        // The current implementation just counts any opening and closing braces. That's Cheating (and gives inaccurate readings).
        thisLineComplexityAdd += countRegexOccurrences(currentLine,"\\{") * lineByLineComplexityIncrement;

        lineByLineComplexity += thisLineComplexityAdd;

        // SCORE DECREMENTS
        // Assume iif closes itself on the same line it opens
        thisLineComplexitySubtract = (currentLine.split("(</cfif|</cfcase|</cfloop|iif\\s*\\()").length  - 1) * lineByLineComplexityIncrement;
        thisLineComplexitySubtract += countRegexOccurrences(currentLine,"\\}") * lineByLineComplexityIncrement;
        lineByLineComplexity -=  thisLineComplexitySubtract;

        complexity += mcCabeComplexity + lineByLineComplexity;
        return complexity;
    }

    private int countRegexOccurrences(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        int matches = 0;
        while (matcher.find()) {
            matches = matches + 1;
        }
        return matches;
    }

}

