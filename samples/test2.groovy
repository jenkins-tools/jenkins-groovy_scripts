#!/usr/bin/env groovy

import groovy.transform.Synchronized

class Data {
    final def test = ['wall.lge.com', 'gpro.palm.com', 'g2g.palm.com'].toList()

    @Synchronized
    String getItem(){
        def rValue = test.get(0)
        test.remove(0)
        return rValue

    }
}

def testData = new Data()

10.times {
    Thread.start {
        def exitValue = "false"
        while(exitValue =="false") {
            try
            {
                def repo_name = testData.getItem()
                println Thread.currentThread().getName() + ":"+repo_name
                def outputs = "ssh ${repo_name} gerrit ls-projects".execute().text.split('\n')
                outputs.each{
                    println "${repo_name}:${it}"
                }
            }catch(ex){
               exitValue = "true"
            }
        }
    }
}

