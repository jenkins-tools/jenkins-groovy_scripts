File f = new File("/tmp/list.sh")
writer = new PrintWriter(f)

println("(")
writer.println("(")

def count=0, MAXSIZE=100
for (aSlave in hudson.model.Hudson.instance.slaves) {
  launcher  = aSlave.launcher;
  println("\""+launcher.host+"\"");
  writer.println("\""+launcher.host+"\""); 
}
println(")")
writer.println(")")
writer.close()
println "env".execute().text
