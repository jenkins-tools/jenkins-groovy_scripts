import hudson.model.*
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
def serverPrefix = ""
def commands=""
def getParameter = { paramName ->
    parameters.findAll {
        it.name == paramName
    }[0].value
}

range_start = getParameter("RANGE_START")
range_end = getParameter("RANGE_END")
serverPrefix = getParameter("SERVER_PREFIX")
commands= getParameter("COMMANDS")

(range_start.toInteger()..range_end.toInteger()).each{
    slaves.add(serverPrefix+it)
}


def runCommand = { targetnode, targetcommands ->
    def jenkinsNode = jenkins.getNode(targetnode)
    if ( jenkinsNode != null ) {
        def launcher = jenkinsNode.launcher
        def mhost = launcher.getHost()
        println "INFO : Run commmands - ${targetcommands} - on ${targetnode}:${mhost}"
        println "ssh ${mhost} ${targetcommands}".execute().text
    }
}

slaves.each{
    runCommand(it, commands)
}
