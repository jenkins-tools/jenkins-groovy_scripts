import com.cloudbees.plugins.credentials.CredentialsStore
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.cloudbees.plugins.credentials.CredentialsScope


import hudson.slaves.*
import jenkins.model.*

def credentials_store =
        Jenkins.instance.getExtensionList(
                'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
        )[0].getStore()

StandardUsernamePasswordCredentials st = new UsernamePasswordCredentialsImpl(CredentialsScope.USER, "credential-id-2", "test2", "sunjoo", "kuie1996")
credentials_store.addCredentials(Domain.global(), st)