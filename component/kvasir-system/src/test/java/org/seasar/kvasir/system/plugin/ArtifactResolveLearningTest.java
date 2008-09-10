package org.seasar.kvasir.system.plugin;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderConsoleLogger;
import org.codehaus.plexus.util.FileUtils;
import org.seasar.framework.util.ResourceUtil;


public class ArtifactResolveLearningTest extends TestCase
{

    private File classFile_;


    protected void setUp()
        throws Exception
    {
        super.setUp();
        classFile_ = ResourceUtil.getResourceAsFile(
            ArtifactResolveLearningTest.class.getName(), "class");
    }


    /**
     * バージョン番号を指定して、artifactをダウンロードする。
     */
    public void testDownload()
        throws Exception
    {
        // ## Arrange ##
        final File remoteRepo = new File(classFile_.getParentFile(), "remote-1");
        final File localRepo = new File(classFile_.getParentFile(), "local-1");
        FileUtils.deleteDirectory(localRepo);
        assertEquals(false, localRepo.exists());

        // ## Act ##
        final MavenEmbedder maven = new MavenEmbedder();
        maven.setClassLoader(Thread.currentThread().getContextClassLoader());
        maven.setLogger(new MavenEmbedderConsoleLogger());
        maven.start();

        final ArtifactRepository localRepository = maven
            .createLocalRepository(localRepo);
        final ArtifactRepository remoteRepository = maven.createRepository(
            remoteRepo.toURI().toURL().toExternalForm(), "testingRepo");
        System.out.println("localRepository=" + localRepository);
        System.out.println("remoteRepository=" + remoteRepository);

        final Artifact artifact = maven.createArtifact("com.example",
            "com.example", "1.0.0", null, "jar");
        assertEquals(false, localRepo.exists());
        final File downloaded = new File(localRepo,
            "com/example/com.example/1.0.0/com.example-1.0.0.jar");
        assertEquals(false, downloaded.exists());
        final ArtifactRepository[] remoteRepositories = new ArtifactRepository[] { remoteRepository };
        maven.resolve(artifact, Arrays.asList(remoteRepositories),
            localRepository);
        maven.stop();

        // ## Assert ##
        assertEquals(true, downloaded.exists());
    }


    /**
     * 存在しないgroupIdへのMavenEmbedder#resolveはArtifactNotFoundExceptionが
     * 投げられること。
     */
    public void testNoGroupId()
        throws Exception
    {
        assertResolveFailure("badGroupId", "com.example", "1.0.0", null, "jar");
    }


    /**
     * 存在しないartifactIdへのMavenEmbedder#resolveはArtifactNotFoundExceptionが
     * 投げられること。
     */
    public void testNoArtifactId()
        throws Exception
    {
        assertResolveFailure("com.example", "badArtifactId", "1.0.0", null,
            "jar");
    }


    /**
     * 指定したバージョンが存在しない場合はArtifactNotFoundExceptionが
     * 投げられること。
     */
    public void testNoVersion()
        throws Exception
    {
        assertResolveFailure("com.example", "com.example", "2.0.0", null, "jar");
    }


    public void testNoType()
        throws Exception
    {
        assertResolveFailure("com.example", "com.example", "1.0.0", null, "war");
    }


