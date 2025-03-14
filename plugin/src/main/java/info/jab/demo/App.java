package info.jab.demo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * POT Maven Plugin - Programming Overload Therapy
 * This plugin provides a simple way to enhance your project's capabilities.
 */
@Mojo(name = "enhance", defaultPhase = LifecyclePhase.VERIFY)
public class App extends AbstractMojo {

    /**
     * Message to display when the mojo executes
     */
    @Parameter(property = "pot.message", defaultValue = "Programming Overload Therapy active!")
    private String message;

    /**
     * The Maven project this plugin is being used on
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("POT Maven Plugin executing...");
        getLog().info(message);
        
        try {
            // Count the test files
            TestCountResult testCounts = countTestFiles();
            
            // Log the test counts
            getLog().info("Test Count Summary:");
            getLog().info("------------------");
            getLog().info("Unit Tests (*Test.java): " + testCounts.getUnitTestCount());
            getLog().info("Integration Tests (*IT.java): " + testCounts.getIntegrationTestCount());
            getLog().info("Total Tests: " + (testCounts.getUnitTestCount() + testCounts.getIntegrationTestCount()));
            getLog().info("------------------");
        } catch (Exception e) {
            getLog().error("Error counting test files", e);
            throw new MojoExecutionException("Error counting test files", e);
        }
        
        getLog().info("POT Maven Plugin completed successfully!");
    }
    
    /**
     * Counts the unit tests and integration tests in the project.
     * 
     * @return TestCountResult containing the counts
     */
    private TestCountResult countTestFiles() throws MojoExecutionException {
        AtomicInteger unitTestCount = new AtomicInteger(0);
        AtomicInteger integrationTestCount = new AtomicInteger(0);
        
        Path testSourceDirectory = Paths.get(project.getBasedir().getAbsolutePath(), "src", "test");
        
        if (!Files.exists(testSourceDirectory)) {
            getLog().info("Test directory not found: " + testSourceDirectory);
            return new TestCountResult(0, 0);
        }
        
        try {
            Files.walk(testSourceDirectory)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    String fileName = path.getFileName().toString();
                    
                    if (fileName.endsWith("Test.java")) {
                        unitTestCount.incrementAndGet();
                        getLog().debug("Found unit test: " + path);
                    } else if (fileName.endsWith("IT.java")) {
                        integrationTestCount.incrementAndGet();
                        getLog().debug("Found integration test: " + path);
                    }
                });
        } catch (Exception e) {
            throw new MojoExecutionException("Error walking test directory", e);
        }
        
        return new TestCountResult(unitTestCount.get(), integrationTestCount.get());
    }
    
    /**
     * Simple class to hold the test count results
     */
    private static class TestCountResult {
        private final int unitTestCount;
        private final int integrationTestCount;
        
        public TestCountResult(int unitTestCount, int integrationTestCount) {
            this.unitTestCount = unitTestCount;
            this.integrationTestCount = integrationTestCount;
        }
        
        public int getUnitTestCount() {
            return unitTestCount;
        }
        
        public int getIntegrationTestCount() {
            return integrationTestCount;
        }
    }
}
