import hudson.model.*
import jenkins.model.Jenkins
import groovy.transform.Synchronized

class MyData {
    def test = []

    @Synchronized
    String getItem(){
        def rValue = test.get(0)
        test.remove(0)
        return rValue

    }

    void addItem(String node){
        def jenkinsNode = Jenkins.instance.getNode(node)
        if (jenkinsNode != null){
            def launcher=jenkinsNode.launcher
            def nodeIp = launcher.host
            test.add(nodeIp)
        }
    }

    int size(){
        return test.size()
    }

}


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

def myData = new MyData()

(range_start.toInteger()..range_end.toInteger()).each
        {
            println "IT : " + it
            slaves.add(serverPrefix+it)
            myData.addItem(serverPrefix+it)
            println "end : " + it
        }


println "SIZE : " + myData.size()

def runCommand = { targetnode, targetcommands ->
    def threadName = Thread.currentThread().getName()
    println "INFO : ${threadName} : Run commmands - ${targetcommands} - on ${targetnode}"
    println "INFO : ${threadName} : Result : "+"ssh ${targetnode} ${targetcommands}".execute().text
    println "INFO : ${threadName} : END"
    return Thread.currentThread().getName() +":Return Value"
}

def threads = []
10.times {
    threads << Thread.start {
        def exitValue = "false"
        while(exitValue=="false"){
            try{
                def testvalue=myData.getItem()
                println Thread.currentThread().getName() + ":" + testvalue
                def testString = runCommand(testvalue, "hostname")
                println testString

            }catch(ex){
                exitValue="true"
            }
        }
    }
}

threads.each{
    it.join()
}
