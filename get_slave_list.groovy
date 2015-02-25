import hudson.model.*
  
  println("host,ip")

File file = new File("datat.csv") //assume it's initialized 



for (aSlave in hudson.model.Hudson.instance.slaves) {
  launcher  = aSlave.launcher;
  println(aSlave.name + "," +launcher.host);
  file << aSlave.name + "," + launcher.host + "\n"
}


def parameters = build?.actions.find{ it instanceof ParametersAction }?.parameters
def slaves = []
def label_contents =""

println ""

//Reserve for only verification builds
parameters.each {
println it.value
}

