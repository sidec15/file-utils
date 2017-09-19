to deploy project in Dropbox follow this guide: http://www.jitblog.net/use-dropbox-to-host-a-maven-repository/


mvn deploy -DskipTests=true -DaltDeploymentRepository=dropbox::default::file:///home/simone/Dropbox/Public/maven-repo/releases/  
