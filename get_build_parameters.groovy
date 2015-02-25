import hudson.model.*

// get current thread / Executor
def thr = Thread.currentThread()
// get current build
def build = thr?.executable


// get parameters
def parameters = build?.actions.find{ it instanceof ParametersAction }?.parameters

println "Before Loop"
//Reserve for only verification builds
parameters.each {
  println "start loop"
  println "${it.name}"
  println "End Loop"
  
}
def workspace =build.getEnvVars()["WORKSPACE"]
def buildNumber =build.getEnvVars()["BUILD_NUMBER"]
println "${workspace}"
println "${buildNumber}"
