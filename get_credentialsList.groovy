import jenkins.model.*


def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
        com.cloudbees.plugins.credentials.common.StandardCredentials.class,
        Jenkins.instance,
        null,
        null
);
for (c in creds) {
    println(c.id + ": " + c.description + " : " + c.getClass().name)

}