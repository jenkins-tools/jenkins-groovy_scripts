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

//Reserve for only verification builds
parameters.each {
    if ( it.name.contains('swfarm-gateuobl'))
    {
        if (it.value) {
            //slaves.add(it.name)
            println "NODE : ${it.name} - just displayed"
        }
    }  else if ( it.name.contains('Reserve or Free') ) {
        if ( it.value.contains('Reserve for official/integrate builds')) {
            label_contents = 'integration-build official-build'
        } else if ( it.value.contains('Free for all builds')) {
            label_contents = 'official-build verification integration-build engineering-build'
        } else if ( it.value.contains('Reserve for only verification builds')) {
            label_contents = 'verification'
        } else if ( it.value.contains('NONE')) {
            label_contents = ''
        }
    } else if ( it.name.contains('RANGE_START') ) {
        range_start = it.value.toInteger()

    } else if ( it.name.contains('RANGE_END') ) {

        range_end = it.value.toInteger()
    } else if ( it.name.contains('SERVER_PREFIX') ) {
        serverPrefix = it.value
    }
}

println "START : ${range_start}"
println "END: ${range_end}"


for ( x in range_start..range_end	 ) {
    //slaves.add("swfarm-gateuobld"+x)
    slaves.add(serverPrefix+x)
    //println serverPrefix+x
}

println(label_contents)
slaves.each{
    try {
        node  = jenkins.model.Jenkins.instance.getNode(it)

        node.setLabelString(label_contents)
    } catch (Exception e)
    {
        println it + " is not available"
    }
}

