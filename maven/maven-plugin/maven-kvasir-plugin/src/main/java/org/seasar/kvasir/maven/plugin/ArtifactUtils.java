package org.seasar.kvasir.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;


/**
 * @author YOKOTA Takehiko
 */
public class ArtifactUtils
{
    public static void copyPluginOuterLibraries(Log log, File pluginDirectory,
        String libraryDirectoryPath, Map libraryMap, Set artifactSet)
        throws IOException, ArtifactNotFoundException
    {
        Artifact[] artifacts = getMatchedArtifacts(log, artifactSet, libraryMap);
        if (log != null) {
            log.debug("libraryMap: " + libraryMap);
        }
        for (int i = 0; i < artifacts.length; i++) {
            Artifact artifact = artifacts[i];
            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();
            ArtifactPattern pattern = (ArtifactPattern)libraryMap.get(groupId
                + ":" + artifactId);
            if (pattern == null) {
                pattern = (ArtifactPattern)libraryMap.get(artifactId);
            }
            if (log != null) {
                log.debug("Pattern for artifact " + artifact.getArtifactId()
                    + ": " + pattern);
            }
            String dirPath = libraryDirectoryPath;
            String fileName = null;
            if (pattern != null) {
                String destination = pattern.getDestination();
                if (destination != null) {
                    int slash = destination.lastIndexOf('/');
                    if (slash >= 0) {
                        dirPath = destination.substring(0, slash);
                        String name = destination.substring(slash + 1);
                        if (name.length() > 0) {
                            fileName = name;
                        }
                    } else {
                        fileName = destination;
                    }
                }
            }
            File file = artifact.getFile();
            File dir = new File(pluginDirectory, dirPath);
            dir.mkdirs();
            if (log != null) {
                log.debug("COPY(2): copy " + artifact + " to " + dir + " as "
                    + (fileName != null ? fileName : file.getName()));
            }
            FileUtils.copyFileToDirectory(file, dir);
            if (fileName != null) {
                new File(dir, file.getName()).renameTo(new File(dir, fileName));
            }
        }
    }


    public static Artifact[] getMatchedArtifacts(Log log, MavenProject project,
        Map patternMap)
        throws ArtifactNotFoundException
    {
        return getMatchedArtifacts(log, project.getArtifacts(), patternMap);
    }


    public static Artifact[] getMatchedArtifacts(Log log, Set artifacts,
        Map patternMap)
        throws ArtifactNotFoundException
    {
        patternMap = new HashMap(patternMap);

        List list = new ArrayList(artifacts.size());
        Set matchedPatternSet = new HashSet();
        for (Iterator itr = artifacts.iterator(); itr.hasNext();) {
            Artifact artifact = (Artifact)itr.next();
            if (log != null) {
                log.debug("ARTIFACT: " + artifact + ":" + artifact.getScope());
            }
            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();
            String key;
            boolean matched = false;
            do {
                if (artifactId != null) {
                    if (groupId != null) {
                        key = groupId + ":" + artifactId;
                        if (artifact.hasClassifier()) {
                            key += "-" + artifact.getClassifier();
                        }
                        if (patternMap.containsKey(key)) {
                            matched = true;
                            break;
                        }
                    }
                    key = artifactId;
                    if (artifact.hasClassifier()) {
                        key += "-" + artifact.getClassifier();
                    }
                    if (patternMap.containsKey(key)) {
                        matched = true;
                        break;
                    }
                }
                key = null;
                if (patternMap.containsKey(key)) {
                    matched = true;
                    break;
                }
            } while (false);
            if (!matched) {
                continue;
            }
            ArtifactPattern pattern = (ArtifactPattern)patternMap.get(key);
            if (log != null) {
                log.debug("PATTERN: " + pattern);
            }
            String type = pattern.getType();
            if (type != null && !artifact.getType().equals(type)) {
                continue;
            }
            String version = pattern.getVersion();
            if (version != null && !artifact.getType().equals(version)) {
                continue;
            }
            String scope = pattern.getScope();
            if (scope != null
                && !adjustScope(artifact.getScope()).equals(scope)) {
                continue;
            }

            if (log != null) {
                log.debug("MATCHED: " + artifact);
            }
            if (key != null) {
                if (matchedPatternSet.contains(key)) {
                    throw new RuntimeException(
                        "Specified pattern is ambiguous: " + pattern);
                }
                matchedPatternSet.add(key);
                patternMap.remove(key);
            }
            list.add(artifact);
        }

        // patternMapはnullキーを持つことがあるが、
        // 未解決articleの存在をチェックする前にそれを削除しておく必要がある。
        for (Iterator itr = patternMap.keySet().iterator(); itr.hasNext();) {
            if (itr.next() == null) {
                itr.remove();
            }
        }
        if (patternMap.size() > 0) {
            StringBuffer sb = new StringBuffer("Article not found: ");
            boolean first = true;
            for (Iterator itr = patternMap.values().iterator(); itr.hasNext();) {
                ArtifactPattern pattern = (ArtifactPattern)itr.next();
                if (!first) {
                    sb.append(", ");
                } else {
                    first = false;
                }
                sb.append(pattern);
            }
            throw new ArtifactNotFoundException(sb.toString(),
                (ArtifactPattern[])patternMap.values().toArray(
                    new ArtifactPattern[0]));
        }

        return (Artifact[])list.toArray(new Artifact[0]);
    }


