/**
 * Created by sunjoo on 7/1/15.
 */


def env = System.getenv()
//Print all the environment variables.


def changeSubject = env['GERRIT_CHANGE_SUBJECT']
def changeMessage= env['GERRIT_CHANGE_COMMIT_MESSAGE']
def gerritHost = env['GERRIT_HOST'];
def gerritChangeNumber = env['GERRIT_CHANGE_NUMBER'];
def gerritChangeUrl= env['GERRIT_CHANGE_URL'];
def gerritPatchsetNumber = env['GERRIT_PATCHSET_NUMBER'];
/*
def decodingCommitMessage = "echo ${commitMessageEncoded}".execute() | "base64 --decode".execute()

def changeMessage =  decodingCommitMessage.text
*/



def errorMessage = [];

if ( changeSubject.matches("^(meta-lg-webos|meta-starfish|meta-).*:.*")){
    errorMessage << "Do not use a subject starting with meta-* ";
}

def changeMessageList  = changeMessage.split("\n");

changeMessageList.each { line ->
    println line;
}

errorMessage.each{
    println it;
}

def user = "sunjoo.park";
def password = "kuie1996";

if ( errorMessage.size() > 0 ){
    def message="\'{\"message\":\"";
    errorMessage.each{
        message="${message}\\n${it}";
    }
    message="${message}\"}\'";


    def reviewCommand = ['bash', '-c', "curl -H \"Content-Type: application/json\" --digest --user ${user}:${password} -d ${message} https://gpro.lgsvl.com/a/changes/${gerritChangeNumber}/revisions/${gerritPatchsetNumber}/review"];
    println "Review Command : ${reviewCommand}";
    def reviewTrigger = reviewCommand.execute(null, new File("/tmp"));
    def pcOut= new StringBuffer(), pcError= new StringBuffer()
    reviewTrigger.consumeProcessOutput(pcOut,pcError);
    reviewTrigger.waitFor();
    println "Exit: ${reviewTrigger.exitValue()}";
    println "OUT: ${pcOut}";
    println "ERROR: ${pcError}";
}

