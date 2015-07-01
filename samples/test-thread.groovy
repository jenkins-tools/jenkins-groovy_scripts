import groovy.transform.*

class Bucket {
    final def items = []

    @WithWriteLock
    void add(String item) {
        items << item
    }

    @Synchronized
    List getAllItems() {
        def name = items.get(0)
        items.remove(0)
        return name
    }
}

def bucket = new Bucket()

// Create read/write threads that use the same
// bucket instance.
def allThreads = []

bucket.add("wall.lge.com")
bucket.add("gpro.palm.com")
bucket.add("g2g.palm.com")
3.times {
    allThreads << Thread.start {
        def exitValue = "false"
        while(exitValue=="false"){
             try {
                def repo_name = bucket.getItems()
                def result =""
                result += "REPO : ${repo_name}\n"
                result += "ssh ${repo_name} gerrit ls-projects".execute().text
                println repo_name
            }catch(ex){
                exitValue = "true"
            }
        }
    }
}

allThreads.each { it.join() }

