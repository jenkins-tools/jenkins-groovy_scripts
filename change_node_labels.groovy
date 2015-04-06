import hudson.model.*

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
def labelMode = ""

def getParameter = { paramName ->
    parameters.findAll {
        it.name == paramName
    }[0].value
}

//Reserve for only verification builds

labelMode = getParameter("Reserve or Free")
if (labelMode=="Reserve for official/integrate builds"){
    label_contents = 'integration-build official-build'
} else if ( labelMode=='Free for all builds') {
    label_contents = 'official-build verification integration-build engineering-build'
} else if ( labelMode =='Reserve for only verification builds') {
    label_contents = 'verification'
}else if ( labelMode=='NONE') {
    label_contents = ''
}

range_start=getParameter('RANGE_START').toInteger()
range_end=getParameter('RANGE_END').toInteger()
serverPrefix=getParameter('SERVER_PREFIX')


println "START : ${range_start}"
println "END: ${range_end}"


for ( x in range_start..range_end	 ) {
    slaves.add(serverPrefix+x)
}

println(label_contents)
println slaves
slaves.each{
    try {
        node  = jenkins.model.Jenkins.instance.getNode(it)
        node.setLabelString(label_contents)
    } catch (Exception e)
    {
        println it + " is not available"
    }
}

