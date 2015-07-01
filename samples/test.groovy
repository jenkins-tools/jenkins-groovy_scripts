import com.cloudbees.jenkins.plugins.sshcredentials.SSHUserPassword
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.CredentialsStore
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.cloudbees.plugins.credentials.CredentialsProvider
import hudson.plugins.sshslaves.SSHLauncher
import hudson.slaves.*
import jenkins.model.*

jenkins = Jenkins.instance
jenkins.slaves.each{ it ->
    println "##########################"
    println "it class : " + it.getClass().name
    println "it.launcher : " + it.launcher.getClass().name
    println "Host : " + it.launcher.host
    println "JVM Options: " + it.launcher.jvmOptions
    println "Working directory: " + it.launcher.getWorkingDirectory(it)
    def credentials_store =
            Jenkins.instance.getExtensionList(
                    'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
            )[0].getStore()
    StandardUsernamePasswordCredentials st = new UsernamePasswordCredentialsImpl(CredentialsScope.USER, "credential-id", "test", "sunjoo", "kuie1996")
    credentials_store.addCredentials(Domain.global(), st)
    def slave1 = new DumbSlave("test-slave","test slave description","/tmp/jenkins","1",Node.Mode.NORMAL,"test-slave-label",
                    new SSHLauncher("127.0.0.1", 22, st, "", "", "", "", "", ""),
                    new RetentionStrategy.Always(), new LinkedList())

    println "Root Path: " + it.getRootPath()

}

/*
for ( item in jenkins.items)
{
  println("# ${item.name}")
    println "# " + item.getClass().name
    for ( build in item.builds )
    {
      println( " -" + build.number + " : " + build.duration / 60000 )
    }
}
*/
println "test message"
