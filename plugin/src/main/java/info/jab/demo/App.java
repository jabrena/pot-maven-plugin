package info.jab.demo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * POT Maven Plugin - Programming Overload Therapy
 * This plugin provides a simple way to enhance your project's capabilities.
 */
@Mojo(name = "enhance", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class App extends AbstractMojo {

    /**
     * Message to display when the mojo executes
     */
    @Parameter(property = "pot.message", defaultValue = "Programming Overload Therapy active!")
    private String message;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("POT Maven Plugin executing...");
        getLog().info(message);
        getLog().info("POT Maven Plugin completed successfully!");
    }
}
