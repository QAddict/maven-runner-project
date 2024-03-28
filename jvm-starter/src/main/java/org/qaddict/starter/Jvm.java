package org.qaddict.starter;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

import static java.io.File.pathSeparator;
import static java.lang.String.join;
import static java.util.Objects.requireNonNull;

public class Jvm {

    private final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    private final String command;
    private final List<String> vmArgs = new ArrayList<>(runtimeMXBean.getInputArguments());
    private String classPath = runtimeMXBean.getClassPath();
    private String mainClass;
    private final List<String> args = new ArrayList<>();

    private Jvm(String command) {
        this.command = command;
    }

    public static Jvm of(String command) {
        return new Jvm(requireNonNull(command, "Java executable must not be null."));
    }

    public static Jvm ofCurrent() {
        return of(ProcessHandle.current().info().command().orElseThrow(() -> new IllegalStateException("Unable to get current process executable.")));
    }

    public Jvm vmArgs(List<String> values) {
        vmArgs.clear();
        vmArgs.addAll(values);
        return this;
    }

    public Jvm vmArgs(String... values) {
        return vmArgs(List.of(values));
    }

    public Jvm classPath(String value) {
        classPath = requireNonNull(value, "Class path must not be null.");
        return this;
    }

    public Jvm classPath(List<String> classPath) {
        return classPath(join(pathSeparator, classPath));
    }

    public Jvm classPathOf(String... classPath) {
        return classPath(join(pathSeparator, classPath));
    }

    public Jvm mainClass(String value) {
        mainClass = value;
        return this;
    }

    public Jvm args(List<String> values) {
        args.clear();
        args.addAll(values);
        return this;
    }

    public Jvm args(String... values) {
        return args(List.of(values));
    }

    public Process start() throws IOException {
        List<String> commandLine = new ArrayList<>(4 + vmArgs.size() + args.size());
        commandLine.add(command);
        commandLine.addAll(vmArgs);
        commandLine.add("-cp");
        commandLine.add(classPath);
        commandLine.add(requireNonNull(mainClass, "Main class not specified. Use Jvm.mainClass(\"example.MainClass\") to specify it."));
        commandLine.addAll(args);
        System.out.println("Executing: " + join(" ", commandLine));
        return new ProcessBuilder().inheritIO().command(commandLine).start();
    }

    public int startAndWaitFor() throws IOException, InterruptedException {
        return start().waitFor();
    }

}
