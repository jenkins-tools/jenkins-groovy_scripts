import hudson.model.*
import hudson.node_monitors.*
import hudson.slaves.*
import java.util.concurrent.*
    
jenkins = Hudson.instance

println("job_name,build_number,build_duration,change_number,patchset_number,diff,start,in_queue,Triggered_in_gerrit,diff_in_millis,diff_in_minutes,Start_In_KST,GERRIT")

for ( item in jenkins.items)
{
  if ( item.name ==~ /starfish-angeles-verify-m14tv/ )
  {
    println item.builds.size()
    for ( build in item.builds )
    {

      build_directory="/opt/gatekeeper/jenkins_home/jobs/"+ item.name +"/builds/" + build.number 
      env_file = build_directory + "/injectedEnvVars.txt"
      build_file = build_directory + "/build.xml"
      change_number=""
      patchset_number=""
      gerrit_host=""
      built_on="test"

      long written_on_time = 0L

      def xml_body = ""

      try {
        def builds = new File(build_file)
        builds.each {
            if ( it.contains("xml version='1.0' encoding")){
              xml_body = xml_body
            } else {
              xml_body = xml_body + "\n" + it
            }
        }

        def records = new XmlParser().parseText(xml_body)

        def action_node = records.get("actions")[0]
        def parametersAction = action_node.get("hudson.model.ParametersAction")[0]
        def parameters = parametersAction.get("parameters")[0]

        def parameter_lists = [:] 

        parameters.children().each {
            def name = it.get("name")[0].value()[0]
            def value = it.get("value")[0].value()[0]
            parameter_lists.put(name, value)
        }
        change_number = parameter_lists.get("GERRIT_CHANGE_NUMBER")
        patchset_number = parameter_lists.get("GERRIT_PATCHSET_NUMBER")
        gerrit_host = parameter_lists.get("GERRIT_HOST")

        if ( gerrit_host ==~ /gpro.palm.com/ ) {       
          def query_string="SELECT UNIX_TIMESTAMP(written_on) FROM change_messages WHERE
patchset_change_id=${change_number} AND patchset_patch_set_id=${patchset_number} AND message LIKE
'%Build+%'"
          def query_command="echo ${query_string}".execute()
          def db_command="mysql --host=gpro.palm.com --password=welcome1 --user=jenkins
reviewdb".execute()
          def get_time_command = "sed -n 2p".execute()
          command_pipe = query_command | db_command | get_time_command
          def written_on_time_in_seconds = ""
          written_on_time_in_seconds = command_pipe.text
          written_on_time = written_on_time_in_seconds.toLong()*1000
        } else {
          written_on_time = 0    
        }
       
      } catch(java.io.FileNotFoundException e2){
        change_number="FNF"
        patchset_number="FNF"
        continue  
      }catch (Exception e){
        println(e.toString())
        change_number="FNF"
        patchset_number="FNF"
        continue
      }
      
      

      log_file = build_directory + "/log"
      def file1 = new File(log_file)
      
      def file_lines = file1.readLines()
      
      if ( file_lines[0] ==~ /Triggered by Gerrit.*/ ) {
        
        def build_starttime = build.getStartTimeInMillis()
        def build_time = build.getTimeInMillis()
        long diff1=0L
          
        if ( change_number ==~ /FNF/ ) {
          diff1 = 0L    
        } else {
          diff1=build_starttime - written_on_time
        }
        println( item.name + "," 
                + build.number + "," 
                + build.duration / 60000 + "," 
                + change_number + "," 
                + patchset_number + "," 
                + (build_starttime - build_time) + "," 
                + build_starttime + "," 
                + build_time + "," 
                + written_on_time + "," 
                + diff1 + ","
                + diff1/60000 + ","
                + new Date(build.getStartTimeInMillis()) + ","
                + gerrit_host + ","
                + built_on
                
        )
        System.exit(0)
      }
    } //for
  } // if
}//for

