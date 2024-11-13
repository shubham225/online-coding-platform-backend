package com.shubham.onlinetest.service.impl;

import com.shubham.onlinetest.model.enums.Language;
import com.shubham.onlinetest.service.CodeExecutorService;
import com.shubham.onlinetest.service.model.CodeExecutorResult;
import com.shubham.onlinetest.service.model.LanguageProperties;
import com.shubham.onlinetest.utils.PathUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CodeExecutorServiceImpl implements CodeExecutorService {
    @Override
    public CodeExecutorResult executeCode(String objectFile, String arguments, String execDirPath, LanguageProperties language) {
        CodeExecutorResult output = null;
        String action = language.getExecCommand() + " " + objectFile + " " + arguments;

        try {
            output = executeCodeInDocker(execDirPath, language.getDockerImage(), action,true);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return output;
    }

    @Override
    public CodeExecutorResult compileCode(String sourceFile, String arguments, String execDirPath, LanguageProperties language) {
        CodeExecutorResult output = null;
        String action = language.getCompileCommand() + " " + sourceFile + " " + arguments;

        try {
            output = executeCodeInDocker(execDirPath, language.getDockerImage(), action,true);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return output;
    }

    private CodeExecutorResult executeCodeInDocker(String execDirPath,
                                                   String compiler,
                                                   String action,
                                                   boolean execFromWsl) throws IOException, InterruptedException {
        List<String> output;
        List<String> command = new ArrayList<>();

        if (execFromWsl) {
            command.add("wsl");
            execDirPath = PathUtils.getWslPath(execDirPath);
        }
        command.add("docker");
        command.add("run");
        command.add("--rm");
        command.add("-v");
        command.add(execDirPath + ":/app");
        command.add(compiler);
        command.add("/bin/bash");
        command.add("-c");
        command.add("\"cd /app && " + action + "\"");

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        process.waitFor(1, TimeUnit.MINUTES);

        int exitCode = process.exitValue();

        List<String> stdOutput = readInputStream(process.getInputStream());
        List<String> stdError = readInputStream(process.getErrorStream());

        output = (exitCode == 0) ? stdOutput : stdError;

        return new CodeExecutorResult(exitCode, output);
    }

    private List<String> readInputStream(InputStream inStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        List<String> output = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            output.add(line);
        }

        return output;
    }
}