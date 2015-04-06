import hudson.model.*
import hudson.plugins.sshslaves.SSHLauncher
import hudson.slaves.DumbSlave
import jenkins.model.Jenkins

def jenkins = Jenkins.instance
// get current thread / Executor
def thr = Thread.currentThread()
// get current build
def build = thr?.executable

def range_start = ""
def range_end = ""



// get parameters
def parameters = build?.actions.find{ it instanceof ParametersAction }?.parameters
def slaves = []
def label_contents =""
def serverPrefix = ""
def remoteFS=""
def getParameter = { paramName ->
    parameters.findAll {
        it.name == paramName
    }[0].value
}

range_start = getParameter("RANGE_START")
range_end = getParameter("RANGE_END")
serverPrefix = getParameter("SERVER_PREFIX")
remoteFS= getParameter("REMOTE_FS")

(range_start.toInteger()..range_end.toInteger()).each{
    slaves.add(serverPrefix+it)
}

slaves.each{
    def jenkinsNode = jenkins.getNode(it)
    if ( jenkinsNode != null ) {
        def nodeName = it
        def nodeDesc = jenkinsNode.getNodeDescription()?.toString()
        def nodeRemoteFS = jenkinsNode.getRemoteFS()
        def nodeNumExecutores = jenkinsNode.getNumExecutors()
        def nodeNodMode = jenkinsNode.getNodeName()
        def nodeLabels = jenkinsNode.getLabelString()
        SSHLauncher launcher = jenkinsNode.launcher
        def launcherHost = launcher.getHost()
        def launcherCredential = launcher.getCredentials()
        def nodeRetentionStrategy = jenkinsNode.getRetentionStrategy()
        def newSlave = new DumbSlave(
                nodeName,
                nodeDesc,
                remoteFS,
                nodeNumExecutores.toString(),
                Node.Mode.NORMAL,
                nodeLabels,
                launcher,
                nodeRetentionStrategy,
                new LinkedList()
        )
        jenkins.removeNode(jenkinsNode)
        jenkins.addNode(newSlave)
    }
}

