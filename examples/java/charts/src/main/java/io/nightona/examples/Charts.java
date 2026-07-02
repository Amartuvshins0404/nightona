// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.examples;

import io.nightona.sdk.CodeInterpreter;
import io.nightona.sdk.Nightona;
import io.nightona.sdk.Image;
import io.nightona.sdk.RunCodeOptions;
import io.nightona.sdk.Sandbox;
import io.nightona.sdk.model.CreateSandboxFromImageParams;
import io.nightona.sdk.model.ExecuteResponse;
import io.nightona.toolbox.client.model.Chart;
import io.nightona.toolbox.client.model.CodeRunArtifacts;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class Charts {
    public static void main(String[] args) {
        try (Nightona nightona = new Nightona()) {
            CreateSandboxFromImageParams params = new CreateSandboxFromImageParams();
            params.setImage(Image.debianSlim("3.13").pipInstall("matplotlib", "numpy"));
            params.setLanguage("python");

            System.out.println("Creating Python sandbox with matplotlib");
            Sandbox sandbox = nightona.create(params, 300);

            try {
                System.out.println("\n=== CodeInterpreter.runCode (with streaming callbacks) ===");
                CodeInterpreter.ExecutionResult result = sandbox.codeInterpreter.runCode(CODE,
                        new RunCodeOptions()
                                .setOnStdout(chunk -> System.out.print("[stdout] " + chunk))
                                .setOnStderr(chunk -> System.out.print("[stderr] " + chunk))
                );
                if (result.getError() != null) {
                    System.out.println("Error: " + result.getError().getValue());
                }

                System.out.println("\n\n=== Process.codeRun ===");
                ExecuteResponse processResult = sandbox.process.codeRun(CODE);
                System.out.println("Exit code: " + processResult.getExitCode());
                System.out.println("Clean result: " + processResult.getResult());

                Path outputDir = Paths.get(System.getProperty("user.dir"));
                CodeRunArtifacts artifacts = processResult.getArtifacts();
                List<Chart> charts = artifacts != null && artifacts.getCharts() != null ? artifacts.getCharts() : List.of();
                int chartIndex = 0;
                for (Chart chart : charts) {
                    String title = chart.getTitle() != null && !chart.getTitle().isEmpty() ? chart.getTitle() : "chart_" + chartIndex;
                    String png = chart.getPng();

                    System.out.println("Chart type: " + chart.getType());
                    System.out.println("Chart title: " + title);
                    System.out.println("Chart elements: " + (chart.getElements() != null ? chart.getElements().size() : 0));

                    if (png != null && !png.isEmpty()) {
                        String filename = title.replaceAll("[^a-zA-Z0-9_-]", "_") + ".png";
                        Path dest = outputDir.resolve(filename);
                        try (FileOutputStream fos = new FileOutputStream(dest.toFile())) {
                            fos.write(Base64.getDecoder().decode(png));
                        }
                        System.out.println("Saved chart: " + dest);
                    }
                    chartIndex++;
                }
                System.out.println("Total charts saved: " + chartIndex);
            } finally {
                System.out.println("Deleting sandbox");
                sandbox.delete();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static final String CODE = String.join("\n",
            "import matplotlib.pyplot as plt",
            "import numpy as np",
            "",
            "x = np.linspace(0, 10, 30)",
            "y = np.sin(x)",
            "",
            "plt.figure(figsize=(8, 5))",
            "plt.plot(x, y, 'b-', linewidth=2)",
            "plt.title('Line Chart')",
            "plt.xlabel('X-axis')",
            "plt.ylabel('Y-axis')",
            "plt.grid(True)",
            "plt.show()",
            "",
            "plt.figure(figsize=(8, 5))",
            "plt.scatter(x, y, c=y, cmap='viridis', s=100*np.abs(y))",
            "plt.colorbar(label='Value')",
            "plt.title('Scatter Plot')",
            "plt.xlabel('X-axis')",
            "plt.ylabel('Y-axis')",
            "plt.show()",
            "",
            "categories = ['A', 'B', 'C', 'D', 'E']",
            "values = [40, 63, 15, 25, 8]",
            "plt.figure(figsize=(10, 6))",
            "plt.bar(categories, values, color='skyblue', edgecolor='navy')",
            "plt.title('Bar Chart')",
            "plt.xlabel('Categories')",
            "plt.ylabel('Values')",
            "plt.show()"
    );
}
