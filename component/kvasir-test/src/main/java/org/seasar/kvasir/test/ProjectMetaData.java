package org.seasar.kvasir.test;

import junit.framework.TestCase;

import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.impl.FileResource;

public class ProjectMetaData {
    private boolean useKvasirEclipsePluginTestEnvironmentIfPossible_;

    private Resource projectDirectory_;

    private Resource testClassesDirectory_;

    private Resource testResourcesSourceDirectory_;

    private Resource testHomeDirectory_;

    private Resource testHomeSourceDirectory_;

    private Resource mavenClassesDirectory_;

    private Resource mavenTestHomeSourceDirectory_;

    private Resource mavenTestHomeDirectory_;

    private Resource classesDirectory_;

    private boolean runningFromMaven2_;

    private boolean kvasirEclipsePluginProject_;

    public ProjectMetaData(Class<? extends TestCase> clazz) {
        this(clazz, true);
    }

    public ProjectMetaData(Class<? extends TestCase> clazz,
            boolean useKvasirEclipsePluginTestEnvironmentIfPossible) {
        this(new FileResource(ClassUtils.getBaseDirectory(clazz)),
                useKvasirEclipsePluginTestEnvironmentIfPossible);
    }

    public ProjectMetaData(Resource testClassesDirectory) {
        this(testClassesDirectory, true);
    }

    public ProjectMetaData(Resource testClassesDirectory,
            boolean useKvasirEclipsePluginTestEnvironmentIfPossible) {
        useKvasirEclipsePluginTestEnvironmentIfPossible_ = useKvasirEclipsePluginTestEnvironmentIfPossible;
        prepare(testClassesDirectory);
    }

    void prepare(Resource testClassesDirectory) {
        testClassesDirectory_ = testClassesDirectory;
        runningFromMaven2_ = "target".equals(testClassesDirectory
                .getParentResource().getName());
        if (runningFromMaven2_) {
            kvasirEclipsePluginProject_ = false;
        } else {
            kvasirEclipsePluginProject_ = testClassesDirectory
                    .getParentResource().getChildResource("webapp/kvasir")
                    .exists();
        }
        if (kvasirEclipsePluginProject_
                && useKvasirEclipsePluginTestEnvironmentIfPossible_) {
            testHomeDirectory_ = testClassesDirectory.getParentResource()
                    .getChildResource("webapp/kvasir");
        } else {
            testHomeDirectory_ = testClassesDirectory.getParentResource()
                    .getChildResource("test-home");
        }

        // Maven2の場合（TOP_PROJECT/MODULE/target/test-classes）。
        // Eclipse+手動の場合（TOP_PROJECT/MODULE/build/test-classes）。
        Resource projectDirectory = testClassesDirectory.getParentResource()
                .getParentResource();

        projectDirectory_ = projectDirectory;
        testHomeSourceDirectory_ = projectDirectory_
                .getChildResource("src/test/test-home");
        testResourcesSourceDirectory_ = projectDirectory_
                .getChildResource("src/test/resources");
        mavenTestHomeSourceDirectory_ = projectDirectory_
                .getChildResource("src/test/test-home");
        mavenTestHomeDirectory_ = projectDirectory_
                .getChildResource("target/test-home");
        mavenClassesDirectory_ = projectDirectory_
                .getChildResource("target/classes");
        classesDirectory_ = testClassesDirectory.getParentResource()
                .getChildResource("classes");
    }

    public boolean isUseKvasirEclipsePluginTestEnvironmentIfPossible() {
        return useKvasirEclipsePluginTestEnvironmentIfPossible_;
    }

    public boolean isRunningFromMaven2() {
        return runningFromMaven2_;
    }

    public boolean isKvasirEclipsePluginProject() {
        return kvasirEclipsePluginProject_;
    }

    public Resource getMavenClassesDirectory() {
        return mavenClassesDirectory_;
    }

    public Resource getMavenTestHomeDirectory() {
        return mavenTestHomeDirectory_;
    }

    public Resource getMavenTestHomeSourceDirectory() {
        return mavenTestHomeSourceDirectory_;
    }

    public Resource getProjectDirectory() {
        return projectDirectory_;
    }

    public Resource getTestClassesDirectory() {
        return testClassesDirectory_;
    }

    public Resource getTestHomeDirectory() {
        return testHomeDirectory_;
    }

    public Resource getTestHomeSourceDirectory() {
        return testHomeSourceDirectory_;
    }

    public Resource getTestResourcesSourceDirectory() {
        return testResourcesSourceDirectory_;
    }

    public Resource getClassesDirectory() {
        return classesDirectory_;
    }
}
