import hudson.model.*
import hudson.node_monitors.*
import hudson.slaves.*
import java.util.concurrent.*
jenkins = Hudson.instance
  
for ( item in jenkins.items)
{
  println("# ${item.name}")
    for ( build in item.builds )
    {
      println( " -" + build.number + " : " + build.duration / 60000 )
    }
}