import hudson.model.*

// get current thread / Executor
def thr = Thread.currentThread()
// get current build
def build = thr?.executable


// get parameters
def parameters = build?.actions.find{ it instanceof ParametersAction }?.parameters
def slaves = [] 
def label_contents =""

parameters.each {
    if ( it.name.contains('swfarm-oebuild'))
    {
        if (it.value) {
           slaves.add(it.name)
        }
    }  else if ( it.name.contains('Reserve or Free') ) {
        if ( it.value.contains('Reserve for official/integrate builds')) {
            label_contents = 'integration-build official-build'
        } else if ( it.value.contains('Free for all builds')) {
            label_contents = 'engineering-build official-build integration-build'
        } else if ( it.value.contains('Reserve for official/engineering builds')) {
            label_contents = 'engineering-build official-build'
        }
    }
}

println slaves

slaves.each{
    node = jenkins.model.Jenkins.instance.getNode(it)
    node.setLabelString(label_contents)
}