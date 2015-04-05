import jenkins.model.*
import hudson.model.*
import hudson.slaves.*
import hudson.plugins.sshslaves.SSHLauncher
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
  
StandardUsernamePasswordCredentials st = new UsernamePasswordCredentialsImpl(CredentialsScope.USER,
    "credential-id", "test", "sunjoo", "kuie1996")

def slave1 = new DumbSlave("test-slave","test slave description","/tmp/jenkins1","1",Node.Mode.NORMAL,"test-slave-label",
                    new SSHLauncher("127.0.0.1", 22, st, "", "", "", ""),
                    new RetentionStrategy.Always(), new LinkedList()
            )

def node1 = Jenkins.instance.getNode("test-slave")
Jenkins.instance.removeNode(node1)
Jenkins.instance.addNode(slave1)