    static String adjustScope(String scope)
    {
        if (scope != null) {
            return scope;
        } else {
            return Artifact.SCOPE_COMPILE;
        }
    }


    public static Map parseLibraries(String libraries)
    {
        return parseLibraries(libraries, null);
    }


    public static Map parseLibraries(String libraries, String defaultType)
    {
        Map map = new HashMap();

        if (libraries == null) {
            return map;
        }

        StringTokenizer st = new StringTokenizer(libraries, ",");
        while (st.hasMoreTokens()) {
            String tkn = st.nextToken().trim();
            if (tkn.length() == 0) {
                continue;
            }
            String groupId;
            String artifactId;
            String type = null;
            String version = null;
            String destination = null;
            int atmark = tkn.indexOf('@');
            if (atmark >= 0) {
                destination = tkn.substring(atmark + 1);
                tkn = tkn.substring(0, atmark);
            }
            int colon = tkn.indexOf(':');
            if (colon < 0) {
                groupId = null;
                artifactId = tkn;
            } else {
                groupId = tkn.substring(0, colon).trim();
                if (groupId.length() == 0) {
                    groupId = null;
                }
                tkn = tkn.substring(colon + 1).trim();
                colon = tkn.indexOf(':');
                if (colon < 0) {
                    artifactId = tkn;
                } else {
                    artifactId = tkn.substring(0, colon).trim();
                    tkn = tkn.substring(colon + 1).trim();
                    colon = tkn.indexOf(':');
                    if (colon < 0) {
                        type = tkn;
                    } else {
                        type = tkn.substring(0, colon).trim();
                        version = tkn.substring(colon + 1).trim();
                        if (version.length() == 0) {
                            version = null;
                        }
                    }
                    if (type.length() == 0) {
                        type = null;
                    }
                }
            }
            if (type == null) {
                type = defaultType;
            }

            String key = (groupId != null) ? groupId + ":" + artifactId
                : artifactId;
            map.put(key, new ArtifactPattern(groupId, artifactId, type,
                version, null, destination));
        }
        return map;
    }


    public static String getArtifactNameExceptForVersion(Artifact artifact)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(artifact.getArtifactId());
        if (artifact.hasClassifier()) {
            sb.append("-").append(artifact.getClassifier());
        }
        String extension = artifact.getArtifactHandler().getExtension();
        if (extension != null && extension.length() > 0) {
            sb.append(".").append(extension);
        }
        return sb.toString();
    }
}