    private void assertResolveFailure(final String groupId,
        final String artifactId, final String version, final String scope,
        final String type)
        throws Exception
    {

        // ## Arrange ##
        final File remoteRepo = new File(classFile_.getParentFile(), "remote-1");
        final File localRepo = new File(classFile_.getParentFile(), "local-1");
        FileUtils.deleteDirectory(localRepo);
        assertEquals(false, localRepo.exists());

        // ## Act ##
        final MavenEmbedder maven = new MavenEmbedder();
        maven.setClassLoader(Thread.currentThread().getContextClassLoader());
        maven.setLogger(new MavenEmbedderConsoleLogger());
        maven.start();

        final ArtifactRepository localRepository = maven
            .createLocalRepository(localRepo);
        final ArtifactRepository remoteRepository = maven.createRepository(
            remoteRepo.toURI().toURL().toExternalForm(), "testingRepo");
        System.out.println("localRepository=" + localRepository);
        System.out.println("remoteRepository=" + remoteRepository);

        final Artifact artifact = maven.createArtifact(groupId, artifactId,
            version, scope, type);
        assertEquals(false, localRepo.exists());
        final ArtifactRepository[] remoteRepositories = new ArtifactRepository[] { remoteRepository };

        // ## Assert ##
        try {
            maven.resolve(artifact, Arrays.asList(remoteRepositories),
                localRepository);
            fail();
        } catch (final ArtifactNotFoundException e) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            //System.out.println(sw.toString());
        }
        maven.stop();
    }


    /**
     * バージョン番号に"RELEASE"を指定して、artifactをダウンロードする。
     * 
     * リモートレポジトリにある"maven-metadata.xml"のrelease要素に書かれた
     * バージョンが取得される。
     */
    public void testGetReleaseVersion()
        throws Exception
    {
        // ## Arrange ##
        final File remoteRepo = new File(classFile_.getParentFile(), "remote-2");
        final File localRepo = new File(classFile_.getParentFile(), "local-1");
        FileUtils.deleteDirectory(localRepo);
        assertEquals(false, localRepo.exists());

        // ## Act ##
        final MavenEmbedder maven = new MavenEmbedder();
        maven.setClassLoader(Thread.currentThread().getContextClassLoader());
        maven.setLogger(new MavenEmbedderConsoleLogger());
        maven.start();

        final ArtifactRepository localRepository = maven
            .createLocalRepository(localRepo);
        final String remoteRepoId = "testingRemoteRepo";
        final ArtifactRepository remoteRepository = maven.createRepository(
            remoteRepo.toURI().toURL().toExternalForm(), remoteRepoId);
        System.out.println("localRepository=" + localRepository);
        System.out.println("remoteRepository=" + remoteRepository);

        final Artifact artifact = maven.createArtifact("com.example",
            "com.example", Artifact.RELEASE_VERSION, null, "jar");
        assertEquals(false, localRepo.exists());
        final File downloadedJar = new File(localRepo,
            "com/example/com.example/1.0.1/com.example-1.0.1.jar");
        final File downloadedMetadata = new File(localRepo,
            "com/example/com.example/maven-metadata-" + remoteRepoId + ".xml");
        assertEquals(false, downloadedJar.exists());
        assertEquals(false, downloadedMetadata.exists());

        final ArtifactRepository[] remoteRepositories = new ArtifactRepository[] { remoteRepository };
        maven.resolve(artifact, Arrays.asList(remoteRepositories),
            localRepository);
        maven.stop();

        // ## Assert ##
        assertEquals(true, downloadedJar.exists());
        assertEquals(true, downloadedMetadata.exists());
    }


    /*
     * これはfailする。
     * ローカルレポジトリにmetadata XMLファイルが必要そうに見える。
     * 詳しくは追っていない。
     */
    public void no_testLatest()
        throws Exception
    {
        // ## Arrange ##
        final File remoteRepo = new File(classFile_.getParentFile(), "remote-2");
        final File localRepo = new File(classFile_.getParentFile(), "local-1");
        FileUtils.deleteDirectory(localRepo);
        assertEquals(false, localRepo.exists());

        // ## Act ##
        final MavenEmbedder maven = new MavenEmbedder();
        maven.setClassLoader(Thread.currentThread().getContextClassLoader());
        maven.setLogger(new MavenEmbedderConsoleLogger());
        maven.start();

        final ArtifactRepository localRepository = maven
            .createLocalRepository(localRepo);
        final ArtifactRepository remoteRepository = maven.createRepository(
            remoteRepo.toURI().toURL().toExternalForm(), "testingRepo");
        System.out.println("localRepository=" + localRepository);
        System.out.println("remoteRepository=" + remoteRepository);

        final Artifact artifact = maven.createArtifact("com.example",
            "com.example", "LATEST", null, "jar");
        assertEquals(false, localRepo.exists());
        final File downloaded = new File(localRepo,
            "com/example/com.example/1.0.1/com.example-1.0.1.jar");
        assertEquals(false, downloaded.exists());
        final ArtifactRepository[] remoteRepositories = new ArtifactRepository[] { remoteRepository };
        maven.resolve(artifact, Arrays.asList(remoteRepositories),
            localRepository);
        maven.stop();

        // ## Assert ##
        assertEquals(true, downloaded.exists());
    }

}
